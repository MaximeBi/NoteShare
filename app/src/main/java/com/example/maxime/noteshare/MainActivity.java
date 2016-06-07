package com.example.maxime.noteshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public final static String LOCAL_TEXT = "local_text";
    public final static String SERVER_TEXT = "server_text";
    public final static String FINAL_TEXT = "final_text";
    public final static String NOTE = "note";
    public final static int CODE_CONFLICT_RESOLUTION = 1001;
    public final static int CODE_COLLABORATORS_LIST = 2001;

    private LocalManager localManager;
    private ServerManager serverManager;
    private Note originalNote;
    private EditText titleEdit;
    private EditText contentEdit;
    private GestureDetectorCompat detector;
    private ListView listView;
    private NoteAdapter localAdapter, serverAdapter;
    private TextView tabLocal, tabServer;
    private int currentTab;

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

        localManager = LocalManager.getInstance(this);
        localAdapter = new NoteAdapter(this, localManager);
        serverManager = ServerManager.getInstance(this);
        serverAdapter = new NoteAdapter(this, serverManager);

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
                if(findViewById(R.id.server_action_collaborators).isFocused()) {
                    manageCollaborators((Note) listView.getItemAtPosition(position));
                } else if (listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
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
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 2) {
                    if (isLocalTab()) {
                        localManager.filter(s.toString());
                    } else {
                        serverManager.filter(s.toString());
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
        if (requestCode == CODE_CONFLICT_RESOLUTION) {
            if (resultCode == RESULT_OK) {
                String finalText =  data.getStringExtra(FINAL_TEXT);
                Toast.makeText(this, finalText, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CODE_COLLABORATORS_LIST) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(NOTE);
                serverManager.updateCollaborators(note);
            }
        }
    }

    private void setActionsButtonsListeners() {
        ImageButton localActionNew = (ImageButton) findViewById(R.id.local_action_new);
        ImageButton localActionDelete = (ImageButton) findViewById(R.id.local_action_delete);
        final ImageButton serverActionCollaborators = (ImageButton) findViewById(R.id.server_action_collaborators);
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
                deleteNotesListener(localManager, R.id.local_action_delete);
            }
        });

        serverActionCollaborators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(serverActionCollaborators.isFocused()) {
                    serverActionCollaborators.setFocusableInTouchMode(false);
                    serverActionCollaborators.clearFocus();
                } else {
                    serverActionCollaborators.setFocusableInTouchMode(true);
                    serverActionCollaborators.requestFocus();
                }
            }
        });

        serverActionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotesListener(serverManager, R.id.server_action_delete);
            }
        });

        serverActionRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverManager.getNotes();
            }
        });
    }

    private void deleteNotesListener(NotesManager notesManager, int idActionDeleteButton) {
        ImageButton actionDelete = (ImageButton) findViewById(idActionDeleteButton);
        if (listView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            actionDelete.setImageResource(R.drawable.ic_done_white_24dp);
        } else {
            SparseBooleanArray checked = listView.getCheckedItemPositions();
            ArrayList<Note> toDelete = new ArrayList<Note>();
            for (int i = 0; i < listView.getCount(); i++) {
                if (checked.get(i)) {
                    toDelete.add((Note) listView.getItemAtPosition(i));
                }
            }
            listView.clearChoices();
            notesManager.delete(toDelete);
            listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            actionDelete.setImageResource(R.drawable.ic_delete_white_24dp);
        }
    }

    private void updateTabs(int currentTab, NoteAdapter adapter, TextView tabToHighlight, int idLineToHighlight, int idActionsToDisplay, TextView tabToUnhighlight, int idLineToUnhighlight, int idActionsToRemove, int idDrawableLocalIcon, int idDrawableServerIcon) {
        this.currentTab = currentTab;
        ImageView lineToHighlight = (ImageView) findViewById(idLineToHighlight);
        ImageView lineToUnhighlight = (ImageView) findViewById(idLineToUnhighlight);
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
        startActivityForResult(intent, CODE_CONFLICT_RESOLUTION);
    }

    public void manageCollaborators(Note n) {
        Intent intent = new Intent(this, CollaboratorsListActivity.class);
        intent.putExtra(NOTE, n);
        startActivityForResult(intent, CODE_COLLABORATORS_LIST);
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
            originalNote = localManager.create(titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_created);
        } else {
            originalNote = localManager.update(originalNote, titleEdit.getText().toString(), contentEdit.getText().toString());
            showMessage(R.string.note_updated);
        }
    }

    public void sendNote() {
        if (originalNote != null) {
            serverManager.sendNote(originalNote);
        }
    }

    public void updateAuthor(String author) {
        localManager.updateNotesAuthor(author);
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
        listView.clearChoices();
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        EditText edit = (EditText) findViewById(R.id.text_search);
        if(!edit.getText().toString().isEmpty()) {
            edit.setText("");
        }
        if (isLocalTab()) {
            localAdapter.notifyDataSetChanged();
            ((ImageButton) findViewById(R.id.local_action_delete)).setImageResource(R.drawable.ic_delete_white_24dp);
        } else {
            serverAdapter.notifyDataSetChanged();
            ((ImageButton) findViewById(R.id.server_action_delete)).setImageResource(R.drawable.ic_delete_white_24dp);
        }
    }

    private boolean isLocalTab() {
        return currentTab == 0;
    }

    public void showMessage(int id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }
}
