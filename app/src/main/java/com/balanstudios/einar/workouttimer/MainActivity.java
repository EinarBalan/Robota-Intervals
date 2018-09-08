package com.balanstudios.einar.workouttimer;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import static com.balanstudios.einar.workouttimer.BaseApp.NOTIF_CHANNEL;

public class MainActivity extends AppCompatActivity implements DescriptionDialog.DescriptionDialogListener, QueueClearDialog.QueueDialogListener {

    //shared prefs
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String WORKOUTS = "workoutsList";
    public static final String WORKOUTS_QUEUE = "workoutsQueue";
    public static final String CURRENT_WORKOUT = "currentWorkout";
    public static final String ALERT_VOLUME = "alertVolume";
    public static final String VIBRATION_ENABLED = "vibrationEnabled";
    public static final String DARK_MODE_ENABLED = "darkModeEnabled";
    public static final String HIDE_DESCRIPTION_ENABLED = "hideDescriptionEnabled";

    private BottomNavigationView mMainNav;
    private TimerFragment2 timerFragment2;
    private ExercisesFragment exercisesFragment;
    private DashboardFragment dashboardFragment;
    private MediaPlayer mediaPlayer;
    private FragmentManager fragmentManager;
    private NotificationManagerCompat notificationManagerCompat;

    private int wTime = 20; //initial values before user changes them in input screen
    private int rTime = 10;
    private int endWorkoutRest = 0;
    private int numSets = 8;
    private int numCycles = 1;
    private int numRepeats = 1; // number of times repeating the whole workout
    private int prepTime = 5;
    private int timerIndex = 0;
    private long backPressedTime;

    private String name = "Tabata";
    private Workout currentWorkout;
    private Workout cached = new Workout();
    private Workout suggestedWorkout = new Workout();
    private ArrayList<TimerInterval> timers = new ArrayList<>();
    private ArrayList<Workout> workouts;
    private ArrayList<Workout> workoutQueue = new ArrayList<>();

    private boolean isAbsExpanded = false;
    private boolean isCardioExpanded = false;
    private boolean isUpperBodyExpanded = false;
    private boolean isLowerBodyExpanded = false;

    //settings
    private int alertVolume = 100;
    private boolean vibrationEnabled = false;
    private boolean darkModeEnabled = false;
    private boolean hideDescriptionEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadSettingsData();

