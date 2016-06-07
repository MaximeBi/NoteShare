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

public class LocalManager extends NotesManager {

    private static LocalManager instance = null;
    private static final String FOLDER_NAME = "NoteShare";
    private File folder;

    private LocalManager(MainActivity activity) {
        super(activity);
        folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static LocalManager getInstance(MainActivity activity) {
        if (instance == null) {
            instance = new LocalManager(activity);
        }
        return instance;
    }

    public Note create(String title, String content) {
        Note note = new Note(title, content);
        String login = getLogin();
        if(login != null) {
            note.setAuthor(login);
        }
        notes.add(note);
        createOrUpdateFile(note);
        notifyObservers();
        return note;
    }

    public Note update(Note originalNote, String title, String content) {
        originalNote.setTitle(title);
        originalNote.setContent(content);
        originalNote.setLastUpdate(new Date());
        createOrUpdateFile(originalNote);
        notifyObservers();
        return originalNote;
    }

    public void updateNotesAuthor(String author) {
        for(Note note : notes) {
            note.setAuthor(author);
        }
    }

    protected void loadNotes(String keywords) {
        File[] directoryListing = folder.listFiles();
        if (directoryListing != null) {
            for (File note : directoryListing) {
                Note n = loadNote(note);
                if (keywords == null || keywords != null && (n.getTitle().contains(keywords) || n.getKeywords().contains(keywords))) {
                    notes.add(n);
                }
            }
        }
    }

    protected void loadNotes() {
        loadNotes(null);
    }

    protected void deleteNotes(ArrayList<Note> notesToDelete) {
        for(Note n : notesToDelete) {
            deleteFile(n);
        }
        notes.removeAll(notesToDelete);
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

    private void deleteFile(Note note) {
        File file = new File(folder+File.separator+note.getId()+".ser");
        file.delete();
    }
}
