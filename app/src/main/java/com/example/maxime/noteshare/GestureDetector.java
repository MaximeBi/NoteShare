package com.example.maxime.noteshare;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private boolean onTouch = false;
    private LinearLayout choices;
    private MainActivity activity;
    private RequestQueue queue;

    public GestureDetector(MainActivity activity) {
        this.activity = activity;
        this.queue = Volley.newRequestQueue(activity);
        this.choices = (LinearLayout) activity.findViewById(R.id.choice_upload);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if(onTouch){
            choices.setVisibility(View.INVISIBLE);
            this.onTouch = false;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        this.onTouch = true;
        this.choices.setVisibility(View.VISIBLE);

        Button send_host = (Button) activity.findViewById(R.id.host);
        Button send_local = (Button) activity.findViewById(R.id.local);

        send_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopSwipe();
            }
        });

        send_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBottomSwipe();
            }
        });

        super.onLongPress(e);
    }

    public void onTopSwipe(){

        SenderJSon final_url = new SenderJSon(activity.getOriginalNote());

        try{
            JsonObjectRequest toto = new JsonObjectRequest(final_url.getFinalUrl(), new JSONObject(final_url.noteToJSon()), new Response.Listener<JSONObject>() {
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

            queue.add(toto);

        }catch (JSONException e){
            e.printStackTrace();
        }

        choices.setVisibility(View.INVISIBLE);
    }

    public void onBottomSwipe(){
        activity.saveNote();
        choices.setVisibility(View.INVISIBLE);
    }
}
