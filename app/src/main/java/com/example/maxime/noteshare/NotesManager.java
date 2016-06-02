package com.example.maxime.noteshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

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

public class NotesManager {

    private static NotesManager instance = null;
    private static final String FOLDER_NAME = "NoteShare";
    private File folder;
    private ArrayList<Note> notes;
    private MainActivity activity;

    private NotesManager(MainActivity activity) {
        folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        notes = new ArrayList<>();
        this.activity = activity;
        loadNotesFromFolder();
    }

    public static NotesManager getInstance(MainActivity activity) {
        if(instance == null) {
            instance = new NotesManager(activity);
        }
        return instance;
    }

    private String getLogin() {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.login_setting), null);
    }

    public Note create(String title, String content) {
        Note note = new Note(title, content);
        String login = getLogin();
        if(login != null) {
            note.setAuthor(login);
        }
        notes.add(note);
        createOrUpdateFile(note);
        return note;
    }

    public Note update(Note originalNote, String title, String content) {
        originalNote.setTitle(title);
        originalNote.setContent(content);
        originalNote.setLastUpdate(new Date());
        createOrUpdateFile(originalNote);
        return originalNote;
    }

    public boolean deleteNotes(ArrayList<Note> notesToDelete) {
        for(Note n : notesToDelete) {
            deleteFile(n);
        }
        return notes.removeAll(notesToDelete);
    }

    private void deleteFile(Note note) {
        File file = new File(folder+File.separator+note.getId()+".ser");
        file.delete();
    }

    private void createOrUpdateFile(Note note) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(folder+File.separator+note.getId()+".ser")));
            oos.writeObject(note);
            oos.close();
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

    public void updateNotesAuthor(String author) {
        for(Note note : notes) {
            note.setAuthor(author);
        }
    }

    public ArrayList<Note> getNotes(String keywords) {
        if(keywords != null && !keywords.isEmpty()) {
            ArrayList<Note> filtered = new ArrayList<Note>();
            for (Note n : notes) {
                if (n.getTitle().contains(keywords) || n.getKeywords().contains(keywords)) {
                    filtered.add(n);
                }
            }
            return filtered;
        }
        return notes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
