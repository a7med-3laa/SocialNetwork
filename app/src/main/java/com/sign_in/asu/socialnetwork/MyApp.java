package com.sign_in.asu.socialnetwork;

import android.app.Application;

/**
 * Created by ahmed on 30/10/2016.
 */

public class MyApp extends Application {

    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

}

