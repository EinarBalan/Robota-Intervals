package com.balanstudios.einar.workouttimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestedWorkoutFragment extends Fragment {

    Workout suggestedWorkout;

    private ImageButton backButton;
    private ImageButton moreButton;
    private Button queueButton;
    private Button applyButton;

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView prepTextView;
    private TextView workTextView;
    private TextView restTextView;
    private TextView cyclesTextView;
    private TextView setsTextView;
    private TextView totalTimeTextView;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backButton:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case R.id.moreButton:
                    PopupMenu menu = new PopupMenu(getContext(), moreButton);
                    menu.getMenuInflater().inflate(R.menu.suggested_menu, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.summaryMenuItem:
                                    ((MainActivity) getActivity()).setFragmentReturnable(new SummaryFragment());
                                    return true;
                                case R.id.addMenuItem:
                                    boolean hasDuplicateName = false;

                                    for (Workout w : ((MainActivity) getActivity()).getWorkouts()) { //make sure no duplicate names
                                        if (w.getWorkoutName().equals(suggestedWorkout.getWorkoutName())) {
                                            hasDuplicateName = true;
                                            break;
                                        }
                                    }

                                    if (((MainActivity) getActivity()).getWorkouts().size() > 25) {
                                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, "Max number of workouts reached");
                                    } else if (hasDuplicateName) {
                                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, "Workout with same name already present in My Workouts");
                                    } else {
                                        ((MainActivity) getActivity()).getWorkouts().add(new Workout(suggestedWorkout.getTimers(), suggestedWorkout.getWorkoutName(), suggestedWorkout.getDescription()));
                                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_bookmark_border_black_24dp, "Added " + suggestedWorkout.getWorkoutName() + " to My Workouts");
                                        ((MainActivity) getActivity()).saveWorkoutData();
                                    }
                                    return true;
                            }
                            return false;
                        }
                    });
                    menu.show();
                    break;
                case R.id.queueButton:
                    ((MainActivity) getActivity()).getWorkoutQueue().add(new Workout(suggestedWorkout.getTimers(), suggestedWorkout.getWorkoutName(), suggestedWorkout.getDescription()));
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_playlist_add_black_24dp, "Added " + suggestedWorkout.getWorkoutName() + " to queue");
                    ((MainActivity) getActivity()).saveWorkoutData();
                    break;
                case R.id.applyButton:
                    ((MainActivity) getActivity()).setCurrentWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_check_black_24dp, "Successfully set " + ((MainActivity) getActivity()).getSuggestedWorkout().getWorkoutName() + " as current workout");

                    ((MainActivity) getActivity()).getCachedWorkout().setNumCycles(suggestedWorkout.getNumCycles());
                    ((MainActivity) getActivity()).getCachedWorkout().setwTime(suggestedWorkout.getwTime());
                    ((MainActivity) getActivity()).getCachedWorkout().setrTime(suggestedWorkout.getrTime());
                    ((MainActivity) getActivity()).getCachedWorkout().setNumSets(suggestedWorkout.getNumSets());
                    ((MainActivity) getActivity()).getCachedWorkout().setPrepTime(suggestedWorkout.getPrepTime());
                    ((MainActivity) getActivity()).getCachedWorkout().setWorkoutName(suggestedWorkout.getWorkoutName());
                    ((MainActivity) getActivity()).getCachedWorkout().setNumRepeats(suggestedWorkout.getNumRepeats());
                    ((MainActivity) getActivity()).getCachedWorkout().setEndWorkoutRest(suggestedWorkout.getEndWorkoutRest());
                    ((MainActivity) getActivity()).getCachedWorkout().setDescription(suggestedWorkout.getDescription());

                    ((MainActivity) getActivity()).saveWorkoutData();
                    break;
            }
        }
    };

    public SuggestedWorkoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (((MainActivity) getActivity()).getmMainNav().getVisibility() == View.VISIBLE) {
            ((MainActivity) getActivity()).getmMainNav().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);

        View v = inflater.inflate(R.layout.fragment_suggested_workout, container, false);

        if (((MainActivity) getActivity()).getSuggestedWorkout() != null) {
            suggestedWorkout = ((MainActivity) getActivity()).getSuggestedWorkout();
        }

        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(onClickListener);
        moreButton = v.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(onClickListener);
        queueButton = v.findViewById(R.id.queueButton);
        queueButton.setOnClickListener(onClickListener);
        applyButton = v.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(onClickListener);

        nameTextView = v.findViewById(R.id.nameTextView);
        descriptionTextView = v.findViewById(R.id.descriptionTextView);
        prepTextView = v.findViewById(R.id.prepTextView);
        workTextView = v.findViewById(R.id.workTextView);
        restTextView = v.findViewById(R.id.restTextView);
        cyclesTextView = v.findViewById(R.id.cyclesTextView);
        setsTextView = v.findViewById(R.id.setsTextView);
        totalTimeTextView = v.findViewById(R.id.totalTimeTextView);

        initializeDisplay();

        return v;
    }

    private void initializeDisplay() {
        nameTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getWorkoutName());
        try {
            descriptionTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getDescription());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Description not found!", Toast.LENGTH_SHORT);
        }
        prepTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getPrepTimeString());
        workTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getwTimeString());
        restTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getrTimeString());
        cyclesTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getNumCycles() + "");
        setsTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getNumSets() + "");
        totalTimeTextView.setText(((MainActivity) getActivity()).getSuggestedWorkout().getTimeTotalString());
    }


}
