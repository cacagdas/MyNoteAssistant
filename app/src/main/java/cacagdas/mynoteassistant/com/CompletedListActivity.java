package cacagdas.mynoteassistant.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cacagdas.mynoteassistant.com.MainActivity.completedListAdapter;
import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;
import static cacagdas.mynoteassistant.com.NoteAddActivity.completedNoteList;
import static cacagdas.mynoteassistant.com.NoteAddActivity.currentFirebaseUser;
import static cacagdas.mynoteassistant.com.NoteAddActivity.mNoteReference;
import static cacagdas.mynoteassistant.com.NoteAddActivity.noteList;

public class CompletedListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ListView lvCompletedList;
    public static boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lvCompletedList = (ListView) findViewById(R.id.lvCompletedList);
        lvCompletedList.setAdapter(completedListAdapter);

        onFirebaseData();
    }

    public void onFirebaseData() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = currentFirebaseUser.getUid();
        System.out.println("email: "+currentFirebaseUser.getEmail());
        mNoteReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("completedNotes");

        mNoteReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completedNoteList.clear();
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
                    completedNoteList.add(note);
                    completedListAdapter.notifyDataSetChanged();
                    if(completedNoteList.size() == 0) {
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
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    public void listEmpty() {
        Toast.makeText(getApplicationContext(), "List is empty.", Toast.LENGTH_LONG).show();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            Intent myIntent = new Intent(CompletedListActivity.this, MainActivity.class);
            CompletedListActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_notes_completed) {

        } else if (id == R.id.nav_friends) {
            Intent intent = new Intent(this, FriendsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(CompletedListActivity.this, LoginActivity.class);
            CompletedListActivity.this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
