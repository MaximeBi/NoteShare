package com.example.maxime.noteshare;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kapil on 12/05/2016.
 */
public class NoteAdapter extends ArrayAdapter<Note> {

    Context context;
    ArrayList<Note> data;
    private static LayoutInflater inflater = null;

    public NoteAdapter(Context context, ArrayList<Note> data) {
        //uper(context, 0, data);
        super(context, R.layout.activity_main, data);
        this.data = data;
        this.context=context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /*
    public NoteAdapter(Context context, ArrayList<Note> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = (LayoutInflater) LayoutInflater.from(getContext()).inflate(R.id.menu_left, parent, false);
    }
    */
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
        //View vi = convertView;
        //if (vi == null) {
            //vi = inflater.inflate(R.layout.activity_main, parent, false);
        //}
        //LayoutInflater inflater = context.getLayoutInflater();
        System.out.println("inflater : "+inflater);
        View vi = inflater.inflate(R.layout.element_list_note, null, true);
        if (!data.isEmpty()) {
            Note note =  data.get(position);

            TextView text = (TextView) vi.findViewById(R.id.note_title);
            text.setText(note.getTitle());

            TextView date = (TextView) vi.findViewById(R.id.note_date);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm");
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

            //date.setText(note.getLastUpdate().toString());

            /*
            TextView priority = (TextView) vi.findViewById(R.id.item_priority);
            priority.setText(note.getCreationDate().toString());

            TextView status = (TextView) vi.findViewById(R.id.item_status);
            status.setText(note.getLastUpdate().toString());

            TextView deadline = (TextView) vi.findViewById(R.id.item_deadline);
            deadline.setText(note.getId());
            */
        }
        return vi;
    }
}