        if (darkModeEnabled) {
            setTheme(R.style.WorkoutTimerDark);
        } else {
            setTheme(R.style.WorkoutTimerLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateTimers(timers, prepTime, numSets, numCycles, numRepeats, wTime, rTime);
        loadWorkoutData(WORKOUTS);

        mMainNav = findViewById(R.id.mainNav);
        timerFragment2 = new TimerFragment2();
        exercisesFragment = new ExercisesFragment();
        dashboardFragment = new DashboardFragment();

        fragmentManager = getSupportFragmentManager();
        notificationManagerCompat = NotificationManagerCompat.from(this);

        //navbar code
        mMainNav.setVisibility(View.VISIBLE);
        mMainNav.setSelectedItemId(R.id.nav_timer);
        setFragment(timerFragment2);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_timer:
                        if (mMainNav.getSelectedItemId() != R.id.nav_timer)
                            setFragment(timerFragment2);
                        return true;

                    case R.id.nav_exercise:
                        if (mMainNav.getSelectedItemId() != R.id.nav_exercise)
                            setFragment(exercisesFragment);
                        return true;

                    case R.id.nav_dash:
                        if (mMainNav.getSelectedItemId() != R.id.nav_dash)
                            setFragment(dashboardFragment);
                        return true;

                    default:
                        return false;

                }

            }
        });

        clearNotifChannelTimer(mMainNav);


    }


    @Override
    protected void onStop() {
        super.onStop();
        saveWorkoutData();
    }

    @Override
    protected void onDestroy() {
        if (timerFragment2.getIntervalCountdown() != null) {
            timerFragment2.getIntervalCountdown().cancel();
        }
        notificationManagerCompat.cancel(1);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    //use to replace current fragment with new
    public void setFragment(Fragment f) {
        if (timerFragment2.getIntervalCountdown() != null) {
            timerFragment2.getIntervalCountdown().cancel();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, f, "NAV_FRAGMENT")
                .commit();
    }

    public void setFragmentSubtle(Fragment f) {
        if (timerFragment2.getIntervalCountdown() != null) {
            timerFragment2.getIntervalCountdown().cancel();
        }
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.transition_subtle_in, R.anim.fade_out, R.anim.fade_in, R.anim.transition_subtle_out)
                .replace(R.id.mainFrame, f, "FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    public void setFragmentReturnable(Fragment f) {
        if (timerFragment2.getIntervalCountdown() != null) {
            timerFragment2.getIntervalCountdown().cancel();
        }
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_transition_in, R.anim.fade_out, R.anim.fade_in, R.anim.fragment_transition_out)
                .replace(R.id.mainFrame, f, "FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    //use if bundle wanted
    public void setFragmentReturnable(Fragment f, String key, int index) {
        if (timerFragment2.getIntervalCountdown() != null) {
            timerFragment2.getIntervalCountdown().cancel();
        }

        Bundle bundle = new Bundle();
        bundle.putInt(key, index);
        f.setArguments(bundle);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.transition_subtle_in, R.anim.fade_out, R.anim.fade_in, R.anim.transition_subtle_out)
                .replace(R.id.mainFrame, f, "FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    //prevents accidental exits by user if presses back
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                if (timerFragment2.getIntervalCountdown() != null)
                    timerFragment2.getIntervalCountdown().cancel();
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    //use to prevent null pointer exceptions
    public boolean hasText(EditText et) {
        return (et.getText().toString().trim().length() > 0);
    }

    //choose between which end sound to play
    public void playEndSound(int numSounds) {
        float logVol = (float) (Math.log(100 - alertVolume) / Math.log(100));

        switch (numSounds) {
            case 1:
                mediaPlayer = MediaPlayer.create(this, R.raw.five_second_marker);
                mediaPlayer.setVolume(1 - logVol, 1 - logVol);
                mediaPlayer.start();
                break;
            case 2:
                mediaPlayer = MediaPlayer.create(this, R.raw.interval_end);
                mediaPlayer.setVolume(1 - logVol, 1 - logVol);
                mediaPlayer.start();
                break;
            case 3:
                mediaPlayer = MediaPlayer.create(this, R.raw.timer_end);
                mediaPlayer.setVolume(1 - logVol, 1 - logVol);
                mediaPlayer.start();
                break;
            default:
                mediaPlayer = MediaPlayer.create(this, R.raw.interval_end);
                mediaPlayer.setVolume(1 - logVol, 1 - logVol);
                mediaPlayer.start();
                break;
        }
    }

    //choose between which vibration pattern to play
    public void vibrate(int numVibrations) {
        if (vibrationEnabled && ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            switch (numVibrations) {
                case 1:
                    long[] pattern01 = {0, 250};
                    v.vibrate(pattern01, -1);
                    break;
                case 2:
                    long[] pattern02 = {0, 150, 50, 150};
                    v.vibrate(pattern02, -1);

                    break;
                case 3:
                    long[] pattern03 = {0, 400, 50, 400, 50, 400};
                    v.vibrate(pattern03, -1);
                    break;
                default:
                    long[] pattern = {0, 250};
                    v.vibrate(pattern, -1);
                    break;
            }
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Vibrate permission not granted", Toast.LENGTH_SHORT);
        }
    }

    //use main activity variables to generate a list of all the timers
    public void populateTimers(ArrayList<TimerInterval> timers, int prepTime, int numSets, int numReps, int numRepeats, int wTime, int rTime) {
        timers.clear();
        if (prepTime > 0)
            timers.add(new TimerInterval(false, prepTime, 0, 0, "Prep"));
        for (int repeat = 0; repeat < numRepeats; repeat++) {
            for (int set = 0; set < numSets; set++) {
                for (int cycle = 0; cycle < numReps; cycle++) {
                    if (wTime > 0)
                        timers.add(new TimerInterval(true, wTime, rTime, cycle + 1, set + 1, repeat + 1));
                }
                if (rTime > 0)
                    timers.add(new TimerInterval(false, wTime, rTime, 0, set + 1, repeat + 1));
            }

            //remove last rest interval because it's unnecessary
            if (!timers.isEmpty()) {
                if (!(timers.get(timers.size() - 1).getIsWork()))
                    timers.remove(timers.size() - 1);
            }
            if (getCurrentWorkout() != null) {
                if (getCurrentWorkout().getEndWorkoutRest() > 0)
                    timers.add(new TimerInterval(false, wTime, getCurrentWorkout().getEndWorkoutRest(), 0, numSets, repeat + 1));
            }
        }

        if (!timers.isEmpty()) {
            if (!(timers.get(timers.size() - 1).getIsWork()))
                timers.remove(timers.size() - 1);
        }
    }

    public void populateTimers(ArrayList<TimerInterval> timers, int prepTime, int numSets, int numReps, int numRepeats, int wTime, int rTime, String workDescription) {
        timers.clear();
        if (prepTime > 0)
            timers.add(new TimerInterval(false, prepTime, 0, 0, "Prep"));
        for (int repeat = 0; repeat < numRepeats; repeat++) {
            for (int set = 0; set < numSets; set++) {
                for (int cycle = 0; cycle < numReps; cycle++) {
                    if (wTime > 0)
                        timers.add(new TimerInterval(true, wTime, cycle + 1, set + 1, workDescription));
                }
                if (rTime > 0)
                    timers.add(new TimerInterval(false, wTime, rTime, 0, set + 1, repeat + 1));
            }

            //remove last rest interval because it's unnecessary
            if (!timers.isEmpty()) {
                if (!(timers.get(timers.size() - 1).getIsWork()))
                    timers.remove(timers.size() - 1);
            }
            if (getCurrentWorkout() != null) {
                if (getCurrentWorkout().getEndWorkoutRest() > 0)
                    timers.add(new TimerInterval(false, wTime, getCurrentWorkout().getEndWorkoutRest(), 0, numSets, repeat + 1));
            }
        }

        if (!timers.isEmpty()) {
            if (!(timers.get(timers.size() - 1).getIsWork()))
                timers.remove(timers.size() - 1);
        }
    }

    //shared prefs for switches - most likely unnecessary now because of the new way I handle switches, but I'll leave it just in case
    public void saveSwitchData(String SWITCH, Switch s) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (s != null) {
            editor.putBoolean(SWITCH, s.isChecked());
            editor.apply();
        }
    }

    public boolean getSwitchData(String SWITCH) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(SWITCH, false);
    }

    //shared prefs for workout data
    public void saveWorkoutData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String jsonList = gson.toJson(workouts);
        String jsonWorkout = gson.toJson(currentWorkout);
        String jsonListQueue = gson.toJson(workoutQueue);

        editor.putString(WORKOUTS, jsonList);
        editor.putString(CURRENT_WORKOUT, jsonWorkout);
        editor.putString(WORKOUTS_QUEUE, jsonListQueue);

        editor.apply();
    }

    public void loadWorkoutData(String WORKOUTS) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();

        String jsonWorkout = sharedPreferences.getString(CURRENT_WORKOUT, null);
        Type typeWorkout = new TypeToken<Workout>() {
        }.getType();
        currentWorkout = gson.fromJson(jsonWorkout, typeWorkout);

        String jsonList = sharedPreferences.getString(WORKOUTS, null);
        Type typeList = new TypeToken<ArrayList<Workout>>() {
        }.getType();
        workouts = gson.fromJson(jsonList, typeList);

        String jsonListQueue = sharedPreferences.getString(WORKOUTS_QUEUE, null);
        Type typeQueue = new TypeToken<ArrayList<Workout>>() {
        }.getType();
        workoutQueue = gson.fromJson(jsonListQueue, typeQueue);


        if (currentWorkout == null) {
            currentWorkout = new Workout(timers, name, numRepeats, endWorkoutRest);
        }

        if (workouts == null) {
            workouts = new ArrayList<>();
        }

        if (workoutQueue == null) {
            workoutQueue = new ArrayList<>();
        }
    }

    //description handling
    public void saveDescription(String description, int pos) {
        getWorkouts().get(pos).setDescription(description);
    }

    public String loadDescription(int pos) {
        return getWorkouts().get(pos).getDescription();
    }

    //shared prefs for settings
    public void saveSettingsData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(ALERT_VOLUME, alertVolume);
        editor.putBoolean(VIBRATION_ENABLED, vibrationEnabled);
        editor.putBoolean(DARK_MODE_ENABLED, darkModeEnabled);
        editor.putBoolean(HIDE_DESCRIPTION_ENABLED, hideDescriptionEnabled);
        editor.apply();
    }

    public void loadSettingsData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        alertVolume = sharedPreferences.getInt(ALERT_VOLUME, 100);
        vibrationEnabled = sharedPreferences.getBoolean(VIBRATION_ENABLED, false);
        darkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_ENABLED, false);
        hideDescriptionEnabled = sharedPreferences.getBoolean(HIDE_DESCRIPTION_ENABLED, false);

    }

    //notification showing timer handling
    public void sendNotifChannelTimer(View v, String title, String text, String subtext, boolean isRunning) {
        Notification notification;
        if (isRunning) {
            notification = new NotificationCompat.Builder(this, NOTIF_CHANNEL)
                    .setSmallIcon(R.drawable.work_timer)
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryLight, null))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSubText(subtext)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }

        //pause case here

    }

    public void clearNotifChannelTimer(View v) {
        notificationManagerCompat.cancel(1);
    }

    //needed to programmatically set colors to theme colors, not my code by the way I ripped it off stack overflow lol
    public int getAttributeColor(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        int colorRes = typedValue.resourceId;
        int color = -1;
        try {
            color = ResourcesCompat.getColor(getResources(), colorRes, null);
        } catch (Resources.NotFoundException e) {
            Log.w("check", "Not found color resource by id: " + colorRes);
        }
        return color;
    }

    //prevent loss of current workout if user doesn't input anything in add screen
    public void restoreCache() {
        if (getCachedWorkout().getwTime() > 0) {
            if (!(getCurrentWorkout().equals(getCachedWorkout()))) {
                Workout cachedWorkout = getCachedWorkout();
                setCurrentWorkout(cachedWorkout);
            }
        }
    }

    public void showLayoutToast(int drawable, String text) {
        View toastLayout = getLayoutInflater().inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toastRoot));
        ImageView toastImage = toastLayout.findViewById(R.id.toastImage);
        TextView toastText = toastLayout.findViewById(R.id.toastText);

        toastImage.setImageDrawable(getResources().getDrawable(drawable, null));
        toastText.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    //getters and setters, use these get strings for input screen
    public int getwTime() {
        return currentWorkout.getwTime();
    }

    public void setwTime(int wTime) {
        this.wTime = wTime;
        currentWorkout.setwTime(wTime);

    }

    public String getwTimeString() {
        int min = getwTime() / 60;
        int sec = getwTime() % 60;
        return String.format(Locale.getDefault(), "%dm %02ds", min, sec);
    }

    public int getrTime() {
        return currentWorkout.getrTime();
    }

    public void setrTime(int rTime) {
        this.rTime = rTime;
        currentWorkout.setrTime(rTime);
    }

    public String getrTimeString() {
        int min = getrTime() / 60;
        int sec = getrTime() % 60;
        return String.format(Locale.getDefault(), "%dm %02ds", min, sec);
    }

    public int getTimeTotal() {
        if (currentWorkout == null)
            return (((wTime * numCycles) + rTime) * numSets - rTime + endWorkoutRest) * numRepeats + prepTime - endWorkoutRest;
        return currentWorkout.getTimeTotal();
    }

    public int getNumSets() {
        return currentWorkout.getNumSets();
    }

    public void setNumSets(int numSets) {
        this.numSets = numSets;
        currentWorkout.setNumSets(numSets);
    }

    public int getNumCycles() {
        return currentWorkout.getNumCycles();
    }

    public void setNumCycles(int numCycles) {
        this.numCycles = numCycles;
        currentWorkout.setNumCycles(numCycles);
    }

    public int getPrepTime() {
        return currentWorkout.getPrepTime();
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
        currentWorkout.setPrepTime(prepTime);
    }

    public String getPrepTimeString() {
        int min = getPrepTime() / 60;
        int sec = getPrepTime() % 60;
        return String.format(Locale.getDefault(), "%dm %02ds", min, sec);
    }

    public int getEndWorkoutRest() {
        return currentWorkout.getEndWorkoutRest();
    }

    public void setEndWorkoutRest(int endWorkoutRest) {
        this.endWorkoutRest = endWorkoutRest;
        currentWorkout.setEndWorkoutRest(endWorkoutRest);
    }

    public String getEndWorkoutRestString() {
        int min = getEndWorkoutRest() / 60;
        int sec = getEndWorkoutRest() % 60;
        return String.format(Locale.getDefault(), "%dm %02ds", min, sec);
    }

    public int getNumRepeats() {
        return currentWorkout.getNumRepeats();
    }

    public void setNumRepeats(int numRepeats) {
        this.numRepeats = numRepeats;
        currentWorkout.setNumRepeats(numRepeats);
    }

    public int getTimerIndex() {
        return timerIndex;
    }

    public void setTimerIndex(int timerIndex) {
        this.timerIndex = timerIndex;
    }

    public void incrementTimerIndex() {
        timerIndex++;
    }

    public String getWorkoutName() {
        return currentWorkout.getWorkoutName();
    }

    public void setWorkoutName(String name) {
        this.name = name;
        currentWorkout.setWorkoutName(name);
    }

    public Workout getCurrentWorkout() {
        return currentWorkout;
    }

    public void setCurrentWorkout(Workout currentWorkout) {
        this.currentWorkout.setWorkoutName(currentWorkout.getWorkoutName());
        this.currentWorkout.setEndWorkoutRest(currentWorkout.getEndWorkoutRest());
        this.currentWorkout.setNumRepeats(currentWorkout.getNumRepeats());
        this.currentWorkout.setNumCycles(currentWorkout.getNumCycles());
        this.currentWorkout.setNumSets(currentWorkout.getNumSets());
        this.currentWorkout.setPrepTime(currentWorkout.getPrepTime());
        this.currentWorkout.setwTime(currentWorkout.getwTime());
        this.currentWorkout.setrTime(currentWorkout.getrTime());
        this.currentWorkout.setDescription(currentWorkout.getDescription());
        this.currentWorkout.setTimers(currentWorkout.getTimers());
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    public BottomNavigationView getmMainNav() {
        return mMainNav;
    }

    public Workout getCachedWorkout() {
        return cached;
    }

    public Workout getSuggestedWorkout() {
        return suggestedWorkout;
    }

    public void setSuggestedWorkout(Workout suggestedWorkout) {
        this.suggestedWorkout.setWorkoutName(suggestedWorkout.getWorkoutName());
        this.suggestedWorkout.setEndWorkoutRest(suggestedWorkout.getEndWorkoutRest());
        this.suggestedWorkout.setNumRepeats(suggestedWorkout.getNumRepeats());
        this.suggestedWorkout.setNumCycles(suggestedWorkout.getNumCycles());
        this.suggestedWorkout.setNumSets(suggestedWorkout.getNumSets());
        this.suggestedWorkout.setPrepTime(suggestedWorkout.getPrepTime());
        this.suggestedWorkout.setwTime(suggestedWorkout.getwTime());
        this.suggestedWorkout.setrTime(suggestedWorkout.getrTime());
        this.suggestedWorkout.setDescription(suggestedWorkout.getDescription());
        this.suggestedWorkout.setTimers(suggestedWorkout.getTimers());
    }

    public TimerFragment2 getTimerFragment2() {
        return timerFragment2;
    }

    public int getAlertVolume() {
        return alertVolume;
    }

    public void setAlertVolume(int alertVolume) {
        this.alertVolume = alertVolume;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }

    public ArrayList<Workout> getWorkoutQueue() {
        return workoutQueue;
    }

    public void setWorkoutQueue(ArrayList<Workout> workoutQueue) {
        this.workoutQueue = workoutQueue;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int isAbsExpanded() {
        if (isAbsExpanded)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    public void setAbsExpanded(boolean absExpanded) {
        isAbsExpanded = absExpanded;
    }

    public int isCardioExpanded() {
        if (isCardioExpanded)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    public void setCardioExpanded(boolean cardioExpanded) {
        isCardioExpanded = cardioExpanded;
    }

    public int isUpperBodyExpanded() {
        if (isUpperBodyExpanded)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    public void setUpperBodyExpanded(boolean upperBodyExpanded) {
        isUpperBodyExpanded = upperBodyExpanded;
    }

    public int isLowerBodyExpanded() {
        if (isLowerBodyExpanded)
            return View.VISIBLE;
        else
            return View.GONE;
    }

    public void setLowerBodyExpanded(boolean lowerBodyExpanded) {
        isLowerBodyExpanded = lowerBodyExpanded;
    }

    public boolean isHideDescriptionEnabled() {
        return hideDescriptionEnabled;
    }

    public void setHideDescriptionEnabled(boolean hideDescriptionEnabled) {
        this.hideDescriptionEnabled = hideDescriptionEnabled;
    }

}

