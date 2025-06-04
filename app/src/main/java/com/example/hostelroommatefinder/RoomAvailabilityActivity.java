package com.example.hostelroommatefinder;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RoomAvailabilityActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView textViewRooms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_availability);

        db = FirebaseFirestore.getInstance();  // Get the Firestore instance directly
        textViewRooms = findViewById(R.id.tvRoomCount);

        loadRooms();
    }

    private void loadRooms() {
        db.collection("rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    Log.e("Firestore", "No rooms found in Firestore!");
                    textViewRooms.setText("No rooms available.");
                    return;
                }

                StringBuilder roomsText = new StringBuilder();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("Firestore", "Document Data: " + document.getData()); // Debugging log

                    // Fetch all fields
                    String roomNo = document.getString("roomId");
                    Long capacity = document.getLong("capacity");
                    String location = document.getString("location");
                    Long availability = document.getLong("vacancies");


                    // Check for nulls and append data to the StringBuilder
                    roomsText.append("Room No: ").append(roomNo != null ? roomNo : "N/A").append("\n")
                            .append("Capacity: ").append(capacity != null ? capacity : "N/A").append("\n")
                            .append("Location: ").append(location != null ? location : "N/A").append("\n")
                            .append("Available: ").append(availability != null ? availability : "N/A").append("\n\n");

                }
                textViewRooms.setText(roomsText.toString());
            } else {
                Log.e("Firestore", "Error getting rooms", task.getException());
                textViewRooms.setText("Failed to load rooms.");
            }
        });
    }
}
