package com.example.hostelroommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelroommatefinder.models.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getIntent().getBooleanExtra("openProfile", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new ProfileFragment())
                    .commit();
        }

        // Use local variables instead of class-level fields
        Button btnFindRoommate = findViewById(R.id.btnFindRoommate);
        Button btnRoomAvailability = findViewById(R.id.btnRoomAvailability);
        Button btnChat = findViewById(R.id.btnChat);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Button click actions
        btnFindRoommate.setOnClickListener(view -> startActivity(new Intent(this, RoommateMatchingActivity.class)));
        btnRoomAvailability.setOnClickListener(view -> startActivity(new Intent(this, RoomAvailabilityActivity.class)));
        btnChat.setOnClickListener(view -> startActivity(new Intent(this, addroom.class)));

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close DashboardActivity
        });
    }
}
