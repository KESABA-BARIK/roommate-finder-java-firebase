package com.example.hostelroommatefinder.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.models.FriendRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
private String name;
    private Context context;
    private List<FriendRequest> requestList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FriendRequestAdapter(Context context, List<FriendRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position) {
        FriendRequest request = requestList.get(position);
        db.collection("users").document(request.getFromUserId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("name");
                        holder.tvFromUser.setText("From: " + name);
                        if (name != null) {
                            Log.d("Firestore", "User name: " + name);
                            // You can use this name in a notification, UI, etc.
                        } else {
                            Log.w("Firestore", "Name field is missing");
                        }
                    } else {
                        Log.w("Firestore", "No such user found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to fetch user", e);
                });


        holder.btnAccept.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Accept Request
            db.collection("friend_requests")
                    .document(request.getFromUserId() + "_" + request.getToUserId())
                    .update("status", "accepted")
                    .addOnSuccessListener(unused -> {

                        // Add to current user's friends list
                        db.collection("friends")
                                .document(request.getToUserId())
                                .collection("myFriends")
                                .document(request.getFromUserId())
                                .set(Collections.singletonMap("addedOn", System.currentTimeMillis()));

                        // Add to sender's friends list
                        db.collection("friends")
                                .document(request.getFromUserId())
                                .collection("myFriends")
                                .document(request.getToUserId())

                                .set(Collections.singletonMap("addedOn", System.currentTimeMillis()));

                        Toast.makeText(context, "Friend request accepted", Toast.LENGTH_SHORT).show();

                        // Disable the button after accepting
                        holder.btnAccept.setEnabled(false);
                        holder.btnAccept.setText("Added");

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromUser;
        Button btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromUser = itemView.findViewById(R.id.tvFromUser);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }
}
