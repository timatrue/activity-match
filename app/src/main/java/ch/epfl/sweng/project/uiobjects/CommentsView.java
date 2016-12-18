package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by artem on 18/12/2016.
 */

public class CommentsView extends LinearLayout {

    private TextView idComment;
    private TextView ratingComment;
    private TextView contentComment;

    private Object userComments;

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public CommentsView(Context context, Object userComments){
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        this.userComments = userComments;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.activity_user_profile_comment, (ViewGroup) findViewById(R.id.userProfileActivityList));
        setComments(userComments, childLayout, context);

        this.addView(childLayout);

    }

    private void setComments(Object userComments, View childLayout, Context context){

            //DO Stuff
            idComment = (TextView) childLayout.findViewById(R.id.idComment);
            //idComment.setText(userComments.getID());

            ratingComment = (TextView) childLayout.findViewById(R.id.ratingComment);
            //ratingComment.setText(userComments.getRating());

            contentComment = (TextView) childLayout.findViewById(R.id.contentComment);
            //contentComment.setText(userComments.getContenz());

    }
}
