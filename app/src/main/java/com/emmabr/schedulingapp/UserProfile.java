package com.emmabr.schedulingapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {


    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout Variables

    private CircleImageView mDisplayImage;
    private TextView mName;

    private Button mImageBtn;
    private Button mNameBtn;

    private ImageView mCoverPhoto1;

    private static final int GALLERY_PICK = 1;

    //Storage Firebase
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    private AnimationDrawable mAnimationDrawable1;

    // getting user data
    private TextView mNumGroups;
    private TextView mNumCals;

    private String current_uid;

    @Override
    protected void onStart() {
        super.onStart();

        mAnimationDrawable1.start();

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // start Cover photo animation
        mCoverPhoto1 = findViewById(R.id.ivCoverPhoto);

        mAnimationDrawable1 = (AnimationDrawable) mCoverPhoto1.getBackground();
        mAnimationDrawable1.setEnterFadeDuration(1000);
        mAnimationDrawable1.setExitFadeDuration(1000);


        mDisplayImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settingsName);
        mImageBtn = findViewById(R.id.settingsImageBtn);
        mNameBtn = findViewById(R.id.settingsNameBtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mNumGroups = findViewById(R.id.tvNumGroups);
        mNumCals = findViewById(R.id.tvNumCal);

        current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("nickName").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mName.setText(name);

                if (!image.equals("default")) {
                    Glide.with(getBaseContext())
                            .load(image)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.default_pic)
                                    .fitCenter())
                            .into(mDisplayImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(UserProfile.this);
            }
        });

        mNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, PopUpActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final ArrayList<String> numGroups = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(current_uid).child("userGroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childData : dataSnapshot.getChildren()) {
                    String groupName = childData.getKey();
                    numGroups.add(groupName);
                    mNumGroups.setText(Integer.toString(numGroups.size()));
                }
                setUser(numGroups);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


                mProgressDialog = new ProgressDialog(UserProfile.this);
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                String current_user_id = mCurrentUser.getUid();
                final StorageReference filepath = mImageStorage.child("user_profile_images").child(current_user_id + ".jpg");
                filepath.putFile(resultUri).continueWithTask(
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
                        return mUserDatabase.child("image").setValue(task.getResult().toString());
                    }

                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(UserProfile.this, "Success Uploading", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(UserProfile.this, "Error uploading image", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        }

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

    private void setUser(final ArrayList<String> userGroups) {
        final HashSet<String> userCalendars = new HashSet<>();
        FirebaseDatabase.getInstance().getReference().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String uniqGroup : userGroups) {
                    for (DataSnapshot childDate : dataSnapshot.getChildren()) {
                        if (uniqGroup.contentEquals(childDate.getKey())) {
                            FirebaseDatabase.getInstance().getReference().child("groups").child(uniqGroup).child("Recipients").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                                        userCalendars.add(childData.getKey());
                                        mNumCals.setText(Integer.toString(userCalendars.size() - 1));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}