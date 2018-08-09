package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.emmabr.schedulingapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

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
        Button btAddCal;
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

            btAddCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(mContext,
                            Collections.singleton("https://www.googleapis.com/auth/calendar"));
                    GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(mContext);
                    if (accountGoogle != null) {
                        credential.setSelectedAccount(new Account(accountGoogle.getEmail().toString(), "com.emmabr.schedulingapp"));
                    }
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    final Calendar service = new Calendar.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credential)
                            .setApplicationName("SchedulingApp").build();
                    final Event eventToCal = new Event()
                            .setSummary("Meeting scheduled by ScheduleMe");
                    String totalTime = mBusyTime.getText().toString();
                    int cutOff = totalTime.indexOf(" ");
                    int endCutOff = totalTime.lastIndexOf(" ");
                    String startTime = totalTime.substring(0, cutOff);
                    String endTime = totalTime.substring(endCutOff+1);
                    DateTime startEventDT = new DateTime("2018-04-10T" + startTime + "-07:00");
                    EventDateTime start = new EventDateTime()
                            .setDateTime(startEventDT)
                            .setTimeZone("America/Los_Angeles");
                    eventToCal.setStart(start);
                    DateTime endEventDT = new DateTime("2018-04-10T" + endTime + "-07:00");
                    EventDateTime end = new EventDateTime()
                            .setDateTime(endEventDT)
                            .setTimeZone("America/Los_Angeles");
                    eventToCal.setEnd(end);

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Event> eventCreation = new AsyncTask<Void, Void, Event>() {
                        @Override
                        protected Event doInBackground(Void... voids) {
                            try {
                                return service.events().insert("primary", eventToCal).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Event event) {
                            if (event != null) {
                                System.out.printf("Event created: %s\n", event.getHtmlLink());
                            }
                        }
                    };
                    eventCreation.execute();

                }
            });
        }

    }
}
