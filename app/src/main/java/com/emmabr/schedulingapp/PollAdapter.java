package com.emmabr.schedulingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> mOptions;
    private Context mContext;
    private String mGroupID;
    private String mMessageID;

    public PollAdapter(ArrayList<ArrayList<String>> options, String groupID, String messageID) {
        this.mOptions = options;
        this.mGroupID = groupID;
        this.mMessageID = messageID;
    }

    @Override
    public void onBindViewHolder(@NonNull final PollAdapter.ViewHolder holder, int position) {
        final ArrayList<String> option = mOptions.get(position);
        holder.tvOption.setText(option.get(0));
        holder.tvVotes.setText(Integer.toString(option.size() - 1));
        if (option.contains(FirebaseAuth.getInstance().getUid()))
            holder.ivCheck.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.checkbox_on_background));
        else
            holder.ivCheck.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.checkbox_off_background));

        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (option.contains(FirebaseAuth.getInstance().getUid()))
                    FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").child(mMessageID).child("options").child(option.get(0)).child(FirebaseAuth.getInstance().getUid()).removeValue();
                else {
                    FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").child(mMessageID).child("options").child(option.get(0)).child(FirebaseAuth.getInstance().getUid()).setValue(FirebaseAuth.getInstance().getUid());
                    for (ArrayList<String> miniArray : mOptions)
                        if (miniArray.contains(FirebaseAuth.getInstance().getUid()))
                            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").child(mMessageID).child("options").child(miniArray.get(0)).child(FirebaseAuth.getInstance().getUid()).removeValue();
                }
            }
        });
    }

    @NonNull
    @Override
    public PollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_poll_option, viewGroup, false);
        return new PollAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOption;
        TextView tvVotes;
        ImageView ivCheck;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvOption = itemView.findViewById(R.id.tvOption);
            tvVotes = itemView.findViewById(R.id.tvVotes);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }
    }
}
