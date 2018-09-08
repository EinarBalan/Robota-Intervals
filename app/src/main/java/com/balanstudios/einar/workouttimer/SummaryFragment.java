package com.balanstudios.einar.workouttimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SummaryFragment extends Fragment {

    private LinearLayout summaryLayout;
    private TextView totalTime;
    private TextView totalTimeWords;
    private ImageButton backButton;

    private int time;
    private int prepTime;
    private int numRepeats;

    private ArrayList<TimerInterval> timers;

    public SummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        summaryLayout = v.findViewById(R.id.summaryLayout);
        totalTime = v.findViewById(R.id.totalTimeText);
        totalTimeWords = v.findViewById(R.id.totalTimeWords);
        backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        time = ((MainActivity) getActivity()).getSuggestedWorkout().getTimeTotal();
        prepTime = ((MainActivity) getActivity()).getSuggestedWorkout().getPrepTime();
        numRepeats = ((MainActivity) getActivity()).getSuggestedWorkout().getNumRepeats();
        timers = ((MainActivity) getActivity()).getSuggestedWorkout().getTimers();

        if (timers.size() > 0)
            populateSummary();

        return v;
    }

    private void populateSummary() {
        int repeatNum = 0;
        int setNum = 0;

        summaryLayout.removeAllViews(); //prevent exponential growth when pressing enter

        //don't want total time to show up in summary if time other than prep = 0
        if (time > prepTime) {
            totalTime.setVisibility(View.VISIBLE);
            totalTimeWords.setVisibility(View.VISIBLE);
            totalTime.setText(String.format(Locale.getDefault(), "%d:%02d", time / 60, time % 60));
        }

        for (TimerInterval ti : timers) {
            if (numRepeats > 1) {
                if (ti.getNumOfRepeat() != repeatNum) {
                    repeatNum = ti.getNumOfRepeat();
                    TextView textView = new TextView(getActivity());
                    textView.setText(String.format(Locale.getDefault(), "Workout %d", ti.getNumOfRepeat()));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    textView.setTextColor(((MainActivity) getActivity()).getAttributeColor(getActivity(), R.attr.highlightTextColor));
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    summaryLayout.addView(textView);
                }
            }
            if (ti.getNumOfSet() != setNum) {
                setNum = ti.getNumOfSet();
                TextView textView = new TextView(getActivity());
                textView.setText(String.format(Locale.getDefault(), "Set %d", ti.getNumOfSet()));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
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
}
