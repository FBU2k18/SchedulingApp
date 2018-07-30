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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.GroupData.saveGroup;

public class AddMemberActivity extends AppCompatActivity {

    String groupID;
    ArrayList<String> groupMembers;

    private final static int RC_SIGN_IN = 34;
    GoogleSignInClient mGoogleSignInClient;


    //firebase variables
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView rvUsers;
    private EditText etSearchUser;


    ArrayList<String> alUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        groupID = getIntent().getStringExtra("groupID");
        groupMembers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childData : dataSnapshot.getChildren())
                    groupMembers.add(childData.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("AddMemeberActivity", "Didn't work");
            }
        });

        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        etSearchUser = findViewById(R.id.etSearchUser);

        //TODO: on edit text changed
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do what you want with your edit text here
                String searchText = etSearchUser.getText().toString();
                firebaseUserSearch(searchText);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                for (String id : alUsers) {
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients").child(id).setValue(id);
                }
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (String id : alUsers) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(id).child("userGroup").child(groupID).child("groupName").setValue(dataSnapshot.child("groupName").getValue().toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(id).child("userGroup").child(groupID).child("imageURL").setValue(dataSnapshot.child("imageURL").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intentHome = new Intent(getApplicationContext(), GroupActivity.class);
                intentHome.putExtra("groupID", groupID);
                startActivity(intentHome);
                finish();
                break;
        }
        return true;
    }

    private void firebaseUserSearch(String searchText) {
        Query query = userDatabase.orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_layout, viewGroup, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {

                holder.setDetails(getApplicationContext(), model.getName(), model.getImage());

                final String user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!groupMembers.contains(user_id)) {
                            if (!alUsers.contains(user_id)) {
                                alUsers.add(user_id);
                                Toast.makeText(AddMemberActivity.this, "Added User!", Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(AddMemberActivity.this, "User already added!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(AddMemberActivity.this, "User already in group!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        rvUsers.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDetails(Context ctx, String userName, String userImage) {
            TextView user_name = (TextView) mView.findViewById(R.id.user_single_name);
            ImageView user_image = (ImageView) mView.findViewById(R.id.user_single_image);

            user_name.setText(userName);

            Glide.with(ctx)
                    .load(userImage)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.default_pic)
                            .fitCenter())
                    .into(user_image);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
