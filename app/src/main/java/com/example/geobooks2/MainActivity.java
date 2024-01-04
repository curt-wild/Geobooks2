package com.example.geobooks2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    MapFragment mapFragment = new MapFragment();
    FilterFragment filterFragment;
    ButtonsFragment buttonsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the MapFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment, "MAP_FRAGMENT")
                .commit();

        // Create and add the FilterFragment
        filterFragment = FilterFragment.newInstance(mapFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, filterFragment, "FILTER_FRAGMENT")
                .commit();

        // Create and add the ButtonsFragment
        buttonsFragment = ButtonsFragment.newInstance(mapFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, buttonsFragment, "BUTTONS_FRAGMENT")
                .commit();
    }
}