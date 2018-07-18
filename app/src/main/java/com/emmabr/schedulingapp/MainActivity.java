package com.emmabr.schedulingapp;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

import com.emmabr.schedulingapp.model.Group;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ArrayList<Group> groups;
    GroupAdapter adapter;
    RecyclerView rvGroups;
    SwipeRefreshLayout srlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groups = new ArrayList<>();
        adapter = new GroupAdapter(groups);
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
                //replace with intent
                Log.i("Menu","Create Group");
                break;
            case R.id.miEditProfile:
                //replace with intent
                Log.i("Menu", "Edit Profile");
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
        if (groups.size() != 0) {
            groups.clear();
            adapter.notifyDataSetChanged();
            srlMain.setRefreshing(false);
        }
        //will get groups from Firebase, but test for now
        for (int i = 0; i < 10; i++) {
            groups.add(Group.randomGroup());
        }

        rvGroups.scrollToPosition(0);
    }
}
