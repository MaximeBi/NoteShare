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

import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
    public RequestQueue queue;
    public StringRequest query;
    public ListView menu_left, menu_right;
    public ArrayList<Note> notes_left, notes_right;

    public LinearLayout choices;
    private NoteAdapter adapter_left;


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
        originalNote = null;
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);

        menu_left = (ListView) findViewById(R.id.menu_left);
        //adapter_left = new NoteAdapter(this, notesManager.getNotes());
        //menu_left.setAdapter(adapter_left);
        notes_left = new ArrayList<>();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();

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
            notes_left.add(new Note("title"+i, "content"+i));
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
            if(today != null && lastUpdate.compareTo(today)==0){
                date = format2.format(notes_left.get(i).getLastUpdate());
            }
            else if(today != null){
                date = format.format(lastUpdate);
            }
            map.put("ItemText", date);
            listItem.add(map);
        }

        SimpleAdapter adapter_left = new SimpleAdapter(this,listItem, R.layout.element_note,
                new String[] {"ItemImage","ItemTitle", "ItemText"},
                new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}
        );

        menu_left.setAdapter(adapter_left);//为ListView绑定适配器

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

        queue = Volley.newRequestQueue(this);
        String url = "http://172.25.12.95:8080";

        query = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Note partagée sur le serveur", Toast.LENGTH_LONG).show();
                contentEdit.setText(response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erreur partage de la note", Toast.LENGTH_LONG).show();
            }
        });

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
        // Handle action bar item clicks here. The action bar will
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
        adapter_left.notifyDataSetChanged();
    }

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
                string = "通知";
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
            queue.add(query);
            choices.setVisibility(View.INVISIBLE);
        }

        public void onBottomSwipe(){
            saveNote();
            Toast.makeText(getApplicationContext(),"Note sauvegardée en mémoire locale", Toast.LENGTH_SHORT).show();
            choices.setVisibility(View.INVISIBLE);
        }
    }
}
