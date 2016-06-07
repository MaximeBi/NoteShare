package com.example.maxime.noteshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CollaboratorsListActivity extends AppCompatActivity {

    private CollaboratorAdapter adapter;
    private Note note;
    private ArrayList<String> collaborators;
    private ListView collaboratorsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborators_list);

        Intent intent = getIntent();
        this.note = (Note) intent.getSerializableExtra(MainActivity.NOTE);
        collaborators = note.getCollaborators();

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(note.getTitle());
        this.collaboratorsView = (ListView) findViewById(R.id.collaborators_list);
        this.adapter = new CollaboratorAdapter(this, this.collaborators);
        this.collaboratorsView.setAdapter(adapter);
        setActionsButtonsListeners();
    }

    private void setActionsButtonsListeners() {
        ImageButton addButton = (ImageButton) findViewById(R.id.add_button);
        final ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_button);
        ImageButton validateButton = (ImageButton) findViewById(R.id.validate_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText add_edit_text = (EditText) findViewById(R.id.add_edit_text);
                if(add_edit_text.getText().toString().isEmpty()) {
                    showMessage(R.string.unvalid_collaborator);
                } else if(collaborators.contains(add_edit_text.getText().toString())) {
                    showMessage(R.string.collaborator_exists);
                }else {
                    collaborators.add(0, add_edit_text.getText().toString());
                    adapter.notifyDataSetChanged();
                    add_edit_text.getText().clear();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collaboratorsView.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
                    collaboratorsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    deleteButton.setImageResource(R.drawable.ic_delete_forever_white_24dp);
                } else {
                    SparseBooleanArray checked = collaboratorsView.getCheckedItemPositions();
                    ArrayList<String> toDelete = new ArrayList<>();
                    for (int i = 0; i < collaboratorsView.getCount(); i++) {
                        if (checked.get(i)) {
                            toDelete.add((String) collaboratorsView.getItemAtPosition(i));
                        }
                    }
                    collaboratorsView.clearChoices();
                    collaborators.removeAll(toDelete);
                    adapter.notifyDataSetChanged();
                    collaboratorsView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    deleteButton.setImageResource(R.drawable.ic_delete_white_24dp);
                }
            }
        });

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                note.setCollaborators(collaborators);
                result.putExtra(MainActivity.NOTE, note);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private void showMessage(int id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }
}
