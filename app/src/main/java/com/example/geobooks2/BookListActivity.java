package com.example.geobooks2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);

        List<String> bookTitles = fetchBookTitlesFromDatabase(lat, lng);

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

    public List<String> fetchBookTitlesFromDatabase(double lat, double lng) {
        List<String> bookTitles = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "BookTitle"
        };

        String selection = "BirthCityLat = ? AND BirthCityLong = ?";
        String[] selectionArgs = {
                String.valueOf(lat),
                String.valueOf(lng)
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while(cursor.moveToNext()) {
            String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("BookTitle"));
            bookTitles.add(bookTitle);
        }
        cursor.close();
        return bookTitles;
    }
}