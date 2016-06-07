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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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
         if(hasLogin(new Runnable() {
             @Override
             public void run() {
                 sendNote(note);
             }
         })) {
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

    public void updateCollaborators(final Note note) {
        if(hasLogin(new Runnable() {
            @Override
            public void run() {
                updateCollaborators(note);
            }
        })) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url+"/collaborators", new JSONObject(Tools.toJson(note)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ServerManager collab", response.toString());
                        activity.showMessage(R.string.collaborators_updated);
                        for(Note n : notes) {
                            if(n.getId() == note.getId()) {
                                n.setCollaborators(note.getCollaborators());
                                break;
                            }
                        }
                        notifyObservers();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.showMessage(R.string.error_collaborators_update);
                    }
                });
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadNotes(final String keywords) {
        if(hasLogin(new Runnable() {
            @Override
            public void run() {
                loadNotes(keywords);
            }
        })) {
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
        if(hasLogin(new Runnable() {
            @Override
            public void run() {
                deleteNotes(notes);
            }
        })) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            try {
                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.DELETE, url, new JSONArray(Tools.toJson(notes)), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("ServerManager delete", response.toString());
                        activity.showMessage(R.string.delete_done);
                        notes.removeAll(notes);
                        notifyObservers();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.showMessage(R.string.error_delete);
                    }
                });
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hasLogin(final Runnable runnable) {
        if(getLogin() == null) {
            createLoginDialog(runnable).show();
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
