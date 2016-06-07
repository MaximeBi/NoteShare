package com.example.maxime.noteshare;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Observable;

public abstract class NotesManager extends Observable {

    protected ArrayList<Note> notes;
    protected MainActivity activity;

    public NotesManager(MainActivity activity) {
        this.activity = activity;
        this.notes = new ArrayList<>();
    }

    protected String getLogin() {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.login_setting), null);
    }

    protected abstract void loadNotes();
    protected abstract void loadNotes(String keywords);
    protected abstract void deleteNotes(ArrayList<Note> notes);

    public void filter(String keywords) {
        notes.clear();
        loadNotes(keywords);
        notifyObservers();
    }

    public void delete(ArrayList<Note> notes) {
        deleteNotes(notes);
        notifyObservers();
    }

    public ArrayList<Note> getNotes() {
        notes.clear();
        loadNotes();
        notifyObservers();
        return notes;
    }

    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

}
