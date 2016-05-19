package com.example.maxime.noteshare;

import android.app.DownloadManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public EditText mTextView;
    private GestureDetectorCompat detector;
    public RequestQueue queue;
    public StringRequest query;
    public ListView menu_left_List; //MenuLeft
    public ListView menur_right_List; //MenuRight
    public ArrayList<Note> notes_left, notes_right;
    //test
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

        mTextView = (EditText) findViewById(R.id.content_edit);
        this.detector = new GestureDetectorCompat(this, new MyGesture());

        menu_left_List = (ListView) findViewById(R.id.menu_left);
        notes_left = new ArrayList<>();
        notes_left.add(new Note("title test", "context test"));
        notes_left.add(new Note("title test2", "context test2"));
        NoteAdapter adapter_left = new NoteAdapter(MainActivity.this, notes_left);
        menu_left_List.setAdapter(adapter_left);

        menur_right_List = (ListView) findViewById(R.id.menu_right);
        notes_right = new ArrayList<>();
        notes_right.add(new Note("title test3", "context test3"));
        notes_right.add(new Note("title test4", "context test4"));
        NoteAdapter adapter_right = new NoteAdapter(MainActivity.this, notes_right);
        menur_right_List.setAdapter(adapter_right);

        mTextView.setOnTouchListener(new View.OnTouchListener() {
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
                mTextView.setText(response);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                string = "nav_manage";
                break;
            case R.id.nav_share:
                string = "nav_share";
                break;
            case R.id.nav_send:
                string = "nav_send";
                break;
        }
        /*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */
        if (!TextUtils.isEmpty(string))
            mTextView.setText("You have clicked "+string);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MyGesture extends GestureDetector.SimpleOnGestureListener{

        private static final int SWIPE_MIN_DISTANCE = 50;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if (diffX > 0) {
                            onRightSwipe();
                        } else {
                            onLeftSwipe();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        if (diffY > 0) {
                            onBottomSwipe();
                        } else {
                            onTopSwipe();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        public void onLeftSwipe(){
            Toast.makeText(getApplicationContext(),"LEFT", Toast.LENGTH_SHORT).show();
        }

        public void onRightSwipe(){
            Toast.makeText(getApplicationContext(),"RIGHT", Toast.LENGTH_SHORT).show();
        }

        public void onTopSwipe(){
            Toast.makeText(getApplicationContext(),"TOP", Toast.LENGTH_SHORT).show();
            queue.add(query);
        }

        public void onBottomSwipe(){
            Toast.makeText(getApplicationContext(),"BOTTOM", Toast.LENGTH_SHORT).show();
        }
    }
}
