package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.CreateActivity;
import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.WelcomeActivity;

/**
 * Created by nathan on 25.10.16.
 */

public class FilterFragment extends DialogFragment {
    Button dismiss;
    DataProvider mDataProvider;
    public List<String> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_layout, container, false);
        getDialog().setTitle(R.string.event_filter_title);

        dismiss = (Button) rootView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(dismissListener);
        final ListView categoriesList = (ListView) rootView.findViewById(R.id.categoriesFilterList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categoryList);

        categoriesList.setAdapter(adapter);
        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String filterCategory = (String) parent.getItemAtPosition(position);
                ((WelcomeActivity)getActivity()).displaySpecifiedActivities(filterCategory);
                dismiss();
            }
        });
        return rootView;
    }

    View.OnClickListener dismissListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

}