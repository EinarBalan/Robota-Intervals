package com.balanstudios.einar.workouttimer;


import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private Fragment suggestedWorkoutFragment = new SuggestedWorkoutFragment();

    private Workout suggestedWorkout;
    private ArrayList<TimerInterval> timers = new ArrayList<>();

    private ImageButton moreButton;

    //abs group
    private TextView absTextView;
    private CardView absCardView;
    private TextView crunchesTextView;
    private TextView plankTextView;
    private TextView lefLiftsTextView;
    private TextView absCircuit01TextView;
    private TextView absCircuit02TextView;


    //cardio group
    private TextView cardioTextView;
    private CardView cardioCardView;
    private TextView runningTextView;
    private TextView hikingTextView;
    private TextView bikingTextView;
    private TextView swimmingTextView;
    private TextView rowingTextView;


    //upper body group
    private TextView upperBodyTextView;
    private CardView upperBodyCardView;
    private TextView bicepCurlsTextView;
    private TextView tricepExtensionsTextView;
    private TextView benchPressTextView;
    private TextView pushUpsTextView;
    private TextView pullUpsTextView;
    private TextView backExtensionsTextView;


    //lower body group
    private TextView lowerBodyTextView;
    private CardView lowerBodyCardView;
    private TextView squatsTextView;
    private TextView lungesTextView;
    private TextView legPressTextView;
    private TextView legExtensionsTextView;
    private TextView hamstringCurlsTextView;


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //this code is gross but necessary
            switch (view.getId()) {
                case R.id.moreButton:
                    PopupMenu popupMenu = new PopupMenu(getActivity(), moreButton);
                    popupMenu.getMenuInflater().inflate(R.menu.dashboard_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.settingsMenuItem:
                                    ((MainActivity) getActivity()).setFragmentReturnable(new SettingsFragment());
                                    return true;
                                case R.id.tutorialMenuItem:
                                    ((MainActivity) getActivity()).setFragmentReturnable(new TutorialFragment());
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                    break;

                //abs group
                case R.id.absTextView:
                    if (absCardView.getVisibility() == View.VISIBLE) {
                        ((MainActivity) getActivity()).setAbsExpanded(false);
                        absCardView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_collapse, null);
                            absTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_down, null);
                            absTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    } else {
                        ((MainActivity) getActivity()).setAbsExpanded(true);
                        absCardView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_expand, null);
                            absTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_up, null);
                            absTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    }
                    break;
                case R.id.crunchesTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "Crunches");
                    setSuggested("Crunches", "Lay on the floor facing up and slowly move your upper body towards your legs. \n\nAny variation can be done including Sit Ups, Bicycle Crunches, or 90/90 Crunches. Work for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.plankTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 3, 1, 1, 120, 30, "Plank");
                    setSuggested("Plank", "Lay on the floor facing down and rest on your forearms, keeping your core raised. \n\nAny variation can be done including oblique planks or extended planks. Work for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.legLiftsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 60, 30, "Leg Lifts");
                    setSuggested("Leg Lifts", "Lay on the floor facing up and keep your legs raised for the duration of the work interval. \n\nAny variation can be done including reverse crunches, individual leg lifts, or leg lift twists.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.absCircuit01TextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 2, 5, 1, 30, 0);
                    setSuggested("Abs Circuit 1", "A challenging circuit for beginners. Consists of five ab workouts, including Bicycle Crunches, Russian Twists, 90/90 Crunches, Plank, and Mountain Climbers. \n\nThese should be done sequentially with no rest for best results.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.absCircuit02TextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 2, 3, 1, 60, 0);
                    setSuggested("Abs Circuit 2", "Another challenging circuit for beginners. Consists of three ab workouts, including Mountain Climbers, Plank, and 90/90 Crunches. \n\nThese should be done sequentially with no rest for best results.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;

                //cardio group
                case R.id.cardioTextView:
                    if (cardioCardView.getVisibility() == View.VISIBLE) {
                        ((MainActivity) getActivity()).setCardioExpanded(false);
                        cardioCardView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_collapse, null);
                            cardioTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_down, null);
                            cardioTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    } else {
                        ((MainActivity) getActivity()).setCardioExpanded(true);
                        cardioCardView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_expand, null);
                            cardioTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_up, null);
                            cardioTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    }
                    break;
                case R.id.runningTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 300, 30, "Running");
                    setSuggested("Running", "The classic workout. \n\nIntensity can be changed by changing speed during the work intervals. Work for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.hikingTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 0, 3, 1, 1, 600, 60, "Hiking");
                    setSuggested("Hiking", "Go on a scenic walk or just a walk around your neighborhood. It's up to you. \n\nChange intensity by changing walking speed.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.bikingTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 300, 30, "Biking");
                    setSuggested("Biking", "Can be done on either a traditional bike or a stationary bike. \n\nIntensity can be changed by changing speed during the work intervals. Work for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.swimmingTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 10, 1, 10, 1, 60, 0, "50m. Swim");
                    setSuggested("Swimming", "Can be an extremely challenging workout if intervals are followed with a high intensity. Swim 50 meters (two lengths) and rest if completed before end of interval. Any stroke can be done, but designed with freestyle in mind. \n\nWorkout intensity can be changed by changing speed during the work intervals. Make sure not to drop your phone in the pool!");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.rowingTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 8, 1, 1, 20, 10, "Rowing");
                    setSuggested("Rowing", "Requires the use of a rowing machine. \n\nIntensity can be changed by changing rowing speed. Work for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;

                //upper body group
                case R.id.upperBodyTextView:
                    if (upperBodyCardView.getVisibility() == View.VISIBLE) {
                        ((MainActivity) getActivity()).setUpperBodyExpanded(false);
                        upperBodyCardView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_collapse, null);
                            upperBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_down, null);
                            upperBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    } else {
                        ((MainActivity) getActivity()).setUpperBodyExpanded(true);
                        upperBodyCardView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_expand, null);
                            upperBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_up, null);
                            upperBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    }
                    break;
                case R.id.bicepCurlsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "10 Bicep Curls");
                    setSuggested("Bicep Curls", "Requires the use of weights. Be careful not to set the weight too high because it could lead to serious injury. Do 5-10 curls per arm and rest if finished before work interval is over. \n\nIntensity can be changed by changing amount of weight or number of curls per arm. ");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.tricepExtensionsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "10 Tricep Extensions");
                    setSuggested("Tricep Extensions", "Requires the use of weights. Be careful not to set the weight too high because it could lead to serious injury. Do 5-10 extensions per arm. \n\nIntensity can be changed by changing amount of weight or number of extensions per arm.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.benchPressTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "5 Presses");
                    setSuggested("Bench Press", "Requires the use of a bench press, weights, and a spotter. Be careful not to set the weight too high because it could lead to serious injury. Do 5-10 reps per set and rest if finished before work interval is over. \n\nIntensity can be changed by changing amount of weight.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.pushUpsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 3, 1, 1, 35, 60, "20 Push Ups");
                    setSuggested("Push Ups", "Push up against the ground and keep chest off the floor. Do 20-30 pushups per set and rest if finished before end of work interval. \n\nIntensity can be changed by changing number of pushups. Any variation can be done including wide grip, clap, or knee push ups.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.pullUpsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 3, 1, 1, 45, 90, "10 Pull Ups");
                    setSuggested("Pull Ups", "Can be very challenging for beginners. Requires the use of a pull up bar. Do 5-15 pull ups per set and rest if finished before end of work interval. \n\nIntensity can be changed by changing number of pullups or by changing grip. Any variation can be done including chin ups, wide grip, or muscle ups.**WARNING** Don't do this if you have never done pull ups or haven't done them in a while. Chances are you won't be able to finish or you'll be extremely sore for the next few days.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.backExtensionsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "15 Back Extensions");
                    setSuggested("Back Extensions", " Lay on the floor facing down and put your hands into a thumbs up position. Move your arms from in front of you to as far back as possible while simultaneously lifting your legs up using your hips. Do 10-15 extensions per set and rest if finished before end of work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;

                //lower body group
                case R.id.lowerBodyTextView:
                    if (lowerBodyCardView.getVisibility() == View.VISIBLE) {
                        ((MainActivity) getActivity()).setLowerBodyExpanded(false);
                        lowerBodyCardView.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_collapse, null);
                            lowerBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_down, null);
                            lowerBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    } else {
                        ((MainActivity) getActivity()).setLowerBodyExpanded(true);
                        lowerBodyCardView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.arrow_animated_expand, null);
                            lowerBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                            ((AnimatedVectorDrawable) arrowAnimated).start();
                        } else {
                            Drawable arrowAnimated = getResources().getDrawable(R.drawable.ic_up, null);
                            lowerBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowAnimated, null);
                        }
                    }
                    break;
                case R.id.squatsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 8, 1, 1, 20, 10, "Squats");
                    setSuggested("Squats", "Plant your feet shoulder width apart and extend your hands out in front of you as you squat. \n\nWork for the whole work interval.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.lungesTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 8, 1, 1, 25, 10, "Lunges");
                    setSuggested("Lunges", "Keep your upper body relaxed and look forward as you take a step forward. Lower your hips until your knees are at a 90 degree angle. Return to your initial position and repeat with your opposite leg. \n\nWork for the whole work interval. ");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.legPressTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "10 Presses");
                    setSuggested("Leg Press", "Requires a leg press machine. Be careful not to set the weight too high because it could lead to serious injury. Do 5-10 presses per set and rest if finished before work interval is over. \n\nIntensity can be changed by increasing amount of weight, but speed should be kept constant.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.legExtensionsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "15 Leg Extensions");
                    setSuggested("Leg Extensions", "Requires a leg extension machine. Put your legs under the bar and lift up for each extension. Be careful not to set the weight too high because it could lead to serious injury. Do 15-20 extensions per set and rest if finished before work interval is over. \n\nIntensity can be changed by increasing amount of weight, but speed should be kept constant. ");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
                case R.id.hamstringCurlsTextView:
                    ((MainActivity) getActivity()).populateTimers(timers, 5, 5, 1, 1, 30, 30, "15 Hamstring Curls");
                    setSuggested("Hamstring Curls", "Requires a hamstring curl machine. Put your legs over the bar and push down for each curl. Be careful not to set the weight too high because it could lead to serious injury. Do 15-20 curls per set and rest if finished before work interval is over. \n\nIntensity can be changed by increasing amount of weight, but speed should be kept constant.");
                    ((MainActivity) getActivity()).setSuggestedWorkout(suggestedWorkout);
                    ((MainActivity) getActivity()).setFragmentReturnable(suggestedWorkoutFragment);
                    break;
            }
        }
    };

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        moreButton = v.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(onClickListener);

        //abs group
        absTextView = v.findViewById(R.id.absTextView);
        absTextView.setOnClickListener(onClickListener);
        absCardView = v.findViewById(R.id.absCardView);
        absCardView.setVisibility(((MainActivity) getActivity()).isAbsExpanded());
        crunchesTextView = v.findViewById(R.id.crunchesTextView);
        crunchesTextView.setOnClickListener(onClickListener);
        plankTextView = v.findViewById(R.id.plankTextView);
        plankTextView.setOnClickListener(onClickListener);
        lefLiftsTextView = v.findViewById(R.id.legLiftsTextView);
        lefLiftsTextView.setOnClickListener(onClickListener);
        absCircuit01TextView = v.findViewById(R.id.absCircuit01TextView);
        absCircuit01TextView.setOnClickListener(onClickListener);
        absCircuit02TextView = v.findViewById(R.id.absCircuit02TextView);
        absCircuit02TextView.setOnClickListener(onClickListener);

        //cardio group
        cardioTextView = v.findViewById(R.id.cardioTextView);
        cardioTextView.setOnClickListener(onClickListener);
        cardioCardView = v.findViewById(R.id.cardioCardView);
        cardioCardView.setVisibility(((MainActivity) getActivity()).isCardioExpanded());
        runningTextView = v.findViewById(R.id.runningTextView);
        runningTextView.setOnClickListener(onClickListener);
        hikingTextView = v.findViewById(R.id.hikingTextView);
        hikingTextView.setOnClickListener(onClickListener);
        bikingTextView = v.findViewById(R.id.bikingTextView);
        bikingTextView.setOnClickListener(onClickListener);
        swimmingTextView = v.findViewById(R.id.swimmingTextView);
        swimmingTextView.setOnClickListener(onClickListener);
        rowingTextView = v.findViewById(R.id.rowingTextView);
        rowingTextView.setOnClickListener(onClickListener);

        //upper body group
        upperBodyTextView = v.findViewById(R.id.upperBodyTextView);
        upperBodyTextView.setOnClickListener(onClickListener);
        upperBodyCardView = v.findViewById(R.id.upperBodyCardView);
        upperBodyCardView.setVisibility(((MainActivity) getActivity()).isUpperBodyExpanded());
        bicepCurlsTextView = v.findViewById(R.id.bicepCurlsTextView);
        bicepCurlsTextView.setOnClickListener(onClickListener);
        tricepExtensionsTextView = v.findViewById(R.id.tricepExtensionsTextView);
        tricepExtensionsTextView.setOnClickListener(onClickListener);
        benchPressTextView = v.findViewById(R.id.benchPressTextView);
        benchPressTextView.setOnClickListener(onClickListener);
        pushUpsTextView = v.findViewById(R.id.pushUpsTextView);
        pushUpsTextView.setOnClickListener(onClickListener);
        pullUpsTextView = v.findViewById(R.id.pullUpsTextView);
        pullUpsTextView.setOnClickListener(onClickListener);
        backExtensionsTextView = v.findViewById(R.id.backExtensionsTextView);
        backExtensionsTextView.setOnClickListener(onClickListener);

        //lower body group
        lowerBodyTextView = v.findViewById(R.id.lowerBodyTextView);
        lowerBodyTextView.setOnClickListener(onClickListener);
        lowerBodyCardView = v.findViewById(R.id.lowerBodyCardView);
        lowerBodyCardView.setVisibility(((MainActivity) getActivity()).isLowerBodyExpanded());
        squatsTextView = v.findViewById(R.id.squatsTextView);
        squatsTextView.setOnClickListener(onClickListener);
        lungesTextView = v.findViewById(R.id.lungesTextView);
        lungesTextView.setOnClickListener(onClickListener);
        legPressTextView = v.findViewById(R.id.legPressTextView);
        legPressTextView.setOnClickListener(onClickListener);
        legExtensionsTextView = v.findViewById(R.id.legExtensionsTextView);
        legExtensionsTextView.setOnClickListener(onClickListener);
        hamstringCurlsTextView = v.findViewById(R.id.hamstringCurlsTextView);
        hamstringCurlsTextView.setOnClickListener(onClickListener);


        ((MainActivity) getActivity()).clearNotifChannelTimer(v);
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.VISIBLE);

        //restore drawable state
        if (absCardView.getVisibility() == View.VISIBLE) {
            absTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrow_animated_collapse, null), null);
        }
        if (cardioCardView.getVisibility() == View.VISIBLE) {
            cardioTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrow_animated_collapse, null), null);
        }
        if (upperBodyCardView.getVisibility() == View.VISIBLE) {
            upperBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrow_animated_collapse, null), null);
        }
        if (lowerBodyCardView.getVisibility() == View.VISIBLE) {
            lowerBodyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.arrow_animated_collapse, null), null);
        }


        return v;
    }

    private void setSuggested(String name, String description) {
        suggestedWorkout = new Workout(timers, name);
        suggestedWorkout.setDescription(description);
    }


}
