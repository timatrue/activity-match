package ch.epfl.sweng.project.uiobjects;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ch.epfl.sweng.project.DeboxActivity;
import ch.epfl.sweng.project.R;


public class UserProfileExpandableListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private Map<String, List<DeboxActivity>> activityCollections;
    private List<String> groups;
    private String modifiableGroup;
    private modifyDeleteListener modifyDeleteListener;

    public UserProfileExpandableListAdapter(Activity context, Map<String,List<DeboxActivity>> activityCollections,
                                            List<String> groups, String modifiableGroup, modifyDeleteListener modifyDeleteListener){
        this.context = context;
        this.activityCollections = activityCollections;
        this.groups = groups;
        this.modifiableGroup = modifiableGroup;
        this.modifyDeleteListener = modifyDeleteListener;
    }
    public Object getChild(int groupPosition, int childPosition){
        String position = groups.get(groupPosition);
        if(activityCollections.get(position).size() <= childPosition) {
            return null;
        }
        return activityCollections.get(position).get(childPosition);
    }
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //getChildView stands for explicit group's events
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent){
        final DeboxActivity activity = (DeboxActivity)getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if(activity == null) {
            convertView = inflater.inflate(R.layout.activity_user_profile_empty_view, null);
            return convertView;
        }

        if(groups.get(groupPosition).equals(modifiableGroup)) {

            convertView = inflater.inflate(R.layout.activity_user_profile_group_child_organized, null);

            ImageButton modifyButton = (ImageButton) convertView.findViewById(R.id.modifyButton);
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyDeleteListener.onItemModified(groupPosition, childPosition);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyDeleteListener.onItemDeleted(groupPosition, childPosition);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyDeleteListener.onItemClicked(groupPosition, childPosition);
                }
            });
        }
        else {
            convertView = inflater.inflate(R.layout.activity_user_profile_group_child, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.userProfileActivityChild);

        item.setText(activity.getTitle());
        return convertView;
    }
    public int getChildrenCount(int groupPosition){
        String position = groups.get(groupPosition);
        int size = activityCollections.get(position).size();
        if(size == 0) {
            return 1;
        }
        return activityCollections.get(position).size();
    }
    public Object getGroup(int groupPosition){
        return groups.get(groupPosition);
    }
    public int getGroupCount(){
        return groups.size();
    }
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //getGroupView stands for group's headers, for example "Interested events"
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


    public interface modifyDeleteListener {
        void onItemModified(int groupPosition, int childPosition);
        void onItemDeleted(int groupPosition, int childPosition);
        void onItemClicked(int groupPosition, int childPosition);
    }
}
