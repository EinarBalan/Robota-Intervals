package com.balanstudios.einar.workouttimer;

import java.util.ArrayList;
import java.util.Locale;

public class Workout {
    private ArrayList<TimerInterval> timers = new ArrayList<>();
    private String workoutName;
    private String description; //long, a few sentences explaining workout
    private int numSets; //number of sets in workout
    private int numCycles; //number of cycles per set
    private int numRepeats;
    private int wTime;
    private int rTime;
    private int endWorkoutRest;
    private int prepTime;

    public Workout() {}

    public Workout(ArrayList<TimerInterval> timers, String workoutName) {
        for (int i = 0; i < timers.size(); i++) {
            this.timers.add(timers.get(i));
            if (timers.get(i).getIsWork()) {
                wTime = timers.get(i).getSeconds();
            } else if (i > 0)
                rTime = timers.get(i).getSeconds();
        }
        this.workoutName = workoutName;
        numRepeats = 1;
        description = "";
        numSets = timers.get(timers.size() - 1).getNumOfSet();
        numCycles = timers.get(timers.size() - 1).getNumOfCycle();
        if (timers.get(0).getDescription() == "Prep")
            prepTime = timers.get(0).getSeconds();

    }

    public Workout(ArrayList<TimerInterval> timers, String workoutName, String description) {
        for (int i = 0; i < timers.size(); i++) {
            this.timers.add(timers.get(i));
            if (timers.get(i).getIsWork()) {
                wTime = timers.get(i).getSeconds();
            } else if (i > 0)
                rTime = timers.get(i).getSeconds();
        }
        this.workoutName = workoutName;
        numRepeats = 1;
        this.description = description;
        numSets = timers.get(timers.size() - 1).getNumOfSet();
        numCycles = timers.get(timers.size() - 1).getNumOfCycle();
        if (timers.get(0).getDescription() == "Prep")
            prepTime = timers.get(0).getSeconds();

    }

    public Workout(ArrayList<TimerInterval> timers, String workoutName, String description, int numRepeats, int endWorkoutRest) {
        for (int i = 0; i < timers.size(); i++) {
            this.timers.add(timers.get(i));
            if (timers.get(i).getIsWork()) {
                wTime = timers.get(i).getSeconds();
            } else if (i > 0)
                rTime = timers.get(i).getSeconds();
        }
        this.workoutName = workoutName;
        this.description = description;
        this.numRepeats = numRepeats;
        this.endWorkoutRest = endWorkoutRest;
        numSets = timers.get(timers.size() - 1).getNumOfSet();
        numCycles = timers.get(timers.size() - 1).getNumOfCycle();
        if (timers.get(0).getDescription() == "Prep")
            prepTime = timers.get(0).getSeconds();

    }

    public Workout(ArrayList<TimerInterval> timers, String workoutName, int numRepeats, int endWorkoutRest) {
        for (int i = 0; i < timers.size(); i++) {
            this.timers.add(timers.get(i));
            if (timers.get(i).getIsWork()) {
                wTime = timers.get(i).getSeconds();
            } else if (i > 0)
                rTime = timers.get(i).getSeconds();
        }
        this.workoutName = workoutName;
        this.numRepeats = numRepeats;
        this.endWorkoutRest = endWorkoutRest;
        description = "";
        numSets = timers.get(timers.size() - 1).getNumOfSet();
        numCycles = timers.get(timers.size() - 1).getNumOfCycle();
        if (timers.get(0).getDescription() == "Prep")
            prepTime = timers.get(0).getSeconds();


    }

    public String toString() {
        return String.format(Locale.getDefault(), "Name: %s, Num Sets: %d, Num Cycles: %d, Total Time: %d, Work Time: %d, Rest Time: %d, Prep Time: %d",
                workoutName, numSets, numCycles, getTimeTotal(), wTime, rTime, prepTime);
    }

    public void clear() {
        timers = new ArrayList<>();
        workoutName = "";
        description = "";
        numSets = 1;
        numCycles = 1;
        numRepeats = 1;
        wTime = 0;
        rTime = 0;
        endWorkoutRest = 0;
        prepTime = 0;
    }


    //getters and setters, use these gets strings for workout cards
    public ArrayList<TimerInterval> getTimers() {
        return timers;
    }

    public void setTimers(ArrayList<TimerInterval> timers) {
        this.timers = timers;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumSets() {
        return numSets;
    }

    public void setNumSets(int numSets) {
        this.numSets = numSets;
    }

    public int getNumCycles() {
        return numCycles;
    }

    public void setNumCycles(int numCycles) {
        this.numCycles = numCycles;
    }

    public int getTimeTotal() {
        return (((wTime * numCycles) + rTime) * numSets - rTime + endWorkoutRest) * numRepeats + prepTime - endWorkoutRest;
    }

    public String getTimeTotalString() {
        int timeTotal = getTimeTotal();
        int min = timeTotal / 60;
        int sec = timeTotal % 60;

        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    public int getwTime() {
        return wTime;
    }

    public void setwTime(int wTime) {
        this.wTime = wTime;
    }

    public String getwTimeString() {
        int min = wTime / 60;
        int sec = wTime % 60;

        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    public int getrTime() {
        return rTime;
    }

    public void setrTime(int rTime) {
        this.rTime = rTime;
    }

    public String getrTimeString() {
        int min = rTime / 60;
        int sec = rTime % 60;

        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public String getPrepTimeString() {
        int min = prepTime / 60;
        int sec = prepTime % 60;

        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    public int getNumRepeats() {
        return numRepeats;
    }

    public void setNumRepeats(int numRepeats) {
        this.numRepeats = numRepeats;
    }

    public int getEndWorkoutRest() {
        return endWorkoutRest;
    }

    public void setEndWorkoutRest(int endWorkoutRest) {
        this.endWorkoutRest = endWorkoutRest;
    }
}