package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emmabr.schedulingapp.Models.GroupData;
import com.emmabr.schedulingapp.Models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.emmabr.schedulingapp.Models.GroupData.saveGroup;

public class GroupCreationActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN = 34;
    GoogleSignInClient mGoogleSignInClient;


    //firebase variables
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView mRVUsers;
    private EditText mETGroupName;
    private EditText mETSearchUser;
    private Button mBtnCreate;

    private ArrayList<String> mALUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        mRVUsers = findViewById(R.id.rvUsers);
        mRVUsers.setLayoutManager(new LinearLayoutManager(this));

        mETGroupName = findViewById(R.id.etGroupName);
        mETSearchUser = findViewById(R.id.etSearchUser);
        mBtnCreate = findViewById(R.id.btnCreate);


        mETSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do what you want with your edit text here
                String searchText = mETSearchUser.getText().toString();
                firebaseUserSearch(searchText);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get group name and set
                String groupName = mETGroupName.getText().toString();
                GroupData groupData = new GroupData(groupName, "", "", mALUsers);
                mALUsers.add(mAuth.getUid());

                saveGroup(groupData, mALUsers);

                Intent intent = new Intent(GroupCreationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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


    private void firebaseUserSearch(String searchText) {

        Query query = mUserDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_layout, viewGroup, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {

                holder.setDetails(getApplicationContext(), model.getName(), model.getImage(), model.getEmail());

                final String user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mALUsers.add(user_id);
                        Toast.makeText(GroupCreationActivity.this, "Added User!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        mRVUsers.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, String userName, String userImage, String userEmail) {
            TextView user_name = mView.findViewById(R.id.user_single_name);
            ImageView user_image = mView.findViewById(R.id.user_single_image);
            TextView user_email = mView.findViewById(R.id.user_single_email);

            user_name.setText(userName);
            user_email.setText(userEmail);

            Glide.with(ctx)
                    .load(userImage)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_pic)
                            .fitCenter())
                    .into(user_image);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Google Sign In was successful
                Log.d("Google Authentication", "Google email successfully authenticated!");

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleLogIn", "Google sign in failed", e);
            }
        }
    }
}

