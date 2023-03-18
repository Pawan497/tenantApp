package com.pawanyadav497.tenantapp.progressdialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pawanyadav497.tenantapp.R;

public class DownloadDBProgressDialogFragment extends DialogFragment {

    private ProgressBar progressBar;
    private TextView textView;

    private TextView additionalTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Downloading Database");
        builder.setMessage("Please wait...");
        builder.setCancelable(true);

        // Inflate the custom layout for the dialog
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_upload_progress_dialog, null);
        progressBar = view.findViewById(R.id.progress_bar);
        textView = view.findViewById(R.id.progress_text);

        // Add the TextView to the view hierarchy
        additionalTextView = view.findViewById(R.id.additional_text);
        additionalTextView.setText("Downloading Database 1 of 2...");

        // Set the custom view for the dialog
        builder.setView(view);

        return builder.create();
    }

    public void updateProgress(int progress) {
        Log.d("download db dialog", "updateProgress in dialogfragment: "+progress);
        progressBar.setProgress(progress);
        textView.setText(progress + "%");
    }

    public void setAdditionalText(String text) {
        additionalTextView.setText(text);
    }

}

