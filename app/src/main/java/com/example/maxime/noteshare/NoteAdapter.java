package com.example.maxime.noteshare;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class NoteAdapter extends Adapter<Note> implements Observer {

//    static class ViewHolder {
//        TextView icon;
//        TextView title;
//        TextView date;
//    }

    private NotesManager notesManager;

    public NoteAdapter(Context context, NotesManager notesManager) {
        super(context, R.layout.element_note, notesManager.getNotes());
        this.notesManager = notesManager;
        this.notesManager.addObserver(this);
        Collections.sort(this.data);
    }

    @Override
    public void notifyDataSetChanged() {
        if(this.data != null) {
            Collections.sort(this.data);
        }
        super.notifyDataSetChanged();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.element_note, null);
//            viewHolder = new ViewHolder();
//            viewHolder.icon = (TextView) convertView.findViewById(R.id.note_icon);
//            viewHolder.title = (TextView) convertView.findViewById(R.id.note_title);
//            viewHolder.date = (TextView) convertView.findViewById(R.id.note_date);
//            convertView.setTag(viewHolder);
//        }
//        else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        Note note = getItem(position);
//
//        viewHolder.icon.setText(note.getTitle().toUpperCase().substring(0,1));
//        ((GradientDrawable) viewHolder.icon.getBackground()).setColor(getColor(note));
//        viewHolder.title.setText(note.getTitle());
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
//        try {
//            Date lastUpdate = format.parse(format.format(note.getLastUpdate()));
//            Date today = format.parse(format.format(new Date()));
//
//            if(lastUpdate.compareTo(today)==0){
//                viewHolder.date.setText(format2.format(note.getLastUpdate()));
//            }
//            else{
//                viewHolder.date.setText(format.format(lastUpdate));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return convertView;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = inflater.inflate(R.layout.element_note, null, true);
        if (!data.isEmpty()) {
            Note note =  data.get(position);

            TextView imageView = (TextView) vi.findViewById(R.id.note_icon);
            imageView.setText(note.getTitle().toUpperCase().substring(0,1));
            ((GradientDrawable) imageView.getBackground()).setColor(getColor(note));
            
            TextView title = (TextView) vi.findViewById(R.id.note_title);
            title.setText(note.getTitle());

            TextView date = (TextView) vi.findViewById(R.id.note_date);
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