package com.example.maxime.noteshare;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class CollaboratorAdapter extends ArrayAdapter<String> {

    private static LayoutInflater inflater = null;
    private ArrayList<String> data;
    private HashMap<String, Integer> colors;

    public CollaboratorAdapter(Context context, ArrayList<String> data) {
        super(context, R.layout.element_collaborator);
        this.data = data;
        this.colors = new HashMap<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = inflater.inflate(R.layout.element_collaborator, null, true);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.element_collaborator, null);
            viewHolder = new ViewHolder();
            viewHolder.icone = (TextView) convertView.findViewById(R.id.collaborator_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.collaborator_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String collaborator = getItem(position);

        viewHolder.icone.setText(collaborator.toUpperCase().substring(0,1));
        ((GradientDrawable) viewHolder.icone.getBackground()).setColor(getColor(collaborator));
        viewHolder.name.setText(collaborator);

        return convertView;
    }

    private int getColor(String collaborator) {
        if(colors.containsKey(collaborator)) {
            return colors.get(collaborator);
        }
        Random mRandom = new Random(System.currentTimeMillis());
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        int color;
        do {
            final int red = (baseRed + mRandom.nextInt(256)) / 2;
            final int green = (baseGreen + mRandom.nextInt(256)) / 2;
            final int blue = (baseBlue + mRandom.nextInt(256)) / 2;
            color = Color.rgb(red, green, blue);
        }while(colors.containsValue(color));

        colors.put(collaborator, color);
        return color;
    }

    static class ViewHolder {
        TextView icone;
        TextView name;
    }

}
