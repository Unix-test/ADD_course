package com.appsnipp.modernlogin.controllfeatures.composer.post_controller;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.modernlogin.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MyViewHolder extends RecyclerView.ViewHolder {


    public ImageView avatars, post_image;
    public VideoView post_video;
    public TextView username, emotions, status, contents;
    public ImageButton more;
    public View mview;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    BottomSheetDialog scrollupDialog;
    LinearLayout editLinearLayout, deleteLinearLayout;
    Button Edit, Delete;


    public MyViewHolder(@NonNull final View itemView) {
        super( itemView );
        avatars = itemView.findViewById( R.id.imageView10 );
        post_image = itemView.findViewById( R.id.imageView11 );
        username = itemView.findViewById( R.id.textView4 );
        contents = itemView.findViewById( R.id.textView6 );
        status = itemView.findViewById( R.id.textView13 );
        more = itemView.findViewById( R.id.more );

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

//        mview = itemView;

    }

}
