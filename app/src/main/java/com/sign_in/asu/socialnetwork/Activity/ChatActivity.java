package com.sign_in.asu.socialnetwork.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sign_in.asu.socialnetwork.MyApp;
import com.sign_in.asu.socialnetwork.R;
import com.sign_in.asu.socialnetwork.SettingsActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m = new MenuInflater(this);
        m.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.my_profile)
            startActivity(new Intent(this, MyProfile.class));
        else if (item.getItemId() == R.id.action_settings)
            startActivity(new Intent(this, SettingsActivity.class));


        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //switch boolean to visible to prevent send notification
        MyApp.activityResumed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //switch boolean to invisible to  send notification
        MyApp.activityPaused();
    }
}
