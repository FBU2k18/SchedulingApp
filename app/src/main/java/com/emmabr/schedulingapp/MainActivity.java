package com.emmabr.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.GroupData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class MainActivity extends AppCompatActivity {

    ArrayList<GroupData> groups;
    MainActivityAdapter adapter;
    RecyclerView rvGroups;
    SwipeRefreshLayout srlMain;

    private String mCurrentUser;
    private DatabaseReference currUserGroupsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groups = new ArrayList<>();
        adapter = new MainActivityAdapter(groups);
        rvGroups = findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvGroups.setAdapter(adapter);
        srlMain = findViewById(R.id.srlMain);
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGroups();
            }
        });

        getGroups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCreateGroup:
                Intent intent = new Intent(MainActivity.this, GroupCreationActivity.class);
                startActivity(intent);
                break;
            case R.id.miEditProfile:
                Intent intent1 = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent1);
                break;
            case R.id.miLogOut:
                FirebaseAuth.getInstance().signOut();
                Intent logBack = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logBack);
                Toast.makeText(MainActivity.this, "Log out successful!", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void getGroups() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            groups.clear();
            // current userID
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // groups for current user
            currUserGroupsData = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser).child("userGroup");
            currUserGroupsData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        if (childData.child("groupName").getValue() != null && childData.child("imageURL").getValue() != null) {
                            String name = childData.child("groupName").getValue().toString();
                            String imgURL = childData.child("imageURL").getValue().toString();
                            GroupData tempGroup = new GroupData(name, imgURL);
                            groups.add(tempGroup);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    srlMain.setRefreshing(false);
                    rvGroups.scrollToPosition(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("MainActivity", "Assigning groups failed");
                }
            });

        }
    }
}
