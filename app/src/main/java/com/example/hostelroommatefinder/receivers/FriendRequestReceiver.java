package com.example.hostelroommatefinder.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.hostelroommatefinder.MainActivity;
import com.example.hostelroommatefinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendRequestReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "friend_request_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "com.example.NEW_FRIEND_REQUEST".equals(intent.getAction())) {
            String fromUserId = intent.getStringExtra("fromUserId");
            String fromUserN = intent.getStringExtra("fromUserName");

            createNotificationChannel(context); // Ensure channel exists

            // Fetch username from Firestore using fromUserId
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(fromUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String username = fromUserN;
                        if (username == null || username.isEmpty()) {
                            username = "Unknown user";
                        }

                        // Intent to open app
                        Intent notificationIntent = new Intent(context, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                context, 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("New Friend Request")
                                .setContentText("Friend request from: " + username)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (manager != null) {
                            int notificationId = (int) System.currentTimeMillis();
                            manager.notify(notificationId, builder.build());
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Fallback if user can't be fetched
                        sendFallbackNotification(context, fromUserId);
                    });
        }
    }

    private void sendFallbackNotification(Context context, String fallbackId) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Friend Request")
                .setContentText("Friend request from: " + fallbackId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Friend Request";
            String description = "Channel for friend request notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
