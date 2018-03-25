package cacagdas.mynoteassistant.com;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;
import static cacagdas.mynoteassistant.com.MapFragment.options;

public class NoteAddActivity extends AppCompatActivity {

    Switch switchLocation;
    Switch switchDate;
    View mapFragment;
    View datePicker;
    Button btnNoteAdd;
    public static LatLng markerPosition;

    public static List<Note> noteList = new ArrayList<>();
    EditText txtNoteHeader, txtNoteContent;
    String stNoteHeader, stNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        initViews();

        mapFragment.setVisibility(View.GONE);

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mapFragment.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    switchDate.setChecked(false);
                }
                else {
                    mapFragment.setVisibility(View.GONE);
                }
            }
        });

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    datePicker.setVisibility(View.VISIBLE);
                    mapFragment.setVisibility(View.GONE);
                    switchLocation.setChecked(false);
                }
                else {
                    datePicker.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        switchLocation = (Switch) findViewById(R.id.switchLocation);
        switchDate = (Switch) findViewById(R.id.switchDate);
        mapFragment = findViewById(R.id.mapFragment);
        datePicker = findViewById(R.id.datePicker);
        txtNoteHeader = (EditText) findViewById(R.id.txtNoteHeader);
        txtNoteContent = (EditText) findViewById(R.id.txtNoteContent);
        btnNoteAdd = (Button) findViewById(R.id.btnNoteAdd);

        btnNoteAdd.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            addNoteToList();
        }
    };


    public void addNoteToList() {

        stNoteHeader = txtNoteHeader.getText().toString();
        stNoteContent = txtNoteContent.getText().toString();

        if(switchLocation.isChecked()) {
            markerPosition = options.getPosition();
           // markerPosition.latitude
            Note note = new Note(stNoteHeader, stNoteContent, markerPosition, false);
            noteList.add(note);
        //    Toast.makeText(getApplicationContext(), "You have added note successfully.\n" +note.getNoteLocation().toString(), Toast.LENGTH_LONG).show();
        } else if(switchDate.isChecked()) {
           // Date noteDate =
        } else {
            Note note = new Note(stNoteHeader, stNoteContent);
            noteList.add(note);
        }

        noteListAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "You have added note successfully.", Toast.LENGTH_LONG).show();
        startService(new Intent(this, GPSService.class));
    }

}
