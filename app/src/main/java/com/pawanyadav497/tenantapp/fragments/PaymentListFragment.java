package com.pawanyadav497.tenantapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.dbhandler.MyPaymentDbHandler;
import com.pawanyadav497.tenantapp.model.Rent;
import com.pawanyadav497.tenantapp.myrecycleview.MyRecyclerViewAdapter;

import java.util.List;
import java.util.Objects;

public class PaymentListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Button addRentbtn;
    private TextView textBalance;

    public PaymentListFragment() {
        // Required empty public constructor
    }

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

        MyPaymentDbHandler myDbHandler = new MyPaymentDbHandler(getContext());

        // Get the currentTenantID from the arguments bundle
        assert getArguments() != null;
        int currentTenantID = getArguments().getInt("tenant_id");

        // Get all rent data
        List<Rent> allRentList = myDbHandler.getAllRentForTenant(currentTenantID);

        // Use Recycler view
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), allRentList);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        //For back button
        LinearLayoutCompat backbtn = view.findViewById(R.id.backbtn_ly2);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        textBalance = view.findViewById(R.id.textBalance);

//        textBalance.setText(myDbHandler.getTotalBalance(currentTenantID));

        int balance = Integer.parseInt(myDbHandler.getTotalBalance(currentTenantID));

        if (balance <= 0) {
            // balance is zero or negative, so set text color to green
            textBalance.setTextColor(Color.GREEN);
        } else {
            // balance is positive, so set text color to red
            textBalance.setTextColor(Color.RED);
        }

        textBalance.setText(String.valueOf(balance));

        addRentbtn = view.findViewById(R.id.addRentbtn);

        addRentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(), "clicked tenant    id->" + currentTenantID, Toast.LENGTH_SHORT).show();
                MyDialogFragment myDialogFragment = MyDialogFragment.newInstance(currentTenantID);
                myDialogFragment.show(requireActivity().getSupportFragmentManager(),"my_dialog");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}