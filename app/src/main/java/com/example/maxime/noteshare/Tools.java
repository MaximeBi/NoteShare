package com.example.maxime.noteshare;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class Tools {

    public static String toJson(Note n){
        Gson gson = new Gson();
        return gson.toJson(n);
    }

    public static String toJson(ArrayList<Note> n){
        Gson gson = new Gson();
        return gson.toJson(n);
    }

    public static String toJson(Map<String, Object> map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static ArrayList<Note> toNotes(String n) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Note>>() {
        }.getType();
        return gson.fromJson(n, listType);
    }

    public static Note toNote(String n) {
        Gson gson = new Gson();
        return gson.fromJson(n, Note.class);
    }
}
