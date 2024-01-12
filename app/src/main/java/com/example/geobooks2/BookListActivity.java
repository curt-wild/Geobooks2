package com.example.geobooks2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import android.util.Pair;

/**
 * This class creates the activity that displays the list of books for a given city.
 * It is called from the MapActivity class.
 */

public class BookListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        String latColumn = getIntent().getStringExtra("latColumn");
        String lngColumn = getIntent().getStringExtra("lngColumn");
        String genre = getIntent().getStringExtra("genre");

        Pair<String, List<String>> data = fetchDataFromDatabase(lat, lng, latColumn, lngColumn, genre);

        //set title to the city name
        TextView textView = findViewById(R.id.textView);
        String cityName = data.first;
        textView.setText(cityName);

        //list of book titles for that city
        List<String> bookTitles = data.second;

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookTitles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedBookTitle = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
                intent.putExtra("BookTitle", selectedBookTitle);
                startActivity(intent);
            }
        });
    }

    public Pair<String, List<String>> fetchDataFromDatabase(double lat, double lng, String latColumn, String lngColumn, String genre) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //set city name column based on which lat/lng column was selected
        String cityNameColumn;
        if ("BirthCityLat".equals(latColumn)) {
            cityNameColumn = "BirthCityName";
        } else if ("ImpCityLat".equals(latColumn)) {
            cityNameColumn = "ImpCityName";
        } else if ("PubCityLat".equals(latColumn)) {
            cityNameColumn = "PubCityName";
        } else {
            throw new IllegalArgumentException("Invalid latitude column: " + latColumn);
        }

        String[] projection = {
                cityNameColumn,
                "BookTitle",
                "Author"
        };

        String selection;
        String[] selectionArgs;

        //if a genre is selected, then filter by genre
        if (genre != null) {
            selection = latColumn + " = ? AND " + lngColumn + " = ? AND genre = ?";
            selectionArgs = new String[] {
                    String.valueOf(lat),
                    String.valueOf(lng),
                    genre
            };
        } else {
            selection = latColumn + " = ? AND " + lngColumn + " = ?";
            selectionArgs = new String[] {
                    String.valueOf(lat),
                    String.valueOf(lng)
            };
        }

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String cityName = null;
        List<String> bookTitles = new ArrayList<>();
        while(cursor.moveToNext()) {
            if (cityName == null) {
                cityName = cursor.getString(cursor.getColumnIndexOrThrow(cityNameColumn));
            }
            String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("BookTitle"));
            String author = cursor.getString(cursor.getColumnIndexOrThrow("Author"));
            String bookInfo = bookTitle + " by " + author;
            bookTitles.add(bookInfo);
        }
        cursor.close();
        return new Pair<>(cityName, bookTitles);
    }
}