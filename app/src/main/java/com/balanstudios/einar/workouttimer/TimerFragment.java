package com.balanstudios.einar.workouttimer;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    public static final String CIRCUIT_SWITCH = "circuitSwitch";
    public static final String PREP_SWITCH = "prepSwitch";
    public static final String REPEAT_SWITCH = "repeatSwitch";
    public static final String SUMMARY_SWITCH = "summarySwitch";
    private Fragment timerFragment2 = new TimerFragment2();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private TutorialFragment tutorialFragment = new TutorialFragment();
    private QueueFragment queueFragment = new QueueFragment();
    private LinearLayout summaryLayout;
    private LinearLayout circuitLayout;
    private LinearLayout prepLayout;
    private LinearLayout repeatLayout;
    private CardView cardSummary;
    private EditText workTimer;
    private EditText restTimer;
    private EditText setsTimer;
    private EditText workoutNameEditText;
    private EditText cyclesTimer;
    private EditText prepTimer;
    private EditText repeatTimer;
    private EditText workoutEndRestTimer;
    private TextView totalTime;
    private TextView totalTimeWords;
    private TextView headerText;
    private TextView summaryTextView;
    private Button enterButton;
    private Button saveButton;
    private ImageButton moreButton;
    private ImageButton backButton;
    private Switch circuitSwitch;
    private Switch prepSwitch;
    private Switch repeatSwitch;
    private Switch summarySwitch;
    private int wTime;
    private int rTime;
    private int time;
    private int numSets;
    private int numCycles;
    private int prepTime;
    private String name;
    private int endWorkoutRest;
    private int numRepeats;

    private boolean isAdd = false; // necessary in order to change ui when clicking from edit button vs from add button
    private boolean isSavedEdit = false; //necessary to edit saved workouts instead of current
    private boolean changesApplied = false;



    private ArrayList<TimerInterval> timers = new ArrayList<TimerInterval>();


    //button click handler
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.enterButton:
                    if (isSavedEdit) {
                        clickSave();
                    } else {
                        clickEnter();
                        if (changesApplied) {//did it this way because I only want to show the toast if the enter button is pressed and not when I update the variables without the user knowing
                            applyCachedWorkout();
                            ((MainActivity) getActivity()).saveWorkoutData();
                            ((MainActivity) getActivity()).saveSwitchData(SUMMARY_SWITCH, summarySwitch);
                            ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_check_black_24dp, getString(R.string.changes_applied));
                        } else {
                            ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.work_interval_greater));

                        }
                    }

                    break;
                case R.id.saveButton:
                    clickSave();
                    ((MainActivity) getActivity()).saveSwitchData(SUMMARY_SWITCH, summarySwitch);
                    break;
                case R.id.backButton:
                    clickBack();
                    ((MainActivity) getActivity()).saveSwitchData(SUMMARY_SWITCH, summarySwitch);
                    break;
                case R.id.moreButton:
                    clickMore();
                    break;
                case R.id.cardSummary:
                    clickEnter();
                    Workout currentWorkout = ((MainActivity) getActivity()).getCurrentWorkout();

                    if (wTime > 0 && numCycles > 0 && numSets > 0 && numRepeats > 0) {//uses local variables because clickEnter won't update the current workout if wTime < 0 || numCycles < 0
                        ((MainActivity) getActivity()).setSuggestedWorkout(new Workout(timers, currentWorkout.getWorkoutName(), numRepeats, endWorkoutRest));
                        ((MainActivity) getActivity()).setFragmentReturnable(new SummaryFragment());
                    } else {
                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.work_interval_greater));
                    }
                    break;
            }
        }
    };


    //make inputting stuff more convenient, don't need to press enter manually to see summary/total time
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (view.getId() == R.id.workTimer || view.getId() == R.id.restTimer || view.getId() == R.id.prepTimer || view.getId() == R.id.workoutEndRestTimer) {
                if (((MainActivity) getActivity()).hasText((EditText) view)) {
                    textToValue((EditText) view);
                    ((EditText) view).selectAll();
                }
            }
        }
    };

    //logic for switches
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) { //show switch options when checked

            switch (compoundButton.getId()) {
                case R.id.circuitSwitch:
                    clickEnter();

                    if (circuitSwitch.isChecked()) {
                        circuitLayout.setVisibility(View.VISIBLE);
                        workTimer.setNextFocusDownId(R.id.cyclesTimer);
                    } else {
                        circuitLayout.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).getCurrentWorkout().setNumCycles(1);
                        workTimer.setNextFocusDownId(R.id.restTimer);
                    }
                    break;

                case R.id.prepSwitch:
                    clickEnter();
                    if (prepSwitch.isChecked()) {
                        prepLayout.setVisibility(View.VISIBLE);
                        restTimer.setNextFocusDownId(R.id.prepTimer);

                    } else {
                        prepLayout.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).getCurrentWorkout().setPrepTime(0);
                        restTimer.setNextFocusDownId(R.id.setsTimer);
                    }
                    break;

                case R.id.repeatSwitch:
                    clickEnter();
                    if (repeatSwitch.isChecked()) {
                        repeatLayout.setVisibility(View.VISIBLE);
                        setsTimer.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        workoutEndRestTimer.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        setsTimer.setNextFocusDownId(R.id.repeatTimer);
                    } else {
                        repeatLayout.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).getCurrentWorkout().setEndWorkoutRest(0);
                        ((MainActivity) getActivity()).getCurrentWorkout().setNumRepeats(1);
                        setsTimer.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }
                    break;
                case R.id.summarySwitch:
                    clickEnter();
                    if (summarySwitch.isChecked()) {
                        cardSummary.setVisibility(View.VISIBLE);
                    } else {
                        cardSummary.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    //when user presses done on keyboard, clickEnter()
    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if ((keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) || i == EditorInfo.IME_ACTION_DONE) {
                clickEnter();
            }
            return false;
        }
    };

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (((MainActivity) getActivity()).getmMainNav().getVisibility() == View.VISIBLE) {
            ((MainActivity) getActivity()).getmMainNav().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer, container, false);


        summaryLayout = v.findViewById(R.id.summaryLayout);
        circuitLayout = v.findViewById(R.id.circuitLayout);
        prepLayout = v.findViewById(R.id.prepLayout);
        repeatLayout = v.findViewById(R.id.repeatLayout);
        cardSummary = v.findViewById(R.id.cardSummary);
        cardSummary.setOnClickListener(onClickListener);

        workTimer = v.findViewById(R.id.workTimer);
        workTimer.setOnFocusChangeListener(onFocusChangeListener);
        workTimer.addTextChangedListener(new TimeFormat(workTimer).getTimeFormatter());

        restTimer = v.findViewById(R.id.restTimer);
        restTimer.setOnFocusChangeListener(onFocusChangeListener);
        restTimer.addTextChangedListener(new TimeFormat(restTimer).getTimeFormatter());

        prepTimer = v.findViewById(R.id.prepTimer);
        prepTimer.setOnFocusChangeListener(onFocusChangeListener);
        prepTimer.addTextChangedListener(new TimeFormat(prepTimer).getTimeFormatter());


        setsTimer = v.findViewById(R.id.setsTimer);
        setsTimer.setOnFocusChangeListener(onFocusChangeListener);
        setsTimer.setOnEditorActionListener(onEditorActionListener);

        cyclesTimer = v.findViewById(R.id.cyclesTimer);
        cyclesTimer.setOnFocusChangeListener(onFocusChangeListener);

        workoutNameEditText = v.findViewById(R.id.workoutNameText);
        workoutNameEditText.setOnFocusChangeListener(onFocusChangeListener);


        repeatTimer = v.findViewById(R.id.repeatTimer);
        repeatTimer.setOnFocusChangeListener(onFocusChangeListener);

        workoutEndRestTimer = v.findViewById(R.id.workoutEndRestTimer);
        workoutEndRestTimer.setOnFocusChangeListener(onFocusChangeListener);
        workoutEndRestTimer.setOnEditorActionListener(onEditorActionListener);
        workoutEndRestTimer.addTextChangedListener(new TimeFormat(workoutEndRestTimer).getTimeFormatter());

        totalTime = v.findViewById(R.id.totalTimeText);
        totalTimeWords = v.findViewById(R.id.totalTimeWords);
        headerText = v.findViewById(R.id.headerText);
        summaryTextView = v.findViewById(R.id.summaryTextView);

        enterButton = v.findViewById(R.id.enterButton);
        enterButton.setOnClickListener(onClickListener);
        saveButton = v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(onClickListener);
        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(onClickListener);
        moreButton = v.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(onClickListener);


        //set input fields to current workout values
        wTime = ((MainActivity) getActivity()).getwTime();
        workTimer.setText(((MainActivity) getActivity()).getwTimeString());
        rTime = ((MainActivity) getActivity()).getrTime();
        restTimer.setText(((MainActivity) getActivity()).getrTimeString());
        prepTime = ((MainActivity) getActivity()).getPrepTime();
        prepTimer.setText(((MainActivity) getActivity()).getPrepTimeString());
        endWorkoutRest = ((MainActivity) getActivity()).getEndWorkoutRest();
        workoutEndRestTimer.setText(((MainActivity) getActivity()).getEndWorkoutRestString());
        numRepeats = ((MainActivity) getActivity()).getNumRepeats();
        repeatTimer.setText("" + numRepeats);
        numSets = ((MainActivity) getActivity()).getNumSets();
        setsTimer.setText("" + numSets);
        numCycles = ((MainActivity) getActivity()).getNumCycles();
        cyclesTimer.setText("" + numCycles);
        name = ((MainActivity) getActivity()).getWorkoutName();
        workoutNameEditText.setText(name);


        //handles switch init and lets checked status hold across screens
        circuitSwitch = v.findViewById(R.id.circuitSwitch);
        circuitSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        if (numCycles > 1)
            circuitSwitch.setChecked(true);
        else
            circuitSwitch.setChecked(false);
        if (circuitSwitch.isChecked())
            circuitLayout.setVisibility(View.VISIBLE);

        prepSwitch = v.findViewById(R.id.prepSwitch);
        prepSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        if (prepTime > 0)
            prepSwitch.setChecked(true);
        else
            prepSwitch.setChecked(false);
        if (prepSwitch.isChecked())
            prepLayout.setVisibility(View.VISIBLE);

        repeatSwitch = v.findViewById(R.id.repeatSwitch);
        repeatSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        if (numRepeats > 1)
            repeatSwitch.setChecked(true);
        else
            repeatSwitch.setChecked(false);
        if (repeatSwitch.isChecked())
            repeatLayout.setVisibility(View.VISIBLE);

        summarySwitch = v.findViewById(R.id.summarySwitch);
        summarySwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        summarySwitch.setChecked(((MainActivity) getActivity()).getSwitchData(SUMMARY_SWITCH));

        if (isAdd) {
            headerText.setText(getString(R.string.add_workout));
            enterButton.setText(getString(R.string.set_as_current));
            clickClear();
        }

        return v;
    }


    //gets text from edit texts and stores it in variables accessible from everywhere in the activity
    public void clickEnter() {

        if ((((MainActivity) getActivity()).hasText(workTimer) && ((MainActivity) getActivity()).hasText(restTimer) && ((MainActivity) getActivity()).hasText(setsTimer))) {
            if (circuitSwitch != null) {
                if (circuitSwitch.isChecked()) {
                    if (cyclesTimer != null) {
                        if (((MainActivity) getActivity()).hasText(cyclesTimer))
                            ((MainActivity) getActivity()).setNumCycles(Integer.parseInt(cyclesTimer.getText().toString()));
                        else
                            cyclesTimer.setText("1");
                    }
                }
            } else
                ((MainActivity) getActivity()).setNumCycles(1);

            if (prepSwitch != null) {
                if (prepSwitch.isChecked()) {
                    if (prepTimer != null) {
                        if (((MainActivity) getActivity()).hasText(prepTimer))
                            ((MainActivity) getActivity()).setPrepTime(textToValue(prepTimer));
                        else
                            prepTimer.setText("0m 00s");
                    }
                } else
                    ((MainActivity) getActivity()).setPrepTime(0);
            }

            if (repeatSwitch != null) {
                if (repeatSwitch.isChecked()) {
                    if (repeatTimer != null) {
                        if (((MainActivity) getActivity()).hasText(repeatTimer) && ((MainActivity) getActivity()).hasText(workoutEndRestTimer)) {
                            ((MainActivity) getActivity()).setNumRepeats(Integer.parseInt(repeatTimer.getText().toString()));
                            ((MainActivity) getActivity()).setEndWorkoutRest(textToValue(workoutEndRestTimer));
                        } else {
                            repeatTimer.setText("1");
                            workoutEndRestTimer.setText("0m 00s");
                        }
                    }
                } else {
                    ((MainActivity) getActivity()).setNumRepeats(1);
                    ((MainActivity) getActivity()).setEndWorkoutRest(0);
                }
            }

            wTime = textToValue(workTimer);
            rTime = textToValue(restTimer);
            prepTime = ((MainActivity) getActivity()).getCurrentWorkout().getPrepTime();
            endWorkoutRest = ((MainActivity) getActivity()).getCurrentWorkout().getEndWorkoutRest();
            numSets = Integer.parseInt(setsTimer.getText().toString());
            numCycles = ((MainActivity) getActivity()).getCurrentWorkout().getNumCycles();
            numRepeats = ((MainActivity) getActivity()).getCurrentWorkout().getNumRepeats();
            name = workoutNameEditText.getText().toString();

            //looking back at this code I honestly can't remember why I decided to set variables to the point of redundancy...maybe there's a reason though so I won't mess with it for now
            if (wTime > 0 && numCycles > 0 && numSets > 0 && numRepeats > 0) { //to prevent bug where user inputs unchanged values then uses navbar to get back to timer screen
                ((MainActivity) getActivity()).getCurrentWorkout().setNumCycles(numCycles);
                ((MainActivity) getActivity()).getCurrentWorkout().setwTime(wTime);
                ((MainActivity) getActivity()).getCurrentWorkout().setrTime(rTime);
                ((MainActivity) getActivity()).getCurrentWorkout().setNumSets(numSets);
                ((MainActivity) getActivity()).getCurrentWorkout().setPrepTime(prepTime);
                ((MainActivity) getActivity()).getCurrentWorkout().setWorkoutName(workoutNameEditText.getText().toString());
                ((MainActivity) getActivity()).getCurrentWorkout().setNumRepeats(numRepeats);
                ((MainActivity) getActivity()).getCurrentWorkout().setEndWorkoutRest(endWorkoutRest);


                time = ((MainActivity) getActivity()).getCurrentWorkout().getTimeTotal();

                changesApplied = true;

            } else {
                changesApplied = false;
            }

            populateTimers();
        }

        //auto fill if user leaves edit text blank
        else {
            if (!((MainActivity) getActivity()).hasText(setsTimer))
                setsTimer.setText("1");
            if (!((MainActivity) getActivity()).hasText(workTimer))
                workTimer.setText("0m 00s");
            if (!((MainActivity) getActivity()).hasText(restTimer))
                restTimer.setText("0m 00s");
            clickEnter();
        }


    }

    private void populateTimers() {
        ((MainActivity) getActivity()).populateTimers(timers, prepTime, numSets, numCycles, numRepeats, wTime, rTime);
    }

    //save summary as string to copy to clipboard
    private String populateSummaryString() {
        int repeatNum = 0;
        int setNum = 0;

        String summary = "";

        summary += "Name - " + ((MainActivity) getActivity()).getWorkoutName() + "\n";
        summary += String.format(Locale.getDefault(), "Total Time - %d:%02d\n", (time / 60), (time % 60));
        summary += "----------------------\n";

        for (TimerInterval ti : timers) {
            if (numRepeats > 1) {
                if (ti.getNumOfRepeat() != repeatNum) {
                    repeatNum = ti.getNumOfRepeat();
                    summary += String.format(Locale.getDefault(), "Workout %d\n", ti.getNumOfRepeat());
                }
            }
            if (ti.getNumOfSet() != setNum) {
                setNum = ti.getNumOfSet();
                summary += String.format(Locale.getDefault(), "\tSet %d\n", ti.getNumOfSet());
            }

            summary += ("\t\t" + ti.toString() + "\n");
        }

        return summary;
    }

    //clears edit texts and timers list
    public void clickClear() {
        // totalTime.setVisibility(View.GONE);
        // totalTimeWords.setVisibility(View.GONE);
        workTimer.setText("");
        restTimer.setText("");
        setsTimer.setText("");
        workoutEndRestTimer.setText("");
        workoutNameEditText.setText("");
        prepTimer.setText("");
        cyclesTimer.setText("");
        repeatTimer.setText("");
        totalTime.setText("0:00");
        time = 0;
        circuitSwitch.setChecked(false);
        prepSwitch.setChecked(false);
        repeatSwitch.setChecked(false);
        summaryLayout.removeAllViews();
        timers.clear();
        ((MainActivity) getActivity()).getCurrentWorkout().setDescription("");


    }

    // enters data and makes sure wTime > 0 again just to be safe
    public void clickStart() {
        clickEnter();
        ((MainActivity) getActivity()).saveWorkoutData();
        ((MainActivity) getActivity()).saveSwitchData(CIRCUIT_SWITCH, circuitSwitch);
        ((MainActivity) getActivity()).saveSwitchData(PREP_SWITCH, prepSwitch);

        if (wTime > 0 && numCycles > 0 && numSets > 0 && numRepeats > 0) {
            ((MainActivity) getActivity()).setFragment(timerFragment2);
            ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, "Work interval must be greater than 0 seconds");

        }

    }

    //shows menu with settings and tutorial
    public void clickMore() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), moreButton);
        popupMenu.getMenuInflater().inflate(R.menu.input_more_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.settingsMenuItem:
                        ((MainActivity) getActivity()).setFragmentReturnable(settingsFragment);
                        return true;
                    case R.id.tutorialMenuItem:
                        ((MainActivity) getActivity()).setFragmentReturnable(tutorialFragment);
                        return true;
                    case R.id.clipboardMenuItem:
                        clickClipboard();
                        return true;
                    case R.id.queueMenuItem:
                        ((MainActivity) getActivity()).setFragmentReturnable(queueFragment);
                        return true;
                    case R.id.clearMenuItem:
                        clickClear();
                        return true;
                    default:
                        return false;

                }

            }
        });
        popupMenu.show();
    }

    //adds workout to workouts list or edits a workout if isSavedEdit
    public void clickSave() {
        clickEnter();
        boolean hasDuplicateName = false;

        //edit existing workouts, sorry for nested ifs had used to not have as many conditions but had to add them later to fix bugs
        if (name.length() > 0) {
            if (isSavedEdit) {
                Bundle bundle = getArguments();
                int pos = bundle.getInt("pos");
                String description = ((MainActivity) getActivity()).getWorkouts().get(pos).getDescription();

                if (wTime > 0 && numCycles > 0 && numSets > 0 && numRepeats > 0) { //set variables of existing workout
                    ((MainActivity) getActivity()).getWorkouts().set(pos,
                            new Workout(timers, name, numRepeats, endWorkoutRest)
                    );
                    ((MainActivity) getActivity()).getWorkouts().get(pos).setDescription(description);
                    ((MainActivity) getActivity()).saveWorkoutData();
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_check_black_24dp, getString(R.string.success_edit));
                } else {
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.work_interval_greater));
                }
            }

            //save new workouts
            else {
                for (Workout w : ((MainActivity) getActivity()).getWorkouts()) { //make sure no duplicate names
                    if (w.getWorkoutName().equals(name)) {
                        hasDuplicateName = true;
                        break;
                    }
                }

                if (((MainActivity) getActivity()).getWorkouts().size() > 25) {
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.max_workouts_reached));


                } else if (hasDuplicateName) {
                    ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.workout_same_name));
                } else {
                    if (wTime > 0 && numCycles > 0 && numSets > 0 && numRepeats > 0) { //set variables of new workout
                        ((MainActivity) getActivity()).getWorkouts().add(
                                new Workout(timers, name, numRepeats, endWorkoutRest)
                        );
                        ((MainActivity) getActivity()).saveWorkoutData();
                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_bookmark_border_black_24dp, getString(R.string.added_workout));
                    } else {
                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.work_interval_greater));
                    }
                }
            }
        } else {
            ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_clear_black_24dp, getString(R.string.no_name));
        }
    }

    public void clickBack() {
        getActivity().onBackPressed();
    }

    public void clickClipboard() {
        clickEnter();

        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Workout", populateSummaryString());
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(getActivity(), getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    //user can input 010 to get 0m 10s or 2030 to get 20m 30s, also accounts for m and s being in the text
    public int textToValue(EditText et) {
        String raw = et.getText().toString().trim();
        int min, sec, index = 2;

        try { //prone to crashing if user deletes only part of the text but not all and then enters
            if (raw.contains("m") || raw.contains("s")) {
                index = 5;


                min = Integer.parseInt(
                        raw.substring(0, raw.length() - index));
                sec = Integer.parseInt(
                        raw.substring(raw.length() - index + 2, raw.length() - 1));
                if (sec >= 60) {
                    min += sec / 60;
                    sec = sec % 60;
                }


            } else {
                if (raw.length() < 3) {
                    min = 0;
                    sec = Integer.parseInt(raw);
                } else {
                    min = Integer.parseInt(
                            raw.substring(0, raw.length() - index));
                    sec = Integer.parseInt(
                            raw.substring(raw.length() - index));
                }


                if (sec >= 60) {
                    min += sec / 60;
                    sec = sec % 60;
                }
            }

            et.setText(String.format(Locale.getDefault(), "%dm %02ds", min, sec));
            return (min * 60 + sec);

        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), getString(R.string.input_proper), Toast.LENGTH_SHORT).show();
        } catch (StringIndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), getString(R.string.input_proper), Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), getString(R.string.input_proper), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }

    public String textToValue(String raw) {
        int min, sec, index = 2;

        try { //prone to crashing if user deletes only part of the text but not all and then enters
            if (raw.contains("m") || raw.contains("s")) {
                index = 5;


                min = Integer.parseInt(
                        raw.substring(0, raw.length() - index));
                sec = Integer.parseInt(
                        raw.substring(raw.length() - index + 2, raw.length() - 1));
                if (sec >= 60) {
                    min += sec / 60;
                    sec = sec % 60;
                }


            } else {
                if (raw.length() < 3) {
                    min = 0;
                    sec = Integer.parseInt(raw);
                } else {
                    min = Integer.parseInt(
                            raw.substring(0, raw.length() - index));
                    sec = Integer.parseInt(
                            raw.substring(raw.length() - index));
                }


//                if (sec >= 60) {
//                    min += sec / 60;
//                    sec = sec % 60;
//                }
            }

            return String.format(Locale.getDefault(), "%dm %02ds", min, sec);

        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Input proper format", Toast.LENGTH_SHORT).show();
        } catch (StringIndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), "Input proper format", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getActivity(), "Input proper format", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    //set cache
    public void applyCachedWorkout() {
        ((MainActivity) getActivity()).getCachedWorkout().setNumCycles(numCycles);
        ((MainActivity) getActivity()).getCachedWorkout().setwTime(wTime);
        ((MainActivity) getActivity()).getCachedWorkout().setrTime(rTime);
        ((MainActivity) getActivity()).getCachedWorkout().setNumSets(numSets);
        ((MainActivity) getActivity()).getCachedWorkout().setPrepTime(prepTime);
        ((MainActivity) getActivity()).getCachedWorkout().setWorkoutName(workoutNameEditText.getText().toString());
        ((MainActivity) getActivity()).getCachedWorkout().setNumRepeats(numRepeats);
        ((MainActivity) getActivity()).getCachedWorkout().setEndWorkoutRest(endWorkoutRest);

    }

    //getters and setters
    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public boolean isSavedEdit() {
        return isSavedEdit;
    }

    public void setSavedEdit(boolean savedEdit) {
        isSavedEdit = savedEdit;
    }

    //formats time inputs while user is typing using Text Watcher and textToValue()
    private class TimeFormat {
        EditText et;
        String raw;
        private TextWatcher timeFormatter = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    raw += charSequence.toString();
                    if (raw.length() > charSequence.length()) {
                        raw = charSequence.toString().replaceAll("m", "");
                        raw = raw.replaceAll(" ", "");
                        raw = raw.replaceAll("s", "");
                        et.removeTextChangedListener(this);
                        et.setText(textToValue(raw));
                        et.addTextChangedListener(this);
                        et.setSelection(textToValue(raw).length() - 1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        public TimeFormat(EditText et) {
            this.et = et;
        }

        public TextWatcher getTimeFormatter() {
            return timeFormatter;
        }
    }




}
