package com.pawanyadav497.tenantapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.dbhandler.MyPaymentDbHandler;
import com.pawanyadav497.tenantapp.dbhandler.MyTenantDbHandler;
import com.pawanyadav497.tenantapp.model.Rent;
import com.pawanyadav497.tenantapp.myrecycleview.MyRecyclerViewAdapter;
import com.pawanyadav497.tenantapp.myrecycleviewcallback.RentDiffCallback;

import java.util.List;
import java.util.Objects;

public class PaymentListFragment extends Fragment implements MyDialogFragment.MyDialogListener {

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Button addRentbtn;
    private TextView textBalance, tenantNameTxt;

    private MyPaymentDbHandler myDbHandler;
    private List<Rent> allRentList;


    public static PaymentListFragment newInstance(int tenantID) {

        PaymentListFragment fragment = new PaymentListFragment();
        Bundle args = new Bundle();
        args.putInt("tenant_id", tenantID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTenant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myDbHandler = new MyPaymentDbHandler(getContext());

        MyTenantDbHandler tdbHandler = new MyTenantDbHandler(getContext());

        // Get the currentTenantID from the arguments bundle
        assert getArguments() != null;
        int currentTenantID = getArguments().getInt("tenant_id");

        // Get all rent data
        allRentList = myDbHandler.getAllRentForTenant(currentTenantID);

        // Use Recycler view
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), allRentList);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        //For back button
        LinearLayoutCompat backbtn = view.findViewById(R.id.backbtn_ly2);
        backbtn.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.my_scale_animation);
                    backbtn.startAnimation(anim);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    requireActivity().onBackPressed();
                }
                return true;
            }
        });

        tenantNameTxt = view.findViewById(R.id.tenantnametxt);
        tenantNameTxt.setText("Tenant Name: " + tdbHandler.getName(currentTenantID));
        tdbHandler.close();

        textBalance = view.findViewById(R.id.textBalance);

        double balanceDouble = Double.parseDouble(myDbHandler.getTotalBalance(currentTenantID));
        balanceDouble = Math.round(balanceDouble * 100.0) / 100.0;
        long balance = (long)(balanceDouble * 100);

        if (balance <= 0) {
            // balance is zero or negative, so set text color to green
            textBalance.setTextColor(Color.GREEN);
        } else {
            // balance is positive, so set text color to red
            textBalance.setTextColor(Color.RED);
        }

        textBalance.setText(myDbHandler.getTotalBalance(currentTenantID));

        addRentbtn = view.findViewById(R.id.addRentbtn);

        addRentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(), "clicked tenant    id->" + currentTenantID, Toast.LENGTH_SHORT).show();
                MyDialogFragment myDialogFragment = MyDialogFragment.newInstance(currentTenantID);
                myDialogFragment.setListener(PaymentListFragment.this); // Set the listener to the current fragment instance
                myDialogFragment.show(requireActivity().getSupportFragmentManager(),"my_dialog");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        myDbHandler.close();

    }

    @Override
    public void onMyDialogDismissed() {
        Log.d("PaymentListFragment", "onMyDialogDismissed() called");
        updateUI();
    }

        private void updateUI() {

            Log.d("PaymentListFragment", "updateUI() called");

            // Get the currentTenantID from the arguments bundle
        assert getArguments() != null;
        int currentTenantID = getArguments().getInt("tenant_id");

        // Get all rent data
        List<Rent> newRentList = myDbHandler.getAllRentForTenant(currentTenantID);

        // Use Recycler view
        if (recyclerView.getAdapter() == null) {
            myRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), newRentList);
            recyclerView.setAdapter(myRecyclerViewAdapter);
        } else {
            // Calculate the differences between the old and new data
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new RentDiffCallback(allRentList, newRentList));

            // Update the adapter with the new data
            allRentList.clear();
            allRentList.addAll(newRentList);
            diffResult.dispatchUpdatesTo(myRecyclerViewAdapter);
        }

        // Update the balance text
        double balanceDouble = Double.parseDouble(myDbHandler.getTotalBalance(currentTenantID));
        balanceDouble = Math.round(balanceDouble * 100.0) / 100.0;
        long balance = (long)(balanceDouble * 100);

            if (balance <= 0) {
                // balance is zero or negative, so set text color to green
                textBalance.setTextColor(Color.GREEN);
            } else {
                // balance is positive, so set text color to red
                textBalance.setTextColor(Color.RED);
            }

            textBalance.setText(myDbHandler.getTotalBalance(currentTenantID));

        }


}