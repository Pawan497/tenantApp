package com.pawanyadav497.tenantapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.model.Tenant;
import com.pawanyadav497.tenantapp.pdfrelatedfile.PdfUploadListener;
import com.pawanyadav497.tenantapp.pdfrelatedfile.UploadPdfTask;
import com.pawanyadav497.tenantapp.viewmodels.TenantListViewModel;

//public class MyTenantDialogFragment extends DialogFragment implements PdfUploadListener {
//
//    private EditText name, phone, address;
//
//    private MaterialButton choosefilebtn;
//    private TextView chosenfiletxt;
//
//    private Tenant tenant;
//
//    Uri uri;
//    String fileName, currentFileName;
//
//    private TenantListViewModel tenantListViewModel;
//
//    private boolean isEdit ;
//
//    public static MyTenantDialogFragment newInstance() {
//        MyTenantDialogFragment fragment = new MyTenantDialogFragment();
//        return fragment;
//    }
//
//    public static MyTenantDialogFragment newInstance(Tenant tenant) {
//        MyTenantDialogFragment fragment = new MyTenantDialogFragment();
//        fragment.tenant = tenant;
//        return fragment;
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_dialog_tenant, null);
//
//        name = view.findViewById(R.id.tenant_name);
//        phone = view.findViewById(R.id.phoneNo);
//        address = view.findViewById(R.id.addresstxt);
//
//        choosefilebtn = view.findViewById(R.id.choosebtn);
//        chosenfiletxt = view.findViewById(R.id.chosenfiletxt);
//
//
//        choosefilebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onchooseClick();
//            }
//        });
//
//        tenantListViewModel = new ViewModelProvider(requireActivity()).get(TenantListViewModel.class);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        if (tenant != null) {
//
//            isEdit = true;
//
//            name.setText(tenant.getName());
//
//            if (!"None".equals(tenant.getPhoneNo())) {
//                phone.setText(tenant.getPhoneNo());
//            } else {
//                phone.setHint("None");
//            }
//
//            if (!"None".equals(tenant.getAddress())) {
//                address.setText(tenant.getAddress());
//            } else {
//                address.setHint("None");
//            }
//
//            if(!tenant.getPdf_path().equalsIgnoreCase("None") ){
//                currentFileName = tenant.getPdf_path().substring(0, tenant.getPdf_path().indexOf(".pdf"));
//                currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/")+1);
//                chosenfiletxt.setText(currentFileName);
//            }
//
//            builder.setTitle("Edit Tenant")
//                    .setView(R.layout.fragment_my_dialog_tenant)
//                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            String nameStr = name.getText().toString();
//                            String phoneNoStr = phone.getText().toString();
//                            String addressStr = address.getText().toString();
//
//                            if (isValidName(nameStr)) {
//                                tenant.setName(nameStr);
//                            }
//                            if (isValidPhoneNumber(phoneNoStr)) {
//                                    tenant.setPhoneNo(phoneNoStr);
//                            }
//
//                            if (isValidAddress(addressStr)) {
//                                    tenant.setAddress(addressStr);
//                            }
//
//                             if (!chosenfiletxt.getText().equals(currentFileName) && uri != null && fileName != null) {
//
//                                UploadPdfTask task = new UploadPdfTask(MyTenantDialogFragment.this, tenant, uri, fileName, MyTenantDialogFragment.this);
//                                task.execute();
//                            }else {
//                                onPdfUploadComplete(null);
//                            }
//
//                        }
//                    }).setNegativeButton("Cancel", null);
//
//        } else {
//
//            isEdit = false;
//
//            builder.setTitle("Add Tenant")
//                    .setView(R.layout.fragment_my_dialog_tenant)
//                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            String nameStr = name.getText().toString();
//                            String phoneNoStr = phone.getText().toString();
//                            String addressStr = address.getText().toString();
//
//                            if (isValidName(nameStr)) {
//                                tenant = new Tenant();
//                                tenant.setName(nameStr);
//
//                                if (isValidPhoneNumber(phoneNoStr)) {
//                                    tenant.setPhoneNo(phoneNoStr);
//                                } else {
//                                    tenant.setPhoneNo("None");
//                                }
//
//                                if (isValidAddress(addressStr)) {
//                                    tenant.setAddress(addressStr);
//                                } else {
//                                    tenant.setAddress("None");
//                                }
//
//
//                                if(!chosenfiletxt.getText().toString().equalsIgnoreCase("No file chosen")){
//                                    int newtenantId = tenantListViewModel.getLastTenantID() + 1;
//                                    tenant.setTenantID(newtenantId);
//
//                                    UploadPdfTask task = new UploadPdfTask(MyTenantDialogFragment.this, tenant, uri, fileName, MyTenantDialogFragment.this);
//                                    task.execute();
//
//                                }
//                                else {
//                                    tenant.setPdf_path("None");
//                                    onPdfUploadComplete(null);
//                                }
//
//                            }
//
//                        }
//                    }).setNegativeButton("Cancel", null);
//
//        }
//
//        builder.setView(view);
//
//        return builder.create();
//    }
//
//    public void onchooseClick(){
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            fileName = getFileName(uri);
//            chosenfiletxt.setText(fileName);
//
//        }
//    }
//
//
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    if (nameIndex >= 0) {
//                        result = cursor.getString(nameIndex);
//                    }
//                }
//            }
//            finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }
//
//    @Override
//    public void onPdfUploadComplete(String pdfFullPath) {
//        if (pdfFullPath != null) {
//            tenant.setPdf_path(pdfFullPath);
//        }
//
//        if(!isEdit) {
//            tenantListViewModel.addTenant(tenant);
////            Toast.makeText(getActivity(), "Database added => " + chosenfiletxt.getText(), Toast.LENGTH_SHORT).show();
//            Log.d("upload", "Database added tenant "+tenant.getName());
//            dismiss();
//        }else {
//            tenantListViewModel.editTenant(tenant);
////            Toast.makeText(getActivity(), "Database edited tenantID "+tenant.getTenantID(), Toast.LENGTH_SHORT).show();
//            Log.d("upload", "Database edited tenant "+tenant.getName());
//            dismiss();
//        }
//
//    }
//
//    private boolean isValidName(String name) {
//        if (TextUtils.isEmpty(name)) {
//            Toast.makeText(getActivity(), "Please enter a name.", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (name.length() < 2 || name.length() > 30) {
//            Toast.makeText(getActivity(), "Name should be between 2 and 30 characters", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (!name.matches("^[a-zA-Z\\s'-]+$")) {
//            Toast.makeText(getActivity(), "Name should only contain letters, spaces, hyphens and apostrophes", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isValidPhoneNumber(String phoneNumber) {
//        if (TextUtils.isEmpty(phoneNumber)) {
//            Toast.makeText(getActivity(), "Phone number is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (!phoneNumber.matches("^\\+?\\d{6,20}$")) {
//            Toast.makeText(getActivity(), "Please enter valid phone number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isValidAddress(String address) {
//        if (TextUtils.isEmpty(address)) {
//            Toast.makeText(getActivity(), "Address is Empty", Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (!address.matches("[a-zA-Z0-9\\s,-]+")) {
//            Toast.makeText(getActivity(), "Not a Valid Address!", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//}

//rules_version = '2';
//        service firebase.storage {
//        match /b/{bucket}/o {
//        match /{allPaths=**} {
//        // Allow authenticated users to upload files
//        allow write: if request.auth != null;
//        // Allow users who uploaded the file to read it
//        allow read: if request.auth != null && resource.metadata.uid == request.auth.uid;
//        }
//        }
//        }


//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

public class MyTenantDialogFragment extends DialogFragment implements PdfUploadListener {

    private EditText name, phone, address;

    private MaterialButton choosefilebtn;
    private TextView chosenfiletxt;

    private Tenant tenant;

    Uri uri;
    String fileName, currentFileName;

    private TenantListViewModel tenantListViewModel;

    private boolean isEdit ;

    public static MyTenantDialogFragment newInstance() {
        MyTenantDialogFragment fragment = new MyTenantDialogFragment();
        return fragment;
    }

    public static MyTenantDialogFragment newInstance(Tenant tenant) {
        MyTenantDialogFragment fragment = new MyTenantDialogFragment();
        fragment.tenant = tenant;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_dialog_tenant, null);

        name = view.findViewById(R.id.tenant_name);
        phone = view.findViewById(R.id.phoneNo);
        address = view.findViewById(R.id.addresstxt);

        choosefilebtn = view.findViewById(R.id.choosebtn);
        chosenfiletxt = view.findViewById(R.id.chosenfiletxt);

        AlertDialog dialog;

        choosefilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onchooseClick();
            }
        });

        tenantListViewModel = new ViewModelProvider(requireActivity()).get(TenantListViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (tenant != null) {

            isEdit = true;

            name.setText(tenant.getName());

            if (!"None".equals(tenant.getPhoneNo())) {
                phone.setText(tenant.getPhoneNo());
            } else {
                phone.setHint("None");
            }

            if (!"None".equals(tenant.getAddress())) {
                address.setText(tenant.getAddress());
            } else {
                address.setHint("None");
            }

            if(!tenant.getPdf_path().equalsIgnoreCase("None") ){
                currentFileName = tenant.getPdf_path().substring(0, tenant.getPdf_path().indexOf(".pdf"));
                currentFileName = currentFileName.substring(currentFileName.lastIndexOf("/")+1);
                chosenfiletxt.setText(currentFileName);
            }

            builder.setTitle("Edit Tenant")
                    .setView(view)
                    .setPositiveButton("Save Changes", null )
                    .setNegativeButton("Cancel", null);

            dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                    // set positive button disabled initially
                    positiveButton.setEnabled(false);

                    // set listener for positive button
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String nameStr = name.getText().toString();
                            String phoneNoStr = phone.getText().toString();
                            String addressStr = address.getText().toString();

                            if (isValidName(nameStr, name)) {
                                tenant.setName(nameStr);
                            }
                            if (isValidPhoneNumber(phoneNoStr, phone)) {
                                tenant.setPhoneNo(phoneNoStr);
                            }

                            if (isValidAddress(addressStr, address)) {
                                tenant.setAddress(addressStr);
                            }

                            if (!chosenfiletxt.getText().equals(currentFileName) && uri != null && fileName != null) {

                                UploadPdfTask task = new UploadPdfTask(MyTenantDialogFragment.this, tenant, uri, fileName, MyTenantDialogFragment.this);
                                task.execute();
                            }else {
                                onPdfUploadComplete(null);
                            }

                        }
                    });

                        // validate inputs and enable/disable positive button accordingly
                        setValidationForFields(positiveButton, name, phone, address);
                        setValidationForChosenPdf(positiveButton, chosenfiletxt);
                }
            });

        } else {

            isEdit = false;

            builder.setTitle("Add Tenant")
                    .setView(view)
                    .setPositiveButton("Add", null).setNegativeButton("Cancel", null);

            dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                    // set positive button disabled initially
                    positiveButton.setEnabled(false);

                    // set listener for positive button
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String nameStr = name.getText().toString();
                            String phoneNoStr = phone.getText().toString();
                            String addressStr = address.getText().toString();

                            if (isValidName(nameStr, name)) {
                                tenant = new Tenant();
                                tenant.setName(nameStr);

                                if (isValidPhoneNumber(phoneNoStr, phone)) {
                                    tenant.setPhoneNo(phoneNoStr);
                                } else {
                                    tenant.setPhoneNo("None");
                                }

                                if (isValidAddress(addressStr, address)) {
                                    tenant.setAddress(addressStr);
                                } else {
                                    tenant.setAddress("None");
                                }


                                if(!chosenfiletxt.getText().toString().equalsIgnoreCase("No file chosen")){
                                    int newtenantId = tenantListViewModel.getLastTenantID() + 1;
                                    tenant.setTenantID(newtenantId);

                                    UploadPdfTask task = new UploadPdfTask(MyTenantDialogFragment.this, tenant, uri, fileName, MyTenantDialogFragment.this);
                                    task.execute();

                                }
                                else {
                                    tenant.setPdf_path("None");
                                    onPdfUploadComplete(null);
                                }

                            }


                        }
                    });

                    // validate inputs and enable/disable positive button accordingly
                    setValidationForFields( positiveButton, name, phone, address);

                }
            });


        }

        return dialog;
    }

    public void onchooseClick(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            fileName = getFileName(uri);
            chosenfiletxt.setText(fileName);

        }
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
            finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onPdfUploadComplete(String pdfFullPath) {
        if (pdfFullPath != null) {
            tenant.setPdf_path(pdfFullPath);
        }

        if(!isEdit) {
            tenantListViewModel.addTenant(tenant);
            Log.d("upload", "Database added tenant "+tenant.getName());
            Toast.makeText(getContext(),"Tenant "+tenant.getName()+" Added!",Toast.LENGTH_SHORT).show();
            dismiss();
        }else {
            tenantListViewModel.editTenant(tenant);
            Log.d("upload", "Database edited tenant "+tenant.getName());
            dismiss();
        }

    }

    private boolean isValidName(String name, EditText field) {
        if (TextUtils.isEmpty(name)) {
            field.setError("Please enter a name.");
            return false;
        } else if (name.length() < 2 || name.length() > 30) {
            field.setError("Name should be between 2 and 30 characters");
            return false;
        } else if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            field.setError("Name should only contain letters, spaces, hyphens and apostrophes");
            return false;
        } else {
            field.setError(null);
            return true;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber, EditText field) {
        if (TextUtils.isEmpty(phoneNumber)) {
            field.setError("Phone number is Empty");
            return false;
        } else if (!phoneNumber.matches("^\\+?\\d{6,20}$")) {
            field.setError("Please enter valid phone number");
            return false;
        } else {
            field.setError(null);
            return true;
        }
    }

    private boolean isValidAddress(String address, EditText field) {
        if (TextUtils.isEmpty(address)) {
            field.setError("Address is Empty");
            return false;
        } else if (!address.matches("[a-zA-Z0-9\\s,-]+")) {
            field.setError("Not a Valid Address!");
            return false;
        } else {
            field.setError(null);
            return true;
        }
    }

    private void setValidationForChosenPdf(Button positiveButton, TextView chosenfiletxt) {
        chosenfiletxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                fileName = editable.toString();
                if(fileName != null && !fileName.equals(currentFileName)
                        && isValidName(name.getText().toString().trim(), name))
                {positiveButton.setEnabled(true);}
                else{
                    setValidationForFields(positiveButton, name, phone, address);
                }
            }
        });

    }

        private void setValidationForFields(Button positiveButton, EditText... fields) {
        for (EditText field : fields) {
            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateInputsAndEnableButton(fields, positiveButton);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void validateInputsAndEnableButton(EditText[] fields, Button positiveButton) {
        boolean allFieldsValid = true;

        for (EditText field : fields) {
            if (!isValidField(field)) {
                allFieldsValid = false;
                break;
            }
        }

        positiveButton.setEnabled(allFieldsValid);
    }

    private boolean isValidField(EditText field) {
        String input = field.getText().toString().trim();

        switch (field.getId()) {
            case R.id.tenant_name:
                return isValidName(input, field);
            case R.id.phoneNo:
                return isValidPhoneNumber(input, field) || TextUtils.isEmpty(input);
            case R.id.addresstxt:
                return isValidAddress(input, field) || TextUtils.isEmpty(input);
            default:
                return false;
        }
    }

}
