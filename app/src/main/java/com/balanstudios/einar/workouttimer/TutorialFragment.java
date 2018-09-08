package com.balanstudios.einar.workouttimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {

    private ImageButton backButton;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backButtonTutorial:
                    getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    };

    public TutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tutorial, container, false);

        if (((MainActivity) getActivity()).getmMainNav().getVisibility() == View.VISIBLE) {
            ((MainActivity) getActivity()).getmMainNav().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        ((MainActivity) getActivity()).getmMainNav().setVisibility(View.GONE);

        backButton = v.findViewById(R.id.backButtonTutorial);
        backButton.setOnClickListener(onClickListener);

        return v;
    }

}
