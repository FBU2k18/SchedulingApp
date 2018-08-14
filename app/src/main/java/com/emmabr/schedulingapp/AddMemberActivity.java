package com.emmabr.schedulingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.emmabr.schedulingapp.Models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    private String mGroupID;
    private ArrayList<String> mGroupMembers;

    private RecyclerView mRVMembers;
    private EditText mETSearchMember;

    private ArrayList<String> mALUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        mGroupID = getIntent().getStringExtra("mGroupID");
        mGroupMembers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childData : dataSnapshot.getChildren())
                    mGroupMembers.add(childData.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mRVMembers = findViewById(R.id.rvMembers);
        mRVMembers.setLayoutManager(new LinearLayoutManager(this));


        mETSearchMember = findViewById(R.id.etSearchUser);

        mETSearchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = mETSearchMember.getText().toString();
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
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("email").startAt(searchText).endAt(searchText + "\uf8ff");

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

                holder.setDetails(getApplicationContext(), model.getName(), model.getImage(), model.getEmail());

                final String user_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mGroupMembers.contains(user_id)) {
                            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("seenStatus").setValue("1");
                            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").child(user_id).setValue(user_id);
                            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userGroup").child(mGroupID).child("groupName").setValue(dataSnapshot.child("groupName").getValue().toString());
                                    FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("userGroup").child(mGroupID).child("imageURL").setValue(dataSnapshot.child("imageURL").getValue().toString());
                                    dataSnapshot.getRef().removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            mETSearchMember.setText("");
                            firebaseUserSearch("");
                            Toast.makeText(AddMemberActivity.this, "User Added to Group!", Toast.LENGTH_LONG).show();
                        } else {
                            if (!mGroupMembers.contains(user_id))
                                if (!mALUsers.contains(user_id)) {
                                    mALUsers.add(user_id);
                                    Toast.makeText(AddMemberActivity.this, "Added User!", Toast.LENGTH_LONG).show();
                                } else
                                    Toast.makeText(AddMemberActivity.this, "User already added!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(AddMemberActivity.this, "User already in group!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        mRVMembers.setAdapter(firebaseRecyclerAdapter);
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
}
