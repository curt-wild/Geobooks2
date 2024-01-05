package com.example.geobooks2;

import android.Manifest;
import android.location.Location;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private boolean isMapCentered = false;

    private String latitudeColumn = "BirthCityLat";
    private String longitudeColumn = "BirthCityLong";
    private float minYear = -800f; // replace with your actual min year
    private float maxYear = 1953f; // replace with your actual max year

    private String genre;


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // initialize location permission request
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
            if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                if (googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), "Location cannot be obtained due to missing permission.", Toast.LENGTH_LONG).show();
            }
        });



        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // get permission to access user location
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        locationPermissionRequest.launch(PERMISSIONS);



        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        updateMap();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                Intent intent = new Intent(getActivity(), BookListActivity.class);
                intent.putExtra("lat", position.latitude);
                intent.putExtra("lng", position.longitude);
                startActivity(intent);
                return false;
            }
        });

        // Add this code to center the map on the user's location when the permission is granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations, this can be null.
                            if (location != null && !isMapCentered) {
                                // Logic to handle location object
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
                                isMapCentered = true;
                            }
                        }
                    });
        }
    }

    public void setFilters(String latitudeColumn, String longitudeColumn) {
        this.latitudeColumn = latitudeColumn;
        this.longitudeColumn = longitudeColumn;
        updateMap();
    }

    public void setYearRange(float minYear, float maxYear) {
        this.minYear = minYear;
        this.maxYear = maxYear;
        updateMap();
    }

    public void setGenre(String genre, int buttonId) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LatLng> locations = new ArrayList<>();

        String[] projection;
        if (buttonId == R.id.button_birth_place) {
            projection = new String[] { "BirthCityLat", "BirthCityLong" };
        } else if (buttonId == R.id.button_pub_city) {
            projection = new String[] { "PubCityLat", "PubCityLong" };
        } else if (buttonId == R.id.button_imp_city) {
            projection = new String[] { "ImpCityLat", "ImpCityLong" };
        } else {
            throw new IllegalArgumentException("Invalid button id: " + buttonId);
        }

        // Filter results WHERE "latitudeColumn" IS NOT NULL AND "longitudeColumn" IS NOT NULL AND "genre" = 'selectedGenre'
        String selection = projection[0] + " IS NOT NULL AND " + projection[1] + " IS NOT NULL AND Genre = ?";
        String[] selectionArgs = { genre };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,   // The table to query
                projection,                   // The array of columns to return (pass null to get all)
                selection,                    // The columns for the WHERE clause
                selectionArgs,                // The values for the WHERE clause
                null,                         // Don't group the rows
                null,                         // Don't filter by row groups
                null                          // The sort order
        );


        googleMap.clear();
        // Use the data in cursor to update your map
        if (cursor.moveToFirst()) { // if Cursor is not empty
            do {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(projection[0]));
                double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(projection[1]));


                // Create a LatLng object with the retrieved lat and lng
                LatLng location = new LatLng(lat, lng);

                // Add a marker to the map at the retrieved location
                googleMap.addMarker(new MarkerOptions().position(location));

                /*
                // Move the camera to the retrieved location
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                 */

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    private void updateMap() {
        List<LatLng> locations = fetchLocationsFromDatabase();
        googleMap.clear();
        for (LatLng location : locations) {
            googleMap.addMarker(new MarkerOptions().position(location));
        }
    }

    private List<LatLng> fetchLocationsFromDatabase() {
        List<LatLng> locations = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                latitudeColumn,
                longitudeColumn
        };



        String selection = latitudeColumn + " IS NOT NULL AND " + longitudeColumn + " IS NOT NULL";
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(latitudeColumn));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(longitudeColumn));
            locations.add(new LatLng(lat, lng));
        }
        cursor.close();
        db.close();
        return locations;
    }
}