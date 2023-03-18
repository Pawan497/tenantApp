package com.pawanyadav497.tenantapp.save_and_fetch;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pawanyadav497.tenantapp.progressdialog.DownloadDBProgressDialogFragment;
import com.pawanyadav497.tenantapp.progressdialog.DownloadProgressDialogFragment;
import com.pawanyadav497.tenantapp.progressdialog.UploadProgressDialogFragment;

import java.io.File;

public class FetchFromDataStorage {
    private DownloadDBProgressDialogFragment downloadProgressDialog;
    
    public void downloadDatabases(AppCompatActivity activity){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the user's bucket and the subdirectory for the tenant
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userRef = storageRef.child(userId);

        // Initialize the DownloadProgressDialogFragment
        downloadProgressDialog = new DownloadDBProgressDialogFragment();
        downloadProgressDialog.show(activity.getSupportFragmentManager(), "download_dbs_progress");
//        downloadProgressDialog.setAdditionalText("Downloading Database 1 of 2...");

        // Download tenant.db
        StorageReference tenantDbRef = userRef.child("tenant.db");
        File tenantDbFile = new File(activity.getDatabasePath("tenant.db").getPath());
        tenantDbRef.getFile(tenantDbFile).addOnSuccessListener(taskSnapshot -> {
            // Successfully downloaded database, do something with it
            Snackbar.make(activity.findViewById(android.R.id.content), "Data tenant.db downloaded successfully", Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle download error
            Log.e("for database upload", "Failed to download tenant.db database", exception);
//            Snackbar.make(activity.findViewById(android.R.id.content), "Failed to download database",
//                    Snackbar.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Get the download progress in bytes
            long bytesDownloaded = snapshot.getBytesTransferred();

            // Get the total size of the file in bytes
            long totalBytes = snapshot.getTotalByteCount();

            // Calculate the download progress as a percentage
            int progress = (int) ((bytesDownloaded * 100) / totalBytes);

            // Update the progress dialog with the new progress
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadProgressDialog.updateProgress(progress);
                }
            });
        });

//        downloadProgressDialog.updateProgress(0);
//        downloadProgressDialog.show(activity.getSupportFragmentManager(), "upload_paydb_progress");

        // Download payment_history.db
        StorageReference paymentDbRef = userRef.child("payment_history.db");
        File paymentDbFile = new File(activity.getDatabasePath("payment_history.db").getPath());
        paymentDbRef.getFile(paymentDbFile).addOnSuccessListener(taskSnapshot -> {
            // Successfully downloaded database, do something with it
            Snackbar.make(activity.findViewById(android.R.id.content), "Data Payment.db downloaded successfully", Snackbar.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            // Handle download error
            Log.e("for database upload", "Failed to download payment.db database", exception);
//            Snackbar.make(activity.findViewById(android.R.id.content), "Failed to download database",
//                    Snackbar.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Get the download progress in bytes
            long bytesDownloaded = snapshot.getBytesTransferred();

            // Get the total size of the file in bytes
            long totalBytes = snapshot.getTotalByteCount();

            // Calculate the download progress as a percentage
            int progress = (int) ((bytesDownloaded * 100) / totalBytes);

            // Update the progress dialog with the new progress
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadProgressDialog.setAdditionalText("Downloading Database 2 of 2...");
                    downloadProgressDialog.updateProgress(progress);
                }
            });
        });

        downloadProgressDialog.dismiss();

    }
}
