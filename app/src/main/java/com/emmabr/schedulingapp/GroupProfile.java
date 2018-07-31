package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;
import me.emmabr.schedulingapp.R;

public class GroupProfile extends AppCompatActivity {

    private CircleImageView civGroupLogo;
    private TextView tvGroup;

    private Button bGroupPic;
    private Button bGroupName;

    private String mGroupID;

    private static final int GALLERY_PICK = 1;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        civGroupLogo = findViewById(R.id.civGroupLogo);
        tvGroup = findViewById(R.id.tvGroup);
        bGroupPic = findViewById(R.id.bGroupPic);
        bGroupName = findViewById(R.id.bGroupName);

        mGroupID = getIntent().getStringExtra("groupID");

        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("groupName").getValue().toString();
                String image = dataSnapshot.child("imageURL").getValue().toString();

                tvGroup.setText(name);

                if (!image.isEmpty()) {
                    Glide.with(getBaseContext())
                            .load(image)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.default_pic)
                                    .fitCenter())
                            .into(civGroupLogo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bGroupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(GroupProfile.this);
            }
        });

        bGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupProfile.this, GroupPopUp.class);
                intent.putExtra("groupID", mGroupID);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


                mProgressDialog = new ProgressDialog(GroupProfile.this);
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("group_images").child(mGroupID + ".jpg");
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
                        final Task t = task;
                        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot recipient : dataSnapshot.getChildren())
                                    FirebaseDatabase.getInstance().getReference().child("users").child(recipient.getKey().toString()).child("userGroup").child(mGroupID).child("imageURL").setValue(t.getResult().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        return FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("imageURL").setValue(task.getResult().toString());
                    }

                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(GroupProfile.this, "Success Uploading", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GroupProfile.this, "Error uploading image", Toast.LENGTH_LONG).show();
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
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                finish();
                break;
        }
        return true;
    }
}
