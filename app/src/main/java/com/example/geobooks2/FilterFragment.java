package com.example.geobooks2;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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

/**
 * This class creates the fragment that contains the filter options.
 * It is called from the MapFragment class.
 */

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

        //The default radio button is clicked when the app starts
        radioButtonDefault.performClick();


        // Restore the selected RadioButton and Button (after closing the FilterFragment)
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int selectedRadioButtonId = sharedPref.getInt("selectedRadioButtonId", R.id.radioButtonDefault);
        lastClickedButtonId = sharedPref.getInt("lastClickedButtonId", R.id.button_birth_place);
        RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
        selectedRadioButton.setChecked(true);

        colorRadioButtons(view, selectedRadioButton);

        // If a button was clicked, set its color, it's either the last clicked button that has a different color or the button "Birth Place" when starting the app
        if (lastClickedButtonId != -1) {
            Button lastClickedButton = view.findViewById(lastClickedButtonId);
            lastClickedButton.setBackgroundColor(Color.parseColor("#776B5D"));
        }
        else{
            buttonBirthPlace.setBackgroundColor(Color.parseColor("#776B5D"));
            buttonImpCity.setBackgroundColor(Color.parseColor("#978F87"));
            buttonPubCity.setBackgroundColor(Color.parseColor("#978F87"));
            lastClickedButtonId = R.id.button_birth_place;
        }


// Set onClickListeners for the buttons
        buttonImpCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the color of the buttons
                buttonImpCity.setBackgroundColor(Color.parseColor("#776B5D"));
                buttonBirthPlace.setBackgroundColor(Color.parseColor("#978F87"));
                buttonPubCity.setBackgroundColor(Color.parseColor("#978F87"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_imp_city;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

                // Set the filters (method from MapFragment)
                mapFragment.setFilters("ImpCityLat", "ImpCityLong");
            }
        });

        buttonBirthPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the color of the buttons
                buttonBirthPlace.setBackgroundColor(Color.parseColor("#776B5D"));
                buttonImpCity.setBackgroundColor(Color.parseColor("#978F87"));
                buttonPubCity.setBackgroundColor(Color.parseColor("#978F87"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_birth_place;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

                // Set the filters (method from MapFragment)
                mapFragment.setFilters("BirthCityLat", "BirthCityLong");
            }
        });

        buttonPubCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the color of the buttons
                buttonPubCity.setBackgroundColor(Color.parseColor("#776B5D"));
                buttonImpCity.setBackgroundColor(Color.parseColor("#978F87"));
                buttonBirthPlace.setBackgroundColor(Color.parseColor("#978F87"));

                // Store the ID of the clicked button
                lastClickedButtonId = R.id.button_pub_city;

                // If defaultRadioButton is not checked, perform a click on it
                if (!radioButtonDefault.isChecked()) {
                    radioButtonDefault.performClick();
                }

                // Set the filters (method from MapFragment)
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

                colorRadioButtons(view, radioButton);
            }
        });

        // Set a listener when the default radio button is clicked
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

    private void colorRadioButtons(View view, RadioButton selectedRadioButton) {
        // Set the color of the RadioButton
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                },
                new int[]{
                        Color.BLACK,   // Color when unchecked
                        Color.parseColor("#776B5D")    // Color when checked
                }
        );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            selectedRadioButton.setButtonTintList(colorStateList);
        }
    }

    //Handles the case when the user closes the fragment
    @Override
    public void onPause() {
        super.onPause();

        // the last clicked button and the selected radio button are saved
        if (radioGroup != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("selectedRadioButtonId", radioGroup.getCheckedRadioButtonId());
            editor.putInt("lastClickedButtonId", lastClickedButtonId);
            editor.apply();
        }
    }

    //Clears the current selection when the app is closed
    public static void clearPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }



}