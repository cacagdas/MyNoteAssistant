package cacagdas.mynoteassistant.com;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static cacagdas.mynoteassistant.com.MainActivity.noteListAdapter;
import static cacagdas.mynoteassistant.com.NoteAddActivity.currentFirebaseUser;
import static cacagdas.mynoteassistant.com.NoteAddActivity.mNoteReference;
import static cacagdas.mynoteassistant.com.NoteAddActivity.noteList;


public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    interface OnLocationUpdateCallback {
        void onLocationChanged(Location location);
    }

    int m;
    public static Location location;
    DatabaseReference nRef;
    SharedPreferences sharedPref;
    //List<Note> noteList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i("GPS Service", "started");
        Toast.makeText(getApplicationContext(), "Service activated.", Toast.LENGTH_LONG).show();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes");
        //showForegroundNotification("Foreground Notification 0");

        /**
        if (currentFirebaseUser != null && currentFirebaseUser.getUid() != null) {
            sharedPref = getSharedPreferences("currentFirebaseUserId", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentFirebaseUserId", currentFirebaseUser.getUid().toString());
            editor.apply();
        }

        if (sharedPref != null) {
            String userId = sharedPref.getString("currentFirebaseUserId", "");
            nRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes");
            onFirebaseData();
        }
         */

        requestLocationUpdates();
    }

    public void onFirebaseData() {
        nRef.addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNotification(String title, String content) {
        Random random = new Random();
        m = random.nextInt(9999 - 1000) + 1000;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification n  = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_round_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificationManager.notify(0, n);
        notificationManager.notify(m, n);

    }

    private static final int NOTIFICATION_ID = 1;

    private void showForegroundNotification(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_round_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void requestLocationUpdates() {
        Log.i("GPS Service","running");
        final LocationRequest request = new LocationRequest();
        request.setInterval(10000); //first, it was 10000
        request.setFastestInterval(5000); //first, it was 5000
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    location = locationResult.getLastLocation();
                    Log.i("lokey" +location.toString(), "lokey");

                //    if ((location != null) && (location.getAccuracy() < 300)) {
                    for (int i=0; i<=noteList.size() - 1 ; i++) {
                        double distance = distance(location.getLatitude(), location.getLongitude(),
                                noteList.get(i).getNoteLocation().latitude, noteList.get(i).getNoteLocation().longitude);
                        if (noteList.get(i).isInBounds()) {
                            if (distance < noteList.get(i).getNoteDistance() && !noteList.get(i).isNotified()) {
                                createNotification(i);
                            }
                            //break;
                        } else if (!noteList.get(i).isInBounds()) {
                            if (distance < noteList.get(i).getNoteDistance() && !noteList.get(i).isNotified()) {
                                noteList.get(i).setCounter(1);
                                nRef.child(noteList.get(i).getKey()).child("counter").setValue(1);
                            } else if (distance > noteList.get(i).getNoteDistance() && !noteList.get(i).isNotified() && noteList.get(i).getCounter() == 1) {
                                createNotification(i);
                                System.out.println("Out of bounds notif: " + "created.");
                            }
                        }
                    }
                 //   }
                }
            }, null);
        }
    }

    private void createNotification(int i) {
        //showForegroundNotification(""+noteList.get(i).getNoteHeader()+": "+noteList.get(i).getNoteContent());
        setNotification(noteList.get(i).getNoteHeader(), noteList.get(i).getNoteContent());
        noteList.get(i).setNotified(true);
        System.out.println("key: " + noteList.get(i).getKey());
        nRef.child(noteList.get(i).getKey()).child("isNotified").setValue(true);
        Log.i("Notified: ", String.valueOf(noteList.get(i).isNotified()));
    }

    public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return Double.valueOf(distance * meterConversion).floatValue();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}