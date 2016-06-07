package com.example.maxime.noteshare;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class Adapter<T> extends ArrayAdapter<T> {

    protected HashMap<T, Integer> colors;
    protected ArrayList<T> data;
    protected static LayoutInflater inflater = null;

    public Adapter(Context context, int resource, ArrayList<T> data) {
        super(context, resource);
        this.colors = new HashMap<>();
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected int getColor(T item) {
        if(colors.containsKey(item)) {
            return colors.get(item);
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

        colors.put(item, color);
        return color;
    }
}
