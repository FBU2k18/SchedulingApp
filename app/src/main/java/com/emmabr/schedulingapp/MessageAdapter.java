package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    String groupID;

    public MessageAdapter(ArrayList<Message> messages, String groupID) {
        this.messages = messages;
        this.groupID = groupID;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        final Message message = messages.get(position);

        if (message.getUserID().equals(FirebaseAuth.getInstance().getUid())) {
            //make look like from self
            if (message.getMessageText() != null) {
                holder.tvTextMe.setText(message.getMessageText());
                holder.tvTextMe.setBackground(ContextCompat.getDrawable(context, R.drawable.out_bubble));
            } else if (message.getImageURL() != null) {
                BitmapScaler scaler = new BitmapScaler();
                holder.ivPicMe.setImageBitmap(scaler.scaleToFitWidth(BitmapFactory.decodeFile(message.getImageURL()), 50));
                holder.ivPicMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            } else {
                //poll
            }
        } else {
            //make look like from someone else
            holder.tvFrom.setText(message.getNickName());
            if (message.getMessageText() != null) {
                holder.tvTextYou.setText(message.getMessageText());
                holder.tvTextYou.setBackground(ContextCompat.getDrawable(context, R.drawable.in_bubble));
            } else if (message.getImageURL() != null) {
                BitmapScaler scaler = new BitmapScaler();
                holder.ivPicYou.setImageBitmap(scaler.scaleToFitWidth(BitmapFactory.decodeFile(message.getImageURL()), 50));
                holder.ivPicYou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewPhotoActivity.class);
                        intent.putExtra("imageURL", message.getImageURL());
                        intent.putExtra("groupID", groupID);
                        context.startActivity(intent);
                    }
                });
            } else {
                //poll
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

    public class BitmapScaler {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public Bitmap scaleToFitWidth(Bitmap b, int width) {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }

        // Scale and maintain aspect ratio given a desired height
        // BitmapScaler.scaleToFitHeight(bitmap, 100);
        public Bitmap scaleToFitHeight(Bitmap b, int height) {
            float factor = height / (float) b.getHeight();
            return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
        }

    }
}
