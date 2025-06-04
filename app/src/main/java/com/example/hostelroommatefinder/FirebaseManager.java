package com.example.hostelroommatefinder;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseManager {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static FirebaseFirestore getDatabase() {
        return db;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }
}
