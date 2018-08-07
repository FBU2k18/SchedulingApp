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

    private ArrayList<Message> mMessages;
    private Context mContext;

    private String mGroupID;

    public MessageAdapter(ArrayList<Message> messages, String groupID) {
        this.mMessages = messages;
        this.mGroupID = groupID;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Message message = mMessages.get(position);

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
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(mContext, R.drawable.user_message_text_background));
            } else if (message.getImageURL() != null) {
                Glide.with(mContext).load(message.getImageURL()).into(holder.ivPicMe);
                holder.ivPicMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        mContext.startActivity(intent);
                    }
                });
            } else if (message.getPollTitle() != null){
                holder.tvTextMe.setText(message.getPollTitle());
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(mContext, R.drawable.message_text_background));
                holder.ivPicYou.setImageDrawable(ContextCompat.getDrawable(mContext, android.R.drawable.ic_menu_sort_by_size));
                holder.ivPicYou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewPollActivity.class);
                        intent.putExtra("mMessageID", message.getMessageID());
                        intent.putExtra("mGroupID", mGroupID);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else {
            //make look like from someone else
            holder.tvFrom.setText(message.getNickName());
            if (message.getMessageText() != null) {
                holder.tvTextYou.setText(message.getMessageText());
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(mContext, R.drawable.in_bubble));
            } else if (message.getImageURL() != null) {
                Glide.with(mContext).load(message.getImageURL()).into(holder.ivPicYou);
                holder.ivPicYou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        intent.putExtra("mGroupID", mGroupID);
                        mContext.startActivity(intent);
                    }
                });
            } else if (message.getPollTitle() != null){
                holder.tvTextYou.setText(message.getPollTitle());
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(mContext, R.drawable.in_bubble));
                holder.ivPicMe.setImageDrawable(ContextCompat.getDrawable(mContext, android.R.drawable.ic_menu_sort_by_size));
                holder.ivPicMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ViewPollActivity.class);
                        intent.putExtra("mMessageID", message.getMessageID());
                        intent.putExtra("mGroupID", mGroupID);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_message, viewGroup, false);
        return new MessageAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
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
