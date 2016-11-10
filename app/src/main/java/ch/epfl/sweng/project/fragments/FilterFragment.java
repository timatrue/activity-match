package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
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
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.WelcomeActivity;

import static com.google.android.gms.internal.zzs.TAG;


/**
 * Created by nathan on 25.10.16.
 */

public class FilterFragment extends DialogFragment {
    Button validate;
    DataProvider mDataProvider;
    public List<String> categoryList;
    String chosenCategory;
    int maxDistance;
    Spinner dropdownMaxDistance;
    Spinner dropDownCategories;

    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    private static final HashMap<String, Integer> maxDistanceMap;
    static
    {
        maxDistanceMap = new HashMap<>();
        maxDistanceMap.put("1 km", 1);
        maxDistanceMap.put("5 km", 5);
        maxDistanceMap.put("10 km", 10);
        maxDistanceMap.put("20 km", 20);
        maxDistanceMap.put("50 km", 50);
        maxDistanceMap.put("All", 21000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<String> categoryListWithAll = new ArrayList<String>();
        categoryListWithAll.add("All");
        categoryListWithAll.addAll(categoryList);

        View rootView = inflater.inflate(R.layout.filter_layout, container, false);
        getDialog().setTitle(R.string.event_filter_title);

        dropdownMaxDistance = (Spinner) rootView.findViewById(R.id.filterMaxDistanceDropDown);
        ArrayAdapter<CharSequence> maxDistanceDropDownAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.max_distance_array, android.R.layout.simple_spinner_item);
        maxDistanceDropDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMaxDistance.setAdapter(maxDistanceDropDownAdapter);
        dropdownMaxDistance.setOnItemSelectedListener(maxDistanceSelectedItemListener);

        dropDownCategories = (Spinner) rootView.findViewById(R.id.filterCategoriesDropDown);
        ArrayAdapter<String> categoriesDropDownAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),
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
            if(maxDistanceMap.get(chosenMaxDistance) != null) {
                ((WelcomeActivity)getActivity()).maxDistanceString = chosenMaxDistance;
                maxDistance = maxDistanceMap.get(chosenMaxDistance);
            }
            else {
                Log.d(TAG, "The max distance selected is not mapped to a value");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            maxDistance = maxDistanceMap.get(((WelcomeActivity)getActivity()).maxDistanceString);
        }
    };

    AdapterView.OnItemSelectedListener categoriesSelectedItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            chosenCategory = parent.getItemAtPosition(position).toString();
            ((WelcomeActivity)getActivity()).filterCategory = chosenCategory;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            chosenCategory = ((WelcomeActivity)getActivity()).filterCategory;
        }
    };

    View.OnClickListener validateListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ((WelcomeActivity)getActivity()).displaySpecifiedActivities(chosenCategory, maxDistance);
            dismiss();
        }
    };

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