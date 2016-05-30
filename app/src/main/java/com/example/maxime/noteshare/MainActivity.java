package com.example.maxime.noteshare;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private ServerManager serverManager;
    private Note originalNote;
    private EditText titleEdit;
    private EditText contentEdit;
    private GestureDetectorCompat detector;

    private ListView menu_left;
    private ArrayList<Note> notes_right;

    private NoteAdapter localAdapter, adapter_right;

    private ViewPager mPageVp;
    private ImageView TabLine1,TabLine2;
    private TextView tab_local, tab_share;

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

        serverManager = ServerManager.getInstance();
        serverManager.loadNotesFromServer(this);
        notesManager = NotesManager.getInstance();
        originalNote = null;
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);

        TabLine1 = (ImageView) findViewById(R.id.tab_line1);
        TabLine2 = (ImageView) findViewById(R.id.tab_line2);

        mPageVp = (ViewPager) findViewById(R.id.vp);
        tab_local = (TextView) findViewById(R.id.id_local);
        tab_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(0);
                menu_left.setAdapter(localAdapter);
                tab_local.setTextColor(Color.WHITE);
                TabLine1.setBackgroundColor(Color.WHITE);
                tab_share.setTextColor(Color.GRAY);
                TabLine2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });
        tab_share = (TextView) findViewById(R.id.id_share);
        tab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(1);
                menu_left.setAdapter(adapter_right);
                tab_share.setTextColor(Color.WHITE);
                TabLine2.setBackgroundColor(Color.WHITE);
                tab_local.setTextColor(Color.GRAY);
                TabLine1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

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

    public void sendNote() {
        if(originalNote != null) {
            serverManager.sendNote(originalNote, this, new Runnable() {
                @Override
                public void run() {
                    showMessage(R.string.enregistrement_online);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    showMessage(R.string.error_enregistrement);
                }
            });
        }
    }
}
