package com.balanstudios.einar.workouttimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment {

    private Animation animation;
    private static QueueAdapter queueAdapter;
    private ImageButton backButton;
    private ImageButton clearButton;
    private Button addToQueueButton;
    private ProgressBar progressBar;
    private LinearLayout queueEmptyLayout;
    private TextView workoutNameText;
    private TextView descriptionText;
    private RecyclerView queueRecyclerView;
    private RecyclerView.LayoutManager queueLayoutManager;
    private SpaceItemDecoration spaceItemDecoration;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backButton:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case R.id.clearButton:
                    clickClear();
                    break;
                case R.id.addToQueueButton:
                    ExercisesFragment exercisesFragment = new ExercisesFragment();
                    exercisesFragment.setIsQueue(true);
                    ((MainActivity) getActivity()).setFragmentSubtle(exercisesFragment);
                    break;
            }
        }
    };

    public QueueFragment() {
        // Required empty public constructor
    }

    //getters and setters
    public static QueueAdapter getQueueAdapter() {
        return queueAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_queue, container, false);

        queueEmptyLayout = v.findViewById(R.id.queueEmptyLayout);
        if (((MainActivity) getActivity()).getWorkoutQueue().isEmpty()) {
            queueEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            queueEmptyLayout.setVisibility(View.GONE);
        }

        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(onClickListener);
        clearButton = v.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(onClickListener);
        addToQueueButton = v.findViewById(R.id.addToQueueButton);
        addToQueueButton.setOnClickListener(onClickListener);
        progressBar = v.findViewById(R.id.progressBar);

        workoutNameText = v.findViewById(R.id.workoutNameText);
        descriptionText = v.findViewById(R.id.descriptionText);

        try {
            if (((MainActivity) getActivity()).isHideDescriptionEnabled()) {
                descriptionText.setText("Description hidden");
            } else {
                if (((MainActivity) getActivity()).getCachedWorkout().getDescription().length() == 0) {
                    descriptionText.setText("No description.");
                } else if (((MainActivity) getActivity()).getCachedWorkout().getDescription().length() < 140) {
                    descriptionText.setText(((MainActivity) getActivity()).getCachedWorkout().getDescription());

                } else {
                    int spaceIndex = ((MainActivity) getActivity()).getCachedWorkout().getDescription().indexOf(" ", 141);
                    String description;
                    if (spaceIndex > 0) {
                        description = ((MainActivity) getActivity()).getCachedWorkout().getDescription().substring(0, 141) + ((MainActivity) getActivity()).getCachedWorkout().getDescription().substring(141, spaceIndex);
                    } else
                        description = ((MainActivity) getActivity()).getCachedWorkout().getDescription();
                    if (description.length() < ((MainActivity) getActivity()).getCachedWorkout().getDescription().length()) {
                        description += "...";
                    }
                    descriptionText.setText(description);
                }
            }
        } catch (Exception e) {
            descriptionText.setText("No description.");
        }


        workoutNameText.setText(((MainActivity) getActivity()).getCachedWorkout().getWorkoutName());

        ExercisesFragment exercisesFragment = new ExercisesFragment();
        exercisesFragment.setIsQueue(false);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        new Thread(new BackgroundRunnable(v)).start();

        return v;
    }

    private void clickClear() {
        QueueClearDialog queueClearDialog = new QueueClearDialog();
        queueClearDialog.show(getActivity().getSupportFragmentManager(), "queueClearDialog");
    }

    public class BackgroundRunnable implements Runnable {
        View v;

        public BackgroundRunnable(View v) {
            this.v = v;
        }

        @Override
        public void run() {
            try {
                queueAdapter = new QueueAdapter(((MainActivity) getActivity()).getWorkoutQueue(), (MainActivity) getActivity());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        queueRecyclerView = v.findViewById(R.id.queueRecyclerView);
                        queueLayoutManager = new LinearLayoutManager(getActivity());
                        spaceItemDecoration = new SpaceItemDecoration(16);
                        ItemTouchHelper.Callback callback = new ItemTouchHelperQueue(queueAdapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

                        progressBar.setVisibility(View.GONE);

                        queueRecyclerView.setLayoutManager(queueLayoutManager);
                        queueRecyclerView.addItemDecoration(spaceItemDecoration);
                        queueRecyclerView.setAdapter(queueAdapter);
                        itemTouchHelper.attachToRecyclerView(queueRecyclerView);
                        if (animation != null) {
                            queueRecyclerView.startAnimation(animation);
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
