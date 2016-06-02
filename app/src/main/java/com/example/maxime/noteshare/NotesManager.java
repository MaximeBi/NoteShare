package com.example.maxime.noteshare;

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

    private NotesManager() {
        folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        notes = new ArrayList<>();
        loadNotesFromFolder();
    }

    public static NotesManager getInstance() {
        if(instance == null) {
            instance = new NotesManager();
        }
        return instance;
    }

    public Note create(String title, String content) {
        Note note = new Note(title, content);
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

    //TODO search by keywords
    public ArrayList<Note> getNotes(String keywords) {
        return notes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
