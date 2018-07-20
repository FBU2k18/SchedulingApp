package com.emmabr.schedulingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import me.emmabr.schedulingapp.R;

public class GroupCreationActivity extends AppCompatActivity {

    //variables
    private DatabaseReference userDatabase;
    private RecyclerView rvUsers;
    private EditText etGroupName;
    private EditText etSearchUser;
    private Button btnAddUser;
    private Button btnCreate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("appUsers");

        etGroupName = findViewById(R.id.etGroupName);
        etSearchUser = findViewById(R.id.etSearchUser);
        btnAddUser = findViewById(R.id.btnAdd);
        btnCreate = findViewById(R.id.btnCreate);

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setHasFixedSize(true);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adds user to a list of users to be added into the group
                String queryText = etSearchUser.getText().toString();
                Query query = userDatabase.orderByChild("name").startAt(queryText).endAt(queryText + "\uf8ff");
                //firebaseUserSearch(query);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get group name and set
                String groupName = etGroupName.getText().toString();

                // get all users and add to group

                // send to firebase

                // set a new intent to go to the new group activity

            }
        });

    }

}
