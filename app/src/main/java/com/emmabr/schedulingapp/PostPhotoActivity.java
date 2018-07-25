package com.emmabr.schedulingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPhotoActivity extends AppCompatActivity {

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    public final static int PICK_PHOTO_CODE = 1046;

    String groupID;

    ImageView ivNewPic;
    Button bPostImage;
    Button bCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        groupID = getIntent().getStringExtra("groupID");

        ivNewPic = findViewById(R.id.ivNewPic);
        bPostImage = findViewById(R.id.bPostImage);
        bCancel = findViewById(R.id.bCancel);
        bPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (photoFile != null) {
                            Date date = new Date();
                            Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, photoFile.getAbsolutePath(), null, null, null, null, null, Long.toString(date.getTime()));
                            saveMessage(message, groupID);
                            Intent intent = new Intent(PostPhotoActivity.this, GroupActivity.class);
                            intent.putExtra("groupID", groupID);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPhotoActivity.this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
                finish();
            }
        });

        if (getIntent().getBooleanExtra("Taking a new picture?", false))
            onLaunchCamera();
        else
            onPickPhoto();
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "GroupActivity");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("GroupActivity", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BitmapScaler scaler = new BitmapScaler();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Bitmap resizedBitmap = scaler.scaleToFitWidth(takenImage, 200);
                // Load the taken image into a preview
                ivNewPic.setImageBitmap(resizedBitmap);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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
