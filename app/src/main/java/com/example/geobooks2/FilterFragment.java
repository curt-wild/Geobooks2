package com.example.geobooks2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends DialogFragment {
    private MapFragment mapFragment;
    private int lastClickedButtonId;

    public interface OnGenreSelectedListener {
        void onGenreSelected(String genre);
    }

    private OnGenreSelectedListener listener;

    public void setOnGenreSelectedListener(OnGenreSelectedListener listener) {
        this.listener = listener;
    }

    public static FilterFragment newInstance(MapFragment mapFragment) {
        FilterFragment fragment = new FilterFragment();
        fragment.mapFragment = mapFragment;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout for this fragment
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_filter, null);

        // Button Filters
        Button buttonBirthPlace = view.findViewById(R.id.button_birth_place);
        Button buttonPubCity = view.findViewById(R.id.button_pub_city);
        Button buttonImpCity = view.findViewById(R.id.button_imp_city);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);


        // Slider Filter
        RangeSlider slider = view.findViewById(R.id.slider);
        slider.setValueFrom(-800f); // replace with your min year
        slider.setValueTo(1953f); // replace with your max year
        slider.setStepSize(1); // This ensures only integers between the min and max can be selected
        List<Float> initialSliderValues = new ArrayList<>();
        initialSliderValues.add(-800f); // replace with your min year
        initialSliderValues.add(1953f); // replace with your max year
        slider.setValues(initialSliderValues);
        slider.addOnChangeListener((slider1, value, fromUser) -> mapFragment.setYearRange(slider.getValues().get(0), slider.getValues().get(1)));

        
// Set onClickListeners for your buttons
        buttonImpCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_imp_city;

                // Set the filters
                mapFragment.setFilters("ImpCityLat", "ImpCityLong");
            }
        });

        buttonBirthPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_birth_place;

                // Set the filters
                mapFragment.setFilters("BirthCityLat", "BirthCityLong");
            }
        });

        buttonPubCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_pub_city;

                // Set the filters
                mapFragment.setFilters("PubCityLat", "PubCityLong");
            }
        });

// Set a listener to be invoked when the checked radio button changes
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected genre from the radio group
                RadioButton radioButton = view.findViewById(checkedId);
                String genre = radioButton.getText().toString();

                // Update the map based on the selected genre and the last clicked button
                mapFragment.setGenre(genre, lastClickedButtonId);
            }
        });

        builder.setView(view);
        return builder.create();
    }
}