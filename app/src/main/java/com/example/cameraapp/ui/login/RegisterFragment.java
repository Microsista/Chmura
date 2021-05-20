package com.example.cameraapp.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cameraapp.R;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public static final String EXTRA_MESSAGE1 = "com.example.loginactivity.username";
    public static final String EXTRA_MESSAGE2 = "com.example.loginactivity.password";
    private LoginViewModel loginViewModel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EditText dobEditText = view.findViewById(R.id.dob);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        MaterialDatePicker picker =  builder.build();

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getFragmentManager(), picker.toString());

            }
        });


        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override public void onPositiveButtonClick(Long selection) {
                dobEditText.setText(picker.getHeaderText());

            }
        });


        CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault( manager  );

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);

        final Button loginButton = view.findViewById(R.id.login);
        final Button registerButton = view.findViewById(R.id.register);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(getActivity(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getActivity(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(view, loginResult.getSuccess());
                }
                //setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
               // finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), getActivity());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new LoginFragment()).commit();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("REGISTERING");
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.register(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), getActivity());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new LoginFragment()).commit();
            }
        });
    }

    private void updateUiWithUser(View view, LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " " + model.getDisplayName();
        // TODO : initiate successful logged in experience

        //Intent intent = new Intent(this, MainActivity.class);
        TextView usernameTextView = (TextView)view.findViewById(R.id.username);
        TextView passwordTextView = (TextView)view.findViewById(R.id.password);
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE1, username);
        //intent.putExtra(EXTRA_MESSAGE2, password);
        //startActivity(intent);

        Toast.makeText(getActivity(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
    }
    static public int LOGIN_RETURN_CODE = 1;

//    public void onRegister(View view) {
//        Intent intent = new Intent(this, RegisterActivity.class);
//    }

    public void onSubmit(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        TextView usernameTextView = (TextView)findViewById(R.id.username);
//        TextView passwordTextView = (TextView)findViewById(R.id.password);
//        String username = usernameTextView.getText().toString();
//        String password = passwordTextView.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE1, username);
//        intent.putExtra(EXTRA_MESSAGE2, password);
//        startActivity(intent);
//        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }
}