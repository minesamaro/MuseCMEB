package org.mines.cmeb.musecmeb;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MusicOptions extends ListActivity {

    private ImageButton backButton;  // Back button above the settings list

    private SharedPreferences preferences;  // Save the selected music to play in Session activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_options);

        // Back button settings
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> onBackPressed());

        preferences = getSharedPreferences("MusicOptionsPrefs", MODE_PRIVATE);

        // Define options
        String[] options = {"Music1", "Music2"};

        // Set up the ArrayAdapter with the built-in layout (simple_list_item_1)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        setListAdapter(adapter);

        // Set up item click listener
        getListView().setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click
            String selectedOption = options[position];

            // Save the chosen option to shared preferences
            preferences.edit().putString("chosenMusic", selectedOption).apply();

            // Show the success message
            showMusicChangedMessage(selectedOption);
        });
    }
    // Message to alert the user that the music has been changed
    private void showMusicChangedMessage(String chosenMusic) {
        String message = "Music changed to " + chosenMusic + "!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
