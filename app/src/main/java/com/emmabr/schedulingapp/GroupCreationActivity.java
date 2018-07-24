package com.emmabr.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.GroupData;
import com.emmabr.schedulingapp.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.GroupData.saveGroup;

public class GroupCreationActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN = 34;
    GoogleSignInClient mGoogleSignInClient;

    //firebase variables
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;

    private EditText etGroupName;
    private EditText etSearchUser;
    private Button btnAddUser;
    private Button btnCreate;


    ArrayList<String> alUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        etGroupName = findViewById(R.id.etGroupName);
        etSearchUser = findViewById(R.id.etSearchUser);
        btnAddUser = findViewById(R.id.btnAdd);
        btnCreate = findViewById(R.id.btnCreate);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userDatabase.orderByChild("email").equalTo(etSearchUser.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userId =  dataSnapshot.getChildren().iterator().next().getKey().toString();
                            alUsers.add(userId);
                            Toast.makeText(GroupCreationActivity.this, "Added User!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GroupCreationActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get group name and set
                String groupName = etGroupName.getText().toString();
                GroupData groupData = new GroupData(groupName, "", "");
                alUsers.add(mAuth.getUid());


                saveGroup(groupData, alUsers);

                Intent intent = new Intent(GroupCreationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


            }
        });

    }
}
