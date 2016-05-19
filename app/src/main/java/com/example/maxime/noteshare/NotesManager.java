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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kapil on 12/05/2016.
 */
public class NotesManager {

    private static NotesManager instance = null;
    private static final String FOLDER_NAME = "NoteShare";
    private File folder;
    private Map<String, Note> notes;
    private Context context;

    public NotesManager(Context context) {
        folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        notes = new HashMap<String, Note>();
        this.context = context;
        loadNotesFromFolder();
    }

    public static NotesManager getInstance(Context context) {
        if(instance == null) {
            instance = new NotesManager(context);
        }
        return instance;
    }

    public Note createOrUpdate(Note note) {
        if(note.hasId()) {
            return update(note);
        } else {
            return create(note);
        }
    }

    private Note create(Note newNote) {
        Note note = new Note(newNote);
        notes.put(note.getId(), note);
        createOrUpdateFile(note);
        return note;
    }

    private Note update(Note updatedNote) {
        Note note = notes.get(updatedNote.getId());
        note.update(updatedNote);
        createOrUpdateFile(note);
        return note;
    }

    public void createOrUpdateFile(Note note) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(folder+File.separator+note.getId()+".ser")));
            oos.writeObject(note);
            oos.close();
            Toast.makeText(context, note.getId()+".ser created", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNotesFromFolder() {
        File[] directoryListing = folder.listFiles();
        if (directoryListing != null) {
            for (File note : directoryListing) {
                Note n = loadNote(note);
                notes.put(n.getId(), n);
            }
        }
    }

    public Note loadNote(File file) {
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

    public File getFolder() {
        return folder;
    }

    public Map<String, Note> getNotes() {
        return notes;
    }
}
