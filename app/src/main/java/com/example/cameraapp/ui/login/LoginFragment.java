package com.example.cameraapp.ui.login;

import android.app.Notification;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cameraapp.ImageFragment;
import com.example.cameraapp.MainActivity;
import com.example.cameraapp.R;
import com.example.cameraapp.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import static com.android.volley.VolleyLog.TAG;
import static com.example.cameraapp.App.CHANNEL_1_ID;
import static com.example.cameraapp.App.CHANNEL_2_ID;

public class LoginFragment extends Fragment {
    static boolean polish = false;

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private String m_token;

    EditText usernameEditText = null;
    EditText passwordEditText = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SSL
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SSLContext context = null;

    public void doBasicAuth(View view) {
        new Connection().execute();
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            connect();
            //check()
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    private void connect() {
        // Create certificate factory
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return false;
                }
            });
            // Certificate pinning, make sure the certificate is filled and present in backend.
            InputStream caInput = getActivity().getAssets().open("raw/mycertificate.crt");
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in out KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses out TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

        } catch(IOException e) {
            e.printStackTrace();
        }catch(KeyManagementException e) {
            e.printStackTrace();
        }catch(KeyStoreException e) {
            e.printStackTrace();
        }catch(CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        URL url = null;
        try {
            url = new URL("https://192.168.1.13:8443/api/auth/signIn");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection)url.openConnection();

            String user_pass = usernameEditText.getText() + ":" + passwordEditText.getText();
            // Change to bearer authorization?
            //final String basicAuth = "Bearer " + m_token;//Base64.encodeToString(user_pass.getBytes(), Base64.NO_WRAP);
            //System.out.println(basicAuth);
            //urlConnection.setRequestProperty("Authorization", basicAuth);
        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.setSSLSocketFactory(context.getSocketFactory());
//        urlConnection.getSSLSocketFactory().
        try {
            System.out.println(urlConnection.getResponseMessage());
            System.out.println(urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder out = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                System.out.println(out.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Lifetime methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Constructor
    public LoginFragment() {
        // Required empty public constructor
    }

    // On FRAGMENT create
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager = NotificationManagerCompat.from(getActivity());
    }

    // On VIEW create
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    // On VIEW ALREADY CREATED
    private LoginViewModel loginViewModel;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("IS ADDED? " + isAdded());

//        editTextTitle = view.findViewById(R.id.edit_text_title);
//        editTextMessage = view.findViewById(R.id.edit_text_message);

        CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault( manager  );

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // References to UI elements
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        final Button loginButton = view.findViewById(R.id.login);
        final Button registerButton = view.findViewById(R.id.register);
        final ImageButton darkModeButton = view.findViewById(R.id.bDarkMode);
        final ImageButton languageButton = view.findViewById(R.id.bTranslate);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.loading);
//        final Button bSend1 = view.findViewById(R.id.bSend1);
//        final Button bSend2 = view.findViewById(R.id.bSend2);
//        final Button bSSL = view.findViewById(R.id.bSSL);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Observers (to control object visiblity, availability etc.)
        ////////////////////////////////////////////////////////////////////////////////////////////

        loginViewModel.getLoginFormState().observe(getActivity(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    //usernameEditText.setError(getString(loginFormState.getUsernameError()));
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
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Listeners (to react to user input)
        ////////////////////////////////////////////////////////////////////////////////////////////

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
                            passwordEditText.getText().toString(), getActivity(), getFragmentManager(), usernameEditText, passwordEditText);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                boolean loggedIn = loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), getActivity(), getFragmentManager(), usernameEditText, passwordEditText);


//                Bundle bundle = new Bundle();
//                bundle.putString("username", usernameEditText.getText().toString());
//                bundle.putString("password", passwordEditText.getText().toString());
//                ImageFragment fragment = new ImageFragment();
//                fragment.setArguments(bundle);
//                getFragmentManager().beginTransaction().replace(R.id.flFragment, fragment).commit();


                if (loggedIn == true)
                ;
                else {
                    Toast.makeText(getActivity(), "incorrect credentials", Toast.LENGTH_LONG).show();
                }

            }
        });


        darkModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(polish)
                    ((MainActivity)getActivity()).setLocale("en_US");
                else
                    ((MainActivity)getActivity()).setLocale("pl");
                polish = !polish;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new RegisterFragment()).commit();
            }
        });

//        bSSL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doBasicAuth(view);
//            }
//        });
//
//
//        bSend1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendOnChannel1(view);
//            }
//        });
//
//        bSend2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendOnChannel2(view);
//            }
//        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateUiWithUser(View view, LoggedInUserView model) {
        //String welcome = getString(R.string.welcome) + " " + model.getDisplayName();

        //Toast.makeText(getActivity(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void sendOnChannel1(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_1)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }
}