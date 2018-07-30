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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.emmabr.schedulingapp.R;


public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> implements Filterable {

    ArrayList<GroupData> groups;
    ArrayList<GroupData> filteredGroups;
    Context context;


    public MainActivityAdapter(ArrayList<GroupData> groups) {
        this.groups = groups;
        this.filteredGroups = this.groups;
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {
        GroupData currGroup = filteredGroups.get(position);
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
        return filteredGroups.size();
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
                GroupData group = filteredGroups.get(getAdapterPosition());
                //replace with intent to go to group screen
                Intent intent = new Intent(context, GroupActivity.class);
                intent.putExtra("groupID", group.getGroupId());
                context.startActivity(intent);
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
                    filteredGroups = groups;
                else {
                    ArrayList<GroupData> temp = new ArrayList<>();
                    for (GroupData group : groups)
                        if (group.getGroupName().toLowerCase().contains(charString))
                            temp.add(group);
                    filteredGroups = temp;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredGroups;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredGroups = (ArrayList<GroupData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
