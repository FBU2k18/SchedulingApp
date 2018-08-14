package com.emmabr.schedulingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.Models.TimeOption;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TimeOptionAdapter extends RecyclerView.Adapter<TimeOptionAdapter.ViewHolder> {

    private ArrayList<TimeOption> mTimes;
    private Context mContext;
    private DayAdapter.ViewHolder mParent;

    public TimeOptionAdapter(ArrayList<TimeOption> times, DayAdapter.ViewHolder parent) {
        this.mTimes = times;
        this.mParent = parent;

    }

    @Override
    public void onBindViewHolder(@NonNull TimeOptionAdapter.ViewHolder holder, int position) {
        TimeOption time = mTimes.get(position);
        time.setVotes();
        time.setHolder(holder);
        holder.tvTime.setText(time.getTime());
        holder.tvVotes.setText(Integer.toString(time.getVotes()));
        if (time.getUpVoters().contains(FirebaseAuth.getInstance().getUid()))
            holder.rlOption.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        else if (time.getDownVoters().contains(FirebaseAuth.getInstance().getUid()))
            holder.rlOption.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        else
            holder.rlOption.setBackgroundColor(Color.TRANSPARENT);
    }

    @NonNull
    @Override
    public TimeOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.item_time_option, viewGroup, false);
        return new TimeOptionAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mTimes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;
        TextView tvVotes;
        ImageButton ibUpVote;
        ImageButton ibDownVote;
        RelativeLayout rlOption;
        ImageView btAddCal;
        TextView mBusyTime;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTime);
            tvVotes = itemView.findViewById(R.id.tvVotes);
            ibUpVote = itemView.findViewById(R.id.ibUpVote);
            ibDownVote = itemView.findViewById(R.id.ibDownVote);
            rlOption = itemView.findViewById(R.id.rlOption);
            btAddCal = itemView.findViewById(R.id.ibAddCal);
            mBusyTime = itemView.findViewById(R.id.tvTime);

            ibUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTimes.get(getAdapterPosition()).upVote(FirebaseAuth.getInstance().getUid());
                }
            });
            ibDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTimes.get(getAdapterPosition()).downVote(FirebaseAuth.getInstance().getUid());
                }
            });

            btAddCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postEvent = new Intent(mContext, AddCalendarActivity.class);
                    postEvent.putExtra("mBusyTime", mBusyTime.getText().toString());
                    mContext.startActivity(postEvent);
                }
            });
        }

        public void move(TimeOption time, boolean isMe) {
            int oldPos = mTimes.indexOf(time);
            int newPosUp = oldPos - 1;
            int newPosDown = oldPos + 1;
            while (newPosUp > -1 && time.getVotes() > mTimes.get(newPosUp).getVotes())
                newPosUp--;
            newPosUp++;
            while (newPosDown < mTimes.size() && time.getVotes() < mTimes.get(newPosDown).getVotes())
                newPosDown++;
            newPosDown--;
            if (newPosUp == newPosDown) {
                TimeOptionAdapter.this.notifyItemChanged(oldPos);
            } else if (oldPos == newPosDown) {
                mTimes.remove(oldPos);
                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                if (isMe)
                    TimeOptionAdapter.this.mParent.scrollToPosition(newPosUp);
                mTimes.add(newPosUp, time);
                TimeOptionAdapter.this.notifyItemInserted(newPosUp);
            } else if (oldPos == newPosUp) {
                mTimes.remove(oldPos);
                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                if (isMe)
                    TimeOptionAdapter.this.mParent.scrollToPosition(newPosDown);
                mTimes.add(newPosDown, time);
                TimeOptionAdapter.this.notifyItemInserted(newPosDown);
            }
        }
    }
}
