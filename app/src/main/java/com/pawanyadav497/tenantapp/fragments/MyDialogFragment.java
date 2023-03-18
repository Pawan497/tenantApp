package com.pawanyadav497.tenantapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.dbhandler.MyPaymentDbHandler;
import com.pawanyadav497.tenantapp.model.Rent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDialogFragment extends DialogFragment {

    private EditText monthStart, monthEnd, date , amtDue, amtPaid;
    private MaterialButton closebtn;

    private Rent rent;

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

        //Database handler initialisation
        MyPaymentDbHandler myDbHandler = new MyPaymentDbHandler(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(rent != null){
            monthStart.setText(rent.getFrom());
            monthEnd.setText(rent.getTo());
            date.setText(rent.getPayment_date());
            amtDue.setText(rent.getAmt_due());
            amtPaid.setText(rent.getAmt_paid());

            builder.setTitle("Edit Payment")
                    .setView(R.layout.fragment_my_dialog)
                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String start = monthStart.getText().toString().trim();
                            String end = monthEnd.getText().toString().trim();
                            String strDate = date.getText().toString().trim();
                            String due = amtDue.getText().toString().trim();
                            String paid = amtPaid.getText().toString().trim();
                            String balance = (Integer.parseInt(due) - Integer.parseInt(paid)) + "";

                            if (isValidStart(start) && isValidEnd(end)
                                    && isValidAmtDue(due) && isValidAmtPaid(paid) && isValidDate(strDate)) {
//                                Rent rent = new Rent();
                                rent.setFrom(start);
                                rent.setTo(end);
                                rent.setPayment_date(strDate);
                                rent.setAmt_due(due);
                                rent.setAmt_paid(paid);
//                                rent.setPayment_date((new Date()).toString());
                                rent.setBalance(balance);
                                rent.setTenantID(rent.getTenantID());

                                myDbHandler.editRent(rent);
                                Toast.makeText(getActivity(), "Database edited", Toast.LENGTH_SHORT).show();


                                Fragment tenantPayListFragment = PaymentListFragment.newInstance(rent.getTenantID());
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, tenantPayListFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }


                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    myDbHandler.deleteRent(rent);
                    Toast.makeText(getActivity(), "Database deleted", Toast.LENGTH_SHORT).show();

                    Fragment tenantPayListFragment = PaymentListFragment.newInstance(rent.getTenantID());
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, tenantPayListFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

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

            builder.setTitle("Add Payment")
                    .setView(R.layout.fragment_my_dialog)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String start = monthStart.getText().toString().trim();
                            String end = monthEnd.getText().toString().trim();
                            String strdate = date.getText().toString();
                            String due = amtDue.getText().toString().trim();
                            String paid = amtPaid.getText().toString().trim();
                            String balance = (Integer.parseInt(due) - Integer.parseInt(paid)) + "";

                            if (isValidStart(start) && isValidEnd(end)
                                    && isValidAmtDue(due) && isValidAmtPaid(paid) && isValidDate(strdate)) {
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

                                Fragment tenantPayListFragment = PaymentListFragment.newInstance(currentTenantId);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, tenantPayListFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }


                        }
                    }).setNegativeButton("Cancel", null);
        }
            builder.setView(view);

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();
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

    private boolean isValidStart(String start) {
        if (!TextUtils.isEmpty(start) && start.trim().matches("\\d{1,2} [a-zA-Z]{3}")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid start date!. Please enter date in format dd MMM (e.g. 01 Jan)", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidEnd(String end) {
        if (!TextUtils.isEmpty(end) && end.trim().matches("\\d{1,2} [a-zA-Z]{3}")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid end date! Please enter date in format dd MMM (e.g. 01 Jan)", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidDate(String date) {
        if (!TextUtils.isEmpty(date) && date.trim().matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid date! Please enter date in format dd/MM/yyyy (e.g. 01/01/2023)", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidAmtDue(String amtDue) {
        if (!TextUtils.isEmpty(amtDue) && amtDue.matches("^[\\d.-]+$")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid amount due! Please enter a valid amount.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidAmtPaid(String amtPaid) {
        if (!TextUtils.isEmpty(amtPaid) && amtPaid.matches("^[\\d.-]+$")) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Invalid amount paid! Please enter a valid amount.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}