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

public class DayAdapter extends RecyclerView.Adapter<com.emmabr.schedulingapp.DayAdapter.ViewHolder> {

    private ArrayList<String> mDays;
    private Context mContext;
    private GroupActivity mParent;

    public DayAdapter(ArrayList<String> days, GroupActivity parent) {
        this.mDays = days;
        this.mParent = parent;
    }

    @Override
    public void onBindViewHolder(@NonNull final DayAdapter.ViewHolder holder, final int position) {
        holder.mTVDay.setText(mDays.get(position));
        holder.mIVForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.scrollToPosition((position + 1) % 7);
            }
        });
        holder.mIVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.scrollToPosition((position - 1) % 7);
            }
        });
//        holder.getTimes(position);
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

//        public void getTimes(int day) {
////            //will pull times from Firebase, but testing for now
////            for (int i = 0; i < 10; i++)
////                mTimes.add(TimeOption.newTime());
////            Collections.sort(mTimes);
////            mTimeAdapter.notifyDataSetChanged();
//        }

        public void scrollToPosition(int position) {
            mRVTimes.scrollToPosition(position);
        }
    }
}


