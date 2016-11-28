package ch.epfl.sweng.project.uiobjects;

import android.content.Context;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.ImageProvider;
import ch.epfl.sweng.project.R;

import java.util.Calendar;
import java.text.DateFormat;
import java.util.List;
import java.util.Random;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static ch.epfl.sweng.project.R.id.imageView;
import static java.text.DateFormat.getDateInstance;


import java.util.List;
/**
 * Created by nathan on 07.10.16.
 */

public class ActivityPreview extends LinearLayout {

    private DeboxActivity event;
    private String eventTime;
    private Calendar timeStart;
    private DateFormat dateFormat;

    private TextView titleEvent;
    private TextView previewEvent;
    private TextView dateEvent;
    private TextView sizeEvent;

    private List<String> imagesList;
    private ImageView imageView;
    private String eventId;
    LinearLayout imagesLayout;

    public ActivityPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public String getEventId() {
        return event.getId();
    }

    public ActivityPreview(Context context, DeboxActivity event) {
        super(context);
        setOrientation(VERTICAL);

        this.event = event;
        dateFormat = getDateInstance();
        timeStart = event.getTimeStart();
        eventTime = dateFormat.format(timeStart.getTime());

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.content_data_row, (ViewGroup) findViewById(R.id.activityPreviewsLayout));
        setEventPreview(event, childLayout, context);
        this.addView(childLayout);
    }

    private void setEventPreview(DeboxActivity event, View childLayout, Context context){
        titleEvent = (TextView) childLayout.findViewById(R.id.titleEvent);
        titleEvent.setText(event.getTitle());

        previewEvent = (TextView) childLayout.findViewById(R.id.previewEvent);
        previewEvent.setText(event.getShortDescription());

        dateEvent = (TextView) childLayout.findViewById(R.id.dateEvent);
        dateEvent.setText(eventTime);

        int nbParticipants = event.getNbOfParticipants();
        if(nbParticipants >= 0) {
            sizeEvent = (TextView) childLayout.findViewById(R.id.sizeEvent);
            sizeEvent.setText("Participants: " +  nbParticipants);
        }

        imagesList = event.getImageList();
        imageView = (ImageView) childLayout.findViewById(R.id.activityImage);
        TextView imageTextImage = (TextView) childLayout.findViewById(R.id.activityTextImage);
        eventId = event.getId();

        if(imagesList != null){
            if(imagesList.size() > 0) {
                String image = imagesList.get(0);
                new ImageProvider().previewImage(context, eventId, childLayout, image);
            }

        } else {
            /*
            imageTextImage.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            String letter = event.getTitle().substring(0,1);
            imageTextImage.setText(letter);
            imageTextImage.setTextSize(getResources().getDimension(R.dimen.textsize));*/

            String letter = event.getTitle().substring(0,1);
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getMatColor("400", context));
            canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);
            paint.setTextAlign(Paint.Align.CENTER);
            int xPos = (canvas.getWidth()/2);
            int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
            canvas.drawText(letter, xPos, yPos, paint);

            imageView.setImageBitmap(bitmap);
        }
    }

    private int getMatColor(String typeColor, Context context)
    {
        int returnColor = Color.BLACK;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

}
