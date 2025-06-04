package com.example.hostelroommatefinder.adapters;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.ChatActivity;
import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.models.FriendRequest;
import com.example.hostelroommatefinder.models.User;
import com.example.hostelroommatefinder.receivers.FriendRequestReceiver;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    public interface OnRoommateClickListener {
        void onRoommateClick(User user);
    }

    private Context context;
    private List<User> userList;
    private String currentUserId;
    private OnRoommateClickListener listener;
    public List<User> fullUserList;
    // ✅ Constructor with all parameters
    public RoomAdapter(Context context, List<User> roommateList, String currentUserId, OnRoommateClickListener listener) {
        this.context = context;
        this.userList = roommateList;
        this.listener = listener;
        this.currentUserId = currentUserId;
    }

    // ✅ Constructor without currentUserId and listener
    public RoomAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    // ✅ Constructor without currentUserId
    public RoomAdapter(Context context, List<User> roommateList, OnRoommateClickListener listener) {
        this.context = context;
        this.userList = roommateList;
        this.listener = listener;
    }
    public RoomAdapter(Context context, List<User> roommateList, String currentUserId) {
        this.context = context;
        this.userList = new ArrayList<>(roommateList);
        this.fullUserList = new ArrayList<>(roommateList);
        this.currentUserId = currentUserId;
    }
    // IMPROVED Filter method with debugging
    public void filter(String query) {
        Log.d("RoomAdapter", "Filter called with: '" + query + "'");
        Log.d("RoomAdapter", "fullUserList size: " + (fullUserList != null ? fullUserList.size() : "null"));

        if (fullUserList == null) {
            Log.e("RoomAdapter", "fullUserList is null!");
            return;
        }

        userList.clear();

        if (query == null || query.trim().isEmpty()) {
            userList.addAll(fullUserList);
            Log.d("RoomAdapter", "Empty query - added all users: " + userList.size());
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (User user : fullUserList) {
                if (user != null && user.getName() != null &&
                        user.getName().toLowerCase().contains(lowerCaseQuery)) {
                    userList.add(user);
                    Log.d("RoomAdapter", "Added user: " + user.getName());
                }
            }
            Log.d("RoomAdapter", "Filtered result size: " + userList.size());
        }

        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvAge.setText("Age: " + user.getAge());
        holder.tvGender.setText("Gender: " + user.getGender());
        holder.tvInterests.setText("Interests: " + user.getInterests());
        holder.tvPreferences.setText("Preferences: " + user.getPreferences());

        // Chat on card tap
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverId", user.getUserId());
            context.startActivity(intent);
        });
        FirebaseFirestore.getInstance()
                .collection("friends")
                .document(currentUserId)
                .collection("myFriends")
                .document(user.getUserId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        holder.btnSendRequest.setText("Added");
                        holder.btnSendRequest.setEnabled(false);
                    } else {
                        holder.btnSendRequest.setText("Send Request");
                        holder.btnSendRequest.setEnabled(true);

                        holder.btnSendRequest.setOnClickListener(v -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            FriendRequest request = new FriendRequest(currentUserId, user.getUserId(), "pending",user.getName());
                            String requestId = currentUserId + "_" + user.getUserId();

                            db.collection("friend_requests").document(requestId)
                                    .set(request)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT).show();
                                        sendFriendRequest(context, currentUserId, user.getUserId(),user.getName());
                                        holder.btnSendRequest.setText("Requested");
                                        holder.btnSendRequest.setEnabled(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to send request", Toast.LENGTH_SHORT).show();
                                    });
                        });
                    }
                });


    }

    private void sendFriendRequest(Context context, String fromUserId, String toUserId,String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FriendRequest request = new FriendRequest(fromUserId, toUserId, "pending",username);
        String requestId = fromUserId + "_" + toUserId;


        db.collection("friend_requests").document(requestId)
                .set(request)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Friend request sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("com.example.NEW_FRIEND_REQUEST");
                    intent.putExtra("fromUserId", fromUserId);
                    intent.putExtra("fromUserName", username);
                    context.sendBroadcast(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to send request", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    //  Only one correct ViewHolder with the Button
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvGender, tvInterests, tvPreferences;
        Button btnSendRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvInterests = itemView.findViewById(R.id.tvInterests);
            tvPreferences = itemView.findViewById(R.id.tvPreferences);
            btnSendRequest = itemView.findViewById(R.id.btnSendRequest);
        }
    }
}