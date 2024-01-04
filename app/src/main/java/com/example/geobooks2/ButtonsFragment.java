package com.example.geobooks2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ButtonsFragment extends Fragment {

    private MapFragment mapFragment; // Reference to MapFragment

    public ButtonsFragment(MapFragment mapFragment) { // Constructor with MapFragment as parameter
        this.mapFragment = mapFragment;
    }


    public static ButtonsFragment newInstance(MapFragment mapFragment) {
        return new ButtonsFragment(mapFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);

        // Find the buttons in the layout
        Button aboutButton = view.findViewById(R.id.about_button);
        Button showMapButton = view.findViewById(R.id.show_map_button);
        Button filterButton = view.findViewById(R.id.filter_button);

        // Set click listeners for the buttons
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the About button click here
                AboutDialogFragment aboutDialog = new AboutDialogFragment();
                aboutDialog.show(getFragmentManager(), "com.example.geobooks2.AboutDialogFragment");
            }
        });

        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Show Map button click here
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Filter button click here
                FilterFragment filterFragment = FilterFragment.newInstance(mapFragment);
                filterFragment.show(getFragmentManager(), "com.example.geobooks2.FilterFragment");
            }
        });

        return view;
    }
}