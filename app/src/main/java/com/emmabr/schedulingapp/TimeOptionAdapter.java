package com.emmabr.schedulingapp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class TimeOptionAdapter extends RecyclerView.Adapter<TimeOptionAdapter.ViewHolder> {

    private ArrayList<TimeOption> mTimes;
    private Context mContext;
    private GroupActivity mParent;

    public TimeOptionAdapter(ArrayList<TimeOption> times, GroupActivity parent) {
        this.mTimes = times;
        this.mParent = parent;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeOptionAdapter.ViewHolder holder, int position) {
        TimeOption time = mTimes.get(position);
        time.setVotes();
        holder.tvTime.setText(time.getTime());
        holder.tvVotes.setText(Integer.toString(time.getVotes()));
        //get current user
        if (time.getUpVoters().contains(new User("abc123")))
            holder.rlOption.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            //get current user again
        else if (time.getDownVoters().contains(new User("abc123")))
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

        public ViewHolder(final View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tvTime);
            tvVotes = itemView.findViewById(R.id.tvVotes);
            ibUpVote = itemView.findViewById(R.id.ibUpVote);
            ibDownVote = itemView.findViewById(R.id.ibDownVote);
            rlOption = itemView.findViewById(R.id.rlOption);

            //need function to get current user for onClickListeners below
            ibUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int oldPos = getAdapterPosition();
                    if (oldPos != RecyclerView.NO_POSITION) {
                        TimeOption time = mTimes.get(oldPos);
                        //replace line below
                        time.upVote(new User("abc123"));
                        int newPos;
                        if (time.getVotes() > Integer.parseInt(tvVotes.getText().toString())) {
                            newPos = oldPos - 1;
                            while (newPos > -1 && time.getVotes() > mTimes.get(newPos).getVotes())
                                newPos--;
                            newPos++;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                mTimes.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.mParent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                mTimes.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        } else {
                            newPos = oldPos + 1;
                            while (newPos < mTimes.size() && time.getVotes() < mTimes.get(newPos).getVotes())
                                newPos++;
                            newPos--;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                mTimes.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.mParent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                mTimes.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        }
                    }
                }
            });
            ibDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int oldPos = getAdapterPosition();
                    if (oldPos != RecyclerView.NO_POSITION) {
                        TimeOption time = mTimes.get(oldPos);
                        //replace line below
                        time.downVote(new User("abc123"));
                        int newPos;
                        if (time.getVotes() > Integer.parseInt(tvVotes.getText().toString())) {
                            newPos = oldPos - 1;
                            while (newPos > -1 && time.getVotes() > mTimes.get(newPos).getVotes())
                                newPos--;
                            newPos++;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                mTimes.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.mParent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                mTimes.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        } else {
                            newPos = oldPos + 1;
                            while (newPos < mTimes.size() && time.getVotes() < mTimes.get(newPos).getVotes())
                                newPos++;
                            newPos--;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                mTimes.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.mParent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                mTimes.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        }
                    }
                }
            });
        }
    }
}
