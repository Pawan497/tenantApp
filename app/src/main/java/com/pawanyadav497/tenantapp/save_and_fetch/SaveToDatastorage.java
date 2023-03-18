package com.pawanyadav497.tenantapp.save_and_fetch;

import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pawanyadav497.tenantapp.progressdialog.UploadDBProgressDialogFragment;
import com.pawanyadav497.tenantapp.progressdialog.UploadProgressDialogFragment;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SaveToDatastorage {

    private UploadDBProgressDialogFragment uploadProgressDialog;

    public void uploadDatabase(AppCompatActivity activity){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the user's bucket and the subdirectory for the tenant
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userRef = storageRef.child(userId);


        // Initialize the UploadProgressDialogFragment
        uploadProgressDialog = new UploadDBProgressDialogFragment();
        uploadProgressDialog.show(activity.getSupportFragmentManager(), "upload_dbs_progress");
//        uploadProgressDialog.setAdditionalText("Uploading Database 1 of 2...");

        // Delete all files in the user's directory
        userRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();
            for (StorageReference item : items) {
                item.delete().addOnSuccessListener(aVoid -> {
                    // File deleted successfully
                    Log.d("for database upload", "Deleted file: " + item.getName());
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Log.e("for database upload", "Failed to delete file: " + item.getName(), exception);
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e("for database upload", "Failed to list files in directory", exception);
        });

        // Upload tenant.db
        String tenantDbPath = activity.getDatabasePath("tenant.db").getPath();
        Uri tenantDbUri = Uri.fromFile(new File(tenantDbPath));
        StorageReference tenantDbRef = userRef.child("tenant.db");
        tenantDbRef.putFile(tenantDbUri).addOnSuccessListener(taskSnapshot -> {
            // Successfully downloaded database, do something with it
            Snackbar.make(activity.findViewById(android.R.id.content), "Data tenant.db uploaded successfully", Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle download error
            Log.e("for database upload", "Failed to upload tenant.db database", exception);
//            Snackbar.make(activity.findViewById(android.R.id.content), "Failed to upload tenant.db database",
//                    Snackbar.LENGTH_SHORT).show();
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // Update progress for tenant database upload
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                // Update the progress dialog with the current progress
                final int progressInt = (int) Math.round(progress);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadProgressDialog.updateProgress(progressInt);
                    }
                });
            }
        });


        // Upload payment_history.db
        String paymentDbPath = activity.getDatabasePath("payment_history.db").getPath();
        Uri paymentDbUri = Uri.fromFile(new File(paymentDbPath));
        StorageReference paymentDbRef = userRef.child("payment_history.db");
        paymentDbRef.putFile(paymentDbUri).addOnSuccessListener(taskSnapshot -> {
            // Successfully downloaded database, do something with it
            Snackbar.make(activity.findViewById(android.R.id.content), "Data payment.db uploaded successfully", Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle download error
            Log.e("for database upload", "Failed to upload payment.db database", exception);
//            Snackbar.make(activity.findViewById(android.R.id.content), "Failed to upload payment.db database",
//                    Snackbar.LENGTH_SHORT).show();
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // Update progress for tenant database upload
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                // Update the progress dialog with the current progress
                final int progressInt = (int) Math.round(progress);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadProgressDialog.setAdditionalText("Uploading Database 2 of 2...");
                        uploadProgressDialog.updateProgress(progressInt);
                    }
                });
            }
        });


        uploadProgressDialog.dismiss();
    }

    public void uploadDatabase(AppCompatActivity activity, CountDownLatch latch){
        uploadDatabase(activity);
        latch.countDown();
    }
}
