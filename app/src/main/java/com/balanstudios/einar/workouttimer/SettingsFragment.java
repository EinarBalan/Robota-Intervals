package com.balanstudios.einar.workouttimer;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private ImageButton backButton;
    private SeekBar volumeSeekBar;
    private Switch vibrationsSwitch;
    private Switch darkModeSwitch;
    private Switch descriptionHideSwitch;
    private Button rateButton;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.backButtonSettings:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case R.id.rateButton:
                    clickRate();
                    break;


            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()){
                case R.id.vibrationsSwitch:
                    ((MainActivity) getActivity()).setVibrationEnabled(vibrationsSwitch.isChecked());
                    ((MainActivity) getActivity()).saveSettingsData();
                    break;
                case R.id.darkModeSwitch:
                    if ( ((MainActivity) getActivity()).isDarkModeEnabled() != darkModeSwitch.isChecked() ) {
                        ((MainActivity) getActivity()).setDarkModeEnabled(darkModeSwitch.isChecked());
                        ((MainActivity) getActivity()).saveSettingsData();

                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                    break;
                case R.id.descriptionHideSwitch:
                    ((MainActivity) getActivity()).setHideDescriptionEnabled(descriptionHideSwitch.isChecked());
                    ((MainActivity) getActivity()).saveSettingsData();
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ((MainActivity) getActivity()).setAlertVolume(seekBar.getProgress());
            ((MainActivity) getActivity()).saveSettingsData();
        }
    };


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        if (((MainActivity) getActivity()).getmMainNav().getVisibility() == View.VISIBLE) {
            ((MainActivity) getActivity()).getmMainNav().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);

        backButton = v.findViewById(R.id.backButtonSettings); backButton.setOnClickListener(onClickListener);
        volumeSeekBar = v.findViewById(R.id.volumeSeekBar); volumeSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        vibrationsSwitch = v.findViewById(R.id.vibrationsSwitch); vibrationsSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        darkModeSwitch = v.findViewById(R.id.darkModeSwitch); darkModeSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        descriptionHideSwitch = v.findViewById(R.id.descriptionHideSwitch); descriptionHideSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        rateButton = v.findViewById(R.id.rateButton); rateButton.setOnClickListener(onClickListener);

        volumeSeekBar.setProgress( ((MainActivity) getActivity()).getAlertVolume());
        vibrationsSwitch.setChecked( ((MainActivity) getActivity()).isVibrationEnabled());
        darkModeSwitch.setChecked( ((MainActivity) getActivity()).isDarkModeEnabled());
        descriptionHideSwitch.setChecked( ((MainActivity) getActivity()).isHideDescriptionEnabled());



        return v;
    }

    private void clickRate(){
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com")));
        }
    }
}
