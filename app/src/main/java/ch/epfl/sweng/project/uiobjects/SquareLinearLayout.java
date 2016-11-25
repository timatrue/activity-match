package ch.epfl.sweng.project.uiobjects;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * Created by artem on 23/11/2016.
 */



public class SquareLinearLayout extends LinearLayout{
    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        // or super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}