package com.balanstudios.einar.workouttimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;

import java.util.ArrayList;

public class QueueClearDialog extends AppCompatDialogFragment {
    QueueDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogStyle);
        final QueueFragment queueFragment = new QueueFragment();

        builder.setTitle("Clear Queue?")
                .setMessage("Are you sure you want to clear the queue?")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.getWorkoutQueue().clear();
                        queueFragment.getQueueAdapter().notifyDataSetChanged();
                        listener.saveWorkoutData();
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (QueueDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DescriptionDialogListener");
        }
    }

    public interface QueueDialogListener {
        ArrayList<Workout> getWorkoutQueue();

        void saveWorkoutData();

        void setFragment(Fragment f);
    }
}
