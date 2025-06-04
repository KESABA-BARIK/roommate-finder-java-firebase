package com.example.hostelroommatefinder.models;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hostelroommatefinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private EditText editTextName, editTextAge, editTextInterests, editTextPreferences;
    private Spinner genderSpinner;
    private Button buttonSaveProfile;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = view.findViewById(R.id.editTextName);
        editTextAge = view.findViewById(R.id.editTextAge);
        editTextInterests = view.findViewById(R.id.editTextInterests);
        editTextPreferences = view.findViewById(R.id.editTextPreferences);
        genderSpinner = view.findViewById(R.id.genderSpinner);


        buttonSaveProfile.setOnClickListener(v -> saveUserProfile());

        return view;
    }

    private void saveUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        String name = editTextName.getText().toString().trim();
        int age = Integer.parseInt(editTextAge.getText().toString().trim());
        String gender = genderSpinner.getSelectedItem().toString();
        String interests = editTextInterests.getText().toString().trim();
        String preferences = editTextPreferences.getText().toString().trim();


        UserProfile userProfile = new UserProfile(userId, name, age, gender, interests, preferences, auth.getCurrentUser().getEmail());

        db.collection("users").document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving profile", Toast.LENGTH_SHORT).show());
    }
}
