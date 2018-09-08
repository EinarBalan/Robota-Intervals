package com.balanstudios.einar.workouttimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DescriptionDialog extends AppCompatDialogFragment {
    private EditText descriptionEditText;
    private DescriptionDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogStyle);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_description, null);

        final Bundle bundle = getArguments();
        final ExercisesFragment exercisesFragment = new ExercisesFragment();

        descriptionEditText = v.findViewById(R.id.descriptionEditText);
        descriptionEditText.setText(listener.loadDescription(bundle.getInt("pos")));

        builder.setView(v)
                .setTitle("Edit Description")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String description = descriptionEditText.getText().toString();
                        listener.saveDescription(description, bundle.getInt("pos"));
                        exercisesFragment.getCustomAdapter().notifyDataSetChanged();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DescriptionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DescriptionDialogListener");
        }
    }


    public interface DescriptionDialogListener {
        void saveDescription(String description, int pos);

        String loadDescription(int pos);

        int getAttributeColor(Context context, int attributeId);
    }
}
