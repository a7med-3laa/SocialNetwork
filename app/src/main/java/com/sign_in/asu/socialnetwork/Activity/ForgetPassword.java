package com.sign_in.asu.socialnetwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sign_in.asu.socialnetwork.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPassword extends AppCompatActivity {

    @Bind(R.id.email_restore)
    EditText emailRestore;

    @OnClick(R.id.restore_password)
    public void getPassword() {
        String email = emailRestore.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            startActivity(new Intent(ForgetPassword.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(ForgetPassword.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Password sent to your email", Toast.LENGTH_SHORT).show();
                    } else

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            emailRestore.setError("Enter Email correctly ");
    }

    @OnClick(R.id.back_to_login)
    public void back() {
        startActivity(new Intent(ForgetPassword.this, LoginActivity.class));


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
