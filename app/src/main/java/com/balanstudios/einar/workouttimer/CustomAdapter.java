package com.balanstudios.einar.workouttimer;

import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements ItemTouchHelperCustom.ItemTouchHelperAdapter {
    public static ArrayList<Workout> workouts;
    public static Workout currentWorkout;
    public MainActivity mainActivity;


    public CustomAdapter(MainActivity mainActivity, ArrayList<Workout> workouts, Workout currentWorkout) {
        CustomAdapter.workouts = workouts;
        CustomAdapter.currentWorkout = currentWorkout;
        this.mainActivity = mainActivity;

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_workout, viewGroup, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(v, mainActivity);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        Workout workout = workouts.get(i);

        customViewHolder.nameText.setText(workout.getWorkoutName());

        if (mainActivity.isHideDescriptionEnabled()) {
            customViewHolder.descriptionText.setText("Description hidden");
        } else {
            if (workout.getDescription().length() == 0) {
                customViewHolder.descriptionText.setText("No description.");
            } else if (workout.getDescription().length() < 140) {
                customViewHolder.descriptionText.setText(workout.getDescription());

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
                customViewHolder.descriptionText.setText(description);
            }
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void filterList(ArrayList<Workout> workouts) {
        CustomAdapter.workouts = workouts;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(workouts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(workouts, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        mainActivity.saveWorkoutData();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView nameText;
        public TextView descriptionText;
        public ImageButton moreButton;
        MainActivity mainActivity;
        TimerFragment timerFragment = new TimerFragment();
        int index; //index of workout card that is being interacted with
        ExercisesFragment exercisesFragment = new ExercisesFragment();
        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.cardView:
                        if (exercisesFragment.getIsQueue()) {
                            index = getAdapterPosition();
                            mainActivity.getWorkoutQueue().add(workouts.get(index));
                            Toast.makeText(mainActivity, "Added " + workouts.get(index).getWorkoutName() + " to queue", Toast.LENGTH_SHORT).show();
                        } else {
                            clickSelect();
                            setCachedWorkout();
                            showToast();
                        }
                        break;
                    case R.id.moreButton:
                        clickMore();
                        break;
                }
            }
        };


        public CustomViewHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(onClickListener);
            nameText = itemView.findViewById(R.id.workoutNameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);

            moreButton = itemView.findViewById(R.id.moreButton);
            moreButton.setOnClickListener(onClickListener);
            if (exercisesFragment.getIsQueue()) {
                moreButton.setVisibility(View.GONE);
            }
            this.mainActivity = mainActivity;

        }

        private void clickSelect() {
            index = getAdapterPosition();
            int wTime = workouts.get(index).getwTime();
            int rTime = workouts.get(index).getrTime();
            int prepTime = workouts.get(index).getPrepTime();
            int endWorkoutRest = workouts.get(index).getEndWorkoutRest();
            int totalTime = workouts.get(index).getTimeTotal();
            int numSets = workouts.get(index).getNumSets();
            int numCycles = workouts.get(index).getNumCycles();
            int numRepeats = workouts.get(index).getNumRepeats();
            String name = workouts.get(index).getWorkoutName();
            String description = workouts.get(index).getDescription();

            currentWorkout.setwTime(wTime);
            currentWorkout.setrTime(rTime);
            currentWorkout.setPrepTime(prepTime);
            currentWorkout.setNumSets(numSets);
            currentWorkout.setNumCycles(numCycles);
            currentWorkout.setWorkoutName(name);
            currentWorkout.setNumRepeats(numRepeats);
            currentWorkout.setEndWorkoutRest(endWorkoutRest);
            currentWorkout.setDescription(description);

        }


        private void clickMore() {
            index = getAdapterPosition();
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), moreButton);
            popupMenu.getMenuInflater().inflate(R.menu.workout_card_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.deleteWorkoutMenuItem:
                            workouts.remove(index);
                            mainActivity.saveWorkoutData();
                            exercisesFragment.getCustomAdapter().notifyItemRemoved(index);
                            return true;
                        case R.id.descriptionMenuItem:
                            openDialog();
                            return true;
                        case R.id.editWorkoutMenuItem:
                            setCachedWorkout();
                            clickSelect();
                            timerFragment.setSavedEdit(true);
                            mainActivity.setFragmentReturnable(timerFragment, "pos", index);
                            return true;
                        case R.id.queueMenuItem:
                            mainActivity.getWorkoutQueue().add(workouts.get(index));
                            mainActivity.showLayoutToast(R.drawable.ic_playlist_add_black_24dp, "Added " + workouts.get(index).getWorkoutName() + " to queue");
                            return true;
                        default:
                            return false;

                    }

                }
            });
            popupMenu.show();
        }

        private void showToast() {
            mainActivity.showLayoutToast(R.drawable.ic_check_black_24dp, String.format(Locale.getDefault(), "Set %s as current workout", currentWorkout.getWorkoutName()));
        }

        private void setCachedWorkout() {
            mainActivity.getCachedWorkout().setDescription(mainActivity.getCurrentWorkout().getDescription());
            mainActivity.getCachedWorkout().setWorkoutName(mainActivity.getCurrentWorkout().getWorkoutName());
            mainActivity.getCachedWorkout().setwTime(mainActivity.getCurrentWorkout().getwTime());
            mainActivity.getCachedWorkout().setrTime(mainActivity.getCurrentWorkout().getrTime());
            mainActivity.getCachedWorkout().setPrepTime(mainActivity.getCurrentWorkout().getPrepTime());
            mainActivity.getCachedWorkout().setNumSets(mainActivity.getCurrentWorkout().getNumSets());
            mainActivity.getCachedWorkout().setNumCycles(mainActivity.getCurrentWorkout().getNumCycles());
            mainActivity.getCachedWorkout().setNumRepeats(mainActivity.getCurrentWorkout().getNumRepeats());
            mainActivity.getCachedWorkout().setEndWorkoutRest(mainActivity.getCurrentWorkout().getEndWorkoutRest());
            mainActivity.getCachedWorkout().setDescription(mainActivity.getCurrentWorkout().getDescription());


        }

        private void openDialog() {
            DescriptionDialog descriptionDialog = new DescriptionDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("pos", getAdapterPosition());
            descriptionDialog.setArguments(bundle);
            descriptionDialog.show(mainActivity.getSupportFragmentManager(), "description dialog");
        }

    }

}
