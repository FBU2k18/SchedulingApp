package com.emmabr.schedulingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.GroupData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<GroupData> mGroups;
    private MainActivityAdapter mAdapter;
    private RecyclerView mRVGroups;
    private SwipeRefreshLayout mSRLMain;
    private SearchView mSVSearch;
    private TextView mTVHasGroups;

    private String mCurrentUser;
    private DatabaseReference mCurrUserGroupsData;


    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;
    private final String mCHANNEL_ID = "";
    private String mGroupOpen = "";
    private Date mDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotifications();

        mGroups = new ArrayList<>();
        mAdapter = new MainActivityAdapter(mGroups, this);
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
        mTVHasGroups = findViewById(R.id.tvHasGroups);

        getGroups();
    }

    @Override
    protected void onResume() {
        super.onResume();
        blockNotifications("");
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
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
                GoogleSignInOptions gsoLO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClientLogOut = GoogleSignIn.getClient(getApplicationContext(), gsoLO);
                mGoogleSignInClientLogOut.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Intent logBack = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(logBack);
                        Toast.makeText(MainActivity.this, "Log out successful!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
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
            // current userID
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // mGroups for current user
            mCurrUserGroupsData = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser).child("userGroup");
            mCurrUserGroupsData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mGroups.clear();
                    int pos = 0;
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        if (childData.child("groupName").getValue() != null && childData.child("imageURL").getValue() != null) {
                            String groupID = childData.getKey().toString();
                            sendMessageNotification(childData, groupID, pos);
                            String name = childData.child("groupName").getValue().toString();
                            String imgURL = childData.child("imageURL").getValue().toString();
                            final ArrayList<String> members = new ArrayList<>();
                            GroupData tempGroup = new GroupData(name, imgURL, groupID, members);
                            mGroups.add(tempGroup);
                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    members.clear();
                                    for (final DataSnapshot member : dataSnapshot.getChildren()) {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(member.getKey().toString()).child("nickName").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() == null)
                                                    return;
                                                members.add(dataSnapshot.getValue().toString());
                                                mAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            pos++;
                        }
                    }
                    mNotificationManager.cancelAll();
                    mAdapter.notifyDataSetChanged();
                    mSRLMain.setRefreshing(false);
                    mRVGroups.scrollToPosition(0);
                    if (mGroups.size() == 0)
                        mTVHasGroups.setText("It appears you have no groups. Open the menu above to create your first!");
                    else
                        mTVHasGroups.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("MainActivity", "Assigning mGroups failed");
                }
            });
        }
    }

    public void createNotifications() {
        if (Build.VERSION.SDK_INT > 23)
            mNotificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT > 26) {
            mNotificationChannel = new NotificationChannel(mCHANNEL_ID, "Messages", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }
    }

    public void sendMessageNotification(final DataSnapshot group, final String groupID, final int pos) {
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDate = new Date();
                Long createdAt = Long.parseLong(dataSnapshot.child("createdAt").getValue().toString());
                Long difference = mDate.getTime() - createdAt;
                if ((!dataSnapshot.child("userID").getValue().toString().equals(FirebaseAuth.getInstance().getUid())) && difference < 1000 && (!mGroupOpen.equals(groupID))) {
                    String message = dataSnapshot.child("nickName").getValue().toString();
                    if (dataSnapshot.hasChild("messageText"))
                        message = message.concat(": " + dataSnapshot.child("messageText").getValue().toString());
                    else if (dataSnapshot.hasChild("imageURL"))
                        message = message.concat("has sent an image.");
                    else
                        message = message.concat(": " + dataSnapshot.child("pollTitle").getValue().toString());
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(mGroups.get(pos).getGroupId()).child("unreadMessages").setValue("true");
                    mNotificationManager.notify(s, pos, new NotificationCompat.Builder(MainActivity.this, mCHANNEL_ID).setContentTitle(group.child("groupName").getValue().toString()).setContentText(message).setSmallIcon(R.drawable.blacklogo).setContentIntent(PendingIntent.getActivity(MainActivity.this, pos, new Intent(MainActivity.this, GroupActivity.class).putExtra("mGroupID", groupID), 0)).setAutoCancel(true).build());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void clearNotifications(int pos) {
        StatusBarNotification[] notifications = new StatusBarNotification[0];
        if (Build.VERSION.SDK_INT > 23)
            notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications)
            if (notification.getId() == pos)
                mNotificationManager.cancel(notification.getTag(), pos);
    }

    public void blockNotifications(String groupID) {
        mGroupOpen = groupID;
    }
}
