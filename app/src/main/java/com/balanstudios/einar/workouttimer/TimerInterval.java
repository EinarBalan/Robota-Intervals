package com.balanstudios.einar.workouttimer;

import java.util.Locale;

public class TimerInterval {
    private boolean isWork;
    private int seconds;
    private long millis;
    private int numOfSet;
    private int numOfCycle;
    private int numOfRepeat;
    private String description; //short, like one word

    public TimerInterval(boolean isWork, int workTime, int restTime, int numOfCycle, int numOfSet, int numOfRepeat) {
        this.isWork = isWork;
        this.numOfSet = numOfSet;
        this.numOfRepeat = numOfRepeat;

        if (isWork) {
            this.seconds = workTime;
            millis = workTime * 1000;
            this.numOfCycle = numOfCycle;
            this.description = "Work";
        } else {
            this.seconds = restTime;
            millis = restTime * 1000;
            this.numOfCycle = 0;
            this.description = "Rest";

        }
    }


    public TimerInterval(boolean isWork, int seconds, int numOfCycle, int numOfSet, String description) {
        this.isWork = isWork;
        this.numOfSet = numOfSet;
        this.numOfRepeat = 0;

        if (description == null) {
            this.description = "";
        } else {
            this.description = description;
        }

        if (isWork) {
            this.numOfCycle = numOfCycle;

        } else {
            this.numOfCycle = 0;

        }

        this.seconds = seconds;
        millis = seconds * 1000;
    }


    public String toString() {
        if (description != "")
            return (description.trim() + " for " + String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60));
        else {
            if (isWork)
                return ("Work for " + String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60));
            else
                return ("Rest for " + String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60));
        }

    }

    public boolean getIsWork() {
        return isWork;
    }

    public int getSeconds() {
        return seconds;
    }

    public long getMillis() {
        return millis;
    }

    public int getNumOfSet() {
        return numOfSet;
    }

    public int getNumOfCycle() {
        return numOfCycle;
    }

    public String getDescription() {
        return description;
    }

    public int getNumOfRepeat() {
        return numOfRepeat;
    }
}
