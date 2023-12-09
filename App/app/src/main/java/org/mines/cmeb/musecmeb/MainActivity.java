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

        // Startup in the menu tab
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu3);
        replaceFragment(new MenuFragment());


        // Change Fragments According to the selected tab
        binding.bottomNavigationView.setOnItemSelectedListener(item-> {
            if(item.getItemId()==R.id.profile4) {
                replaceFragment(new ProfileFragment());
            }
            if(item.getItemId()==R.id.menu3) {
                replaceFragment(new MenuFragment());
            }
            if(item.getItemId()==R.id.history) {
                replaceFragment(new HistoryFragment());
            }

            return false;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Check if the fragment is an instance of ProfileFragment
        if (fragment instanceof ProfileFragment) {
            // Set the slide animation
            transaction.setCustomAnimations(R.anim.slide_in_right, 0);
        } else if (fragment instanceof HistoryFragment) {
            // Set the slide animation
            transaction.setCustomAnimations(R.anim.slide_in_left, 0);
        }
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }







}