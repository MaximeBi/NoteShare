package com.example.maxime.noteshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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

    private boolean hasLogin(final MainActivity activity) {
        if(getLogin(activity) == null) {
            createLoginDialog(activity, new Runnable() {
                @Override
                public void run() {
                    loadNotesFromServer(activity);
                }
            }).show();
            return false;
        }
        return true;
    }

    public void sendNote(Note note, final MainActivity activity) {
         if(hasLogin(activity)) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(toJson(note)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        activity.showMessage(R.string.enregistrement_online);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.showMessage(R.string.error_enregistrement);
                    }
                });
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNotesFromServer(final MainActivity activity, String parameters) {
        if(hasLogin(activity)) {
            String keywords = "";
            String login = "/login/"+getLogin(activity);
            if(parameters != null && !parameters.isEmpty()) {
                keywords = "/keywords/" + parameters.replaceAll(" ", ",");
            }
            RequestQueue queue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url+login+keywords, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    toNotes(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.showMessage(R.string.error_connexion_serveur);
                }
            });
            queue.add(stringRequest);
        }
    }

    public void loadNotesFromServer(final MainActivity activity) {
        loadNotesFromServer(activity, null);
    }

    private String getLogin(MainActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(activity.getString(R.string.login_setting), null);
    }

    private void setLogin(String login, MainActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.login_setting), login);
        editor.commit();
        activity.updateAuthor(login);
    }

    private Dialog createLoginDialog(final MainActivity activity, final Runnable runnable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.login_dialog);
        builder.setMessage(R.string.enter_login);

        // Use an EditText view to get user input.
        final EditText input = new EditText(activity);
        builder.setView(input);

        builder.setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                setLogin(input.getText().toString(), activity);
                runnable.run();
                return;
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.showMessage(R.string.login_needed);
                return;
            }
        });

        return builder.create();
    }

    public String toJson(Note n){
        Gson gson = new Gson();
        return gson.toJson(n);
    }

    public void toNotes(String n) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Note>>() {
        }.getType();
        notes = gson.fromJson(n, listType);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
