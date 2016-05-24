package com.example.maxime.noteshare;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import android.widget.LinearLayout;

import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NotesManager notesManager;
    private Note originalNote;
    private EditText titleEdit;
    public EditText contentEdit;
    private GestureDetectorCompat detector;

    public ListView menu_left, menu_right;
    public ArrayList<Note> notes_left, notes_right;
    public ArrayList<HashMap<String, Object>> listItem;

    public LinearLayout choices;
    private SimpleAdapter adapter_left;

    private RequestQueue queue;
    private StringRequest query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationleftView = (NavigationView) findViewById(R.id.nav_left_view);
        navigationleftView.setNavigationItemSelectedListener(this);

        NavigationView navigationrightView = (NavigationView) findViewById(R.id.nav_right_view);
        navigationrightView.setNavigationItemSelectedListener(this);

        notesManager = NotesManager.getInstance(getApplicationContext());
        originalNote = new Note();
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);

        menu_left = (ListView) findViewById(R.id.menu_left);
        notes_left = new ArrayList<>();
        listItem = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm");
        Date today = null;
        try {
            today = format.parse(format.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i=0;i<5;i++)
        {
            notes_left.add(new Note("title" + i, "content" + i));
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemImage", R.mipmap.note_share_icon);
            map.put("ItemTitle", notes_left.get(i).getTitle());
            Date lastUpdate = null;
            try {
                lastUpdate = format.parse(format.format(notes_left.get(i).getLastUpdate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String date = null;
            if(lastUpdate != null && today != null && lastUpdate.compareTo(today)==0){
                date = format2.format(notes_left.get(i).getLastUpdate());
            }
            else if(today != null){
                date = format.format(lastUpdate);
            }
            map.put("ItemText", date);
            listItem.add(map);
        }

        adapter_left = new SimpleAdapter(this,listItem, R.layout.element_note,
                new String[] {"ItemImage","ItemTitle", "ItemText"},
                new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );

        menu_left.setAdapter(adapter_left);

//        menu_left = (ListView) findViewById(R.id.menu_left);
//        NoteAdapter adapter_left = new NoteAdapter(this,notes_left);
//        menu_left.setAdapter(adapter_left);

        menu_right = (ListView) findViewById(R.id.menu_right);
        notes_right = new ArrayList<>();
        notes_right.add(new Note("Title 3","Content 3"));
        notes_right.add(new Note("Title 4","Content 4"));
        NoteAdapter adapter_right = new NoteAdapter(this,notes_right);
        menu_right.setAdapter(adapter_right);

        choices = (LinearLayout) findViewById(R.id.choice_upload);

        this.detector = new GestureDetectorCompat(this, new MyGesture());

        contentEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        this.queue = Volley.newRequestQueue(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar w
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new) {
            createNewNote();
            return true;
        }
        if (id == R.id.action_save) {
            saveNote();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewNote() {
        originalNote = null;
        titleEdit.setText(null);
        contentEdit.setText(null);
    }

    private void saveNote() {
        System.out.println("Note "+ originalNote);
        System.out.println("Title "+ titleEdit.getText().toString());
        System.out.println("Content "+ contentEdit.getText().toString());
        System.out.println(contentEdit.getText());
        originalNote.setLastUpdate(new Date());
        originalNote = notesManager.createOrUpdate(originalNote, titleEdit.getText().toString(), contentEdit.getText().toString());
        HashMap<String, Object> map = new HashMap<>();
        map.put("ItemImage", R.mipmap.note_share_icon);
        map.put("ItemTitle", originalNote.getTitle());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm");
        Date lastUpdate = null;
        try {
            lastUpdate = format.parse(format.format(originalNote.getLastUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date today = new Date();
        String date = null;
        if(lastUpdate != null && today != null && lastUpdate.compareTo(today)==0){
            date = format2.format(originalNote.getLastUpdate());
        }
        else if(today != null){
            date = format.format(lastUpdate);
        }
        map.put("ItemText", date);
        listItem.add(map);
        adapter_left.notifyDataSetChanged();
        System.out.println(adapter_left);
        //adapter_left.add(map);
    }

    //pour des version de navigation view
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String string = null;

        switch(id){
            case R.id.nav_camera:
                string = "nav_camera";
                break;
            case R.id.nav_gallery:
                string = "nav_gallery";
                break;
            case R.id.nav_slideshow:
                string = "nav_slideshow";
                break;
            case R.id.nav_manage:
                string = "nav_manage";
                break;
            case R.id.nav_share:
                string = "nav_share";
                break;
            case R.id.nav_send:
                string = "nav_send";
                break;
        }

        if (!TextUtils.isEmpty(string))
            contentEdit.setText("You have clicked "+ string);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MyGesture extends GestureDetector.SimpleOnGestureListener{

        private static final int SWIPE_MIN_DISTANCE = 50;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;
        private boolean onTouch = false;

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
            choices.setVisibility(View.VISIBLE);

            Button send_host = (Button) findViewById(R.id.host);
            Button send_local = (Button) findViewById(R.id.local);

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

            Note n = new Note("noteTest","Bonjour, ceci est un test");

            SenderJSon final_url = new SenderJSon(n,"192.168.1.58","8080");

            String url = "http://192.168.1.58:8080";

            try{
                JSONObject objet = new JSONObject("{\"type\":\"example\"}");

                JsonObjectRequest toto = new JsonObjectRequest(url, objet, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.enregistrement_online, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), R.string.error_enregistrement, Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(toto);

            }catch (JSONException e){
                e.printStackTrace();
            }


            query = new StringRequest(Request.Method.GET, final_url.constructQuery(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), R.string.enregistrement_online, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), R.string.error_enregistrement, Toast.LENGTH_SHORT).show();
                }
            });

            choices.setVisibility(View.INVISIBLE);
        }

        public void onBottomSwipe(){
            saveNote();
            Toast.makeText(getApplicationContext(), R.string.enregistrement_local, Toast.LENGTH_SHORT).show();

            choices.setVisibility(View.INVISIBLE);
        }
    }
}
