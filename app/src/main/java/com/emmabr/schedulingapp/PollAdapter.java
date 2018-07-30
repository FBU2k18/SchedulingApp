package com.emmabr.schedulingapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {

    ArrayList<ArrayList<String>> options;
    Context context;
    String groupID;
    String messageID;

    public PollAdapter(ArrayList<ArrayList<String>> options, String groupID, String messageID) {
        this.options = options;
        this.groupID = groupID;
        this.messageID = messageID;
    }

    @Override
    public void onBindViewHolder(@NonNull final PollAdapter.ViewHolder holder, int position) {
        final ArrayList<String> option = options.get(position);
        holder.tvOption.setText(option.get(0));
        holder.tvVotes.setText(Integer.toString(option.size() - 1));
        if (option.contains(FirebaseAuth.getInstance().getUid()))
            holder.ivCheck.setImageDrawable(context.getResources().getDrawable(android.R.drawable.checkbox_on_background));
        else
            holder.ivCheck.setImageDrawable(context.getResources().getDrawable(android.R.drawable.checkbox_off_background));

        holder.tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (option.contains(FirebaseAuth.getInstance().getUid()))
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").child(messageID).child("options").child(option.get(0)).child(FirebaseAuth.getInstance().getUid()).removeValue();
                else {
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").child(messageID).child("options").child(option.get(0)).child(FirebaseAuth.getInstance().getUid()).setValue(FirebaseAuth.getInstance().getUid());
                    for (ArrayList<String> miniArray : options)
                        if (miniArray.contains(FirebaseAuth.getInstance().getUid()))
                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").child(messageID).child("options").child(miniArray.get(0)).child(FirebaseAuth.getInstance().getUid()).removeValue();
                }
            }
        });
    }

    @NonNull
    @Override
    public PollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_poll_option, viewGroup, false);
        return new PollAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return options.size();
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
