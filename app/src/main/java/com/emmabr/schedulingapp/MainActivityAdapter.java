package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.emmabr.schedulingapp.Models.GroupData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.emmabr.schedulingapp.R;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> implements Filterable {

    private ArrayList<GroupData> mGroups;
    private ArrayList<GroupData> mFilteredGroups;
    private Context mContext;
    private MainActivity mHome;

    private ArrayList<String> groupMembers = new ArrayList<>();
    private String userlist = "";
    private DatabaseReference mDatabaseRef;


    public MainActivityAdapter(ArrayList<GroupData> groups, MainActivity home) {
        this.mGroups = groups;
        this.mFilteredGroups = this.mGroups;
        this.mHome = home;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainActivityAdapter.ViewHolder holder, final int position) {
        GroupData currGroup = mFilteredGroups.get(position);

        userlist = "";

        groupMembers = currGroup.getUsers();
        for (int i = 0; i < groupMembers.size() - 1; ++i) {
            userlist = userlist + groupMembers.get(i) + ", ";
        }
        if (groupMembers.size() > 0) {
            userlist = userlist + groupMembers.get(groupMembers.size() - 1);
        }
        holder.tvGroupMembers.setText(userlist);

        currGroup = mFilteredGroups.get(position);
        holder.tvGroupName.setText(currGroup.getGroupName());
        if (!currGroup.getImageURL().isEmpty())
            Glide.with(mContext)
                    .load(currGroup.getImageURL())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.ivGroupLogo);
        else
            holder.ivGroupLogo.setImageDrawable(mHome.getDrawable(R.drawable.group_default_logo));
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(currGroup.getGroupId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("unreadMessages")) {
                    Drawable icon = mHome.getDrawable(android.R.drawable.ic_notification_overlay);
                    icon.setTint(mHome.getResources().getColor(R.color.colorAccent));
                    holder.ivNotification.setImageDrawable(icon);
                } else
                    holder.ivNotification.setImageDrawable(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_group, viewGroup, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("groups");

        return new MainActivityAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mFilteredGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivGroupLogo;
        TextView tvGroupName;
        ImageView ivNotification;
        TextView tvGroupMembers;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivGroupLogo = itemView.findViewById(R.id.ivGroupLogo);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            ivNotification = itemView.findViewById(R.id.ivNotification);
            tvGroupMembers = itemView.findViewById(R.id.tvGroupMembers);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                GroupData group = mFilteredGroups.get(getAdapterPosition());
                //replace with intent to go to group screen
                Intent intent = new Intent(mContext, GroupActivity.class);
                intent.putExtra("mGroupID", group.getGroupId());
                mContext.startActivity(intent);
                mHome.clearNotifications(getAdapterPosition());
                mHome.blockNotifications(group.getGroupId());
                notifyItemChanged(getAdapterPosition());
                Log.i("GroupData", group.getGroupName());
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty())
                    mFilteredGroups = mGroups;
                else {
                    ArrayList<GroupData> temp = new ArrayList<>();
                    for (GroupData group : mGroups)
                        if (group.getGroupName().toLowerCase().contains(charString))
                            temp.add(group);
                    mFilteredGroups = temp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredGroups;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredGroups = (ArrayList<GroupData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
