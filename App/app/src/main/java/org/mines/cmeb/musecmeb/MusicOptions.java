package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MusicOptions extends AppCompatActivity {
    private ImageButton backButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Back button settings
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> onBackPressed());
    }
}
