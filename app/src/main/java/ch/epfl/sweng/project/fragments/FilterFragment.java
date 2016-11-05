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

import java.util.ArrayList;
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
    String filterCategory;
    Spinner dropdownMaxDistance;
    Spinner dropDownCategories;

    int maxDistance = 21000;

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

        validate = (Button) rootView.findViewById(R.id.validate);
        validate.setOnClickListener(validateListener);

        /*final ListView categoriesList = (ListView) rootView.findViewById(R.id.categoriesFilterList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categoryList);

        categoriesList.setAdapter(adapter);
        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterCategory = (String) parent.getItemAtPosition(position);
            }
        });*/

        return rootView;
    }

    AdapterView.OnItemSelectedListener maxDistanceSelectedItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String maxDistanceString = parent.getItemAtPosition(position).toString();
            if(maxDistanceMap.get(maxDistanceString) != null) {
                maxDistance = maxDistanceMap.get(maxDistanceString);
            }
            else {
                Log.d(TAG, "The max distance selected is not mapped to a value");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            maxDistance = maxDistanceMap.get("All");
        }
    };

    AdapterView.OnItemSelectedListener categoriesSelectedItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            filterCategory = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            filterCategory = "All";
        }
    };

    View.OnClickListener validateListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ((WelcomeActivity)getActivity()).displaySpecifiedActivities(filterCategory, maxDistance);
            dismiss();
        }
    };

}