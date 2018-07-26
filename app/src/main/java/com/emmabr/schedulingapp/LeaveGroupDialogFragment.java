package com.emmabr.schedulingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import me.emmabr.schedulingapp.R;

public class LeaveGroupDialogFragment extends DialogFragment {

    LeaveGroupDialogFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Leave group?")
                .setPositiveButton("LEAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.leaveGroup();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LeaveGroupDialogFragmentListener) {
            mListener = (LeaveGroupDialogFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement LeaveGroupDialogFragment.LeaveGroupDialogFragmentListener");
        }
    }

    interface LeaveGroupDialogFragmentListener {
        void leaveGroup();
    }
}
