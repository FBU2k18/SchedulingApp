package com.emmabr.schedulingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.emmabr.schedulingapp.R;

public class PopUpActivity extends Activity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mNameStorage;

    private Button mBtnSet;
    private EditText mETUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        //variables
        mETUsername = findViewById(R.id.etPollName);
        mBtnSet = findViewById(R.id.btnSet);

        //firebase variables
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mNameStorage = FirebaseStorage.getInstance().getReference();

        //display stuff
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(height * .28));

        //listeners
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        mBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newName = mETUsername.getText().toString();

                mUserDatabase.child("nickName").setValue(newName);

                Toast.makeText(PopUpActivity.this, "Username updated!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
