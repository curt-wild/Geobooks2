package com.example.geobooks2;

import android.content.Context;
import android.content.SharedPreferences;
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

        // Clear the saved filter preferences to make them not show the previous state after restarting the app (by calling a method int the FilterFragment)
        FilterFragment.clearPreferences(this);

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