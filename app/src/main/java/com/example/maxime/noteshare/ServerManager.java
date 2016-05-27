package com.example.maxime.noteshare;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServerManager {

    private static final String IP_ADDRESS = "172.25.12.95";
    private static final String PORT = "8080";
    private String url;
    private static ServerManager instance = null;
    private ArrayList<Note> notes;

    private ServerManager() {
        this.url = "http://" + IP_ADDRESS + ":" + PORT;
    }

    public static ServerManager getInstance() {
        if(instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void sendNote(Note note, Context context, final Runnable onResponse, final Runnable onError) {
        RequestQueue queue = Volley.newRequestQueue(context);
        try{
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(toJson(note)), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onResponse.run();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onError.run();
                }
            });
            queue.add(jsonObjectRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void loadNotesFromServer(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("testestestest", response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("testestestest", "error");
            }
        });
        queue.add(stringRequest);
    }

    public String toJson(Note n){
        Gson gson = new Gson();
        return gson.toJson(n);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
