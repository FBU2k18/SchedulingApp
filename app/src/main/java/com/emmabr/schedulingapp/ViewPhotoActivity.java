package com.emmabr.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.emmabr.schedulingapp.R;

public class ViewPhotoActivity extends AppCompatActivity {

    ImageView ivPic;

    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        groupID = getIntent().getStringExtra("groupID");

        ivPic = findViewById(R.id.ivPic);
        Glide.with(this).load(getIntent().getStringExtra("imageURL")).into(ivPic);


        //display stuff
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .50));

        getSupportActionBar().hide();
        //setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                intent.putExtra("up", true);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
