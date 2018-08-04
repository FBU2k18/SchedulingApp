package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.emmabr.schedulingapp.Models.GroupData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.emmabr.schedulingapp.R;


public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    ArrayList<GroupData> groups;
    Context context;
    ArrayList<String> groupMembers = new ArrayList<>();

    public MainActivityAdapter(ArrayList<GroupData> groups) {
        this.groups = groups;
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {
        GroupData currGroup = groups.get(position);

        FirebaseDatabase.getInstance().getReference().child("groups").child(currGroup.getGroupId()).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot member : dataSnapshot.getChildren()) {
                    member.getRef().setPriority(null).continueWithTask(new Continuation<Void, Task<DataSnapshot>>() {
                        @Override
                        public Task<DataSnapshot> then(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(member.getKey().toString()).child("nickName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    groupMembers.add(dataSnapshot.getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                            return null;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        String userlist = "";
        for (int i = 0; i < groupMembers.size() - 1; ++i) {
            userlist = userlist + groupMembers.get(i) + ", ";
        }
        //userlist = userlist + groupMembers.get(groupMembers.size() - 1);
        holder.tvGroupMembers.setText(userlist);

        holder.tvGroupName.setText(currGroup.getGroupName());
        if (currGroup.getImageURL() != null && !currGroup.getImageURL().equals(""))
            Glide.with(context)
                    .load(currGroup.getImageURL())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.ivGroupLogo);
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_group, viewGroup, false);

        return new MainActivityAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivGroupLogo;
        TextView tvGroupName;
        TextView tvGroupMembers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivGroupLogo = itemView.findViewById(R.id.ivGroupLogo);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvGroupMembers = itemView.findViewById(R.id.tvGroupMembers);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                GroupData group = groups.get(getAdapterPosition());
                //replace with intent to go to group screen
                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra("groupID", group.getGroupId());
                context.startActivity(intent);
                Log.i("GroupData",group.getGroupName());
            }
        }
    }
}
