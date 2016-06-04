package com.example.maxime.noteshare;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NoteAdapter extends ArrayAdapter<Note> {

    Context context;
    ArrayList<Note> data;
    private static LayoutInflater inflater = null;
    private MainActivity activity;

    public NoteAdapter(MainActivity activity, ArrayList<Note> data) {
        super(activity, R.layout.activity_main, data);
        this.activity = activity;
        this.data = data;
        Collections.sort(this.data);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged() {
        Collections.sort(this.data);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Note getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = inflater.inflate(R.layout.element_note, null, true);
        if (!data.isEmpty()) {
            Note note =  data.get(position);

            ImageView imageView = (ImageView) vi.findViewById(R.id.ItemImage);
            imageView.setImageResource(R.mipmap.note_share_icon);

            TextView title = (TextView) vi.findViewById(R.id.ItemTitle);
            title.setText(note.getTitle());

            TextView date = (TextView) vi.findViewById(R.id.ItemText);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            try {
                Date lastUpdate = format.parse(format.format(note.getLastUpdate()));
                Date today = format.parse(format.format(new Date()));

                if(lastUpdate.compareTo(today)==0){
                    date.setText(format2.format(note.getLastUpdate()));
                }
                else{
                    date.setText(format.format(lastUpdate));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return vi;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<Note>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    results.values = NotesManager.getInstance(activity).getNotes();
                    results.count = NotesManager.getInstance(activity).getNotes().size();
                } else {
                    ArrayList<Note> filteredList = new ArrayList<Note>();

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < data.size(); i++) {
                        Note n = data.get(i);
                        if (n.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))  {
                            filteredList.add(n);
                        }
                    }

                    results.count = filteredList.size();
                    results.values = filteredList;
                }
                return results;
            }
        };

        return filter;
    }
}