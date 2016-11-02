package com.sign_in.asu.socialnetwork.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.sign_in.asu.socialnetwork.R;
import com.sign_in.asu.socialnetwork.SocialNetworkNotification;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Bind(R.id.email_login)
    EditText inputEmail;

    @Bind(R.id.password_login)
    EditText inputPassword;

    @Bind(R.id.containLogin)
    LinearLayout linearLayout;

    @Bind(R.id.progressBarLogin)
    ProgressBar bar;

    @OnClick(R.id.btn_login)
    public void login() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(email))
            inputEmail.setError("Email is empty");
        if (TextUtils.isEmpty(password))
            inputPassword.setError("Password is empty");
        else if (password.length() < 6)
            inputPassword.setError("your password is Short");
        else {
            // make keyboard invisible
            View v = getCurrentFocus();
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            // make animation
            linearLayout.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    linearLayout.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                bar.setVisibility(View.GONE);
                                startActivity(new Intent(getBaseContext(), ChatActivity.class));
                                finish();
                            } else {

                                linearLayout.animate().alpha(1).setDuration(400).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        bar.setVisibility(View.GONE);
                                        linearLayout.setVisibility(View.VISIBLE);

                                    }
                                });
                                showMassege(task.getException().getMessage());
                            }
                        }

                    });

                }
            });

        }
    }

    @OnClick(R.id.btn_forget)
    public void btnOnclick() {

        startActivity(new Intent(this, ForgetPassword.class));
    }

    @OnClick(R.id.btn_register_activty)
    public void btn() {

        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (isFirstTime()) {
            editor = sharedPreferences.edit();
            editor.putBoolean("FirstTime", false);
            editor.commit();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        ButterKnife.bind(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if user is already login start chat Activity
        if (user != null) {
            startActivity(new Intent(this, ChatActivity.class));
            finish();
        }


    }

    private void showMassege(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private boolean isFirstTime() {
        return sharedPreferences.getBoolean("FirstTime", true);

    }
}
