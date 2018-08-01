package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.Message;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPhotoActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private Uri mResultUri;

    private String mGroupID;

    private ImageView mIVNewPic;
    private Button mBPostImage;
    private Button mBCancelPic;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        mGroupID = getIntent().getStringExtra("mGroupID");

        mIVNewPic = findViewById(R.id.ivNewPic);
        mBPostImage = findViewById(R.id.bPostImage);
        mBCancelPic = findViewById(R.id.bCancelPic);

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

        mBCancelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mResultUri = result.getUri();

                mIVNewPic.setImageURI(mResultUri);

                mIVNewPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(PostPhotoActivity.this);
                    }
                });

                mBPostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProgressDialog = new ProgressDialog(PostPhotoActivity.this);
                        mProgressDialog.setTitle("Uploading image...");
                        mProgressDialog.setMessage("Please wait while we upload and process the image");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();

                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Date date = new Date();
                                final DatabaseReference ref = saveMessage(new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, mResultUri.toString(), null, Long.toString(date.getTime())), mGroupID);
                                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("message_images").child(ref.getKey().toString() + ".jpg");
                                filepath.putFile(mResultUri).continueWithTask(
                                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    return filepath.getDownloadUrl();
                                                }
                                                return null;
                                            }
                                        }
                                ).continueWithTask(new Continuation<Uri, Task<Void>>() {
                                    @Override
                                    public Task<Void> then(@NonNull Task<Uri> task) {
                                        return FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").child(ref.getKey().toString()).child("imageURL").setValue(task.getResult().toString());
                                    }

                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(PostPhotoActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }

    }
}
