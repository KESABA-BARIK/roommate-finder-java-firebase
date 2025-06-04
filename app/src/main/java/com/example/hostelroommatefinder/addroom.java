package com.example.hostelroommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import  com.example.hostelroommatefinder.models.Rooms;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class    addroom extends AppCompatActivity {

    private EditText roomid, capacity, vacant, location,desc, phoneNumber;
    private TextView tvLatitude, tvLongitude;
    private Button add, btnPickLocation;
    private FirebaseFirestore db;

    // Store selected lat/lng
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room);

        db = FirebaseFirestore.getInstance();

        roomid = findViewById(R.id.room_no);
        capacity = findViewById(R.id.capacity);
        vacant = findViewById(R.id.vacancy);
        desc = findViewById(R.id.description);
        phoneNumber = findViewById(R.id.phone_number);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLongitude = findViewById(R.id.tv_longitude);
        add = findViewById(R.id.addroom);
        btnPickLocation = findViewById(R.id.btn_pick_location);
        location = findViewById(R.id.location);

        btnPickLocation.setOnClickListener(v -> {
            // Open map picker activity
            Intent intent = new Intent(addroom.this, MapPickerActivity.class);
            startActivityForResult(intent, 100);
        });

        add.setOnClickListener(v -> addRoomToFirebase());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            selectedLatitude = data.getDoubleExtra("latitude", 0);
            selectedLongitude = data.getDoubleExtra("longitude", 0);

            tvLatitude.setText("Latitude: " + selectedLatitude);
            tvLongitude.setText("Longitude: " + selectedLongitude);
        }
    }

    private void addRoomToFirebase() {
        String roomId = roomid.getText().toString().trim();
        String description = desc.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String locations = location.getText().toString().trim();

        int roomCapacity, vacancies;
        try {
            roomCapacity = Integer.parseInt(capacity.getText().toString().trim());
            vacancies = Integer.parseInt(vacant.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for capacity and vacancies", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomId.isEmpty() || description.isEmpty() || phone.isEmpty() || selectedLatitude == 0 || selectedLongitude == 0) {
            Toast.makeText(this, "Please fill all fields and pick location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Rooms object with phone and lat/lng
        Rooms room = new Rooms(roomId, locations, roomCapacity, vacancies, description, phone, selectedLatitude, selectedLongitude);

        db.collection("rooms").document(roomId).set(room)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(addroom.this, "Room added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(addroom.this, "Error adding room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        roomid.setText("");
        capacity.setText("");
        vacant.setText("");
        desc.setText("");
        phoneNumber.setText("");
        tvLatitude.setText("Latitude: Not selected");
        tvLongitude.setText("Longitude: Not selected");
        selectedLatitude = 0;
        selectedLongitude = 0;
    }
}
