package com.example.hostelroommatefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.example.hostelroommatefinder.adapters.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String currentUserId;
    private List<User> pendingRequestsList = new ArrayList<>();
    private PendingRequestAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        recyclerView = findViewById(R.id.recyclerViewPendingRequests);
        progressBar = findViewById(R.id.progressBarPendingRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("friend_requests")
                .whereEqualTo("toUserId", currentUserId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> fromUserIds = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        fromUserIds.add(doc.getString("fromUserId"));
                    }
                    fetchUsers(fromUserIds);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("PendingRequests", "Error fetching requests", e);
                });
    }

    private void fetchUsers(List<String> userIds) {
        if (userIds.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        pendingRequestsList.clear();
        for (String userId : userIds) {
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            user.setUserId(userId);
                            pendingRequestsList.add(user);
                            adapter = new PendingRequestAdapter(this, pendingRequestsList);
                            recyclerView.setAdapter(adapter);
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        }
    }
}
