package com.emmabr.schedulingapp;

import android.content.Context;
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
import com.emmabr.schedulingapp.model.Group;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    ArrayList<Group> groups;
    Context context;

    public GroupAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.tvGroupName.setText(group.getName());
        if (group.getPhotoPath() != null && !group.getPhotoPath().equals(""))
            Glide.with(context)
                    .load(group.getPhotoPath())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.ivGroupLogo);
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_group, viewGroup, false);
        return new GroupAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivGroupLogo;
        TextView tvGroupName;

        public ViewHolder(View itemView) {
            super(itemView);

            ivGroupLogo = itemView.findViewById(R.id.ivGroupLogo);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                Group group = groups.get(getAdapterPosition());
                //replace with intent to go to group screen
                Log.i("Group",group.getName());
            }
        }
    }
}
