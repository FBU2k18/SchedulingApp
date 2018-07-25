package com.emmabr.schedulingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmabr.schedulingapp.Models.Message;
import com.emmabr.schedulingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> messages;
    Context context;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);

        //use method to determine who the current user is and use to replace currentUser
        if (message.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
            //make look like from self
            holder.tvTextMe.setText(message.getMessageText());
            holder.tvTextMe.setBackground(ContextCompat.getDrawable(context, R.drawable.out_bubble));
        } else {
            //make look like from someone else
            holder.tvFrom.setText(message.getNickName());
            holder.tvTextYou.setText(message.getMessageText());
            holder.tvTextYou.setBackground(ContextCompat.getDrawable(context, R.drawable.in_bubble));
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_message, viewGroup, false);
        return new MessageAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFrom;
        TextView tvTextMe;
        TextView tvTextYou;
        ImageView ivPicMe;
        ImageView ivPicYou;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTextMe = itemView.findViewById(R.id.tvTextMe);
            tvTextYou = itemView.findViewById(R.id.tvTextYou);
            ivPicMe = itemView.findViewById(R.id.ivPicMe);
            ivPicYou = itemView.findViewById(R.id.ivPicYou);
        }
    }
}
