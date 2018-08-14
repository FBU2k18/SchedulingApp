package com.emmabr.schedulingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private ArrayList<String> mPicURLs;
    private Context mContext;

    public PicAdapter(ArrayList<String> pics) {
        this.mPicURLs = pics;
    }

    @Override
    public void onBindViewHolder(@NonNull PicAdapter.ViewHolder holder, int position) {
        if (mPicURLs.get(position).isEmpty())
            holder.civPic.setImageDrawable(mContext.getDrawable(R.drawable.group_default_logo));
        else if (mPicURLs.get(position).equals("default"))
            holder.civPic.setImageDrawable(mContext.getDrawable(R.drawable.default_pic));
        else
            Glide.with(mContext)
                    .load(mPicURLs.get(position))
                    .into(holder.civPic);
    }

    @NonNull
    @Override
    public PicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_pic, viewGroup, false);
        return new PicAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mPicURLs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civPic;

        public ViewHolder(View itemView) {
            super(itemView);
            civPic = itemView.findViewById(R.id.civPic);
        }
    }
}

