package org.mines.cmeb.musecmeb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mines.cmeb.musecmeb.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Binding object for the activity
    ActivityMainBinding binding;
    // ViewPager for managing fragments
    ViewPager viewPager;
    // Adapter for the ViewPager
    ViewPagerAdapter viewPagerAdapter;
    // FrameLayout for holding fragments
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the database helper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        ((GlobalMuse) getApplication()).setConnectedMuse(null);

        // Inflate the layout for this activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the FrameLayout
        frameLayout = findViewById(R.id.frame_layout); // Add this line

        // Set up ViewPager with FragmentPagerAdapter
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        //Set Offscreen page limit of 2 pages, to ensure faster transition between pages
        viewPager.setOffscreenPageLimit(2);

        // Set initial page to MenuFragment
        viewPager.setCurrentItem(1);

        // Set initial item in BottomNavigationView to MenuFragment
        binding.bottomNavigationView.setSelectedItemId(R.id.menu3);

        // Synchronize ViewPager page changes with BottomNavigationView
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override  // Method called when page is swiped
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override  // Method called when page is selected to change the highlighted item in the Bottom Navigation menu
            public void onPageSelected(int position) {
                if (position < binding.bottomNavigationView.getMenu().size()) {
                    binding.bottomNavigationView.getMenu().getItem(position).setChecked(true);
                }
            }

            @Override // Called when the user starts dragging, when the pager is automatically settling to the current page, or when it's fully stopped
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Change Fragments According to the selected tab
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            // Set visibility is used to show the correct part to the user
            // This is used so that the past session fragment does not need to be added to the viewpager and appear as a swipe option
            viewPager.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            if (item.getItemId() == R.id.profile4) {
                viewPager.setCurrentItem(2);
            } else if (item.getItemId() == R.id.menu3) {
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.history) {
                viewPager.setCurrentItem(0);
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (frameLayout.getVisibility() == View.VISIBLE) {
            viewPager.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        // Used to initialize Fragment Manager and add the desired fragments to the list
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            fragmentList.add(new HistoryFragment());
            fragmentList.add(new MenuFragment());
            fragmentList.add(new ProfileFragment());
        }

        // Used by ViewPager to get the fragment corresponding to a specified position
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        // Used by ViewPager to know how many pages to keep in memory
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        // Used by ViewPager to find the position of a specified object
        @Override
        public int getItemPosition(@NonNull Object object) {
            int index = fragmentList.indexOf(object);
            if (index == -1) {
                return POSITION_NONE;
            } else {
                return index;
            }
        }
    }

    // Getter method for the FrameLayout
    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    // Getter method for the ViewPager
    public ViewPager getViewPager() {
        return viewPager;
    }
}