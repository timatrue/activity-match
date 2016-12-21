package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import ch.epfl.sweng.project.DataProvider;
import ch.epfl.sweng.project.DisplayActivity;
import ch.epfl.sweng.project.R;
/**
 * Created by olga on 08.12.16.
 */

public class RatingFragment extends DialogFragment {

    RatingBar rateEvent;
    EditText rateComment;
    Button rateEventButton;
    public String eventId;

    DataProvider mDataProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.rating_layout, container, true);
        getDialog().setTitle(R.string.event_rating_title);



        rateEvent = (RatingBar) rootView.findViewById(R.id.rateEventWidget);

        rateComment = (EditText) rootView.findViewById(R.id.rateCommentInputBox);
        rateComment.setHint(R.string.event_rating_comment_hint);

        rateEventButton = (Button) rootView.findViewById(R.id.rateEventButton);
        rateEventButton.setText(R.string.event_rating_button);
        rateEventButton.setOnClickListener(rateButtonListener);


        LayerDrawable stars = (LayerDrawable) rateEvent.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.strokeGreyDebox), PorterDuff.Mode.SRC_ATOP);

        return rootView;
    }

    View.OnClickListener rateButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            DisplayActivity da = ((DisplayActivity) getActivity());

            String comment = rateComment.getText().toString();
            int rating = Math.round(rateEvent.getRating());
            mDataProvider = new DataProvider();
            mDataProvider.rankUser(eventId, rating, comment);

            da.findViewById(R.id.rateButton).setClickable(false);
            da.findViewById(R.id.rateButton).setBackgroundColor(getResources().getColor(R.color.strokeGreyDebox));

            dismiss();
        }
    };
}
