package cacagdas.mynoteassistant.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;
import static cacagdas.mynoteassistant.com.MapFragment.options;

public class NoteAddActivity extends AppCompatActivity {

    Switch switchLocation;
    Switch switchDate;
    View mapFragment;
    View datePicker;
    Button btnNoteAdd;
    SeekBar seekBar;
    public static LatLng markerPosition;

    public static List<Note> noteList = new ArrayList<>();
    public static List<Note> completedNoteList = new ArrayList<>();

    EditText txtNoteHeader;
    EditText txtNoteContent;
    TextView txtBarPosition;
    String stNoteHeader, stNoteContent;
    int intBarPosition;
    long barPosition;

    public static DatabaseReference mDatabase;
    public static FirebaseUser currentFirebaseUser;
    public static DatabaseReference mNoteReference;

    Switch switchSend;
    EditText txtEmail;
    String stEmail;
    View layoutLoc;
    Switch switchInLoc, switchOutLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews();

        mapFragment.setVisibility(View.GONE);
        txtEmail.setVisibility(View.GONE);

        /**
         * zamana göre not ekleme seçeneği kapatıldı.
         */

        switchLocation.setChecked(true);
        switchLocation.setVisibility(View.GONE);
        switchDate.setVisibility(View.GONE);
        mapFragment.setVisibility(View.VISIBLE);
        layoutLoc.setVisibility(View.VISIBLE);
        switchInLoc.setChecked(true);

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mapFragment.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                    switchDate.setChecked(false);
                    layoutLoc.setVisibility(View.VISIBLE);
                    switchInLoc.setChecked(true);
                }
                else {
                    mapFragment.setVisibility(View.GONE);
                    layoutLoc.setVisibility(View.GONE);
                }
            }
        });

        switchInLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    switchOutLoc.setChecked(false);
                } else {
                    switchOutLoc.setChecked(true);
                }
            }
        });

        switchOutLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    switchInLoc.setChecked(false);
                } else {
                    switchInLoc.setChecked(true);
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

        switchSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    txtEmail.setVisibility(View.VISIBLE);
                    btnNoteAdd.setText("SEND");
                }
                else {
                    txtEmail.setVisibility(View.GONE);
                    btnNoteAdd.setText("ADD TO NOTE LIST");
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
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        txtBarPosition = (TextView) findViewById(R.id.tvBarPosition);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        switchSend = (Switch) findViewById(R.id.switchSend);
        layoutLoc = findViewById(R.id.layoutLoc);
        switchInLoc = (Switch) findViewById(R.id.switchInLoc);
        switchOutLoc = (Switch) findViewById(R.id.switchOutLoc);


        btnNoteAdd.setOnClickListener(onClickListener);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtBarPosition.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        barPosition = seekBar.getProgress();
//        barPosition = (long) intBarPosition;
        stEmail = txtEmail.getText().toString();

        if (switchSend.isChecked()) {
            if (switchLocation.isChecked()) {
                markerPosition = options.getPosition();
                if(markerPosition != null) {
                    if (switchInLoc.isChecked()) {
                        final String userId = stEmail;
                        Note note = new Note(stNoteHeader, stNoteContent, markerPosition, barPosition, false, true, 0);
                        String key = mDatabase.child("users").child(userId).child("notes").push().getKey();
                        Map<String, Object> postValues = note.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/users/" + userId + "/notes/" + key, postValues);
                        mDatabase.updateChildren(childUpdates);
                    } else if (switchOutLoc.isChecked()) {
                        final String userId = stEmail;
                        Note note = new Note(stNoteHeader, stNoteContent, markerPosition, barPosition, false, false, 0);
                        String key = mDatabase.child("users").child(userId).child("notes").push().getKey();
                        Map<String, Object> postValues = note.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/users/" + userId + "/notes/" + key, postValues);
                        mDatabase.updateChildren(childUpdates);
                    }
                    Toast.makeText(getApplicationContext(), "You have sent note successfully.", Toast.LENGTH_LONG).show();
                    markerPosition = null;
                    backToMain();
                } else Toast.makeText(getApplicationContext(), "Please select a place.", Toast.LENGTH_LONG).show();
            } else if (switchDate.isChecked()) {
                Toast.makeText(getApplicationContext(), "Please select a location.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please select a location.", Toast.LENGTH_LONG).show();
            }

        } else {
            if(switchLocation.isChecked()) {
                markerPosition = options.getPosition();
                if(markerPosition != null) {
                    if (switchInLoc.isChecked()) {
                        //startService(new Intent(this, GPSService.class));
                        Note note = new Note(stNoteHeader, stNoteContent, markerPosition, barPosition, false, true, 0);
                        //noteList.add(note);
                        final String userId = currentFirebaseUser.getUid();
                        String key = mDatabase.child("users").child(userId).child("notes").push().getKey();
                        Map<String, Object> postValues = note.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/users/" + userId + "/notes/" + key, postValues);
                        mDatabase.updateChildren(childUpdates);
                    } else if (switchOutLoc.isChecked()) {
                        Note note = new Note(stNoteHeader, stNoteContent, markerPosition, barPosition, false, false, 0);
                        //noteList.add(note);
                        final String userId = currentFirebaseUser.getUid();
                        String key = mDatabase.child("users").child(userId).child("notes").push().getKey();
                        Map<String, Object> postValues = note.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/users/" + userId + "/notes/" + key, postValues);
                        mDatabase.updateChildren(childUpdates);
                    }
                    Toast.makeText(getApplicationContext(), "You have added note successfully.", Toast.LENGTH_LONG).show();
                    markerPosition = null;
                    backToMain();
                } else Toast.makeText(getApplicationContext(), "Please select a place.", Toast.LENGTH_LONG).show();

            } else if(switchDate.isChecked()) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(currentDate);

                Toast.makeText(getApplicationContext(), "Please select a location.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please select a location.", Toast.LENGTH_LONG).show();
            }
        }

        noteListAdapter.notifyDataSetChanged();

    }

    public void backToMain() {
        Intent myIntent = new Intent(NoteAddActivity.this, MainActivity.class);
        NoteAddActivity.this.startActivity(myIntent);
    }

}
