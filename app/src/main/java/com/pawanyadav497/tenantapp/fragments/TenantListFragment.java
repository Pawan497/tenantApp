package com.pawanyadav497.tenantapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.dbhandler.MyPaymentDbHandler;
import com.pawanyadav497.tenantapp.dbhandler.MyTenantDbHandler;
import com.pawanyadav497.tenantapp.model.Rent;
import com.pawanyadav497.tenantapp.model.Tenant;
import com.pawanyadav497.tenantapp.myrecycleview.MyRecyclerViewAdapter;
import com.pawanyadav497.tenantapp.myrecycleview.MyTenantRecyclerViewAdapter;
import com.pawanyadav497.tenantapp.viewmodels.TenantListViewModel;

import java.util.ArrayList;
import java.util.List;

public class TenantListFragment extends Fragment {

    private MyTenantDbHandler mDbHandler;
    private TenantListViewModel mViewModel;

    private RecyclerView recyclerView;
    private MyTenantRecyclerViewAdapter myTenantRecyclerViewAdapter;

    public TenantListFragment() {}

    public static TenantListFragment newInstance() {
        return new TenantListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHandler = new MyTenantDbHandler(getActivity());

        mViewModel = new ViewModelProvider(requireActivity()).get(TenantListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tenant_list, container, false);


        //For back button
        LinearLayoutCompat backbtn = view.findViewById(R.id.backbtn_ly1);
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


                recyclerView = view.findViewById(R.id.recyclerViewTenant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myTenantRecyclerViewAdapter = new MyTenantRecyclerViewAdapter(getContext(), getActivity(), new ArrayList<Tenant>());
        recyclerView.setAdapter(myTenantRecyclerViewAdapter);

        mViewModel.getTenantsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Tenant>>() {
            @Override
            public void onChanged(List<Tenant> tenants) {
                myTenantRecyclerViewAdapter.setData(tenants);
                myTenantRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDbHandler.setOnDatabaseChangedListener(null);
    }
}
