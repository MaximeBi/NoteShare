package com.example.maxime.noteshare;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NotesManager notesManager;
    private Note originalNote;
    private EditText titleEdit;
    private EditText contentEdit;
    private GestureDetectorCompat detector;

    private ListView menu_left, menu_right;
    private ArrayList<Note> notes_right;

    private NoteAdapter localAdapter, adapter_right;

    private ViewPager mPageVp;
    private ImageView mTabLineIv;
    private TextView tab_local, tab_share;
    private int currentIndex;
    private int screenWidth;

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

        notesManager = NotesManager.getInstance();
        originalNote = null;
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);


        mPageVp = (ViewPager) findViewById(R.id.vp);
        tab_local = (TextView) findViewById(R.id.id_local);
        tab_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(0);
                menu_left.setAdapter(localAdapter);
                tab_local.setTextColor(Color.WHITE);
                tab_share.setTextColor(Color.GRAY);
            }
        });
        tab_share = (TextView) findViewById(R.id.id_share);
        tab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(1);
                menu_left.setAdapter(adapter_right);
                tab_share.setTextColor(Color.WHITE);
                tab_local.setTextColor(Color.GRAY);
            }
        });
        mTabLineIv = (ImageView) findViewById(R.id.id_tab_line_iv);

        menu_left = (ListView) findViewById(R.id.menu_left);
        localAdapter = new NoteAdapter(getApplicationContext(), notesManager.getNotes());
        menu_left.setAdapter(localAdapter);

        menu_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openNote(position);
            }
        });

        notes_right = new ArrayList<>();
        notes_right.add(new Note("Title 3", "Content 3"));
        notes_right.add(new Note("Title 4", "Content 4"));
        adapter_right = new NoteAdapter(this,notes_right);

        this.detector = new GestureDetectorCompat(this, new GestureDetector(this));

        contentEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        mPageVp.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_local.setTextColor(Color.GRAY);
                tab_share.setTextColor(Color.GRAY);
                switch (position) {
                    case 0:
                        tab_local.setTextColor(Color.WHITE);
                        break;
                    case 1:
                        tab_share.setTextColor(Color.WHITE);
                        break;
                }
                currentIndex = position;
            }
        });

        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
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

    public void showMessage(int id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void createNewNote() {
        originalNote = null;
        titleEdit.setText(null);
        contentEdit.setText(null);
    }

    public void saveNote() {
        if(originalNote == null) {
            originalNote = notesManager.create(titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_created);
        } else {
            originalNote = notesManager.update(originalNote, titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_updated);
        }
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

    public Note getOriginalNote() {
        return originalNote;
    }
}
