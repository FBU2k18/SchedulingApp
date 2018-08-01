package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.emmabr.schedulingapp.R;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> implements Filterable {

    private ArrayList<GroupData> mGroups;
    private ArrayList<GroupData> mFilteredGroups;
    private Context mContext;


    public MainActivityAdapter(ArrayList<GroupData> groups) {
        this.mGroups = groups;
        this.mFilteredGroups = this.mGroups;
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {
        GroupData currGroup = mFilteredGroups.get(position);
        holder.tvGroupName.setText(currGroup.getGroupName());
        if (!currGroup.getImageURL().isEmpty())
            Glide.with(mContext)
                    .load(currGroup.getImageURL())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(holder.ivGroupLogo);
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_group, viewGroup, false);
        return new MainActivityAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mFilteredGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivGroupLogo;
        TextView tvGroupName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivGroupLogo = itemView.findViewById(R.id.ivGroupLogo);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);

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
