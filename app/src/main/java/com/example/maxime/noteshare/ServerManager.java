package com.example.maxime.noteshare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

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

public class ServerManager extends NotesManager {

    private static final String IP_ADDRESS = "172.25.12.95";
    private static final String PORT = "8080";
    private String url;
    private static ServerManager instance = null;

    private ServerManager(MainActivity activity) {
        super(activity);
        this.url = "http://" + IP_ADDRESS + ":" + PORT;
    }

    public static ServerManager getInstance(MainActivity activity) {
        if(instance == null) {
            instance = new ServerManager(activity);
        }
        return instance;
    }

    public void sendNote(final Note note) {
         if(hasLogin()) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(Tools.toJson(note)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ServerManager sendNote", response.toString());
                        activity.showMessage(R.string.enregistrement_online);
                        notes.add(note);
                        notifyObservers();
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

    protected void loadNotes(String keywords) {
        if(hasLogin()) {
            String parameters = "";
            String login = "/login/"+getLogin();
            if(keywords != null && !keywords.isEmpty()) {
                parameters = "/keywords/" + keywords.toLowerCase().replaceAll(" ", ",");
            }
            RequestQueue queue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url+login+parameters, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("ServerManager loadNotes", response);
                    notes.clear();
                    notes.addAll(Tools.toNotes(response));
                    notifyObservers();
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

    protected void loadNotes() {
        loadNotes(null);
    }

    protected void deleteNotes(final ArrayList<Note> notes) {
        Log.d("ServerManager", "Delete Notes");
        if(hasLogin()) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url+"/delete", new JSONObject(Tools.toJson(notes)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ServerManager delete", response.toString());
                        activity.showMessage(R.string.enregistrement_online);
                        notes.removeAll(notes);
                        notifyObservers();
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

    private boolean hasLogin() {
        if(getLogin() == null) {
            createLoginDialog(new Runnable() {
                @Override
                public void run() {
                    getNotes();
                }
            }).show();
            return false;
        }
        return true;
    }

    private void setLogin(String login) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(activity.getString(R.string.login_setting), login);
        editor.commit();
        activity.updateAuthor(login);
    }

    private Dialog createLoginDialog(final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.login_dialog);
        builder.setMessage(R.string.enter_login);

        final EditText input = new EditText(activity);
        builder.setView(input);

        builder.setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                setLogin(input.getText().toString());
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
}
