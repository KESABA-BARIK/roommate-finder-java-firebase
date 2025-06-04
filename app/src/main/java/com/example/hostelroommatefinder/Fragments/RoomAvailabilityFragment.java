package com.example.hostelroommatefinder.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.DashboardActivity;
import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.adapters.RoommateAdapter;
import com.example.hostelroommatefinder.addroom;
import com.example.hostelroommatefinder.models.Rooms;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RoomAvailabilityFragment extends Fragment {

    private TextView tvRoomCount;
    private RecyclerView recyclerViewRooms;
    private RoommateAdapter adapter;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_room_availability, container, false);

        tvRoomCount = view.findViewById(R.id.tvRoomCount);
        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        EditText etSearch = view.findViewById(R.id.etSearch);
        FloatingActionButton fabAddRoom = view.findViewById(R.id.fabAddRoom);

        adapter = new RoommateAdapter(getContext(), new ArrayList<>());
        recyclerViewRooms.setAdapter(adapter);

        //  Set up search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), addroom.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        loadRooms();

        return view;
    }

    private void loadRooms() {
        db.collection("rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Rooms> rooms = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String roomId = doc.getString("roomId");
                    Integer capacity = doc.getLong("capacity") != null ? doc.getLong("capacity").intValue() : null;
                    String location = doc.getString("location");
                    Integer vacancies = doc.getLong("vacancies") != null ? doc.getLong("vacancies").intValue() : null;
                    String contact = doc.getString("phoneNumber");
                    Double latitude = doc.getDouble("latitude");
                    Double longitude = doc.getDouble("longitude");

                    if (roomId != null && capacity != null && location != null && vacancies != null
                            && latitude != null && longitude != null) {
                        rooms.add(new Rooms(roomId, location, capacity, vacancies,contact, latitude, longitude));
                    }
                }

                adapter.setRooms(rooms);
                tvRoomCount.setText(rooms.size() + " rooms available");

                if (rooms.isEmpty()) {
                    tvRoomCount.setText("No rooms available");
                }
            } else {
                Log.e("Firestore", "Error getting rooms", task.getException());
                tvRoomCount.setText("Failed to load rooms");
            }
        });
    }
}
