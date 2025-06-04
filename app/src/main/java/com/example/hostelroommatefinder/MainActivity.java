package com.example.hostelroommatefinder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hostelroommatefinder.Fragments.DashboardFragment;
import com.example.hostelroommatefinder.Fragments.ProfileFragment;
import com.example.hostelroommatefinder.Fragments.RoomAvailabilityFragment;
import com.example.hostelroommatefinder.Fragments.RoommateMatchingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Redirect to login if user not authenticated
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Load default fragment
        loadFragment(new ProfileFragment());

        // Handle navigation with if-else to avoid "constant expression required" error
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_matching) {
                selectedFragment = new RoomAvailabilityFragment();
            } else if (itemId == R.id.nav_availability) {
                selectedFragment = new RoommateMatchingFragment();
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
