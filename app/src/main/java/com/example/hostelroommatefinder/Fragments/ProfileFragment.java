package com.example.hostelroommatefinder.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hostelroommatefinder.FriendRequestActivity;
import com.example.hostelroommatefinder.Friendslist;
import com.example.hostelroommatefinder.LoginActivity;
import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfilePic;
    private TextView textViewUserName, textViewUserEmail, textViewAge, textViewGender, textViewInterests, textViewPreferences;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiles, container, false);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        // Initialize UI
        imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        textViewAge = view.findViewById(R.id.textViewAge);
        textViewGender = view.findViewById(R.id.textViewGender);
        textViewInterests = view.findViewById(R.id.textViewInterests);
        textViewPreferences = view.findViewById(R.id.textViewPreferences);
        FloatingActionButton fabAddRoom = view.findViewById(R.id.fabAddRoom3);
        loadUserProfile();
        btnLogout.setOnClickListener(v -> {
            fadeOutAndLogout(view);
        });

        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Friendslist.class);
            startActivity(intent);
        });
        return view;
    }

    private void fadeOutAndLogout(View view) {
        AnimationSet animationSet = new AnimationSet(true);

        // Fade out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

        // Scale down animation
        ScaleAnimation scaleDown = new ScaleAnimation(
                1.0f, 0.3f,  // X scale: from 100% to 30%
                1.0f, 0.3f,  // Y scale: from 100% to 30%
                Animation.RELATIVE_TO_SELF, 0.5f,  // pivot X at center
                Animation.RELATIVE_TO_SELF, 0.5f   // pivot Y at center
        );

        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(scaleDown);
        animationSet.setDuration(600);
        animationSet.setFillAfter(true);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Sign out after animation
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        view.startAnimation(animationSet);
    }


    private void loadUserProfile() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            textViewUserName.setText(user.getName());
                            textViewUserEmail.setText(user.getEmail());
                            textViewAge.setText("Age: " + user.getAge());
                            textViewGender.setText("Gender: " + user.getGender());
                            textViewInterests.setText("Interests: " + user.getInterests());
                            textViewPreferences.setText("Preferences: " + user.getPreferences());
                            String imageUrl = documentSnapshot.getString("profileImage");

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(user.getProfileImage())
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.profile)
                                        .into(imageViewProfilePic);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}