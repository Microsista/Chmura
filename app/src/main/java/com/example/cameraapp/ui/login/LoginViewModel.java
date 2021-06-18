package com.example.cameraapp.ui.login;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.example.cameraapp.data.LoginRepository;
import com.example.cameraapp.data.Result;
import com.example.cameraapp.data.model.LoggedInUser;
import com.example.cameraapp.R;

import java.time.LocalDate;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    static LoggedInUser data;
    private String m_token;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public boolean login(String username, String password, Context context, FragmentManager manager, EditText usernameEditText, EditText passwordEditText) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password, context, manager, usernameEditText, passwordEditText);
        System.out.println("RESULT: " + result);
        if (result instanceof Result.Success) {
            data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
            return true;
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
            return false;
        }
    }

    public String getDisplayName() {
        return data.getDisplayName();
    }

    public boolean register(String username, String password, String email, String dob, Context context, FragmentManager manager, LocalDate date) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.register(username, password, email, dob, context, manager, date);
        System.out.println("RESULT: " + result);
        if (result instanceof Result.Success) {
            data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
            return true;
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
            return false;
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 6;
    }

    public String getToken() {
        return data.getToken();
    }
}