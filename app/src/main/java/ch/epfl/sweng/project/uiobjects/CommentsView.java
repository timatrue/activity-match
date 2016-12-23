package ch.epfl.sweng.project.uiobjects;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Map;

import ch.epfl.sweng.project.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class CommentsView extends LinearLayout {

    TextView idComment;
    SpannableStringBuilder idCommentText;
    TextView ratingComment;
    TextView contentComment;
    String eventId;
    String eventRate;
    Resources res;

    RatingBar eventRating;

    public String getEventId() {
        return eventId;
    }

    Map<String, String> userComments;

    public CommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public CommentsView(Context context, Map<String, String> userComments){
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        res = getResources();



        this.userComments = userComments;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.activity_user_profile_comment, (ViewGroup) findViewById(R.id.commentsLayout));
        setComments(userComments, childLayout);

        this.addView(childLayout,layoutParams);

    }

    private void setComments(Map<String, String> userComments, View childLayout){

        String comment = String.valueOf(userComments.get("comment"));
        eventId = String.valueOf(userComments.get("eventId"));
        int rate = Integer.valueOf(String.valueOf(userComments.get("rating")));

        eventRating = (RatingBar) childLayout.findViewById(R.id.ratingBar);
        eventRating.setNumStars(rate);

        idComment = (TextView) childLayout.findViewById(R.id.idComment);
        idCommentText = new SpannableStringBuilder("See related activity");
        idCommentText.setSpan(new UnderlineSpan(), 0, idCommentText.length(), 0);
        idComment.setText(idCommentText);

        contentComment = (TextView) childLayout.findViewById(R.id.contentComment);
        contentComment.setText(comment);

    }
}
