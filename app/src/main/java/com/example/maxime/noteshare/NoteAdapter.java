package com.example.maxime.noteshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kapil on 12/05/2016.
 */
public class NoteAdapter extends BaseAdapter {

    Context context;
    ArrayList<Note> data;
    private static LayoutInflater inflater = null;

    public NoteAdapter(Context context, ArrayList<Note> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.note, null);
        }
        if (!data.isEmpty()) {
            Note note = (Note) data.get(position);
            TextView text = (TextView) vi.findViewById(R.id.item_text);
            text.setText(note.getTitle());

            TextView priority = (TextView) vi.findViewById(R.id.item_priority);
            priority.setText(note.getCreationDate().toString());

            TextView status = (TextView) vi.findViewById(R.id.item_status);
            status.setText(note.getLastUpdate().toString());

            TextView deadline = (TextView) vi.findViewById(R.id.item_deadline);
            deadline.setText(note.getId());
        }
        return vi;
    }
}