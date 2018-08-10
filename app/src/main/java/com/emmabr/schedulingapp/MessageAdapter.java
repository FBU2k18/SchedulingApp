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
import java.util.Date;

import com.emmabr.schedulingapp.R;

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
        holder.tvTime.setText("");

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
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(mContext, R.drawable.user_message_text_background));
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
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(mContext, R.drawable.message_text_background));
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
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(mContext, R.drawable.message_text_background));
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

        Date date = new Date();
        Date createdAt = new Date(Long.parseLong(message.getCreatedAt()));
        Date previous = new Date();
        boolean first = position == 0;
        if (!first)
            previous = new Date(Long.parseLong(mMessages.get(position - 1).getCreatedAt()));
        //same day
        if (date.toString().substring(0, 11).concat(date.toString().substring(24)).equals(createdAt.toString().substring(0, 11).concat(createdAt.toString().substring(24)))) {
            //if first message or differs from message above by > an hour
            if (first || createdAt.getTime() - previous.getTime() > 3600000)
                holder.tvTime.setText(createdAt.toString().substring(11, 16));
            //within a week
        } else if (date.getTime() - createdAt.getTime() < 604800000 && (!date.toString().substring(0, 3).equals(createdAt.toString().substring(0, 3)))) {
            //if first message or differs from message above by > an hour
            if (first || createdAt.getTime() - previous.getTime() > 3600000)
                holder.tvTime.setText(createdAt.toString().substring(0, 4).concat(createdAt.toString().substring(11, 16)));
            //within a month
        } else if (date.toString().substring(4, 8).concat(date.toString().substring(24)).equals(createdAt.toString().substring(4, 8).concat(createdAt.toString().substring(24)))) {
            //if first message or isn't from same day as message above
            if (first || (!createdAt.toString().substring(0, 11).concat(createdAt.toString().substring(24)).equals(previous.toString().substring(0, 11).concat(previous.toString().substring(24)))))
                switch (createdAt.toString().substring(4, 7)) {
                    case "Jan":
                        holder.tvTime.setText("01/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Feb":
                        holder.tvTime.setText("02/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Mar":
                        holder.tvTime.setText("03/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Apr":
                        holder.tvTime.setText("04/" + createdAt.toString().substring(8, 10));
                        break;
                    case "May":
                        holder.tvTime.setText("05/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Jun":
                        holder.tvTime.setText("06/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Jul":
                        holder.tvTime.setText("07/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Aug":
                        holder.tvTime.setText("08/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Sep":
                        holder.tvTime.setText("09/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Oct":
                        holder.tvTime.setText("10/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Nov":
                        holder.tvTime.setText("11/" + createdAt.toString().substring(8, 10));
                        break;
                    case "Dec":
                        holder.tvTime.setText("12/" + createdAt.toString().substring(8, 10));
                        break;
                }
            //within a year
        } else if (date.toString().substring(24).equals(createdAt.toString().substring(24))) {
            //if first message or isn't from same month as message above
            if (first || (!createdAt.toString().substring(4, 7).equals(previous.toString().substring(4, 7))))
                holder.tvTime.setText(createdAt.toString().substring(4, 7));
            //outside a year
        } else
            //if first message or isn't from same year as message above
            if (first || (!createdAt.toString().substring(24).equals(previous.toString().substring(24))))
                holder.tvTime.setText(createdAt.toString().substring(24));
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
        TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTextMe = itemView.findViewById(R.id.tvTextMe);
            tvTextYou = itemView.findViewById(R.id.tvTextYou);
            ivPicMe = itemView.findViewById(R.id.ivPicMe);
            ivPicYou = itemView.findViewById(R.id.ivPicYou);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
