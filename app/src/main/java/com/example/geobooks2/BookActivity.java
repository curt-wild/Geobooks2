// BookActivity.java

package com.example.geobooks2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class creates the activity that displays the book details.
 * It is called from the BookAdapter class.
 */

public class BookActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        String bookTitleWithAuthor = getIntent().getStringExtra("BookTitle");
        String bookTitle = bookTitleWithAuthor.split(" by ")[0];

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "BookTitle",
                "Author",
                "PublicationYear",
                "Genre",
                "Synapse",
                "PGPage",
                "HtmlReader"
        };

        String selection = "BookTitle = ?";
        String[] selectionArgs = { bookTitle };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String author = cursor.getString(cursor.getColumnIndexOrThrow("Author"));
            int publicationYear = cursor.getInt(cursor.getColumnIndexOrThrow("PublicationYear"));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow("Genre"));
            String synapse = cursor.getString(cursor.getColumnIndexOrThrow("Synapse"));
            String pgPage = cursor.getString(cursor.getColumnIndexOrThrow("PGPage"));
            String htmlReader = cursor.getString(cursor.getColumnIndexOrThrow("HtmlReader"));

            TextView titleTextView = findViewById(R.id.titleTextView);
            TextView authorTextView = findViewById(R.id.authorTextView);
            TextView yearTextView = findViewById(R.id.yearTextView);
            TextView genreTextView = findViewById(R.id.genreTextView);
            TextView synapseTextView = findViewById(R.id.synapseTextView);
            Button pgPageButton = findViewById(R.id.pgPageButton);
            Button htmlReaderButton = findViewById(R.id.htmlReaderButton);

            titleTextView.setText(bookTitle);
            authorTextView.setText(Html.fromHtml("<b>" + "Author: " + "</b>" + author));
            yearTextView.setText(Html.fromHtml("<b>" + "Year: " + "</b>" + publicationYear));
            genreTextView.setText(Html.fromHtml("<b>" + "Genre: " + "</b>" + genre));
            synapseTextView.setText(Html.fromHtml("<b>" + "Synapse: " + "</b>" + synapse));

            pgPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pgPage));
                    startActivity(browserIntent);
                }
            });

            htmlReaderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(htmlReader));
                    startActivity(browserIntent);
                }
            });
        }
        cursor.close();
    }
}