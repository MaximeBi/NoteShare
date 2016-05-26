package com.example.maxime.noteshare;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotesManager {

    private static NotesManager instance = null;
    private static final String FOLDER_NAME = "NoteShare";
    private File folder;
    private ArrayList<Note> notes;
    private Context context;

    private NotesManager(Context context) {
        folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        notes = new ArrayList<>();
        this.context = context;
        loadNotesFromFolder();
    }

    public static NotesManager getInstance(Context context) {
        if(instance == null) {
            instance = new NotesManager(context);
        }
        return instance;
    }

    public Note createOrUpdate(Note original, String title, String content) {
        if(original == null) {
            return create(title, content);
        } else {
            return update(original, title, content);
        }
    }

    private Note create(String title, String content) {
        Note note = new Note(title, content);
        notes.add(note);
        createOrUpdateFile(note, R.string.note_created);
        return note;
    }

    private Note update(Note originalNote, String title, String content) {
        originalNote.setTitle(title);
        originalNote.setContent(content);
        originalNote.setLastUpdate(new Date());
        createOrUpdateFile(originalNote, R.string.note_updated);
        return originalNote;
    }

    private void createOrUpdateFile(Note note, int message) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(folder+File.separator+note.getId()+".ser")));
            oos.writeObject(note);
            oos.close();
            Toast.makeText(context, context.getResources().getString(message), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotesFromFolder() {
        File[] directoryListing = folder.listFiles();
        if (directoryListing != null) {
            for (File note : directoryListing) {
                Note n = loadNote(note);
                notes.add(n);
            }
        }
    }

    private Note loadNote(File file) {
        Note note = null;
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            note = (Note) input.readObject();
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return note;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
