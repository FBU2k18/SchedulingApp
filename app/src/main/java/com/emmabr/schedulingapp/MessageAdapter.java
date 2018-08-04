package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> messages;
    Context context;

    String groupID;

    public MessageAdapter(ArrayList<Message> messages, String groupID) {
        this.messages = messages;
        this.groupID = groupID;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        final Message message = messages.get(position);

        holder.tvTextMe.setText("");
        holder.tvTextMe.setBackground(null);
        holder.ivPicMe.setImageDrawable(null);
        holder.tvFrom.setText("");
        holder.tvTextYou.setText("");
        holder.tvTextYou.setBackground(null);
        holder.ivPicYou.setImageDrawable(null);

        if (message.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
            //make look like from self
            if (message.getMessageText() != null) {
                holder.tvTextMe.setText(message.getMessageText());
                //TODO: Change drawable
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(context, R.drawable.user_message_text_background));
            } else if (message.getImageURL() != null) {
                Glide.with(context).load(message.getImageURL()).into(holder.ivPicMe);
                holder.ivPicMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            } else if (message.getPollTitle() != null){
                holder.tvTextMe.setText(message.getPollTitle());
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(context, R.drawable.message_text_background));
                holder.ivPicYou.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_sort_by_size));
                holder.ivPicYou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPollActivity.class);
                        intent.putExtra("messageID", message.getMessageID());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            //make look like from someone else
            holder.tvFrom.setText(message.getNickName());
            if (message.getMessageText() != null) {
                holder.tvTextYou.setText(message.getMessageText());
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(context, R.drawable.in_bubble));
            } else if (message.getImageURL() != null) {
                Glide.with(context).load(message.getImageURL()).into(holder.ivPicYou);
                holder.ivPicYou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            } else if (message.getPollTitle() != null){
                holder.tvTextYou.setText(message.getPollTitle());
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(context, R.drawable.in_bubble));
                holder.ivPicMe.setImageDrawable(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_sort_by_size));
                holder.ivPicMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPollActivity.class);
                        intent.putExtra("messageID", message.getMessageID());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            }
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
