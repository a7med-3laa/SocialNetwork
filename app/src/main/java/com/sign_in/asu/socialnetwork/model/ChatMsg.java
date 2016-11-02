package com.sign_in.asu.socialnetwork.model;

import java.util.Date;

/**
 * Created by ahmed on 27/10/2016.
 */

public class ChatMsg {
    private String msg;
    private String senderID;
    private String name;

    public ChatMsg() {
    }

    public ChatMsg(String msg, String senderID, String name) {
        this.msg = msg;
        this.senderID = senderID;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }


    public String getSenderID() {
        return senderID;
    }
}
