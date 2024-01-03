package org.mines.cmeb.musecmeb;

import org.mines.cmeb.musecmeb.MusicOptionsAdapter; // Import the custom adapter
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MusicOptions extends ListActivity {

    private ImageButton backButton;  // Back button above the settings list
    private SharedPreferences preferences;  // Save the selected music to play in Session activity
    private MediaPlayer mediaPlayer;
    private String selectedMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_options);

        // Back button settings
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Stop the currently playing music
            stopMusic();

            // Handle back button action
            onBackPressed();
        });

        preferences = getSharedPreferences("MusicOptionsPrefs", MODE_PRIVATE);

        // Define options
        String[] options = {"No Music", "Soothing Rain", "Nature Jungle"};

        // Change options to a List<String> to easily manipulate the adapter
        List<String> optionsList = Arrays.asList(options);

        // Instantiate the custom adapter
        MusicOptionsAdapter musicAdapter = new MusicOptionsAdapter(this, android.R.layout.simple_list_item_1, optionsList, preferences.getString("chosenMusic", ""));



        // Set the custom adapter
        getListView().setAdapter(musicAdapter);

        // Set up item click listener
        getListView().setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click
            String selectedOption = options[position];

            // Save the chosen option to shared preferences
            preferences.edit().putString("chosenMusic", selectedOption).apply();

            // Notify the adapter that the data set has changed
            musicAdapter.notifyDataSetChanged();

            // Update the selected music in the adapter
            musicAdapter.setSelectedMusic(selectedOption);

            // Play the first 10 seconds of the selected music
            int getChosenMusicResourceId = getChosenMusicResourceId();

            if (getChosenMusicResourceId != 0) {
                playMusicForDuration(getChosenMusicResourceId, 10000);
            }
            else {
                stopMusic();
            }

            // Show the success message
            showMusicChangedMessage(selectedOption);
        });
    }



    // Message to alert the user that the music has been changed
    private void showMusicChangedMessage(String chosenMusic) {
        String message = "Music changed to " + chosenMusic + "!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to get the resource ID of the chosen music
    private int getChosenMusicResourceId() {
        // Retrieve the chosen music option from shared preferences
        String chosenMusic = preferences.getString("chosenMusic", "");

        // Check if the chosenMusic is empty, return the resource ID of the default music
        if (chosenMusic.isEmpty()) {
            return R.raw.rain; // Assuming Soothing Rain is a default music in the raw folder
        }

        // Map the chosen music to its corresponding resource ID
        if ("Soothing Rain".equals(chosenMusic)) {
            return R.raw.rain;
        } else if ("Nature Jungle".equals(chosenMusic)) {
            return R.raw.jungle;
        }

        // Default value, for when no music is selected
        return 0;
    }

    // Method to play music for a specified duration
    private void playMusicForDuration(int musicResourceId, int duration) {
        // If there is already music playing, stop it
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            stopMusic();
        }

        // Initialize mediaPlayer with the chosen music
        mediaPlayer = MediaPlayer.create(this, musicResourceId);

        // Set the on completion listener to release the MediaPlayer when playback is complete
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        // Start playing
        mediaPlayer.start();

        // Stop playback after the specified duration
        new Handler().postDelayed(() -> {
            stopMusic(); // Stop playback after the specified duration
        }, duration);
    }

    // Method to stop the currently playing music
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;  // Set mediaPlayer to null after releasing
        }
    }
}
