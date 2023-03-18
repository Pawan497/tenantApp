package com.pawanyadav497.tenantapp.progressdialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pawanyadav497.tenantapp.R;

public class UploadProgressDialogFragment extends DialogFragment {

    private ProgressBar progressBar;
    private TextView textView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Uploading PDF");
        builder.setMessage("Please wait...");
        builder.setCancelable(true);

        // Inflate the custom layout for the dialog
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_upload_progress_dialog, null);
        progressBar = view.findViewById(R.id.progress_bar);
        textView = view.findViewById(R.id.progress_text);

        // Set the custom view for the dialog
        builder.setView(view);

        return builder.create();
    }

    public void updateProgress(int progress) {
        Log.d("upload pdf dialog", "updateProgress in dialogfragment: "+progress);
        progressBar.setProgress(progress);
        textView.setText(progress + "%");
    }

//    public void setTitle(String message) {
//        AlertDialog alertDialog = (AlertDialog) getDialog();
//        if (alertDialog != null) {
//            alertDialog.setTitle(message);
//        }
//    }
}
