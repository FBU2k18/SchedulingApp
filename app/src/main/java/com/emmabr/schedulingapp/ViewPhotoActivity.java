package com.emmabr.schedulingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.emmabr.schedulingapp.R;

public class ViewPhotoActivity extends AppCompatActivity {

    private ImageView mIVPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        mIVPic = findViewById(R.id.ivPic);
        Glide.with(this).load(getIntent().getStringExtra("imageURL")).into(mIVPic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
