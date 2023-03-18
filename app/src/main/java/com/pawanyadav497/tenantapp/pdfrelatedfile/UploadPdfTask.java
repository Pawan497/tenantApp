package com.pawanyadav497.tenantapp.pdfrelatedfile;

import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.pawanyadav497.tenantapp.fragments.MyTenantDialogFragment;
import com.pawanyadav497.tenantapp.model.Tenant;

import java.lang.ref.WeakReference;

public class UploadPdfTask extends AsyncTask<Void, Void, String> {

//    private WeakReference<MyTenantDialogFragment> dialogFragment;
    private MyTenantDialogFragment dialogFragment;
    private Tenant tenant;
    private Uri uri;
    private String fileName;
    private PdfUploadListener listener;


    public UploadPdfTask(MyTenantDialogFragment dialogFragment, Tenant tenant, Uri uri, String fileName, PdfUploadListener listener) {
//        this.dialogFragment = new WeakReference<>(dialogFragment) ;
        this.dialogFragment = dialogFragment;
        this.tenant = tenant;
        this.uri = uri;
        this.fileName = fileName;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        PdfUploader pdfUploader = new PdfUploader();
        try {
            return pdfUploader.uploadAndCopyPdf(tenant.getTenantID(), uri, fileName, (AppCompatActivity) dialogFragment.getActivity());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String pdfFullPath) {
        super.onPostExecute(pdfFullPath);
        tenant.setPdf_path(pdfFullPath);
//        dialogFragment.dismiss();

//        MyTenantDialogFragment fragment = dialogFragment.get();
        if (dialogFragment != null) {
            // Notify the fragment that the file upload is complete
            listener.onPdfUploadComplete(pdfFullPath);

        }
    }
}
