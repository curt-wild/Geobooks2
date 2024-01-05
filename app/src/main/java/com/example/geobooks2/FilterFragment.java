package com.example.geobooks2;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private int lastClickedButtonId = -1;
    private RadioGroup radioGroup;

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

        radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton radioButtonDefault = view.findViewById(R.id.radioButtonDefault);
        //This is clicked when the app starts
        radioButtonDefault.performClick();


        // Restore the selected RadioButton and Button
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int selectedRadioButtonId = sharedPref.getInt("selectedRadioButtonId", R.id.radioButtonDefault);
        lastClickedButtonId = sharedPref.getInt("lastClickedButtonId", R.id.button_birth_place);
        RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
        selectedRadioButton.setChecked(true);

        // If a button was clicked, set its color
        if (lastClickedButtonId != -1) {
            Button lastClickedButton = view.findViewById(lastClickedButtonId);
            lastClickedButton.setBackgroundColor(Color.parseColor("#ff0000"));
        }
        else{
            buttonBirthPlace.setBackgroundColor(Color.parseColor("#ff0000"));

            buttonImpCity.setBackgroundColor(Color.parseColor("#800080"));
            buttonPubCity.setBackgroundColor(Color.parseColor("#800080"));
            lastClickedButtonId = R.id.button_birth_place;
        }


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
                buttonImpCity.setBackgroundColor(Color.parseColor("#ff0000"));

                buttonBirthPlace.setBackgroundColor(Color.parseColor("#800080"));
                buttonPubCity.setBackgroundColor(Color.parseColor("#800080"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_imp_city;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

                // Set the filters
                mapFragment.setFilters("ImpCityLat", "ImpCityLong");
            }
        });

        buttonBirthPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBirthPlace.setBackgroundColor(Color.parseColor("#ff0000"));

                buttonImpCity.setBackgroundColor(Color.parseColor("#800080"));
                buttonPubCity.setBackgroundColor(Color.parseColor("#800080"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_birth_place;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

                // Set the filters
                mapFragment.setFilters("BirthCityLat", "BirthCityLong");
            }
        });

        buttonPubCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPubCity.setBackgroundColor(Color.parseColor("#ff0000"));

                buttonImpCity.setBackgroundColor(Color.parseColor("#800080"));
                buttonBirthPlace.setBackgroundColor(Color.parseColor("#800080"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_pub_city;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

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

        radioButtonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonDefault.setChecked(true);
                // Call setFilters based on the last clicked button
                if (lastClickedButtonId == R.id.button_birth_place) {
                    mapFragment.setFilters("BirthCityLat", "BirthCityLong");
                } else if (lastClickedButtonId == R.id.button_pub_city) {
                    mapFragment.setFilters("PubCityLat", "PubCityLong");
                } else if (lastClickedButtonId == R.id.button_imp_city) {
                    mapFragment.setFilters("ImpCityLat", "ImpCityLong");
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onPause() {
        super.onPause();

        // Save the selected button
        if (radioGroup != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("selectedRadioButtonId", radioGroup.getCheckedRadioButtonId());
            editor.putInt("lastClickedButtonId", lastClickedButtonId);
            editor.apply();
        }
    }

    public static void clearPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

}