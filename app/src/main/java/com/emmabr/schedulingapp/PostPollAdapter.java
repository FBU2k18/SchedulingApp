package com.emmabr.schedulingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import me.emmabr.schedulingapp.R;

public class PostPollAdapter extends RecyclerView.Adapter<PostPollAdapter.ViewHolder> {

    private Map<String, Integer> hashMap;
    //private ArrayList<String> mPollOptions;
    private String mGroupID;
    private Context mContext;
    private View.OnClickListener onClickListener;

    public PostPollAdapter(Map<String, Integer> hashMap, String groupId) {
        this.hashMap = hashMap;
        this.mGroupID = groupId;
    }

    @NonNull
    @Override
    public PostPollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View optionView = inflater.inflate(R.layout.item_new_poll_option, viewGroup, false);
        return new PostPollAdapter.ViewHolder(optionView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostPollAdapter.ViewHolder viewHolder, final int i) {
        String option = "";
        int j = 0;
        for(Map.Entry entry : hashMap.entrySet()) {
            if (j == i) {
                option = entry.getKey().toString();
            }
            ++j;
        }
        viewHolder.tvPollOption.setText(option);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((AppCompatTextView) view).getText()
                //i is no the new view that is clicked
                //mPollOptions.remove(((AppCompatTextView) view).getText());

                if (((AppCompatTextView) view).getText() != null) {
                    hashMap.remove(((AppCompatTextView) view).getText());
                    //notify item removed at the position removed from
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                    //reset the hash map
                    int counter = 0;
                    for(Map.Entry entry : hashMap.entrySet()) {
                        entry.setValue(counter);
                        ++counter;
                    }

                }



            }
        };

        viewHolder.tvPollOption.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return hashMap.size();
    }

    public Map getMappedUsers() {
        return hashMap;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPollOption;

        public ViewHolder(final View itemView) {
            super(itemView);

            tvPollOption = itemView.findViewById(R.id.tvPollOption);
        }
    }

}
