package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.epfl.sweng.project.R;

/**
 * Created by nathan on 25.10.16.
 */

public class FilterFragment extends DialogFragment {
    Button dismiss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_layout, container, false);
        getDialog().setTitle("Simple Dialog");

        dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(dismissListener);


        return rootView;
    }

    View.OnClickListener dismissListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

}