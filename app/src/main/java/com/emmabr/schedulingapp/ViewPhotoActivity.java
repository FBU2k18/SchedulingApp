package com.emmabr.schedulingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.BitmapScaler.scaleToFitWidth;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
