package com.example.hostelroommatefinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.hostelroommatefinder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextPassword, editTextAge, editTextInterests, editTextPreferences;
    private Spinner genderSpinner;
    private Button buttonSignup;
    private ImageView imageViewProfile;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for selecting an image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI Components
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextAge = findViewById(R.id.editTextAge);
        genderSpinner = findViewById(R.id.genderSpinner);
        editTextInterests = findViewById(R.id.editTextInterests);
        editTextPreferences = findViewById(R.id.editTextPreferences);
        buttonSignup = findViewById(R.id.buttonSignup);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        progressBar = findViewById(R.id.progressBar);

        // Set click listener for selecting an image
        imageViewProfile.setOnClickListener(v -> selectProfileImage());

        buttonSignup.setOnClickListener(view -> registerUser());
    }

    // Open Gallery to Pick an Image
    private void selectProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the Selected Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageViewProfile); // Display selected image
        }
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String interests = editTextInterests.getText().toString().trim();
        String preferences = editTextPreferences.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(interests) || TextUtils.isEmpty(preferences)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    if (imageUri != null) {
                        uploadProfileImage(firebaseUser.getUid(), name, age, gender, interests, preferences, email);
                    } else {
                        saveUserToFirestore(firebaseUser.getUid(), name, age, gender, interests, preferences, email, "");
                    }
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Signup failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadProfileImage(String userId, String name, int age, String gender, String interests,
                                    String preferences, String email) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String profileImageUrl = uri.toString();
                    saveUserToFirestore(userId, name, age, gender, interests, preferences, email, profileImageUrl);
                })).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserToFirestore(String userId, String name, int age, String gender, String interests,
                                     String preferences, String email, String profileImage) {
        User user = new User(userId, name, age, gender, interests, preferences, email, profileImage);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error saving profile!", Toast.LENGTH_SHORT).show();
                });
    }
}
