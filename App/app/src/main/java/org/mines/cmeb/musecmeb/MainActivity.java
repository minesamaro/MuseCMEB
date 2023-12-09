package org.mines.cmeb.musecmeb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mines.cmeb.musecmeb.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set MenuFragment as the initial fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new MenuFragment())
                .commit();

        // Startup in the menu tab
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu3);

        // Change Fragments According to the selected tab
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (item.getItemId() == R.id.profile4) {
                if (currentFragment instanceof MenuFragment || currentFragment instanceof HistoryFragment) {
                    // Apply the slide in left transition
                    transaction.setCustomAnimations(R.anim.slide_in_right, 0);
                }
                transaction.replace(R.id.frame_layout, new ProfileFragment());
            } else if (item.getItemId() == R.id.menu3) {
                if (currentFragment instanceof ProfileFragment) {
                    // Apply the slide in left transition
                    transaction.setCustomAnimations(R.anim.slide_in_left, 0);
                } else if (currentFragment instanceof HistoryFragment) {
                    // Apply the slide in right transition
                    transaction.setCustomAnimations(R.anim.slide_in_right, 0);
                }
                transaction.replace(R.id.frame_layout, new MenuFragment());

            } else if (item.getItemId() == R.id.history) {
                if (currentFragment instanceof MenuFragment || currentFragment instanceof ProfileFragment) {
                    // Apply the slide in left transition
                    transaction.setCustomAnimations(R.anim.slide_in_left, 0);
                }
                transaction.replace(R.id.frame_layout, new HistoryFragment());
            }

            transaction.commit();
            return false;
        });
    }
}