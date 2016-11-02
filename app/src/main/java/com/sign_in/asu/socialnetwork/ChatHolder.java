package com.sign_in.asu.socialnetwork;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ahmed on 29/10/2016.
 */
public class ChatHolder extends RecyclerView.ViewHolder {
    public TextView name, msg;
    public CircleImageView Pp;

    public ChatHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.txtInfo);
        Pp = (CircleImageView) itemView.findViewById(R.id.profilePic);
        msg = (TextView) itemView.findViewById(R.id.txtMessage);
        Pp.setBorderColor(Color.BLACK);
        Pp.setBorderWidth(2);

    }

}
