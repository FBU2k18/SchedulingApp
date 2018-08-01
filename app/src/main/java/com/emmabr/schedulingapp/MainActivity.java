package com.emmabr.schedulingapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

    private ArrayList<GroupData> mGroups;
    private MainActivityAdapter mAdapter;
    private RecyclerView mRVGroups;
    private SwipeRefreshLayout mSRLMain;
    private SearchView mSVSearch;

    private String mCurrentUser;
    private DatabaseReference mCurrUserGroupsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGroups = new ArrayList<>();
        mAdapter = new MainActivityAdapter(mGroups);
        mRVGroups = findViewById(R.id.rvGroups);
        mRVGroups.setLayoutManager(new LinearLayoutManager(this));
        mRVGroups.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRVGroups.setAdapter(mAdapter);
        mSRLMain = findViewById(R.id.srlMain);
        mSRLMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSVSearch = (SearchView) menu.findItem(R.id.svSearch).getActionView();
        mSVSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSVSearch.setMaxWidth(Integer.MAX_VALUE);
        mSVSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
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

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!mSVSearch.isIconified()) {
            mSVSearch.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    public void getGroups() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mGroups.clear();
            // current userID
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // mGroups for current user
            mCurrUserGroupsData = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser).child("userGroup");
            mCurrUserGroupsData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        if (childData.child("groupName").getValue() != null && childData.child("imageURL").getValue() != null) {
                            String groupID = childData.getKey().toString();
                            String name = childData.child("groupName").getValue().toString();
                            String imgURL = childData.child("imageURL").getValue().toString();
                            GroupData tempGroup = new GroupData(name, imgURL, groupID);
                            mGroups.add(tempGroup);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mSRLMain.setRefreshing(false);
                    mRVGroups.scrollToPosition(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("MainActivity", "Assigning mGroups failed");
                }
            });
        }
    }
}
