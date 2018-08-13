package com.emmabr.schedulingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewPhotoActivity extends AppCompatActivity {

    private ImageView mIVPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        mIVPic = findViewById(R.id.ivPic);
        Glide.with(this).load(getIntent().getStringExtra("imageURL")).into(mIVPic);


        //display stuff
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .50));

        getSupportActionBar().hide();
    }
}
