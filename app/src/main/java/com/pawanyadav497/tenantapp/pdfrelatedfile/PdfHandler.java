package com.pawanyadav497.tenantapp.pdfrelatedfile;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.pawanyadav497.tenantapp.PdfViewerActivity;
import com.pawanyadav497.tenantapp.model.Tenant;
import com.pawanyadav497.tenantapp.progressdialog.DownloadProgressDialogFragment;
import com.pawanyadav497.tenantapp.progressdialog.UploadProgressDialogFragment;

import java.io.File;

public class PdfHandler {
    private Context context;
    private String pdfAbsolutePath;
    private String pdfDownloadPath;
    private Tenant tenant;
    private DownloadProgressDialogFragment downloadProgressDialog;
    private FragmentActivity activity;

    public PdfHandler(Context context, Tenant tenant, FragmentActivity activity) {
        this.activity = activity;
        String pdfUrl = tenant.getPdf_path();
        this.tenant = tenant;
        this.context = context;
        String[] parts = pdfUrl.split("\\|\\|\\|delimiter\\|\\|\\|"); // Escape the | and \ characters with a backslash
        pdfAbsolutePath = parts[0].trim();
        pdfDownloadPath = parts[1].trim();
        Log.d("pdf_path", "PdfHandler: full path: "+pdfUrl+", absolutePath: "+pdfAbsolutePath +", downloadPath: "+pdfDownloadPath);

    }

    public void openOrDownloadPdf() {
        File pdfFile = new File(pdfAbsolutePath);

        if (pdfFile.exists()) {
            // If the PDF file exists at the absolute path, open it in a PDF viewer app
            openPdfFile(pdfFile);
        } else {
            // If the PDF file doesn't exist at the absolute path, download it and save to that path
            downloadAndOpenPdfFile(pdfFile);
        }
    }

    private void openPdfFile(File pdfFile) {
        String currentFileName = tenant.getPdf_path().substring(0, tenant.getPdf_path().indexOf(".pdf"));
        currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/")+1);

        Intent intent = new Intent(context, PdfViewerActivity.class);
        intent.putExtra("pdfAbsolutePath", pdfAbsolutePath);
        intent.putExtra("filename", currentFileName);
        context.startActivity(intent);

    }

    public void downloadAndOpenPdfFile(File pdfFile){

        // Initialize the DownloadProgressDialogFragment
        downloadProgressDialog = new DownloadProgressDialogFragment();
        downloadProgressDialog.show(activity.getSupportFragmentManager(), "download_progress");

        // Create any missing directories in the file path
        File parentDir = pdfFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Create a reference from an HTTPS URL
        // Note that in the URL, characters are URL escaped!
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfDownloadPath);

        // Download the file to the specified path
        httpsReference.getFile(Uri.fromFile(pdfFile))
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // The file download was successful
                        openPdfFile(pdfFile);
                        downloadProgressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception exception) {
                        // The file download failed
                        Log.e("pdf_download", "Failed to download PDF file: " + exception.getMessage());
                        downloadProgressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                // Update the progress dialog with the new progress
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadProgressDialog.updateProgress((int) progress);
                    }
                });
            }
        });


    }

}

