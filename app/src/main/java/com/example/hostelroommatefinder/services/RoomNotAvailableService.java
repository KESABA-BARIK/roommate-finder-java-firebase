package com.example.hostelroommatefinder.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RoomNotAvailableService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Rooms not available", Toast.LENGTH_SHORT).show();
        // Additional logic can be added here, such as sending notifications or logging
        stopSelf(); // Stop the service after execution
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Binding is not used in this service
        return null;
    }
}
