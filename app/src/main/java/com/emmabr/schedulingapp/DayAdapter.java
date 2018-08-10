package com.emmabr.schedulingapp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.util.ArrayList;
import java.util.Collections;

import com.emmabr.schedulingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DayAdapter extends RecyclerView.Adapter<com.emmabr.schedulingapp.DayAdapter.ViewHolder> {

    private ArrayList<String> mDays;
    private Context mContext;
    private GroupActivity mParent;
    private String mGroupID;

    public DayAdapter(ArrayList<String> days, GroupActivity parent, String groupID) {
        this.mDays = days;
        this.mParent = parent;
        this.mGroupID = groupID;
    }

    @Override
    public void onBindViewHolder(@NonNull final DayAdapter.ViewHolder holder, final int position) {
        holder.mTVDay.setText(mDays.get(position));
        holder.mIVForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.scrollToPosition((position + 1) % 8);
            }
        });
        holder.mIVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.scrollToPosition((position - 1) % 8);
            }
        });
        holder.getTimes(position);
        holder.sort();
    }

    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View groupView = inflater.inflate(R.layout.fragment_day, viewGroup, false);
        return new DayAdapter.ViewHolder(groupView);
    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ArrayList<TimeOption> mTimes;
        private TimeOptionAdapter mTimeAdapter;
        private RecyclerView mRVTimes;
        private TextView mTVDay;
        private ImageView mIVForward;
        private ImageView mIVBack;

        public ViewHolder(View itemView) {
            super(itemView);

            mTimes = new ArrayList<>();
            mTimeAdapter = new TimeOptionAdapter(mTimes, this);
            mRVTimes = itemView.findViewById(R.id.rvTimes);
            mRVTimes.setAdapter(mTimeAdapter);
            mRVTimes.setLayoutManager(new LinearLayoutManager(mContext));

            mTVDay = itemView.findViewById(R.id.tvDay);
            mIVForward = itemView.findViewById(R.id.ivForward);
            mIVBack = itemView.findViewById(R.id.ivBack);
        }

        public void getTimes(int day) {
            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("timeOptions").child(Integer.toString(day)).child(mDays.get(day)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mTimes.isEmpty()) {
                        for (DataSnapshot startTime : dataSnapshot.getChildren())
                            mTimes.add(new TimeOption(startTime.child("startTime").getValue().toString(), startTime.child("time").getValue().toString().substring(11), startTime));
                        sort();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void scrollToPosition(int position) {
            mRVTimes.scrollToPosition(position);
        }

        public void sort() {
            Collections.sort(mTimes);
            mTimeAdapter.notifyDataSetChanged();
        }
    }
}


