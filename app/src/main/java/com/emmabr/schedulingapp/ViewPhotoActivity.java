package com.emmabr.schedulingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

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
        BitmapScaler scaler = new BitmapScaler();
        ivPic.setImageBitmap(scaler.scaleToFitWidth(BitmapFactory.decodeFile(getIntent().getStringExtra("imageURL")), 300));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    public class BitmapScaler {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public Bitmap scaleToFitWidth(Bitmap b, int width) {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }

        // Scale and maintain aspect ratio given a desired height
        // BitmapScaler.scaleToFitHeight(bitmap, 100);
        public Bitmap scaleToFitHeight(Bitmap b, int height) {
            float factor = height / (float) b.getHeight();
            return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
        }
    }
}
