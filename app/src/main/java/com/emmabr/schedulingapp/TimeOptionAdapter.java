package com.emmabr.schedulingapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.util.ArrayList;
import java.util.Collections;

import com.emmabr.schedulingapp.R;

public class TimeOptionAdapter extends RecyclerView.Adapter<TimeOptionAdapter.ViewHolder> {

    ArrayList<TimeOption> times;
    Context context;
    GroupActivity parent;

    public TimeOptionAdapter(ArrayList<TimeOption> times, GroupActivity parent) {
        this.times = times;
        this.parent = parent;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeOptionAdapter.ViewHolder holder, int position) {
        TimeOption time = times.get(position);
        holder.tvTime.setText(time.getTime());
        holder.tvVotes.setText(Integer.toString(time.getVotes()));
        //get current user
        if (time.getUpVoters().contains(new User("abc123")))
            holder.rlOption.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            //get current user again
        else if (time.getDownVoters().contains(new User("abc123")))
            holder.rlOption.setBackgroundColor(Color.RED);
        else
            holder.rlOption.setBackgroundColor(Color.TRANSPARENT);
    }

    @NonNull
    @Override
    public TimeOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_time_option, viewGroup, false);
        return new TimeOptionAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return times.size();
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
                        TimeOption time = times.get(oldPos);
                        //replace line below
                        time.upVote(new User("abc123"));
                        int newPos;
                        if (time.getVotes() > Integer.parseInt(tvVotes.getText().toString())) {
                            newPos = oldPos - 1;
                            while (newPos > -1 && time.getVotes() > times.get(newPos).getVotes())
                                newPos--;
                            newPos++;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                times.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.parent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                times.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        } else {
                            newPos = oldPos + 1;
                            while (newPos < times.size() && time.getVotes() < times.get(newPos).getVotes())
                                newPos++;
                            newPos--;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                times.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.parent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                times.add(newPos, time);
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
                        TimeOption time = times.get(oldPos);
                        //replace line below
                        time.downVote(new User("abc123"));
                        int newPos;
                        if (time.getVotes() > Integer.parseInt(tvVotes.getText().toString())) {
                            newPos = oldPos - 1;
                            while (newPos > -1 && time.getVotes() > times.get(newPos).getVotes())
                                newPos--;
                            newPos++;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                times.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.parent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                times.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        } else {
                            newPos = oldPos + 1;
                            while (newPos < times.size() && time.getVotes() < times.get(newPos).getVotes())
                                newPos++;
                            newPos--;
                            if (newPos == oldPos)
                                TimeOptionAdapter.this.notifyItemChanged(newPos);
                            else {
                                times.remove(oldPos);
                                TimeOptionAdapter.this.notifyItemRemoved(oldPos);
                                TimeOptionAdapter.this.parent.scrollToPosition(newPos);
                                tvVotes.setText(Integer.toString(time.getVotes()));
                                times.add(newPos, time);
                                TimeOptionAdapter.this.notifyItemInserted(newPos);
                            }
                        }
                    }
                }
            });
        }
    }
}
