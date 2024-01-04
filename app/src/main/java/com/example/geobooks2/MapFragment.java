package com.example.geobooks2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private String latitudeColumn = "BirthCityLat";
    private String longitudeColumn = "BirthCityLong";
    private float minYear = -800f; // replace with your actual min year
    private float maxYear = 1953f; // replace with your actual max year
    private String genreFilter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setMapToolbarEnabled(false);

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

    public void setGenreFilter(String genre) {
        this.genreFilter = genre;
        updateMap();
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

        // TODO: Add conditions to the query based on year range and genre filter

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                null,
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
        return locations;
    }
}