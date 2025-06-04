package com.example.hostelroommatefinder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hostelroommatefinder.adapters.RoommateAdapter;
import com.example.hostelroommatefinder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoommateMatchingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoommateAdapter roommateAdapter;
    private List<User> roommateList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate_matching);

        // *Initialize UI Components*
        recyclerView = findViewById(R.id.recyclerViewRoommates);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // *Get current user ID*
        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
            Log.d("DEBUG", "Current User ID: " + currentUserId);
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e("ERROR", "User authentication failed");
            finish();
            return;
        }

        // *Setup RecyclerView*
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roommateAdapter = new RoommateAdapter(this, roommateList, user -> {
            if (user != null && user.getName() != null) {
                Toast.makeText(this, "Clicked on " + user.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ERROR", "User object or name is null");
                Toast.makeText(this, "Error: Invalid user data", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(roommateAdapter);

        // *Fetch roommates*
        fetchRoommates();
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
                            Log.d("FirestoreData", "Document: " + document.getData()); // ✅ Log full Firestore data

                            User user = document.toObject(User.class);

                            // ✅ Check if the name is missing
                            if (user.getName() == null) {
                                Log.e("FirestoreError", "User name is null for document: " + document.getId());
                            } else {
                                Log.d("FirestoreSuccess", "User found: " + user.getName());
                            }

                            roommateList.add(user);
                        }
                        roommateAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch roommates!", Toast.LENGTH_SHORT).show();
                        Log.e("FirestoreError", "Error fetching roommates: ", task.getException());
                    }
                });
    }


}
