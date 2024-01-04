package com.example.geobooks2;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends DialogFragment {
    private MapFragment mapFragment;

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

        buttonBirthPlace.setOnClickListener(v -> mapFragment.setFilters("BirthCityLat", "BirthCityLong"));

        buttonPubCity.setOnClickListener(v -> mapFragment.setFilters("PubCityLat", "PubCityLong"));

        buttonImpCity.setOnClickListener(v -> mapFragment.setFilters("ImpCityLat", "ImpCityLong"));

        // Slider Filter
        RangeSlider slider = view.findViewById(R.id.slider);
        slider.setValueFrom(-800f); // replace with your min year
        slider.setValueTo(1953f); // replace with your max year
        List<Float> initialSliderValues = new ArrayList<>();
        initialSliderValues.add(-800f); // replace with your min year
        initialSliderValues.add(1953f); // replace with your max year
        slider.setValues(initialSliderValues);
        slider.addOnChangeListener((slider1, value, fromUser) -> mapFragment.setYearRange(slider.getValues().get(0), slider.getValues().get(1)));

        // List Filter
        ListView listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedGenre = (String) parent.getItemAtPosition(position);
            mapFragment.setGenreFilter(selectedGenre);
        });

        builder.setView(view);
        return builder.create();
    }
}