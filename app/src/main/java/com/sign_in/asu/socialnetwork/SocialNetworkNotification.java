package com.sign_in.asu.socialnetwork;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sign_in.asu.socialnetwork.Activity.ChatActivity;
import com.sign_in.asu.socialnetwork.model.ChatMsg;

public class SocialNetworkNotification extends Service {
    FirebaseUser me;
    DatabaseReference reference;
    long[] pattern;
    Intent intent1;
    PendingIntent pendingIntent;
    String n;
    SharedPreferences pref;
    long vibrateTime;
    String color;

    public SocialNetworkNotification() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        me = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("msgs");
        if (me == null)
            this.stopSelf();
        intent1 = new Intent(getBaseContext(), ChatActivity.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent1, 0);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        n = pref.getString("notifications_new_message_ringtone", "none");
        color = pref.getString("notifications_new_message_light", " ");
        if (pref.getBoolean("notifications_new_message_vibrate", true))
            vibrateTime = 400;
        else
            vibrateTime = 0;

        pattern = new long[]{0, vibrateTime, 0};
        final NotificationManager m = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        reference.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!MyApp.isActivityVisible() && dataSnapshot.exists()) {
                    ChatMsg chatMsg = dataSnapshot.getValue(ChatMsg.class);
                    if (!chatMsg.getSenderID().equals(me.getUid())) {
                        Notification a = new Notification.Builder(getApplicationContext())
                                .setContentTitle(chatMsg.getName() + ":")
                                .setContentText(chatMsg.getMsg())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setSound(Uri.parse(n))
                                .setVibrate(pattern)
                                .setOnlyAlertOnce(true)
                                .setAutoCancel(true)
                                .build();
                        m.notify(0, a);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

}

