package com.example.maxime.noteshare;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import android.widget.LinearLayout;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NotesManager notesManager;
    private Note originalNote;
    private EditText titleEdit;
    public EditText contentEdit;
    private GestureDetectorCompat detector;

    public ListView menu_left, menu_right;
    private ArrayList<Note> notes_right;

    public LinearLayout choices;
    private NoteAdapter localAdapter;

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

        notesManager = NotesManager.getInstance(getApplicationContext());
        originalNote = null;
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);

        menu_left = (ListView) findViewById(R.id.menu_left);
        ArrayList<Note> n = notesManager.getNotes();
        localAdapter = new NoteAdapter(getApplicationContext(), notesManager.getNotes());

        menu_left.setAdapter(localAdapter);
        menu_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openNote(position);
            }
        });

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
        originalNote = notesManager.createOrUpdate(originalNote, titleEdit.getText().toString(), contentEdit.getText().toString());
        localAdapter.notifyDataSetChanged();
    }

    private void openNote(int position) {
        originalNote = localAdapter.getItem(position);
        titleEdit.setText(originalNote.getTitle());
        contentEdit.setText(originalNote.getContent());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
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

            SenderJSon final_url = new SenderJSon(originalNote,"172.25.12.95","8080");

            String url = "http://172.25.12.95:8080";

            try{
                JSONObject objet = new JSONObject(final_url.noteToJSon());

                JsonObjectRequest toto = new JsonObjectRequest(final_url.getFinalUrl(), objet, new Response.Listener<JSONObject>() {
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

            choices.setVisibility(View.INVISIBLE);
        }

        public void onBottomSwipe(){
            saveNote();
            Toast.makeText(getApplicationContext(), R.string.enregistrement_local, Toast.LENGTH_SHORT).show();

            choices.setVisibility(View.INVISIBLE);
        }
    }
}
