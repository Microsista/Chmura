package com.example.cameraapp.data;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.cameraapp.ImageFragment;
import com.example.cameraapp.R;
import com.example.cameraapp.data.model.LoggedInUser;
import com.example.cameraapp.ui.login.LoginFragment;
import com.example.cameraapp.ui.login.VolleyCallBack;

import java.time.LocalDate;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password, Context context, FragmentManager manager, EditText usernameEditText, EditText passwordEditText) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password, context, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                System.out.println("SUKCES");
                Bundle bundle = new Bundle();
                bundle.putString("username", usernameEditText.getText().toString());
                bundle.putString("password", passwordEditText.getText().toString());
                ImageFragment fragment = new ImageFragment();
                fragment.setArguments(bundle);
                manager.beginTransaction().replace(R.id.flFragment, fragment).commit();
            }

            @Override
            public void onFailure() {
                System.out.println("NOT AUTHORIZED, RELOG");
                Toast.makeText(context, "incorrect credentials", Toast.LENGTH_LONG).show();

            }
        });
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> register(String username, String password, String email, String dob, Context context, FragmentManager manager, LocalDate date) {
        // handle login
        Result<LoggedInUser> result = dataSource.register(username, password, email, dob, context, date, new VolleyCallBack() {

            @Override
            public void onSuccess() {
                System.out.println("SUKCES");
                Toast.makeText(context, "zarejestrowany", Toast.LENGTH_LONG).show();
                manager.beginTransaction().replace(R.id.flFragment, new LoginFragment()).commit();
            }

            @Override
            public void onFailure() {
                System.out.println("NOT REGISTERED");
                Toast.makeText(context, "incorrect credentials", Toast.LENGTH_LONG).show();
            }
        }, manager);

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}