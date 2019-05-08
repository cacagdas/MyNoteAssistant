package cacagdas.mynoteassistant.com;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cacagdas on 08.02.2018.
 */

public class MapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    MapView mapView;

    EditText txtLocationSearch;
    Button btnLocationSearch;

    public static Address address;
    List<Address> addressList = null;
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    String location;

    public static MarkerOptions options;
    public static MarkerOptions options2;

    double currentLat = 0;
    double currentLng = 0;
    LatLng currentLatLng;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        txtLocationSearch = (EditText) view.findViewById(R.id.txtLocationSearch);
        btnLocationSearch = (Button) view.findViewById(R.id.btnLocationSearch);
        //switchLocation = (Switch) view.findViewById(R.id.switchLocation);
        mapView = (MapView) view.findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        options = new MarkerOptions();

        btnLocationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMapSearch();
            }
        });
        return view;
    }

    /**
     @Override public void onCreate(@Nullable Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);

     getActivity().setContentView(R.layout.activity_note_add);
     // Obtain the SupportMapFragment and get notified when the map is ready to be used.
     MapFragment mapFragment = (MapFragment) getFragmentManager()
     .findFragmentById(R.id.mapFragment);
     }*/


    public void onMapSearch() {
        location = txtLocationSearch.getText().toString();
        location = location.trim();
        if(location != null && location.equals("") == false) {
            addMarkerToLocation();
        } else Toast.makeText(getActivity(), "Please write a place.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (markerPoints.size() > 0) {
            markerPoints.clear();
            mMap.clear();
        }
        // Adding new item to the ArrayList
        markerPoints.add(latLng);
        // Creating MarkerOptions

        // Setting the position of the marker
        options.position(latLng);
        mMap.addMarker(options);
    }

    public void addMarkerToLocation(){

        if (markerPoints.size() > 0) {
            markerPoints.clear();
            mMap.clear();
        }

        //FIXME burayı sonra düzelt.
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this.getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if( addressList != null && addressList.size() > 0 && addressList.get(0) != null) {
                address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                markerPoints.add(latLng);
                options.position(latLng);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                mMap.addMarker(options);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else Toast.makeText(getActivity(), "Please write a valid place.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((NoteAddActivity) this.getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        /**
        if (markerPoints.size() > 0) {
            markerPoints.clear();
            mMap.clear();
        }
        // Adding new item to the ArrayList
        markerPoints.add(currentLatLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(currentLatLng);

        mMap.addMarker(options);
        */
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /**
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
         */

        if (GPSService.location != null) {
            LatLng zoomLocation = new LatLng(GPSService.location.getLatitude(), GPSService.location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLocation, 15));
        }

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
    }

}
