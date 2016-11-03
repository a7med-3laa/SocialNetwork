package com.sign_in.asu.socialnetwork.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sign_in.asu.socialnetwork.ChatHolder;
import com.sign_in.asu.socialnetwork.R;
import com.sign_in.asu.socialnetwork.SocialNetworkNotification;
import com.sign_in.asu.socialnetwork.model.ChatMsg;
import com.sign_in.asu.socialnetwork.model.Users;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatActivityFragment extends Fragment {
    public static FirebaseUser me;
    DatabaseReference db, dbUser;
    FirebaseRecyclerAdapter<ChatMsg, ChatHolder> j;
    @Bind(R.id.chatlist)
    RecyclerView chatList;

    @Bind(R.id.content_chat)
    FrameLayout chatContent;
    @Bind(R.id.txtmsg)
    EditText text;

    public ChatActivityFragment() {
    }

    @OnClick(R.id.btn_send)
    public void sendMsg() {
        final String msg = text.getText().toString().trim();
        if (!TextUtils.isEmpty(msg)) {

            text.setText("");
            ChatMsg chatMsg;
            chatMsg = new ChatMsg(msg, me.getUid(), me.getDisplayName());

            // send User Msg
            db.push().setValue(chatMsg).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        chatList.scrollToPosition(chatList.getAdapter().getItemCount() - 1);


                }

            });

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, v);
        me = FirebaseAuth.getInstance().getCurrentUser();
        // Databsae for users Profile Pic
        dbUser = FirebaseDatabase.getInstance().getReference("users");
        // Start notification service
        getActivity().startService(new Intent(getActivity(), SocialNetworkNotification.class));

        // Databsae for Msgs
        db = FirebaseDatabase.getInstance().getReference("msgs");

        if (!isNetworkAvailable())
            Snackbar.make(chatContent, "You must be connected to internet", Snackbar.LENGTH_LONG).show();
        LinearLayoutManager a = new LinearLayoutManager(getActivity());
        a.setStackFromEnd(true);

        chatList.setLayoutManager(a);

        // RecycleView Adapter
        j = new FirebaseRecyclerAdapter<ChatMsg, ChatHolder>(ChatMsg.class, R.layout.row_out, ChatHolder.class, db) {


            @Override
            public int getItemViewType(int position) {
                ChatMsg a = getItem(position);

                if (a.getSenderID().equalsIgnoreCase(me.getUid()))
                    return R.layout.row_in;
                else
                    return R.layout.row_out;
            }


            @Override
            protected void populateViewHolder(final ChatHolder viewHolder, final ChatMsg model, int position) {

                viewHolder.name.setText(model.getName());
                viewHolder.msg.setText(model.getMsg());

                dbUser.child(model.getSenderID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            Glide.with(getActivity()).load(dataSnapshot.getValue(Users.class).getPp())
                                    .placeholder(R.drawable.profilepic)
                                    .into(viewHolder.Pp);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            protected void onCancelled(DatabaseError databaseError) {
                super.onCancelled(databaseError);
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        };

        // set Adaoter
        chatList.setAdapter(j);
        return v;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}

