package com.lichsuvietnam.app.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lichsuvietnam.app.R;
import com.lichsuvietnam.app.ui.fragments.*;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) fragment = new HomeFragment();
            else if (id == R.id.nav_map) fragment = new TimeMapFragment();
            else if (id == R.id.nav_learn) fragment = new LearnFragment();
            else if (id == R.id.nav_community) fragment = new CommunityFragment();
            else if (id == R.id.nav_profile) fragment = new ProfileFragment();

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            }
            return true;
        });

        // Default to Home
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    public void selectTab(int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }

    /**
     * Load a fragment into the container without changing the bottom nav selection.
     * Used for screens like DynamicTimelineFragment that are not a bottom nav tab.
     */
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit();
    }
}
