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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.save_and_fetch.FetchFromDataStorage;
import com.pawanyadav497.tenantapp.save_and_fetch.SaveToDatastorage;

import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment {

    private Button seeTenantList, addTenant, saveDatabase, fetchDatabase, helpbtn;
    private LinearLayoutCompat signOutbtn, exitbtn;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home ,container,false);

        seeTenantList = view.findViewById(R.id.tenant_listbtn);
        addTenant = view.findViewById(R.id.add_tenantbtn);
        signOutbtn = view.findViewById(R.id.sign_out_btn);
        exitbtn = view.findViewById(R.id.exit_btn);
        fetchDatabase = view.findViewById(R.id.fetch_data_btn);
        saveDatabase = view.findViewById(R.id.save_data_btn);
        helpbtn = view.findViewById(R.id.helpbtn);

        seeTenantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment tenantListFragment = TenantListFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, tenantListFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        addTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTenantDialogFragment myTenantDialogFragment = MyTenantDialogFragment.newInstance();
                myTenantDialogFragment.show(requireActivity().getSupportFragmentManager(),"my_tenant_dialog");
            }
        });


        signOutbtn.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.my_scale_animation);
                    signOutbtn.startAnimation(anim);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    signOut();
                }
                return true;
            }
        });


        exitbtn.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.my_scale_animation);
                    exitbtn.startAnimation(anim);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Exit the app
                    getActivity().finishAffinity();
                }
                return true;
            }
        });

        fetchDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchFromDataStorage fetcher = new FetchFromDataStorage();
                fetcher.downloadDatabases((AppCompatActivity) getActivity());
            }
        });

        saveDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveToDatastorage save = new SaveToDatastorage();
                save.uploadDatabase((AppCompatActivity) getActivity());
            }
        });

        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment helpFragment = HelpFragment.newInstance();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, helpFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    public void signOut(){

        // Navigate to home sign fragment
        HomeSignFragment homeSignFragment = new HomeSignFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, homeSignFragment);
        ft.commit();

        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Upload database files to Firebase Storage
        SaveToDatastorage save = new SaveToDatastorage();
        CountDownLatch latch = new CountDownLatch(1);
        save.uploadDatabase((AppCompatActivity) getActivity(), latch);


        // Wait for upload to complete
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "InterruptedException home Fragment " + e, Toast.LENGTH_SHORT).show();
        }

        // Delete local database files
        getActivity().deleteDatabase("payment_history.db");
        getActivity().deleteDatabase("tenant.db");

        // Sign out of Firebase Auth
        FirebaseAuth.getInstance().signOut();

    }
}
