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
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public EditText mTextView;
    private GestureDetectorCompat detector;
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

        mTextView = (EditText) findViewById(R.id.editText);
        this.detector = new GestureDetectorCompat(this, new MyGesture());

        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
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
        if (id == R.id.action_settings) {
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
                string = "通知";
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
        }

        public void onBottomSwipe(){
            Toast.makeText(getApplicationContext(),"BOTTOM", Toast.LENGTH_SHORT).show();
        }
    }
}
