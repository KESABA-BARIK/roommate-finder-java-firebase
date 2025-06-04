package com.example.hostelroommatefinder;
import com.bumptech.glide.Glide;
import com.example.hostelroommatefinder.adapters.ChatAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hostelroommatefinder.adapters.ChatAdapter;
import com.example.hostelroommatefinder.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private FirebaseAuth auth;
    private DatabaseReference chatDatabase;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private String senderId, receiverId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // **Initialize UI Components**
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // **Firebase Authentication**
        auth = FirebaseAuth.getInstance();
        senderId = auth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("receiverId");
        TextView textViewName = findViewById(R.id.textView6);
        ImageView imageViewProfile = findViewById(R.id.imageViewK);
        // **Firebase Database Reference**
        chatDatabase = FirebaseDatabase.getInstance().getReference("books");
        FirebaseFirestore.getInstance().collection("users").document(receiverId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String imageUrl = documentSnapshot.getString("profileImage");

                        textViewName.setText(name);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Download image using Firebase Storage reference
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile)  // shown while loading
                                    .error(R.drawable.profile)        // shown on error
                                    .into(imageViewProfile);

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore fetch error
                    textViewName.setText("Unknown User");
                    imageViewProfile.setImageResource(R.drawable.profile);
                });
        // **Set RecyclerView**
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, messageList, senderId); // ✅ Fixed
        recyclerViewChat.setAdapter(chatAdapter);

        // **Load Messages**
        loadChatMessages();

        // **Send Message on Button Click**
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadChatMessages() {
        chatDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null &&
                            ((message.getSenderId().equals(senderId) && message.getReceiverId().equals(receiverId)) ||
                                    (message.getSenderId().equals(receiverId) && message.getReceiverId().equals(senderId)))) {
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            String messageId = chatDatabase.push().getKey();
            long timestamp = System.currentTimeMillis();

            // **Create Message Object**
            Message message = new Message(messageId, senderId, receiverId, messageText, timestamp);

            // **Store Message in Firebase**
            chatDatabase.child(messageId).setValue(message);
            editTextMessage.setText(""); // Clear Input
        }
    }
}
