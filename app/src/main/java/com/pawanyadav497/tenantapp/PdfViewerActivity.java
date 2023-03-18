package com.pawanyadav497.tenantapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private TextView tenantTextView;
    private AppCompatButton closebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        // Hide the status bar and navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // Hide the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        pdfView = findViewById(R.id.pdf_view);
        tenantTextView = findViewById(R.id.pdf_tenant_name);
        closebtn = findViewById(R.id.pdf_close_btn);

        String name = "Pdf File: " + getIntent().getStringExtra("filename");
        tenantTextView.setText(name);

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Load the PDF file from the absolute path using the Android PDF Viewer library
        String pdfAbsolutePath = getIntent().getStringExtra("pdfAbsolutePath");
        if (pdfAbsolutePath != null) {
            File pdfFile = new File(pdfAbsolutePath);
            if (pdfFile.exists()) {
                pdfView.fromFile(pdfFile).load();
            } else {
                Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
