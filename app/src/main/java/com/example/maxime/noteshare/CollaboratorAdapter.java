package com.example.maxime.noteshare;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CollaboratorAdapter extends Adapter<String> {

//    static class ViewHolder {
//        TextView icon;
//        TextView name;
//    }

    public CollaboratorAdapter(Context context, ArrayList<String> data) {
        super(context, R.layout.element_collaborator, data);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.element_collaborator, null);
//            viewHolder = new ViewHolder();
//            viewHolder.icon = (TextView) convertView.findViewById(R.id.collaborator_icon);
//            viewHolder.name = (TextView) convertView.findViewById(R.id.collaborator_name);
//            convertView.setTag(viewHolder);
//        }
//        else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        String collaborator = getItem(position);
//        viewHolder.icon.setText(collaborator.toUpperCase().substring(0,1));
//        ((GradientDrawable) viewHolder.icon.getBackground()).setColor(getColor(collaborator));
//        viewHolder.name.setText(collaborator);
//
//        return convertView;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = inflater.inflate(R.layout.element_collaborator, null, true);

        if (!data.isEmpty()) {
            String collaborator =  data.get(position);
            TextView collaboratorIcon = (TextView) vi.findViewById(R.id.collaborator_icon);
            TextView collaboratorName = (TextView) vi.findViewById(R.id.collaborator_name);
            collaboratorIcon.setText(collaborator.toUpperCase().substring(0,1));
            ((GradientDrawable) collaboratorIcon.getBackground()).setColor(getColor(collaborator));
            collaboratorName.setText(collaborator);
        }
        return vi;
    }
}
