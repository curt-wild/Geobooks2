package com.example.geobooks2;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

/**
 * This class creates a custom dialog fragment to display the about text.
 * It is called from the menu in the main activity.
 */


public class AboutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        // Get the TextView from the custom layout
        TextView dialogText = dialogView.findViewById(R.id.dialog_text);

        // Set the text and the custom font
        dialogText.setText("This app is made to explore the world of books. You can" +
                " navigate through and read a collection of public domain works hosted in Project Gutenberg" +
                " based on location," +
                " with options to select by the author's birth city (the app's default)," +
                " city of publication, or the most important location in the context of the book's" +
                " storyline. Additionally, you can sort the books by genre." +
                " It was created with passion for books and maps by Nico and Ulli. Enjoy!");
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.type);
        dialogText.setTypeface(customFont);

        // Create a new TextView for the title
        TextView title = new TextView(getActivity());
        title.setText("About");
        title.setBackgroundColor(Color.parseColor("#F8F8F8"));
        title.setPadding(10, 30, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(customFont, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);

        // Set the custom view and title to the dialog
        builder.setView(dialogView)
            .setCustomTitle(title);

        return builder.create();
    }
}