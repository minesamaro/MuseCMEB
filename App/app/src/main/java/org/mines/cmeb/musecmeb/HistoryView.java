package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        // Find the back button in the Toolbar
        ImageButton backButton = findViewById(R.id.backButton);

        // Set click listener for the back button
        backButton.setOnClickListener(view -> {
            onBackPressed(); // Finish the activity when the back button is clicked
        });
    }
}
