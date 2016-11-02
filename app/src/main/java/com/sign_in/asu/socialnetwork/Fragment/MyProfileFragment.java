package com.sign_in.asu.socialnetwork.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sign_in.asu.socialnetwork.Activity.LoginActivity;
import com.sign_in.asu.socialnetwork.R;
import com.sign_in.asu.socialnetwork.SocialNetworkNotification;
import com.sign_in.asu.socialnetwork.model.Users;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyProfileFragment extends Fragment {

    FirebaseUser me;
    StorageReference files;
    DatabaseReference dbUser;


    ProgressDialog p;

    @Bind(R.id.useremail)
    TextView txtemail;


    @Bind(R.id.pp2)
    CircleImageView circularImage;


    public MyProfileFragment() {
    }

    @OnClick(R.id.edtemail)
    public void edtemail() {


        AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
        a.setTitle("Change your Email");
        final EditText email = new EditText(getActivity());
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        a.setView(email);
        a.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailStr = email.getText().toString();
                if (TextUtils.isEmpty(emailStr))
                    Toast.makeText(getActivity(), "Email is empty ", Toast.LENGTH_SHORT).show();
                else {
                    me.updateEmail(emailStr).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showMsg("email Changes");
                                me.reload();
                                txtemail.setText(me.getEmail());

                            } else
                                showMsg(task.getException().getMessage());
                        }
                    });

                }
            }
        });
        a.setCancelable(true);
        a.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    @OnClick(R.id.changePassword)
    public void edtpassword() {
        AlertDialog.Builder a = new AlertDialog.Builder(getActivity());
        a.setTitle("Change your Password");
        final EditText pass = new EditText(getActivity());
        pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
        a.setView(pass);
        a.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = pass.getText().toString();
                if (TextUtils.isEmpty(password) || password.length() < 6)
                    Toast.makeText(getActivity(), "enter password correctly ", Toast.LENGTH_SHORT).show();
                else {
                    me.updatePassword(password).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                showMsg("Password Changes");
                            else
                                showMsg(task.getException().getMessage());
                        }
                    });

                }
            }
        });
        a.setCancelable(true);
        a.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }

    // open GALLERY TO get Image
    @OnClick(R.id.uploadImage)
    public void uploadImage() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        p = new ProgressDialog(getActivity());
        p.setMessage("Uploading...");

        startActivityForResult(i, 0);
        p.show();
    }

    // SignOut from FireBase
    @OnClick(R.id.Signout)
    public void signout() {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        getActivity().stopService(new Intent(getActivity(), SocialNetworkNotification.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, v);

        me = FirebaseAuth.getInstance().getCurrentUser();
        //make UserID node in FireBase Storage for Upload Image
        files = FirebaseStorage.getInstance().getReference("photos").child(me.getUid());
        // Database for Users Profile Pic
        dbUser = FirebaseDatabase.getInstance().getReference("users").child(me.getUid());

        txtemail.setText(me.getEmail());
        circularImage.setBorderOverlay(false);
        circularImage.setBorderColor(Color.BLACK);
        circularImage.setBorderWidth(4);
        // set Profile Pic
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users temp = dataSnapshot.getValue(Users.class);
                    Glide.with(getActivity()).load(temp.getPp()).into(circularImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    private void showMsg(String msg) {

        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {
            //get Image Uri
            final Uri uri = data.getData();
            // send Image to FireBase Storage
            files.putFile(uri).addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Users Me = new Users(task.getResult().getDownloadUrl().toString(), me.getUid());

                        dbUser.setValue(Me).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                p.dismiss();
                                dbUser.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Users Temp = dataSnapshot.getValue(Users.class);

                                        Glide.with(MyProfileFragment.this).load(Temp.getPp()).into(circularImage);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }
}
