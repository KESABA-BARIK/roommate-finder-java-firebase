package com.example.hostelroommatefinder.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.FriendRequestActivity;
import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.adapters.RoomAdapter;
import com.example.hostelroommatefinder.adapters.RoommateAdapter;
import com.example.hostelroommatefinder.addroom;
import com.example.hostelroommatefinder.models.FriendRequest;
import com.example.hostelroommatefinder.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class RoommateMatchingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RoomAdapter roommateAdapter;
    private List<User> roommateList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String currentUserId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_roommate_matching, container, false);

        // Initialize
        recyclerView = view.findViewById(R.id.recyclerViewRoommates);
        progressBar = view.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FloatingActionButton fabAddRoom = view.findViewById(R.id.fabAddRoom2);
        // Get current user ID FIRST
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
            Log.d("DEBUG", "Current User ID: " + currentUserId);
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e("ERROR", "User authentication failed");
            return view;
        }

        // NOW set up the adapter with correct currentUserId
        roommateAdapter = new RoomAdapter(getContext(), roommateList, currentUserId, user -> {
            if (user != null && user.getName() != null) {
                Toast.makeText(getContext(), "Clicked on " + user.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(roommateAdapter);
        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FriendRequestActivity.class);
            startActivity(intent);
        });
        // Fetch roommates
        fetchRoommates();

        return view;
    }




    private void fetchRoommates() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .whereNotEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        roommateList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("FirestoreData", "Document: " + document.getData());

                            User user = document.toObject(User.class);
                            if (user.getName() == null) {
                                Log.e("FirestoreError", "User name is null for document: " + document.getId());
                            } else {
                                Log.d("FirestoreSuccess", "User found: " + user.getName());
                            }

                            roommateList.add(user);
                        }
                        roommateAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch roommates!", Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreError", "Error fetching roommates: ", task.getException());
                    }
                });
    }
}
