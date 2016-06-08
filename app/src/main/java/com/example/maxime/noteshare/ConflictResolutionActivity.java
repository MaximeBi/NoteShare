package com.example.maxime.noteshare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class ConflictResolutionActivity extends AppCompatActivity {

    private static final int DELETE = 0;
    private static final int INSERT = 1;
    private static final int EQUAL = 2;

    private int conflicts;
    private Note note;
    private String localText;
    private String serverText;
    private LinearLayout conflictsResolutionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict_resolution);
        Intent intent = getIntent();
        this.note = (Note) intent.getSerializableExtra(MainActivity.NOTE);
        this.localText = note.getContent();
        this.serverText = intent.getStringExtra(MainActivity.SERVER_TEXT);

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(note.getTitle());

        initialiseView();

        final ImageButton validate = (ImageButton) findViewById(R.id.validate);
        validate.setEnabled(false);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                note.setContent(getFinalText());
                result.putExtra(MainActivity.NOTE, note);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        ImageButton reset = (ImageButton) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate.setEnabled(false);
                initialiseView();
            }
        });
    }

    private void initialiseView() {
        this.conflicts = 0;
        diff_match_patch difference = new diff_match_patch();
        difference.Diff_EditCost = 4;
        difference.Diff_Timeout = 1;
        LinkedList<diff_match_patch.Diff> deltas = difference.diff_main(serverText, localText);
        difference.diff_cleanupSemantic(deltas);

        conflictsResolutionLayout = (LinearLayout) findViewById(R.id.conflict_resolution);
        conflictsResolutionLayout.removeAllViews();

        for(diff_match_patch.Diff diff : deltas) {
            if(diff.operation.ordinal() != 2) {
                conflicts++;
            }
            conflictsResolutionLayout.addView(createTextView(diff));
        }
    }

    private String getFinalText() {
        String result = "";
        for(int i = 0 ; i < conflictsResolutionLayout.getChildCount(); i++) {
            TextView textView = (TextView) conflictsResolutionLayout.getChildAt(i);
            result += textView.getText();
        }
        return result;
    }

    private TextView createTextView(final diff_match_patch.Diff diff) {
        final TextView textView = new TextView(this);
        textView.setText(diff.text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        int color = Color.BLACK;
        if(diff.operation.ordinal() == DELETE) {
            color = Color.RED;
        } else if((diff.operation.ordinal() == INSERT)) {
            color = Color.GREEN;
        }
        textView.setTextColor(color);
        if(diff.operation.ordinal() != EQUAL) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu(textView, diff).show();
                }
            });
        }
        return textView;
    }

    private void conflictRemoved() {
        conflicts--;
        if(conflicts == 0) {
            ImageButton validate = (ImageButton) findViewById(R.id.validate);
            validate.setEnabled(true);
        }
    }

    private AlertDialog menu(final TextView textView, final diff_match_patch.Diff diff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(diff.operation.ordinal() == DELETE) {
            builder.setMessage(getString(R.string.element_deleted));
        } else {
            builder.setMessage(getString(R.string.element_added));
        }
        builder.setTitle(getString(R.string.conflict_mangement));
        builder.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                acceptChange(textView, diff);
            }
        });
        builder.setNegativeButton(getString(R.string.refuse), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                refuseChange(textView, diff);
            }
        });

        return builder.create();
    }

    private void refuseChange(TextView textView, diff_match_patch.Diff diff) {
        if(diff.operation.ordinal() == INSERT) {
            ((ViewGroup) textView.getParent()).removeView(textView);
        } else if (diff.operation.ordinal() == DELETE){
            textView.setOnClickListener(null);
            textView.setTextColor(Color.BLACK);
        }
        conflictRemoved();
    }

    private void acceptChange(TextView textView, diff_match_patch.Diff diff) {
        if(diff.operation.ordinal() == INSERT) {
            textView.setOnClickListener(null);
            textView.setTextColor(Color.BLACK);
        } else if (diff.operation.ordinal() == DELETE){
            ((ViewGroup) textView.getParent()).removeView(textView);
        }
        conflictRemoved();
    }
}
