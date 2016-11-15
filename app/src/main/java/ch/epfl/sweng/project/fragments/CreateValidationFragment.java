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
import java.util.List;

import ch.epfl.sweng.project.CreateActivity;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.WelcomeActivity;

import static com.google.android.gms.internal.zzs.TAG;


public class CreateValidationFragment extends DialogFragment {
    Button validate;
    public List<String> categoryList;
    Spinner dropdownMaxDistance;
    Spinner dropDownCategories;

    TextView startDateTextView;
    TextView endDateTextView;
    TextView startTimeTextView;
    TextView endTimeTextView;

    List<String> categoryListWithAll = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_layout, container, false);
        getDialog().setTitle(R.string.event_filter_title);

        CreateActivity ca = (CreateActivity) getActivity();


        //dismiss();

        return rootView;
    }

}