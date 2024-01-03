package org.mines.cmeb.musecmeb;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class SettingsMenu extends ListActivity {

    private ImageButton backButton;  // Back button above the settings list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Back button settings
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> onBackPressed());

        // Define options
        String[] options = {"Connections", "Music"};

        // Set up the ArrayAdapter with the built-in layout (simple_list_item_1)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        setListAdapter(adapter);

        // Set up item click listener
        ListView listView = getListView();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click
            String selectedOption = options[position];

            // You can use the selectedOption value to perform specific actions based on the user's choice
            // For example, you can start a new activity for each option

            // Example: Start a new activity based on the selected option
            if ("Connections".equals(selectedOption)) {
                // Start the ConnectionsActivity
                Intent connectionsIntent = new Intent(SettingsMenu.this, LibTest.class);
                startActivity(connectionsIntent);
            } else if ("Music".equals(selectedOption)) {
                // Start the MusicActivity
                Intent musicIntent = new Intent(SettingsMenu.this, MusicOptions.class);
                startActivity(musicIntent);
            }
        });
    }
}
