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
    ActivityMainBinding binding;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    FrameLayout frameLayout; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the FrameLayout
        frameLayout = findViewById(R.id.frame_layout); // Add this line

        // Set up ViewPager with FragmentPagerAdapter
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        // Set initial page to MenuFragment
        viewPager.setCurrentItem(1);

        // Set initial item in BottomNavigationView to MenuFragment
        binding.bottomNavigationView.setSelectedItemId(R.id.menu3);

        // Synchronize ViewPager page changes with BottomNavigationView
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < binding.bottomNavigationView.getMenu().size()) {
                    binding.bottomNavigationView.getMenu().getItem(position).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Change Fragments According to the selected tab
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

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

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            fragmentList.add(new HistoryFragment());
            fragmentList.add(new MenuFragment());
            fragmentList.add(new ProfileFragment());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void replaceFragment(int position, Fragment fragment) {
            fragmentList.set(position, fragment);
            notifyDataSetChanged();
        }

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