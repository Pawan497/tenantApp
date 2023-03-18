package com.pawanyadav497.tenantapp.pdfrelatedfile;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pawanyadav497.tenantapp.progressdialog.CopyProgressDialogFragment;
import com.pawanyadav497.tenantapp.progressdialog.UploadProgressDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class PdfUploader {

    private UploadProgressDialogFragment uploadProgressDialog;
    private CopyProgressDialogFragment copyProgressDialog;

//    private Handler mHandler = new Handler(Looper.getMainLooper());

    private String filePath;
    private String downloadUrl;

    private String userId, tenantId, fileName;
    Uri uri;

    public String uploadAndCopyPdf(int currentTenantID, Uri uri, String fileName, AppCompatActivity activity) throws InterruptedException{

        CountDownLatch latch = new CountDownLatch(1); // create a CountDownLatch with an initial count of 1

        this.uri = uri;
        this.fileName = fileName;

        //Upload code
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the user's bucket and the subdirectory for the tenant
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tenantId = currentTenantID + "";
        StorageReference tenantRef = storageRef.child(userId).child(tenantId);


        // Initialize the UploadProgressDialogFragment
        uploadProgressDialog = new UploadProgressDialogFragment();
        uploadProgressDialog.show(activity.getSupportFragmentManager(), "upload_progress");

        // Delete all files in the directory
        tenantRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference file : listResult.getItems()) {
                    file.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("upload pdf", "Deleted file: " + file.getName());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            Log.e("upload pdf", "Error deleting file: " + e.getMessage());
                        }
                    });
                }
            }
        });


        // Get the file URI and upload the file
        Uri fileUri = uri; // Replace with the URI of the PDF file
        StorageReference fileRef = tenantRef.child(fileName);
        UploadTask uploadTask = fileRef.putFile(fileUri);

        // Listen for the upload progress and completion
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot snapshot) {
                // Show the upload progress to the user
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                Log.d("upload pdf", "Upload progress: " + progress + "%");

                // Update the progress dialog with the current progress
                final int progressInt = (int) Math.round(progress);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadProgressDialog.updateProgress(progressInt);
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // The file was uploaded successfully
                    Log.d("upload pdf", "File uploaded successfully");

                    // Get the download URL for the uploaded file
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();

                            // Dismiss the progress dialog
                            uploadProgressDialog.dismiss();

//                            Toast.makeText(activity, "Successful copying PDF file to app directory ", Toast.LENGTH_SHORT).show();

                            //Copy code
                            copyAfterUpload(activity);

                            // Signal that the copy operation has completed
                            latch.countDown(); // decrement the CountDownLatch count by 1

                        }
                    });
                } else {
                    // An error occurred while uploading the file
                    Log.e("upload pdf", "Error uploading file: " + task.getException().getMessage());
                }
            }
        });

        // Wait for the copy operation to complete
        latch.await(); // block the thread until the count of the CountDownLatch reaches 0

        Log.d("PdfUploader", "PDF file path: " + filePath);

        return filePath;
    }

    private void copyAfterUpload(AppCompatActivity activity){

        // Initialize the UploadProgressDialogFragment for copying
        copyProgressDialog = new CopyProgressDialogFragment();
        copyProgressDialog.show(activity.getSupportFragmentManager(), "copy_progress");


        //Copy code
        try {
            // Open an input stream to the selected PDF file
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);

            // Specify the parent directory and subdirectory names
            String parentDirectoryName = userId;
            String subDirectoryName = tenantId;


            // Create the parent directory if it doesn't exist
            File parentDirectory = new File(activity.getFilesDir(), parentDirectoryName);
            if (!parentDirectory.exists()) {
                parentDirectory.mkdir();
            }


            // Create the subdirectory if it doesn't exist
            File subDirectory = new File(parentDirectory, subDirectoryName);
            if (!subDirectory.exists()) {
                subDirectory.mkdir();
            }


            File file = new File(subDirectory, fileName);

            // Get a list of all the files in the subdirectory
            File[] files = subDirectory.listFiles();

            // Iterate over the files and delete them one by one
            for (File file1 : files) {
                if (file1.isFile()) {
                    if (file1.delete()) {
                        System.out.println("File deleted successfully: " + file1.getName());
                    } else {
                        System.out.println("Failed to delete the file: " + file1.getName());
                    }
                }
            }

            // Create a new file in the subdirectory
            file.createNewFile();


            // Copy the contents of the input stream to the new file using a FileOutputStream
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            double totalBytes = file.length();
            double bytesTransferred = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                bytesTransferred += length;
                double progress = (100.0 * bytesTransferred / totalBytes);
                int progressInt = (int) Math.round(progress);
                Log.d("copyPdf", "copyAfterUpload: "+progressInt);
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        copyProgressDialog.updateProgress(progressInt);
//                    }
//                });
            }

            // Close the input stream and output stream
            inputStream.close();
            outputStream.close();

            // Dismiss the progress dialog
            copyProgressDialog.dismiss();

            Toast.makeText(activity, "The PDF file was uploaded successfully!", Toast.LENGTH_SHORT).show();

            // Update the tenant object with the new file path
            filePath = file.getAbsolutePath();

            filePath = filePath + " |||delimiter||| " + downloadUrl;

            Log.d("PdfUploader", "fileName " + fileName + " ,After copy filePath = " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PdfUploader", "uploadAndCopyPdf: " + e);
            Toast.makeText(activity, e + " <=Error copying PDF file to app directory ", Toast.LENGTH_SHORT).show();
            filePath = "xxxx";
        }

    }

}
