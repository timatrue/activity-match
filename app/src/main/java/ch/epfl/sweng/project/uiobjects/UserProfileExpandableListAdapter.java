package ch.epfl.sweng.project.uiobjects;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.R;

/**
 * Created by artem on 13/11/2016.
 */

public class UserProfileExpandableListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private Map<String, List<String>> activityCollections;
    private List<String> activities;
    public UserProfileExpandableListAdapter(Activity context, Map<String,List<String>> activityCollections,
                                            List<String> activities){
        this.context = context;
        this.activityCollections = activityCollections;
        this.activities = activities;

    }

    public Object getChild(int groupPosition, int childPosition){
        String position = activities.get(groupPosition);
        return activityCollections.get(position).get(childPosition);
    }
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent){
        final String activity = (String)getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();
        //convertView is the ListView Item Cache that is not visible
        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_user_profile_group_child, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.userProfileActivityChild);
        item.setText(activity);
        return convertView;
    }
    public int getChildrenCount(int groupPosition){
        String position = activities.get(groupPosition);
        return activityCollections.get(position).size();
    }
    public Object getGroup(int groupPosition){
        return activities.get(groupPosition);
    }
    public int getGroupCount(){
        return activities.size();
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent){
        String activityName = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflaterG = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflaterG.inflate(R.layout.activity_user_profile_group, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.userProfileActivityChild);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(activityName);
        return convertView;
    }
    public boolean hasStableIds() {
        return true;
    }
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
