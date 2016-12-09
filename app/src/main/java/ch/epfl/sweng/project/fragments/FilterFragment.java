package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.WelcomeActivity;

import static com.google.android.gms.internal.zzs.TAG;


public class FilterFragment extends DialogFragment {
    Button validate;
    public List<String> categoryList;
    public List<String> categoryListWithAll;
    Spinner dropdownMaxDistance;
    Spinner dropDownCategories;

    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    private boolean fragmentIsDisplayed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.filter_layout, container, false);
        getDialog().setTitle(R.string.event_filter_title);

        dropdownMaxDistance = (Spinner) rootView.findViewById(R.id.filterMaxDistanceDropDown);
        ArrayAdapter<CharSequence> maxDistanceDropDownAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.max_distance_array, android.R.layout.simple_spinner_item);
        maxDistanceDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMaxDistance.setAdapter(maxDistanceDropDownAdapter);
        dropdownMaxDistance.setOnItemSelectedListener(maxDistanceSelectedItemListener);

        dropDownCategories = (Spinner) rootView.findViewById(R.id.filterCategoriesDropDown);
        ArrayAdapter<String> categoriesDropDownAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.simple_spinner_item, categoryListWithAll);
        categoriesDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropDownCategories.setAdapter(categoriesDropDownAdapter);
        dropDownCategories.setOnItemSelectedListener(categoriesSelectedItemListener);

        WelcomeActivity wa = (WelcomeActivity)getActivity();
        startDateTextView = (TextView) rootView.findViewById(R.id.startDate);
        endDateTextView = (TextView) rootView.findViewById(R.id.endDate);
        startDateTextView.setText(wa.makeDateString(wa.filterStartCalendar));
        endDateTextView.setText(wa.makeDateString(wa.filterEndCalendar));

        startTimeTextView = (TextView) rootView.findViewById(R.id.startTime);
        endTimeTextView = (TextView) rootView.findViewById(R.id.endTime);
        startTimeTextView.setText(wa.makeTimeString(wa.filterStartCalendar));
        endTimeTextView.setText(wa.makeTimeString(wa.filterEndCalendar));

        validate = (Button) rootView.findViewById(R.id.validate);
        validate.setOnClickListener(validateListener);

        String[] maxDistanceArray = getResources().getStringArray(R.array.max_distance_array);
        List<String> maxDistanceList = Arrays.asList(maxDistanceArray);
        dropdownMaxDistance.setSelection(maxDistanceList.indexOf(wa.maxDistanceString));

        dropDownCategories.setSelection(categoryListWithAll.indexOf(wa.filterCategory));

        return rootView;
    }

    AdapterView.OnItemSelectedListener maxDistanceSelectedItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String chosenMaxDistance = parent.getItemAtPosition(position).toString();
            if(WelcomeActivity.maxDistanceMap.get(chosenMaxDistance) != null) {
                ((WelcomeActivity)getActivity()).maxDistanceString = chosenMaxDistance;
            }
            else {
                Log.d(TAG, "The max distance selected is not mapped to a value");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener categoriesSelectedItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String chosenCategory = parent.getItemAtPosition(position).toString();
            if(categoryListWithAll.contains(chosenCategory)) {
                ((WelcomeActivity)getActivity()).filterCategory = chosenCategory;
            }
            else {
                Log.d(TAG, "The category selected is not in the category list");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener validateListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            WelcomeActivity wa = ((WelcomeActivity)getActivity());
            wa.displaySpecifiedActivities();
            wa.cleanLinearLayout(wa.activityPreviewsLayout);
            getActivity().findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

            dismiss();
        }
    };

    @Override
    public void show(FragmentManager manager, String tag) {
        if(!fragmentIsDisplayed) {
            super.show(manager, tag);
            fragmentIsDisplayed = true;
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        fragmentIsDisplayed = false;
        super.onDismiss(dialog);
    }

    public void updateDateTextViews() {
        WelcomeActivity wa = (WelcomeActivity)getActivity();
        startDateTextView.setText(wa.makeDateString(wa.filterStartCalendar));
        endDateTextView.setText(wa.makeDateString(wa.filterEndCalendar));
    }

    public void updateTimeTextViews() {
        WelcomeActivity wa = (WelcomeActivity)getActivity();
        startTimeTextView.setText(wa.makeTimeString(wa.filterStartCalendar));
        endTimeTextView.setText(wa.makeTimeString(wa.filterEndCalendar));
    }
}