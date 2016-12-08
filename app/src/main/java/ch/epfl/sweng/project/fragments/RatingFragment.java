package ch.epfl.sweng.project.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.DisplayActivity;
/**
 * Created by olga on 08.12.16.
 */

public class RatingFragment extends DialogFragment {

    TextView rateEventTextBox;
    RatingBar rateEvent;
    TextView rateCommentTextBox;
    EditText rateComment;
    Button rateEventButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.rating_layout, container, false);
        getDialog().setTitle(R.string.event_rating_title); //TODO: change the title, maybe event title

        rateEventTextBox = (TextView) rootView.findViewById(R.id.rateEventTextBox);
        rateEventTextBox.setText(R.string.event_rating_box_title);

        rateEvent = (RatingBar) rootView.findViewById(R.id.rateEventWidget);

        rateCommentTextBox = (TextView) rootView.findViewById(R.id.rateCommentTextBox);
        rateCommentTextBox.setText(R.string.event_rating_comment_title);

        rateComment = (EditText) rootView.findViewById(R.id.rateCommentInputBox);
        rateComment.setHint(R.string.event_rating_comment_hint);

        rateEventButton = (Button) rootView.findViewById(R.id.rateEventButton);

        return rootView;
    }
}
