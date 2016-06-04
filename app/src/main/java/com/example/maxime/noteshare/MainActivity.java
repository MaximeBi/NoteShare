package com.example.maxime.noteshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final static String LOCAL_TEXT = "local_text";
    public final static String SERVER_TEXT = "server_text";
    public final static String FINAL_TEXT = "final_text";
    public final static int CODE = 1001;

    private NotesManager notesManager;
    private ServerManager serverManager;
    private Note originalNote;
    private EditText titleEdit;
    private EditText contentEdit;
    private GestureDetectorCompat detector;
    private ListView listView;
    private NoteAdapter localAdapter, serverAdapter;
    private TextView tabLocal, tabServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                resetListView();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        originalNote = null;
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);

        notesManager = NotesManager.getInstance(this);
        localAdapter = new NoteAdapter(this, notesManager.getNotes());
        serverManager = ServerManager.getInstance(this);
        serverAdapter = new NoteAdapter(this, serverManager.getNotes());

        tabLocal = (TextView) findViewById(R.id.id_local);
        tabLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetListView();
                updateTabs(0, localAdapter, tabLocal, R.id.tab_line1, R.id.local_actions, tabServer, R.id.tab_line2, R.id.server_actions, R.drawable.ic_sd_storage_white_36dp, R.drawable.ic_storage_black_36dp);
            }
        });

        tabServer = (TextView) findViewById(R.id.id_share);
        tabServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetListView();
                updateTabs(1, serverAdapter, tabServer, R.id.tab_line2, R.id.server_actions, tabLocal, R.id.tab_line1, R.id.local_actions, R.drawable.ic_sd_storage_black_36dp, R.drawable.ic_storage_white_36dp);
            }
        });

        listView = (ListView) findViewById(R.id.menu_left);
        listView.setAdapter(localAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
                    openNote(position);
                }
            }
        });

        setActionsButtonsListeners();

        this.detector = new GestureDetectorCompat(this, new GestureDetector(this));
        contentEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        EditText edit = (EditText) findViewById(R.id.text_search);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isLocalTab()) {
                    localAdapter.getFilter().filter(s.toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE) {
            if (resultCode == RESULT_OK) {
                String finalText =  data.getStringExtra(FINAL_TEXT);
                Toast.makeText(this, finalText, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setActionsButtonsListeners() {
        ImageButton localActionNew = (ImageButton) findViewById(R.id.local_action_new);
        final ImageButton localActionDelete = (ImageButton) findViewById(R.id.local_action_delete);
        ImageButton serverActionCollaborators = (ImageButton) findViewById(R.id.server_action_collaborators);
        ImageButton serverActionDelete = (ImageButton) findViewById(R.id.server_action_delete);
        ImageButton serverActionRefresh = (ImageButton) findViewById(R.id.server_action_refresh);

        localActionNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNote();
                closeDrawer();

            }
        });

        localActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    localActionDelete.setImageResource(R.drawable.ic_done_white_24dp);
                } else {
                    SparseBooleanArray checked = listView.getCheckedItemPositions();
                    ArrayList<Note> toDelete = new ArrayList<Note>();
                    for (int i = 0; i < listView.getCount(); i++) {
                        if (checked.get(i)) {
                            toDelete.add((Note) listView.getItemAtPosition(i));
                        }
                    }
                    listView.clearChoices();
                    notesManager.deleteNotes(toDelete);
                    localAdapter.notifyDataSetChanged();
                    listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    localActionDelete.setImageResource(R.drawable.ic_delete_white_24dp);
                }
            }
        });

        serverActionRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotesFromServer();
            }
        });
    }

    private void updateTabs(int currentItem, NoteAdapter adapter, TextView tabToHighlight, int idLineToHighlight, int idActionsToDisplay, TextView tabToUnhighlight, int idLineToUnhighlight, int idActionsToRemove, int idDrawableLocalIcon, int idDrawableServerIcon) {
        ViewPager mPageVp = (ViewPager) findViewById(R.id.vp);
        ImageView lineToHighlight = (ImageView) findViewById(idLineToHighlight);
        ImageView lineToUnhighlight = (ImageView) findViewById(idLineToUnhighlight);
        mPageVp.setCurrentItem(currentItem);
        listView.setAdapter(adapter);
        tabToHighlight.setTextColor(Color.WHITE);
        tabToUnhighlight.setTextColor(Color.BLACK);
        lineToHighlight.setBackgroundColor(Color.WHITE);
        lineToUnhighlight.setBackgroundColor(Color.BLACK);
        LinearLayout actionsToDisplay = (LinearLayout) findViewById(idActionsToDisplay);
        actionsToDisplay.setVisibility(View.VISIBLE);
        LinearLayout actionsToRemove = (LinearLayout) findViewById(idActionsToRemove);
        actionsToRemove.setVisibility(View.GONE);
        ImageView localIcon = (ImageView) findViewById(R.id.local_icon);
        localIcon.setImageResource(idDrawableLocalIcon);
        ImageView serverIcon = (ImageView) findViewById(R.id.server_icon);
        serverIcon.setImageResource(idDrawableServerIcon);
    }

    public void manageConflict(String localText, String serverText) {
        Intent intent = new Intent(this, ConflictResolutionActivity.class);
        intent.putExtra(LOCAL_TEXT, localText);
        intent.putExtra(SERVER_TEXT, serverText);
        startActivityForResult(intent, CODE);
    }

    private void createNewNote() {
        originalNote = null;
        titleEdit.setText(null);
        contentEdit.setText(null);
    }

    private void openNote(int position) {
        originalNote = (Note) listView.getItemAtPosition(position);
        titleEdit.setText(originalNote.getTitle());
        contentEdit.setText(originalNote.getContent());
        closeDrawer();
    }

    public void saveNote() {
        if (originalNote == null) {
            originalNote = notesManager.create(titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_created);
        } else {
            originalNote = notesManager.update(originalNote, titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_updated);
        }
        localAdapter.notifyDataSetChanged();
    }

    public void sendNote() {
        if (originalNote != null) {
            serverManager.sendNote(originalNote);
        }
    }

    private void loadNotesFromServer() {
        serverManager.loadNotesFromServer();
        serverAdapter.notifyDataSetChanged();
    }

    public void updateAuthor(String author) {
        notesManager.updateNotesAuthor(author);
    }

    private boolean closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    private void resetListView() {
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listView.clearChoices();
        if (isLocalTab()) {
            localAdapter.notifyDataSetChanged();
            ((ImageButton) findViewById(R.id.local_action_delete)).setImageResource(R.drawable.ic_delete_white_24dp);
        } else {
            serverAdapter.notifyDataSetChanged();
            ((ImageButton) findViewById(R.id.server_action_delete)).setImageResource(R.drawable.ic_delete_white_24dp);
        }
    }

    private boolean isLocalTab() {
        return ((ViewPager) findViewById(R.id.vp)).getCurrentItem() == 0;
    }

    public void showMessage(int id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }
}
