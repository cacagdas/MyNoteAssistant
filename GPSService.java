package cacagdas.mynoteassistant.com;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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

import java.util.Random;

import static cacagdas.mynoteassistant.com.NoteAddActivity.noteList;


public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    interface OnLocationUpdateCallback {
        void onLocationChanged(Location location);
    }


    public static String EVENT_LOCATION_CHANGED = "event.locationChangedInGpsService";

    private static final float DISTANCE_LIMIT = 100f; //meters
    private static final double DISTANCE_GEOFENCE_ROUTE = 100000; //meters
    private static final long TIME_ALERT_COOLDOWN = 2; //minutes

    int m;

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
        Log.i("GPS Service: ", "started");
        Toast.makeText(getApplicationContext(), "Service activated.", Toast.LENGTH_LONG).show();
        //showForegroundNotification("Foreground Notification 0");
        requestLocationUpdates();
        //notification("App","init");
    }

    private void setNotification(String title, String content) {
        Random random = new Random();
        m = random.nextInt(9999 - 1000) + 1000;
        Intent intent = new Intent(this, NotificationReceiver.class);
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
                    Location location = locationResult.getLastLocation();
                    Log.i("lokey" +location.toString(), "lokey");

                //    if ((location != null) && (location.getAccuracy() < 300)) {
                    for (int i=0; i<=noteList.size() - 1 ; i++) {
                        double distance = distance(location.getLatitude(), location.getLongitude(),
                                noteList.get(i).getNoteLocation().latitude, noteList.get(i).getNoteLocation().longitude);
                        if (distance < 500 && !noteList.get(i).isNotified()) {
                            createNotification(i);
                            //break;
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

