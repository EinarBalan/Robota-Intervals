package com.balanstudios.einar.workouttimer;


import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment2 extends Fragment {
    private static boolean timerRunning = false;
    private static CountDownTimer intervalCountDownTimer;

    Fragment timerFragment;
    Drawable toggleTimer;

    private TextView timerTextView;
    private TextView totalTimeView;
    private TextView totalTimeWords;
    private TextView descriptionTextView;
    private TextView setTextView;
    private TextView repTextView;
    private TextView workoutNameTextView;
    private TextView repeatsNumberTextView;
    private TextView queueTextView;

    private LinearLayout summaryLayout;
    private FrameLayout startButtonFrame;
    private FloatingActionButton toggleTimerButton;

    private Button resetTimerButton;
    private Button doneButton;

    private String displayTime;

    private ProgressBar totalTimeProgress;
    private ProgressBar intervalTimeProgress;

    private ArrayList<TimerInterval> timers = new ArrayList<>();
    private long timeStart;
    private long timeLeft;
    private int wTime;
    private int rTime;
    private int numCycles;
    private int numSets;
    private int numRepeats;
    private int totalTime;
    private int timeElapsed;

    //private int timerIndex; added to mainactivity instance variables
    private int prepTime;
    private boolean timerStarted = false;

    //button click handler
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toggleTimerActionButton:
                    if (timerRunning) {
                        pauseTimer();

                        //buttons fade in
                        if (resetTimerButton.getVisibility() != View.VISIBLE) {
                            resetTimerButton.setVisibility(View.VISIBLE);
                            resetTimerButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
                        }
                        if (doneButton.getVisibility() != View.VISIBLE) {
                            doneButton.setVisibility(View.VISIBLE);
                            doneButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
                        }

                        //start button icon animation
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            toggleTimer = getResources().getDrawable(R.drawable.pause_to_play, null);
                            toggleTimerButton.setImageDrawable(toggleTimer);
                            ((AnimatedVectorDrawable) toggleTimer).start();
                        } else {
                            toggleTimer = getResources().getDrawable(R.drawable.ic_play, null);
                            toggleTimerButton.setImageDrawable(toggleTimer);
                        }
                    } else {
                        startTimer();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            toggleTimer = getResources().getDrawable(R.drawable.play_to_pause, null);
                            toggleTimerButton.setImageDrawable(toggleTimer);
                            ((AnimatedVectorDrawable) toggleTimer).start();
                        } else {
                            toggleTimer = getResources().getDrawable(R.drawable.ic_pause, null);
                            toggleTimerButton.setImageDrawable(toggleTimer);
                        }
                    }
                    break;
                case R.id.resetTimerButton:
                    resetTimer();
                    break;
                case R.id.doneButton:
                    if (timerStarted) {
                        Toast.makeText(getContext(), getResources().getString(R.string.reset_or_finish), Toast.LENGTH_SHORT).show();
                    } else {
                        ((MainActivity) getActivity()).clearNotifChannelTimer(view);
                        if (intervalCountDownTimer != null)
                            intervalCountDownTimer.cancel();

                        ((MainActivity) getActivity()).getCachedWorkout().setDescription(((MainActivity) getActivity()).getCurrentWorkout().getDescription());
                        ((MainActivity) getActivity()).getCachedWorkout().setWorkoutName(((MainActivity) getActivity()).getCurrentWorkout().getWorkoutName());
                        ((MainActivity) getActivity()).getCachedWorkout().setwTime(((MainActivity) getActivity()).getCurrentWorkout().getwTime());
                        ((MainActivity) getActivity()).getCachedWorkout().setrTime(((MainActivity) getActivity()).getCurrentWorkout().getrTime());
                        ((MainActivity) getActivity()).getCachedWorkout().setPrepTime(((MainActivity) getActivity()).getCurrentWorkout().getPrepTime());
                        ((MainActivity) getActivity()).getCachedWorkout().setNumSets(((MainActivity) getActivity()).getCurrentWorkout().getNumSets());
                        ((MainActivity) getActivity()).getCachedWorkout().setNumCycles(((MainActivity) getActivity()).getCurrentWorkout().getNumCycles());
                        ((MainActivity) getActivity()).getCachedWorkout().setNumRepeats(((MainActivity) getActivity()).getCurrentWorkout().getNumRepeats());
                        ((MainActivity) getActivity()).getCachedWorkout().setEndWorkoutRest(((MainActivity) getActivity()).getCurrentWorkout().getEndWorkoutRest());

                        ((MainActivity) getActivity()).setFragmentSubtle(timerFragment);
                    }
                    break;
            }
        }
    };


    public TimerFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timer_fragment2, container, false);

        ((MainActivity) getActivity()).restoreCache();
        timerStarted = false;

        timerFragment = new TimerFragment();

        timerTextView = v.findViewById(R.id.timerTextView);
        totalTimeView = v.findViewById(R.id.totalTimeText);
        totalTimeWords = v.findViewById(R.id.totalTimeWords);
        descriptionTextView = v.findViewById(R.id.descriptionTextView);
        setTextView = v.findViewById(R.id.setTextView);
        repTextView = v.findViewById(R.id.repTextView);
        workoutNameTextView = v.findViewById(R.id.nameTextView);
        repeatsNumberTextView = v.findViewById(R.id.repeatsNumberTextView);
        queueTextView = v.findViewById(R.id.queueTextView);

        summaryLayout = v.findViewById(R.id.summaryLayout);
        startButtonFrame = v.findViewById(R.id.startButtonFrame);

        toggleTimerButton = v.findViewById(R.id.toggleTimerActionButton);
        toggleTimerButton.setOnClickListener(onClickListener);
        resetTimerButton = v.findViewById(R.id.resetTimerButton);
        resetTimerButton.setOnClickListener(onClickListener);
        doneButton = v.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(onClickListener);

        totalTimeProgress = v.findViewById(R.id.totalTimeProgress);
        intervalTimeProgress = v.findViewById(R.id.intervalTimeProgress);
        intervalTimeProgress.setVisibility(View.INVISIBLE); //so that bug where progress isn't reset can't be seen


        initializeVariables();
        populateTimers();

        ((MainActivity) getActivity()).getCachedWorkout().clear();

        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);

        populateSummary();
        initializeDisplay();

        return v;
    }

    //handles timer tick and iterating through timers list, also progress bars and sounds/vibrations
    private void startTimer() {

        timerStarted = true;
        if (((MainActivity) getActivity()).getmMainNav().getVisibility() == View.VISIBLE) {
            ((MainActivity) getActivity()).getmMainNav().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);
        doneButton.getBackground().setAlpha(128);

        intervalCountDownTimer = new CountDownTimer(timeLeft, 50) {
            @Override
            public void onTick(long millisLeftUntilFinished) {
                timeLeft = millisLeftUntilFinished;
                timeElapsed += 50;
                totalTimeProgress.setProgress(timeElapsed);
                intervalTimeProgress.setProgress((int) (timeStart + 50 - timeLeft));

                if (timeLeft >= 6000 && timeLeft <= 6055) {
                    ((MainActivity) getActivity()).playEndSound(1);
                    ((MainActivity) getActivity()).vibrate(1);
                }

                if (timeLeft <= 1040) {
                    onFinish();
                }

                updateDisplayTime();
                if (intervalTimeProgress.getVisibility() == View.INVISIBLE) {
                    intervalTimeProgress.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinish() {
                if (((MainActivity) getActivity()).getTimerIndex() != timers.size() - 1) { //if not final timer
                    ((MainActivity) getActivity()).playEndSound(2);
                    ((MainActivity) getActivity()).vibrate(2);
                    ((MainActivity) getActivity()).incrementTimerIndex();
                    setTimeStart();
                    timeLeft = timeStart;
                    pauseTimer();
                    startTimer();
                    showCurrentInterval();
                    intervalTimeProgress.setMax((int) timers.get(((MainActivity) getActivity()).getTimerIndex()).getMillis()); //this is the worst line of code I've ever written I hate it
                    intervalTimeProgress.setProgress(0);

                    //update info on UI
                    descriptionTextView.setText(String.format(Locale.getDefault(), "%s %s", timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription(), getString(R.string.interval)));
                    setTextView.setText(String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfSet(), numSets));

                    if (numRepeats > 1) {
                        repeatsNumberTextView.setText(String.format(Locale.getDefault(), "%s %d / %d", getString(R.string.workout), timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfRepeat(), numRepeats));
                    }

                    if (timers.get(((MainActivity) getActivity()).getTimerIndex()).getIsWork()) {
                        repTextView.setVisibility(View.VISIBLE);
                        repTextView.setText(String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfCycle(), numCycles));
                    } else
                        repTextView.setText(String.format(Locale.getDefault(), "%s", timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription()));

                } else { //if final timer
                    timerStarted = false;
                    ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);
                    doneButton.getBackground().setAlpha(255);

                    ((MainActivity) getActivity()).clearNotifChannelTimer(timerTextView);
                    timeLeft = 0;
                    totalTimeProgress.setProgress(0);
                    intervalTimeProgress.setProgress(0);
                    ((MainActivity) getActivity()).playEndSound(3);
                    ((MainActivity) getActivity()).vibrate(3);
                    pauseTimer();

                    if (resetTimerButton.getVisibility() != View.VISIBLE) {
                        resetTimerButton.setVisibility(View.VISIBLE);
                        resetTimerButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
                    }
                    if (doneButton.getVisibility() != View.VISIBLE) {
                        doneButton.setVisibility(View.VISIBLE);
                        doneButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
                    }
                    startButtonFrame.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_button));
                    startButtonFrame.setVisibility(View.INVISIBLE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //re-enable screen timeout


                    if (!(((MainActivity) getActivity()).getWorkoutQueue().isEmpty())) {
                        ((MainActivity) getActivity()).showLayoutToast(R.drawable.ic_fast_forward_black_24dp, getString(R.string.next_workout));
                        ((MainActivity) getActivity()).setCurrentWorkout(((MainActivity) getActivity()).getWorkoutQueue().remove(0));
                        initializeVariables();
                        populateTimers();
                        initializeDisplay();
                        populateSummary();
                        ((MainActivity) getActivity()).saveWorkoutData();
                        resetTimer();
                    }
                }
            }

        }.start();
        timerRunning = true;
        if (resetTimerButton.getVisibility() == View.VISIBLE) {
            resetTimerButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_button));
            resetTimerButton.setVisibility(View.INVISIBLE);
        }
        if (doneButton.getVisibility() == View.VISIBLE) {
            doneButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_button));
            doneButton.setVisibility(View.INVISIBLE);
        }
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //disable screen timeout

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getmMainNav().setSelectedItemId(R.id.nav_timer);
    }

    private void pauseTimer() {
        intervalCountDownTimer.cancel();
        timerRunning = false;

        ((MainActivity) getActivity()).sendNotifChannelTimer(
                timerTextView, timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription(),
                displayTime,
                String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfSet(), numSets),
                false);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //re-enable screen timeout
    }

    private void resetTimer() {
        if (resetTimerButton.getVisibility() != View.VISIBLE) {
            resetTimerButton.setVisibility(View.VISIBLE);
            resetTimerButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
        }
        if (doneButton.getVisibility() != View.VISIBLE) {
            doneButton.setVisibility(View.VISIBLE);
            doneButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
        }
        if (startButtonFrame.getVisibility() != View.VISIBLE) {
            startButtonFrame.setVisibility(View.VISIBLE);
            startButtonFrame.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_button));
        }

        ((MainActivity) getActivity()).setTimerIndex(0);
        initializeDisplay();
        timeElapsed = 0;

        for (int i = 0; i < summaryLayout.getChildCount(); i++) { //resets highlighted timer in summary
            TextView tv = (TextView) summaryLayout.getChildAt(i);
            tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        showCurrentInterval();

        if (intervalCountDownTimer != null) {
            intervalCountDownTimer.cancel();
        }
        timerStarted = false;
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);
        doneButton.getBackground().setAlpha(255);

        ((MainActivity) getActivity()).clearNotifChannelTimer(timerTextView);
    }

    private void updateDisplayTime() {
        int min = (int) ((timeLeft / 1000) / 60);
        int sec = (int) ((timeLeft / 1000) % 60);

        displayTime = String.format(Locale.getDefault(), "%d:%02d", min, sec);
        timerTextView.setText(displayTime);

        if (timerStarted) { //changed from timerRunning to timerStarted but could cause an issue, not sure yet
            ((MainActivity) getActivity()).sendNotifChannelTimer(
                    timerTextView,
                    timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription(),
                    displayTime,
                    String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfSet(), numSets),
                    timerRunning);
        }
        if (((MainActivity) getActivity()).getTimerIndex() == timers.size() - 1 && timeLeft < 1) {
            ((MainActivity) getActivity()).clearNotifChannelTimer(timerTextView);
        }
    }

    private void setTimeStart() {
        timeStart = timers.get(((MainActivity) getActivity()).getTimerIndex()).getMillis() + 950; // +950 so timer doesn't look weird starting one second earlier than it should
    }

    //highlights current interval in summary, can't figure out how to do it with repeats present
    private void showCurrentInterval() {
        if (numRepeats < 2) {
            int index = ((MainActivity) getActivity()).getTimerIndex() + timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfSet();

            if (summaryLayout.getChildAt(index) != null) {
                TextView current = (TextView) summaryLayout.getChildAt(index);
                current.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                for (int pastIntervals = 0; pastIntervals < index; pastIntervals++) {
                    TextView past = (TextView) summaryLayout.getChildAt(pastIntervals);
                    past.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        }
    }

    private void populateTimers() {
        ((MainActivity) getActivity()).populateTimers(timers, prepTime, numSets, numCycles, numRepeats, wTime, rTime);
    }

    //adds timers to summary
    private void populateSummary() {
        int repeatNum = 0;
        int setNum = 0;

        summaryLayout.removeAllViews(); //prevent exponential growth when pressing enter

        //don't want total time to show up in summary if time other than prep = 0
        if (totalTime > prepTime) {
            totalTimeView.setVisibility(View.VISIBLE);
            totalTimeWords.setVisibility(View.VISIBLE);
        }

        for (TimerInterval ti : timers) {
            if (numRepeats > 1) {
                if (ti.getNumOfRepeat() != repeatNum) {
                    repeatNum = ti.getNumOfRepeat();
                    TextView textView = new TextView(getActivity());
                    textView.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.workout), ti.getNumOfRepeat()));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    textView.setTextColor(((MainActivity) getActivity()).getAttributeColor(getActivity(), R.attr.highlightTextColor));
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    summaryLayout.addView(textView);
                }
            }
            if (ti.getNumOfSet() != setNum) {
                setNum = ti.getNumOfSet();
                TextView textView = new TextView(getActivity());
                textView.setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.set), ti.getNumOfSet()));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                textView.setTextColor(((MainActivity) getActivity()).getAttributeColor(getActivity(), R.attr.highlightTextColor));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                summaryLayout.addView(textView);

            }

            TextView textView = new TextView(getActivity());
            textView.setText(ti.toString());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextColor(((MainActivity) getActivity()).getAttributeColor(getActivity(), R.attr.textColor));
            summaryLayout.addView(textView);
        }
    }

    private void initializeDisplay() {
        if (timers.size() > 0) {
            if (prepTime > 0)
                repTextView.setText(String.format(Locale.getDefault(), "%s", timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription()));
            else
                repTextView.setText(String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfCycle(), numCycles));

            if (numRepeats > 1) {
                repeatsNumberTextView.setText(String.format(Locale.getDefault(), "%s %d / %d", getString(R.string.workout), timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfRepeat(), numRepeats));
            } else
                repeatsNumberTextView.setText("");

            if (!(((MainActivity) getActivity()).getWorkoutQueue().isEmpty())) {
                queueTextView.setText(String.format(Locale.getDefault(), "%s: %s",
                        getString(R.string.next_in_queue),
                        ((MainActivity) getActivity()).getWorkoutQueue().get(0).getWorkoutName())
                );
            } else {
                queueTextView.setText("");
            }

            descriptionTextView.setText(String.format(Locale.getDefault(), "%s %s", timers.get(((MainActivity) getActivity()).getTimerIndex()).getDescription(), getString(R.string.interval)));
            setTextView.setText(String.format(Locale.getDefault(), "%d / %d", timers.get(((MainActivity) getActivity()).getTimerIndex()).getNumOfSet(), numSets));
            workoutNameTextView.setText(((MainActivity) getActivity()).getWorkoutName());
            totalTimeView.setText(String.format(Locale.getDefault(), "%d:%02d", (totalTime / 60), (totalTime % 60)));
            setTimeStart();
            timeLeft = timeStart;

            toggleTimer = getResources().getDrawable(R.drawable.play_to_pause, null);
            toggleTimerButton.setImageDrawable(toggleTimer);

            showCurrentInterval();
            updateDisplayTime();

            clearProgressBars();
            totalTimeProgress.setMax(totalTime * 1000);
            intervalTimeProgress.setMax((int) timers.get(0).getMillis());
            clearProgressBars();

        }
    }

    public void clearProgressBars() {
        totalTimeProgress.setProgress(0);
        intervalTimeProgress.setProgress(0);
    }

    private void initializeVariables() {
        //bring over variables from MainActivity
        wTime = ((MainActivity) getActivity()).getCurrentWorkout().getwTime();
        rTime = ((MainActivity) getActivity()).getCurrentWorkout().getrTime();
        numCycles = ((MainActivity) getActivity()).getCurrentWorkout().getNumCycles();
        numSets = ((MainActivity) getActivity()).getCurrentWorkout().getNumSets();     //number of sets in the workout
        numRepeats = ((MainActivity) getActivity()).getCurrentWorkout().getNumRepeats();
        totalTime = ((MainActivity) getActivity()).getCurrentWorkout().getTimeTotal();
        prepTime = ((MainActivity) getActivity()).getCurrentWorkout().getPrepTime();
        ((MainActivity) getActivity()).setTimerIndex(0);
        timeElapsed = 0;
        timerRunning = false;
    }

    //getter and setters
    public CountDownTimer getIntervalCountdown() {
        return intervalCountDownTimer;
    }

}
