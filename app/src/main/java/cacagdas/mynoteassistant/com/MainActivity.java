package cacagdas.mynoteassistant.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


import static cacagdas.mynoteassistant.com.CompletedListActivity.isActive;
import static cacagdas.mynoteassistant.com.NoteAddActivity.completedNoteList;
import static cacagdas.mynoteassistant.com.NoteAddActivity.currentFirebaseUser;
import static cacagdas.mynoteassistant.com.NoteAddActivity.mNoteReference;
import static cacagdas.mynoteassistant.com.NoteAddActivity.noteList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static NoteListAdapter noteListAdapter;
    public static NoteListAdapter completedListAdapter;
    ListView lvNoteList;
    LinearLayout layoutListViewItem;
    NoteListAdapter.ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, NoteAddActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViews();
        noteListAdapter = new NoteListAdapter(this, 0, noteList);
        lvNoteList.setAdapter(noteListAdapter);

        /**
        lvNoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setIntent();
            }
        });
         */

        completedListAdapter = new NoteListAdapter(getApplicationContext(), 0, completedNoteList);
        isActive = false;

        onFirebaseData();
    }

    public void setIntent() {
        Intent myIntent = new Intent(MainActivity.this, NoteAddActivity.class);
        startActivity(myIntent);
    }

    public void onFirebaseData() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentFirebaseUser.getUid();
        System.out.println("email: "+currentFirebaseUser.getEmail());
        mNoteReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("notes");

        mNoteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    HashMap<String, Object> hashMap = (HashMap<String, Object>) postSnapshot.getValue();

                    HashMap<String, Object> noteLocation = (HashMap<String, Object>) hashMap.get("noteLocation");
                    Double lat = (Double) noteLocation.get("latitude");
                    Double lon = (Double) noteLocation.get("longitude");
                    LatLng ltlng = new LatLng(lat, lon);

                    Note note = new Note((String)hashMap.get("noteHeader"), (String)hashMap.get("noteContent"), ltlng,
                            (long)hashMap.get("noteDistance"), (boolean)hashMap.get("isNotified"),
                            (boolean)hashMap.get("isInBounds"), (long)hashMap.get("counter"), postSnapshot.getKey());
                    System.out.println("firebasenote: " + note);
                    noteList.add(note);
                    noteListAdapter.notifyDataSetChanged();

                    if (noteList.size() != 0) {
                        startService(new Intent(getApplicationContext(), GPSService.class));
                    } else if(noteList.size() == 0) {
                        listEmpty();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = false;
    }

    public void initViews() {
        lvNoteList = (ListView) findViewById(R.id.lvNoteList);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void listEmpty() {
        Toast.makeText(getApplicationContext(), "List is empty.", Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            // Handle the camera action
        } else if (id == R.id.nav_notes_completed) {
            Intent myIntent = new Intent(MainActivity.this, CompletedListActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
