package com.balanstudios.einar.workouttimer;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.QueueViewHolder> implements ItemTouchHelperQueue.ItemTouchHelperAdapter {
    public static ArrayList<Workout> workoutsQueue;
    public MainActivity mainActivity;

    public QueueAdapter(ArrayList<Workout> workoutsQueue, MainActivity mainActivity) {
        this.workoutsQueue = workoutsQueue;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_workout, viewGroup, false);
        QueueViewHolder queueViewHolder = new QueueViewHolder(v, mainActivity);
        return queueViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QueueViewHolder queueViewHolder, int i) {
        Workout workout = workoutsQueue.get(i);

        queueViewHolder.nameText.setText(workout.getWorkoutName());

        if (mainActivity.isHideDescriptionEnabled()) {
            queueViewHolder.descriptionText.setText("Description hidden");
        } else {
            if (workout.getDescription().length() == 0) {
                queueViewHolder.descriptionText.setText("No description.");
            } else if (workout.getDescription().length() < 140) {
                queueViewHolder.descriptionText.setText(workout.getDescription());

            } else {
                int spaceIndex = workout.getDescription().indexOf(" ", 141);
                String description;
                if (spaceIndex > 0) {
                    description = workout.getDescription().substring(0, 141) + workout.getDescription().substring(141, spaceIndex);
                } else
                    description = workout.getDescription();
                if (description.length() < workout.getDescription().length()) {
                    description += "...";
                }
                queueViewHolder.descriptionText.setText(description);
            }
        }
    }

    @Override
    public int getItemCount() {
        return workoutsQueue.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(workoutsQueue, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(workoutsQueue, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        mainActivity.saveWorkoutData();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        workoutsQueue.remove(position);
        notifyItemRemoved(position);
        mainActivity.saveWorkoutData();
    }

    public static class QueueViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView nameText;
        public TextView descriptionText;
        public ImageButton moreButton;
        MainActivity mainActivity;
        int index; //index of workout card that is being interacted with
        QueueFragment queueFragment = new QueueFragment();
        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.moreButton:
                        clickMore();
                        break;
                }
            }
        };

        public QueueViewHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);
            card = itemView.findViewById(R.id.cardView);
            nameText = itemView.findViewById(R.id.workoutNameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);

            moreButton = itemView.findViewById(R.id.moreButton);
            moreButton.setOnClickListener(onClickListener);
            this.mainActivity = mainActivity;
        }

        private void clickMore() {
            index = getAdapterPosition();
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), moreButton);
            popupMenu.getMenuInflater().inflate(R.menu.queue_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.deleteMenuItem:
                            workoutsQueue.remove(index);
                            mainActivity.saveWorkoutData();
                            queueFragment.getQueueAdapter().notifyItemRemoved(index);
                            return true;
                        default:
                            return false;

                    }

                }
            });
            popupMenu.show();
        }
    }
}
