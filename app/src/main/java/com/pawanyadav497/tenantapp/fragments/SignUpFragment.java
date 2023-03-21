package com.pawanyadav497.tenantapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.pawanyadav497.tenantapp.R;

public class SignUpFragment extends Fragment {

    private FirebaseAuth auth;

    private TextInputEditText email,password,confirmPassword;
    private Button signUpbtn;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1) {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //For back button
        LinearLayoutCompat backbtn = view.findViewById(R.id.backbtn_ly);
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


        email = view.findViewById(R.id.editTextEmailAddress2);
        if (getArguments() != null) {
            String emailString = getArguments().getString("email");
            if (emailString != null) {
                email.setText(emailString);
            }
        } else {
            email.setText("");
        }
        password = view.findViewById(R.id.editTextPassword2);
        confirmPassword = view.findViewById(R.id.editTextConfirmPassword2);
        signUpbtn = view.findViewById(R.id.signUpbtn);

        signUpbtn.setEnabled(false); // disable sign-up button initially

        // add text change listeners to all fields
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);

        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                String confirmPasswordString = confirmPassword.getText().toString().trim();

                if (emailString.isEmpty() || passwordString.isEmpty()|| confirmPasswordString.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter email, password and confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if email is valid
                if (!isValidEmail(emailString)) {
                    email.setError("Invalid email address!");
                    return;
                }

                // check if password is valid
                if (!isValidPassword(passwordString)) {
                    password.setError("Invalid password: must be at least 8 characters long, contain at least one digit, one symbol, and one letter");
                    return;
                }

                // check if passwords match
                if (!passwordString.equals(confirmPasswordString)) {
                    confirmPassword.setError("Passwords do not match");
                    return;
                }

                // create account using email and password
                 auth.createUserWithEmailAndPassword(emailString, passwordString)
                     .addOnCompleteListener(task -> {
                         if (task.isSuccessful()) {
                             // account created successfully
                             Toast.makeText(getActivity(), "Account created successfully", Toast.LENGTH_SHORT).show();

                             // navigate to Home fragment
                             HomeSignFragment homeFragment = new HomeSignFragment();
                             FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                             transaction.replace(R.id.fragment_container, homeFragment);
                             transaction.addToBackStack(null);
                             transaction.commit();

                         } else {
                             // account creation failed
                             Toast.makeText(getActivity(), "Account creation failed!! Please check your Internet Connection!", Toast.LENGTH_SHORT).show();

                         }
                     });


            }
        });

        return view;
    }

    // email validation method
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // password validation method
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordRegex);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInputsAndEnableButton();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void validateInputsAndEnableButton() {
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String confirmPasswordString = confirmPassword.getText().toString().trim();

        boolean isValidEmail = isValidEmail(emailString);
        boolean isValidPassword = isValidPassword(passwordString);
        boolean doPasswordsMatch = passwordString.equals(confirmPasswordString);

        // enable sign-up button if all fields are valid
        signUpbtn.setEnabled(isValidEmail && isValidPassword && doPasswordsMatch);

        // set error messages for invalid fields
        if (!isValidEmail) {
            email.setError("Invalid email address!");
        } else {
            email.setError(null); // clear error message
        }

        if (!isValidPassword) {
            password.setError("Invalid password: must be at least 8 characters long, contain at least one digit, one symbol, and one letter");
        } else {
            password.setError(null); // clear error message
        }

        if (!doPasswordsMatch) {
            confirmPassword.setError("Passwords do not match");
        } else {
            confirmPassword.setError(null); // clear error message
        }
    }


}