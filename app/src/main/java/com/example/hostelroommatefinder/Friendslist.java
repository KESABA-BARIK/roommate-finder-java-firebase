package com.example.hostelroommatefinder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hostelroommatefinder.adapters.RoomAdapter;
import com.example.hostelroommatefinder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class Friendslist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RoomAdapter roommateAdapter;
    private List<User> roommateList = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendslist); // Reuse the same XML layout

        recyclerView = findViewById(R.id.recyclerViewRoommates);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup adapter and layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roommateAdapter = new RoomAdapter(this, roommateList, currentUserId, user -> {
            Toast.makeText(this, "Clicked: " + user.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(roommateAdapter);

        fetchAcceptedFriends();
    }

    private void fetchAcceptedFriends() {
        progressBar.setVisibility(View.VISIBLE);
        roommateList.clear();

        // Step 1: Get friend IDs
        db.collection("friends")
                .document(currentUserId)
                .collection("myFriends")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> friendIds = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot) {
                        friendIds.add(doc.getId());
                    }

                    if (friendIds.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "No accepted roommates found", Toast.LENGTH_SHORT).show();
                        roommateAdapter.notifyDataSetChanged();
                        return;
                    }

                    // Step 2: Fetch user data
                    db.collection("users")
                            .whereIn("userId", friendIds)
                            .get()
                            .addOnSuccessListener(userSnapshots -> {
                                for (DocumentSnapshot userDoc : userSnapshots) {
                                    User user = userDoc.toObject(User.class);
                                    if (user != null) {
                                        roommateList.add(user);
                                    }
                                }
                                roommateAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to fetch friends", Toast.LENGTH_SHORT).show();
                });
    }
}
