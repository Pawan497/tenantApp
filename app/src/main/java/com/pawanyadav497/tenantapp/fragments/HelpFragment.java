package com.pawanyadav497.tenantapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.pawanyadav497.tenantapp.R;

public class HelpFragment extends Fragment {

    private String mParam1;
    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_help, container, false);

        //For back button
        LinearLayoutCompat backbtn = view.findViewById(R.id.backbtn_lyhelp);
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


        return view;
    }
}

