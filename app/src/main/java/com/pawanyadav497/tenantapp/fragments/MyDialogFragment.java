package com.pawanyadav497.tenantapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.dbhandler.MyPaymentDbHandler;
import com.pawanyadav497.tenantapp.model.Rent;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDialogFragment extends DialogFragment {

    private EditText monthStart, monthEnd, date , amtDue, amtPaid;
    private MaterialButton closebtn;

    private Rent rent;


    private MyDialogListener listener;

    public interface MyDialogListener {
        void onMyDialogDismissed();
    }

    public void setListener(MyDialogListener listener) {
        Log.d("listener initialised", "setListener: ");
        this.listener = listener;  //listener when PaymentListFragment btn is pressed: PaymentListFragment{f01e8e8} (a08aa72a-3d8b-4a69-a19d-41762655010f id=0x7f0800e5)
    }

    public static MyDialogFragment newInstance(int tenantId) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("tenant_id", tenantId);
        fragment.setArguments(args);
        return fragment;
    }

    public static MyDialogFragment newInstance(Rent rent) {
        MyDialogFragment fragment = new MyDialogFragment();
        fragment.rent = rent;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my_dialog, null);

        monthStart = view.findViewById(R.id.monthStart);
        monthEnd = view.findViewById(R.id.monthEnd);
        date = view.findViewById(R.id.datetxt);
        amtDue = view.findViewById(R.id.amtDue);
        amtPaid = view.findViewById(R.id.amtPaid);
        closebtn = view.findViewById(R.id.closebtn);

        // Create a DecimalFormat object with a pattern that formats numbers with two decimal places
        DecimalFormat df = new DecimalFormat("0.00");

        //Database handler initialisation
        MyPaymentDbHandler myDbHandler = new MyPaymentDbHandler(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AlertDialog dialog;

        if(rent != null){
            monthStart.setText(rent.getFrom());
            monthEnd.setText(rent.getTo());
            date.setText(rent.getPayment_date());
            amtDue.setText(rent.getAmt_due());
            amtPaid.setText(rent.getAmt_paid());

            // initialize positive button as disabled
            builder.setTitle("Edit Payment")
                    .setView(view)
                    .setPositiveButton("Save Changes", null)
                    .setNegativeButton("Delete", null);;

            dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                    // set positive button disabled initially
                    positiveButton.setEnabled(false);

                    // set listener for positive button
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String start = monthStart.getText().toString().trim();
                            String end = monthEnd.getText().toString().trim();
                            String strDate = date.getText().toString().trim();
                            String due = amtDue.getText().toString().trim();
                            String paid = amtPaid.getText().toString().trim();
//                            String balance = (Integer.parseInt(due) - Integer.parseInt(paid)) + "";

                            // Convert the due and paid amounts to integers representing cents
                            long dueInt = (long) (Double.parseDouble(due) * 100);
                            long paidInt = (long) (Double.parseDouble(paid) * 100);

                            // Calculate the balance in cents
                            long balanceInt = dueInt - paidInt;

                            // Convert the balance back to a decimal value with two decimal places
                            String balance = String.format("%.2f", balanceInt / 100.0);

                            // Parse the strings as doubles and format them with two decimal places using the DecimalFormat object
                            due = df.format(Double.parseDouble(due));
                            paid = df.format(Double.parseDouble(paid));

                                rent.setFrom(start);
                                rent.setTo(end);
                                rent.setPayment_date(strDate);
                                rent.setAmt_due(due);
                                rent.setAmt_paid(paid);
                                rent.setBalance(balance);
                                rent.setTenantID(rent.getTenantID());

                                myDbHandler.editRent(rent);
                                Toast.makeText(getActivity(), "Database edited", Toast.LENGTH_SHORT).show();

                                Fragment tenantPayListFragment = PaymentListFragment.newInstance(rent.getTenantID());
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, tenantPayListFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                requireActivity().onBackPressed();
                                dismiss();
                        }
                    });

                        // validate inputs and enable/disable positive button accordingly
                        setValidationForFields( positiveButton, monthStart, monthEnd, date, amtDue, amtPaid);

                    // set listener for negative button
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDbHandler.deleteRent(rent);
                            Toast.makeText(getActivity(), "Database deleted", Toast.LENGTH_SHORT).show();

                            Fragment tenantPayListFragment = PaymentListFragment.newInstance(rent.getTenantID());
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, tenantPayListFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                            requireActivity().onBackPressed();
                            dismiss();
                        }
                    });
                }
                });

        }
        else{
            assert getArguments() != null;
            int currentTenantId = getArguments().getInt("tenant_id");

            try {
                Rent last = myDbHandler.getLastRent(currentTenantId);
                String start = last.getFrom();
                String end = last.getTo();

                monthStart.setText(currentMonthDays(start));
                monthEnd.setText(currentMonthDays(end));

                amtDue.setText(last.getAmt_due());

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Starting of Payment " , Toast.LENGTH_SHORT).show();
            }

            date.setText((new SimpleDateFormat("    dd/MM/yyyy", Locale.getDefault())).format(new Date()));

            // initialize positive button as disabled
            builder.setTitle("Add Payment")
                    .setView(view)
                    .setPositiveButton("Add", null).setNegativeButton("Cancel", null);

            dialog = builder.create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                    // set positive button disabled initially
                    positiveButton.setEnabled(false);

                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String start = monthStart.getText().toString().trim();
                            String end = monthEnd.getText().toString().trim();
                            String strdate = date.getText().toString();
                            String due = amtDue.getText().toString().trim();
                            String paid = amtPaid.getText().toString().trim();
//                            String balance = (Integer.parseInt(due) - Integer.parseInt(paid)) + "";

                            // Convert the due and paid amounts to integers representing cents
                            long dueInt = (long) (Double.parseDouble(due) * 100);
                            long paidInt = (long) (Double.parseDouble(paid) * 100);

                            // Calculate the balance in cents
                            long balanceInt = dueInt - paidInt;

                            // Convert the balance back to a decimal value with two decimal places
                            String balance = String.format("%.2f", balanceInt / 100.0);

                            // Parse the strings as doubles and format them with two decimal places using the DecimalFormat object
                            due = df.format(Double.parseDouble(due));
                            paid = df.format(Double.parseDouble(paid));


                            Rent rent = new Rent();
                                rent.setFrom(start);
                                rent.setTo(end);
                                rent.setPayment_date(strdate);
                                rent.setAmt_due(due);
                                rent.setAmt_paid(paid);
//                                rent.setPayment_date((new Date()).toString());
                                rent.setBalance(balance);
                                rent.setTenantID(currentTenantId);

                                myDbHandler.addRent(rent, currentTenantId);
                                Toast.makeText(getActivity(), "Database added", Toast.LENGTH_SHORT).show();

                                // Call the callback when the "Add" button is clicked
                                listener.onMyDialogDismissed();
                                dismiss();
                        }
                    });

                    // validate inputs and enable/disable positive button accordingly
                    setValidationForFields( positiveButton, monthStart, monthEnd, date, amtDue, amtPaid);

                }
            });

        }

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialog;
    }

    public String currentMonthDays(String startDay) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        try {
            Date dateFrom = dateFormat.parse(startDay);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFrom);

            // Check if startDay is the last day of the month
            int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (calendar.get(Calendar.DAY_OF_MONTH) == lastDayOfMonth) {
                // If it is, add 1 month to the calendar object
                calendar.add(Calendar.MONTH, 1);
                // Set the day of the month to the last day of the month
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                // Otherwise, simply add 1 month to the calendar object
                calendar.add(Calendar.MONTH, 1);
                // If the day of the month is greater than the last day of the month, set it to the last day of the month
                if (calendar.get(Calendar.DAY_OF_MONTH) > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            }

            // Get the new date from the calendar object
            Date newDateFrom = calendar.getTime();
            String newStartDay = dateFormat.format(newDateFrom);
            return newStartDay;

        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Error exception "+ e , Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    private boolean isValidStart(String start, EditText field) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        sdf.setLenient(false);
        try {
                sdf.parse(start);
                field.setError(null);
                return true;
        } catch (ParseException e) {
            field.setError("Invalid start date!. Please enter date in format dd MMM (e.g. 01 Jan)");
            return false;
        }
    }

    private boolean isValidEnd(String end, EditText field) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        sdf.setLenient(false);
        try {
            sdf.parse(end);
            field.setError(null);
                return true;
        } catch (ParseException e) {
            field.setError("Invalid end date! Please enter date in format dd MMM (e.g. 01 Jan)");
            return false;
        }
    }

    private boolean isValidDate(String date, EditText field) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            field.setError(null);
            return true;
        } catch (ParseException e) {
            field.setError("Invalid date! Please enter date in format dd/MM/yyyy (e.g. 01/01/2023)");
            return false;
        }
    }

    private boolean isValidAmtDue(String amtDue, EditText field) {
        if (!TextUtils.isEmpty(amtDue) && amtDue.matches("^-?\\d+(\\.\\d{1,2})?$")) {
            field.setError(null);
            return true;
        } else {
            field.setError("Invalid amount due! Please enter a valid amount.");
            return false;
        }
    }

    private boolean isValidAmtPaid(String amtPaid, EditText field) {
        if (!TextUtils.isEmpty(amtPaid) && amtPaid.matches("^-?\\d+(\\.\\d{1,2})?$")) {
            field.setError(null);
            return true;
        } else {
            field.setError("Invalid amount paid! Please enter a valid amount.");
            return false;
        }
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
            case R.id.monthStart:
                return isValidStart(input, field);
            case R.id.monthEnd:
                return isValidEnd(input, field);
            case R.id.datetxt:
                return isValidDate(input, field);
            case R.id.amtDue:
                return isValidAmtDue(input, field);
            case R.id.amtPaid:
                return isValidAmtPaid(input, field);
            default:
                return false;
        }
    }

}