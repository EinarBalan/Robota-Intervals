package com.balanstudios.einar.workouttimer;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExercisesFragment extends Fragment {

    private Animation animation;
    private static CustomAdapter customAdapter;
    private static Boolean isQueue = false;

    private Handler handler = new Handler();
    private RecyclerView customRecyclerView;
    private RecyclerView.LayoutManager customLayoutManager;
    private SpaceItemDecoration itemDecoration;

    private TimerFragment timerFragment = new TimerFragment();
    private LinearLayout emptyWorkoutsLayout;

    private ImageButton addWorkoutButton;
    private ImageButton presetButton;
    private ImageButton backButton;
    private ProgressBar loadProgressBar;
    private TextView workoutsHeaderText;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addWorkoutButton:
                    clickAdd();
                    break;
                case R.id.presetsButton:
                    clickPreset();
                    break;
                case R.id.backButton:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
            }
        }
    };


    public ExercisesFragment() {
        // Required empty public constructor
    }

    public static Boolean getIsQueue() {
        return isQueue;
    }

    public static void setIsQueue(Boolean isQueue) {
        ExercisesFragment.isQueue = isQueue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exercises, container, false);

        ((MainActivity) getActivity()).clearNotifChannelTimer(v);

        ((MainActivity) getActivity()).restoreCache();

        emptyWorkoutsLayout = v.findViewById(R.id.emptyWorkoutsLayout);
        if (((MainActivity) getActivity()).getWorkouts().isEmpty()) {
            emptyWorkoutsLayout.setVisibility(View.VISIBLE);
        } else {
            emptyWorkoutsLayout.setVisibility(View.GONE);
        }

        addWorkoutButton = v.findViewById(R.id.addWorkoutButton);
        addWorkoutButton.setOnClickListener(onClickListener);
        presetButton = v.findViewById(R.id.presetsButton);
        presetButton.setOnClickListener(onClickListener);
        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(onClickListener);
        loadProgressBar = v.findViewById(R.id.loadProgressBar);


        //load recycler in background so no delay when changing fragments
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        new Thread(new BackgroundRunnable(v)).start();

        if (isQueue) {
            emptyWorkoutsLayout.setVisibility(View.GONE);
            addWorkoutButton.setVisibility(View.GONE);
            presetButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);
        } else {
            addWorkoutButton.setVisibility(View.VISIBLE);
            presetButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).saveWorkoutData();
    }

    private void clickAdd() {
        //save current workout so can be restored if user doesn't input anything in input screen
        ((MainActivity) getActivity()).getCachedWorkout().setDescription(((MainActivity) getActivity()).getCurrentWorkout().getDescription());
        ((MainActivity) getActivity()).getCachedWorkout().setWorkoutName(((MainActivity) getActivity()).getCurrentWorkout().getWorkoutName());
        ((MainActivity) getActivity()).getCachedWorkout().setwTime(((MainActivity) getActivity()).getCurrentWorkout().getwTime());
        ((MainActivity) getActivity()).getCachedWorkout().setrTime(((MainActivity) getActivity()).getCurrentWorkout().getrTime());
        ((MainActivity) getActivity()).getCachedWorkout().setPrepTime(((MainActivity) getActivity()).getCurrentWorkout().getPrepTime());
        ((MainActivity) getActivity()).getCachedWorkout().setNumSets(((MainActivity) getActivity()).getCurrentWorkout().getNumSets());
        ((MainActivity) getActivity()).getCachedWorkout().setNumCycles(((MainActivity) getActivity()).getCurrentWorkout().getNumCycles());
        ((MainActivity) getActivity()).getCachedWorkout().setNumRepeats(((MainActivity) getActivity()).getCurrentWorkout().getNumRepeats());
        ((MainActivity) getActivity()).getCachedWorkout().setEndWorkoutRest(((MainActivity) getActivity()).getCurrentWorkout().getEndWorkoutRest());

        timerFragment.setAdd(true);
        ((MainActivity) getActivity()).setFragmentSubtle(timerFragment);
    }

    private void clickPreset() {
        PopupMenu menu = new PopupMenu(getActivity(), presetButton);
        menu.getMenuInflater().inflate(R.menu.presets_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.presetsTabata:
                        setCurrentWorkout("Tabata", 20, 10, 1, 1, 8, 5, 0);
                        setCachedWorkout(((MainActivity) getActivity()).getCurrentWorkout());
                        showToast();
                        return true;
                    case R.id.presetsCircuit:
                        setCurrentWorkout("Circuit", 30, 30, 1, 5, 3, 5, 0);
                        setCachedWorkout(((MainActivity) getActivity()).getCurrentWorkout());
                        showToast();
                        return true;
                    case R.id.presets3030:
                        setCurrentWorkout("HIIT 30/30", 30, 30, 1, 1, 5, 5, 0);
                        setCachedWorkout(((MainActivity) getActivity()).getCurrentWorkout());
                        showToast();
                        return true;
                    case R.id.presetSevenMinuteWorkout:
                        setCurrentWorkout("Seven Minute Workout", 30, 5, 1, 1, 12, 5, 0);
                        setCachedWorkout(((MainActivity) getActivity()).getCurrentWorkout());
                        showToast();
                    default:
                        return false;
                }


            }
        });
        menu.show();


    }

    //search code currently unused but may implement later
    private void filter(String s) {
        ArrayList<Workout> filteredList = new ArrayList<>();
        ArrayList<Workout> workouts = ((MainActivity) getActivity()).getWorkouts();

        for (Workout w : workouts) {
            if (w.getWorkoutName().toLowerCase().contains(s.toLowerCase()))
                filteredList.add(w);
        }

        customAdapter.filterList(filteredList);
    }

    //duplicate method (except doesn't update description or timers directly)
    private void setCurrentWorkout(String name, int wTime, int rTime, int numRepeats, int numCycles, int numSets, int prepTime, int endWorkoutRest) {
        ((MainActivity) getActivity()).getCurrentWorkout().setWorkoutName(name);
        ((MainActivity) getActivity()).getCurrentWorkout().setwTime(wTime);
        ((MainActivity) getActivity()).getCurrentWorkout().setrTime(rTime);
        ((MainActivity) getActivity()).getCurrentWorkout().setNumRepeats(numRepeats);
        ((MainActivity) getActivity()).getCurrentWorkout().setNumCycles(numCycles);
        ((MainActivity) getActivity()).getCurrentWorkout().setNumSets(numSets);
        ((MainActivity) getActivity()).getCurrentWorkout().setPrepTime(prepTime);
        ((MainActivity) getActivity()).getCurrentWorkout().setEndWorkoutRest(endWorkoutRest);
        ((MainActivity) getActivity()).getCurrentWorkout().setDescription("");
    }

    private void setCachedWorkout(Workout workout) {
        ((MainActivity) getActivity()).getCachedWorkout().setDescription(workout.getDescription());
        ((MainActivity) getActivity()).getCachedWorkout().setWorkoutName(workout.getWorkoutName());
        ((MainActivity) getActivity()).getCachedWorkout().setwTime(workout.getwTime());
        ((MainActivity) getActivity()).getCachedWorkout().setrTime(workout.getrTime());
        ((MainActivity) getActivity()).getCachedWorkout().setPrepTime(workout.getPrepTime());
        ((MainActivity) getActivity()).getCachedWorkout().setNumSets(workout.getNumSets());
        ((MainActivity) getActivity()).getCachedWorkout().setNumCycles(workout.getNumCycles());
        ((MainActivity) getActivity()).getCachedWorkout().setNumRepeats(workout.getNumRepeats());
        ((MainActivity) getActivity()).getCachedWorkout().setEndWorkoutRest(workout.getEndWorkoutRest());
    }

    public RecyclerView.Adapter getCustomAdapter() {
        return customAdapter;
    }

    private void showToast() {
        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_check_black_24dp, String.format(Locale.getDefault(), "Set %s as current workout",
                ((MainActivity) getActivity()).getCurrentWorkout().getWorkoutName()));
    }

    public class BackgroundRunnable implements Runnable {
        View v;

        public BackgroundRunnable(View v) {
            this.v = v;
        }

        @Override
        public void run() {
            try { //causes null pointer exception if exercises fragment happens to load before main activity
                customAdapter = new CustomAdapter((MainActivity) getActivity(), ((MainActivity) getActivity()).getWorkouts(), ((MainActivity) getActivity()).getCurrentWorkout());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        customRecyclerView = v.findViewById(R.id.customRecyclerView);
                        customLayoutManager = new LinearLayoutManager(getActivity());
                        itemDecoration = new SpaceItemDecoration(16);
                        ItemTouchHelper.Callback callback = new ItemTouchHelperCustom(customAdapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                        loadProgressBar.setVisibility(View.GONE);


                        customRecyclerView.setLayoutManager(customLayoutManager);
                        customRecyclerView.addItemDecoration(itemDecoration);
                        customRecyclerView.setAdapter(customAdapter);
                        itemTouchHelper.attachToRecyclerView(customRecyclerView);

                        if (animation != null) {
                            customRecyclerView.startAnimation(animation);
                        }

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
