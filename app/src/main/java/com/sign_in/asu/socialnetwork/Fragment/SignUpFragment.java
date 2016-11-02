package com.sign_in.asu.socialnetwork.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.sign_in.asu.socialnetwork.Activity.ChatActivity;
import com.sign_in.asu.socialnetwork.Activity.LoginActivity;
import com.sign_in.asu.socialnetwork.R;
import com.sign_in.asu.socialnetwork.model.Users;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class SignUpFragment extends Fragment {
    String firstName;
    String lastName;

    @Bind(R.id.email)
    EditText inputMail;

    @Bind(R.id.password)
    EditText inputPassword;

    @Bind(R.id.progressBar)
    ProgressBar bar;

    @Bind(R.id.first_name)
    EditText inputFirstName;

    @Bind(R.id.lastname)
    EditText inputLastName;

    @Bind(R.id.containa)
    LinearLayout linearLayout;

    public SignUpFragment() {
    }

    @OnClick(R.id.btn_login_activity)
    public void returnToLogin() {
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

    @OnClick(R.id.btn_register)
    public void registerUser() {
        final String email = inputMail.getText().toString();
        final String password = inputPassword.getText().toString();
        firstName = inputFirstName.getText().toString();
        lastName = inputLastName.getText().toString();

        if (TextUtils.isEmpty(email))
            inputMail.setError("Email is Empty");
        if (TextUtils.isEmpty(password))
            inputPassword.setError("Password is Empty");
        else if (password.length() < 6)
            inputPassword.setError("your password is Short");
        else if (TextUtils.isEmpty(firstName))
            inputFirstName.setError("firstName is empty");
        else if (TextUtils.isEmpty(lastName))
            inputLastName.setError("lastName is empty");
        else {
            View v = getActivity().getCurrentFocus();
            //hide Keyboard
            if (v != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            //start animation
            linearLayout.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    linearLayout.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    //Sign up with email and password
                    Task<AuthResult> authResultTask = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            bar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                showMassege("Register Complete");
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
                                // set Display name

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName)
                                        .build();

                                FirebaseUser me = FirebaseAuth.getInstance().getCurrentUser();


                                me.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(getActivity(), ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        getActivity().finish();

                                    }
                                });


                            }
                            // if registration fail return to initial state
                            else {
                                linearLayout.setVisibility(View.VISIBLE);
                                linearLayout.setAlpha(1);
                                bar.setVisibility(View.GONE);
                                showMassege(task.getException().getMessage());
                            }

                        }
                    });

                }
            }).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    private void showMassege(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
