package com.example.hp.countries;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class Signup extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Signup() {
    }

    // TODO: Rename and change types and number of parameters
    public static Signup newInstance(String param1, String param2) {
        Signup fragment = new Signup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Button signup_btn;
    EditText mail,pass,confirm_pass;
    private FirebaseAuth mAuth;
    ProgressBar loading_indicator;
    Boolean isConnected;
    TextView error;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signup, container, false);

        init_components();
        validate_input();

        // Watches the text until it matches the validations, then make the button's visibility 100%
        TextWatcher mTextWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // check Fields For Empty Values
                validate_input();
            }
        };
        // Add Text watcher to all edit texts
        mail.addTextChangedListener(mTextWatcher);
        pass.addTextChangedListener(mTextWatcher);
        confirm_pass.addTextChangedListener(mTextWatcher);

        signup_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // check if both password and confirm password fields match
                if (!pass.getText().toString().trim().equals(confirm_pass.getText().toString().trim()))
                {
                    pass.setError(getResources().getString(R.string.password_error));
                }

                else
                {
                    pass.setError(null);
                    confirm_pass.setError(null);
                    registerUser();
                }
            }
        });

        return v;
    }

    /**
     * Link references to UI components
     */
    void init_components()
    {
        signup_btn = (Button) v.findViewById(R.id.signup_btn);
        mail = (EditText)v.findViewById(R.id.mail);
        pass = (EditText)v.findViewById(R.id.pass);
        confirm_pass = (EditText)v.findViewById(R.id.confirm_pass);
        loading_indicator = (ProgressBar) v.findViewById(R.id.loading_indicator);
        error = (TextView) v.findViewById(R.id.error);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Add new user to firebase
     */
    private void registerUser()
    {
        // remove all previous errors
        error.setVisibility(View.GONE);

        // get the connectivity status of the device
        isConnected = checkInternetConnectivity();

        // show the loading indicator
        loading_indicator.setVisibility(View.VISIBLE);

        // attempt to add a new user
        mAuth.createUserWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading_indicator.setVisibility(View.GONE);

                // Go to home screen if everything was alright
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Signup completed successfully!", Toast.LENGTH_LONG).show();

                    // Set the logged in boolean in shared preferences to true so that when user reopens the app
                    // it opens to the home screen directly
                    SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("logged_in", true);
                    editor.commit();
                    startActivity(new Intent(getContext(),Home.class));
                    getActivity().finish();
                }

                else
                {
                    error.setVisibility(View.GONE);
                    // show connection error if the device was not connected to the internet
                    if (isConnected == false)
                    {
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.no_internet);
                    }

                    // Show email error if the typed email already exists
                    else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.email_error);
                    }
                }
            }
        });

    }

    /**
     * Makes sure the the signup button stays faded until all fields have data in it
     * Validates that all edit texts are not empty, and that the email entered matches the mail pattern
     * if okay : make the button visible and active
     */
    void validate_input()
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches() || pass.getText().toString().trim().length() < 6 || confirm_pass.getText().toString().trim().length() < 6 || pass.getText().toString().trim().length()!= confirm_pass.getText().toString().trim().length())
        {
            signup_btn.setEnabled(false);
            signup_btn.setAlpha(0.5f);
        }
        else
        {
            signup_btn.setEnabled(true);
            signup_btn.setAlpha(1f);
        }
    }

    /**
     * Returns the internet connection state of the device (true means the device is connected)
     */
    boolean checkInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connection = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return connection;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
