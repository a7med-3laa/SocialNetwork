package com.sign_in.asu.socialnetwork.model;

/**
 * Created by ahmed on 01/11/2016.
 */

public class Users {
    private String uid;
    private String pp;

    public Users(String pp, String UID) {
        this.pp = pp;
        this.uid = UID;
    }

    public Users() {
    }

    public String getPp() {
        return pp;
    }

    public String getUID() {
        return uid;
    }
}
