package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.controller.LoginController;
import com.example.pantera.questionnaire_depression.model.Patient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login) EditText mLoginView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.textInputLayoutLogin) TextInputLayout textInputLayoutLogin;
    @BindView(R.id.textInputLayoutPassword) TextInputLayout textInputLayoutPassword;
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_form) View mLoginFormView;
    private LoginController loginController;

    @Override
    protected void onStart() {
        super.onStart();
        loginController.onInit(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginController.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        QuestionnaireApplication app = ((QuestionnaireApplication) getApplication());
        loginController = app.getLoginController();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @OnClick(R.id.email_sign_in_button)
    public void logIn(){
        if (isOnline()) {
            attemptLogin();
        }else{
            Snackbar.make(mPasswordView, getResources().getString(R.string.offline_network), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @OnEditorAction(R.id.password)
    public boolean editorAction(TextView textView, int id){
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            if (isOnline()) {
                hideKeyboard();
                attemptLogin();
                return true;
            } else {
                Snackbar.make(textView, getResources().getString(R.string.offline_network), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
            }
        }
        return false;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        textInputLayoutLogin.setError(null);
        textInputLayoutPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            textInputLayoutPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            textInputLayoutLogin.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isLoginValid(email)) {
            textInputLayoutLogin.setError(getString(R.string.error_invalid_email));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loginController.login(email, password);
        }
    }

    private boolean isLoginValid(String login) {
        return login.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    public void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    public void showError(final String msg){
        Snackbar.make(mLoginFormView, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showError( int code){
        switch (code){
            case 401:
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                break;
            default:
                Snackbar.make(mLoginFormView, getResources().getString(R.string.offline_serv), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
    }

    public void loginOk(Patient patient){
        if (patient.isStartQuestionnaire()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, StarterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}

