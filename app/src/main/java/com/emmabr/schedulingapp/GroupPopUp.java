package com.emmabr.schedulingapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.emmabr.schedulingapp.R;

public class GroupPopUp extends Activity {

    private Button mBSetGroup;
    private EditText mETNewGroupName;

    private String mGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pop_up);

        //variables
        mETNewGroupName = findViewById(R.id.etNewGroupName);
        mBSetGroup = findViewById(R.id.bSetGroup);

        mGroupID = getIntent().getStringExtra("mGroupID");

        //display stuff
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * .87), (int)(height * .28));

        mBSetGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("groupName").setValue(mETNewGroupName.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot recipient : dataSnapshot.getChildren())
                            FirebaseDatabase.getInstance().getReference().child("users").child(recipient.getKey().toString()).child("userGroup").child(mGroupID).child("groupName").setValue(mETNewGroupName.getText().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(GroupPopUp.this, "Group name updated!", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}