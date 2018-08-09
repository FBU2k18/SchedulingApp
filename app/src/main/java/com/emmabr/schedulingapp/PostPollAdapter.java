package com.emmabr.schedulingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.emmabr.schedulingapp.R;

public class PostPollAdapter extends RecyclerView.Adapter<PostPollAdapter.ViewHolder> {

    private ArrayList<String> mPollOptions;
    private String mGroupID;
    private Context mContext;

    public PostPollAdapter(ArrayList<String> pollOptions, String groupId) {
        this.mPollOptions = pollOptions;
        this.mGroupID = groupId;
    }

    @NonNull
    @Override
    public PostPollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View optionView = inflater.inflate(R.layout.item_new_poll_option, viewGroup, false);
        return new PostPollAdapter.ViewHolder(optionView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostPollAdapter.ViewHolder viewHolder, final int i) {
        final String option = mPollOptions.get(i);
        viewHolder.tvPollOption.setText(option);

        viewHolder.tvPollOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPollOptions.remove(i);
                notifyItemRemoved(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPollOptions.size();
    }

    public ArrayList<String> getUsersArray() {
        return mPollOptions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPollOption;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvPollOption = itemView.findViewById(R.id.tvPollOption);
        }
    }
}
