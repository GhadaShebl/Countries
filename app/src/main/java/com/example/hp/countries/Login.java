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
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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

    Button login_btn;
    EditText mail,pass;
    private FirebaseAuth mAuth;
    ProgressBar loading_indicator;
    TextView error;
    View v;
    Boolean isConnected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_login, container, false);

        init_components();
        validate_input(v);


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
                validate_input(v);
            }
        };
        mail.addTextChangedListener(mTextWatcher);
        pass.addTextChangedListener(mTextWatcher);
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mail.setError(null);
                pass.setError(null);
                loginUser();
            }
        });

        return v;
    }

    void init_components()
    {
        login_btn = (Button) v.findViewById(R.id.login_btn);
        mail = (EditText)v.findViewById(R.id.mail);
        pass = (EditText)v.findViewById(R.id.pass);
        loading_indicator = (ProgressBar) v.findViewById(R.id.loading_indicator);
        error = (TextView) v.findViewById(R.id.error);
        mAuth = FirebaseAuth.getInstance();
    }

    void validate_input(View v)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches() || pass.getText().toString().trim().length() < 6)
        {
            login_btn.setEnabled(false);
            login_btn.setAlpha(0.5f);
        }
        else
        {

            login_btn.setEnabled(true);
            login_btn.setAlpha(1f);
        }
    }

    private void loginUser()
    {
        // remove all previous errors
        error.setVisibility(View.GONE);

        // get the connectivity status of the device
        isConnected = checkInternetConnectivity();

        // show the loading indicator
        loading_indicator.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(mail.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading_indicator.setVisibility(View.GONE);

                // Go to home screen if everything was alright
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "Welcome Back!", Toast.LENGTH_LONG).show();

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

                    // Show email error if the typed email doesn't exist
                    else if (task.getException() instanceof FirebaseAuthInvalidUserException)
                    {
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.wrong_mail);
                    }

                    // Show email error if the typed email doesn't exist
                    else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        error.setVisibility(View.VISIBLE);
                        error.setText(R.string.wrong_password);
                    }
                }
            }
        });
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
    // TODO: Rename method, update argument and hook method into UI event
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
