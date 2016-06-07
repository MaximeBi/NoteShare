package com.example.maxime.noteshare;

import android.content.Context;
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
import java.util.Observable;
import java.util.Observer;

public class NoteAdapter extends ArrayAdapter<Note> implements Observer {

    private ArrayList<Note> data;
    private static LayoutInflater inflater = null;
    private NotesManager notesManager;

    public NoteAdapter(Context context, NotesManager notesManager) {
        super(context, R.layout.element_note);
        this.notesManager = notesManager;
        this.notesManager.addObserver(this);
        this.data = this.notesManager.getNotes();
        Collections.sort(this.data);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged() {
        if(this.data != null) {
            Collections.sort(this.data);
        }
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
    public void update(Observable observable, Object data) {
        notifyDataSetChanged();
    }
}