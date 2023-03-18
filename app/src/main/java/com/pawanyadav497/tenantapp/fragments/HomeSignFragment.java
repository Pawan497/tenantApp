package com.pawanyadav497.tenantapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pawanyadav497.tenantapp.MainActivity;
import com.pawanyadav497.tenantapp.R;
import com.pawanyadav497.tenantapp.save_and_fetch.FetchFromDataStorage;

public class HomeSignFragment extends Fragment {

    private FirebaseAuth mAuth;

    //new code
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;


    private TextInputEditText emailEditText, passwordEditText;
    private ImageView togglebtn;
    private Button signInbtn, signUpPagebtn;
    private SignInButton signInButton;

    private boolean isPasswordVisible = false;

    public HomeSignFragment() {
        // Required empty public constructor
        super(R.layout.fragment_home_sign);
    }

    public static HomeSignFragment newInstance() {
        HomeSignFragment fragment = new HomeSignFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //new Code
        oneTapClient = Identity.getSignInClient(requireActivity());
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

         activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken !=  null) {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d("google_activity_result", "Got ID token."+idToken);

                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            mAuth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete( Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("google_signin", "signInWithCredential:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                FetchFromDataStorage fetcher = new FetchFromDataStorage();
                                                fetcher.downloadDatabases((AppCompatActivity) getActivity());

                                                // Sign in success, update UI with the signed-in user's information
                                                ((MainActivity) getActivity()).onSignInSuccessful();
                                            } else {
                                                checkInternet();
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(getActivity(), "Authentication failed... ",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.w("google_signin", "firebase signInWithCredential:failure", task.getException());
//                                                updateUI(null);
                                            }
                                        }
                                    });
                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                        switch (e.getStatusCode()) {
                            case CommonStatusCodes.CANCELED:
                                Log.d("google_api_exception", "One-tap dialog was closed.");
                                // Don't re-prompt the user.
                                showOneTapUI = false;
                                break;
                            case CommonStatusCodes.NETWORK_ERROR:
                                Log.d("google_api_exception", "One-tap encountered a network error.");
                                // Try again or just ignore.
                                break;
                            default:
                                Log.d("google_api_exception", "Couldn't get credential from result."
                                        + e.getLocalizedMessage());
                                break;
                        }
                    }
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_sign, container, false);

        signInButton = view.findViewById(R.id.google_sign_in_button);

        //Google Sign in button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signInWithGoogle();
                googleSignInMethod();
            }
        });



        emailEditText = view.findViewById(R.id.editTextEmailAddress);
        passwordEditText = view.findViewById(R.id.editTextPassword);

        togglebtn = view.findViewById(R.id.passwordToggleImageView);


        togglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglebtn.setImageResource(R.drawable.ic_password_hidden);
                    isPasswordVisible = false;
                } else {
                    // Show the password
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    togglebtn.setImageResource(R.drawable.ic_password_visible);
                    isPasswordVisible = true;
                }
            }
        });


        signInbtn = view.findViewById(R.id.signInbtn);
        signUpPagebtn = view.findViewById(R.id.signUpPagebtn);

        signInbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSignInButtonClicked();
                }
        });

        signUpPagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment with SignUpFragment when the signUpPagebtn button is clicked
                Fragment signUpFragment = SignUpFragment.newInstance(emailEditText.getText().toString());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, signUpFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void onSignInButtonClicked() {
        // Get the email and password entered by the user
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a progress dialog while the sign-in process is being executed
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        // Call the Firebase Authentication signInWithEmailAndPassword method to sign the user in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FetchFromDataStorage fetcher = new FetchFromDataStorage();
                            fetcher.downloadDatabases((AppCompatActivity) getActivity());

                            // Sign-in successful, call the onSignInSuccessful method of the MainActivity
                            ((MainActivity) getActivity()).onSignInSuccessful();
                        } else {

                            checkInternet();

                            // Sign-in failed, show an error message to the user
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                emailEditText.setError("This email does not exist");
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                passwordEditText.setError("Incorrect password");
                            }else {
                                Toast.makeText(getActivity(), "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }}
                    }
                });
    }

    public void checkInternet(){
        // Check internet connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            // Show red toast message if internet is not connected
            Toast.makeText(getActivity(), "Internet is not connected!!", Toast.LENGTH_SHORT).show();

        }
    }

    //new code
    private void googleSignInMethod() {
        checkInternet();

        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);

                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d("google_sign_up", e.getLocalizedMessage());
                    }
                });
    }
}
