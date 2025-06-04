package com.example.hostelroommatefinder;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private Button buttonSignup;
    private FirebaseAuth auth;
    private View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseManager.getAuth();
        rootView = findViewById(R.id.loginRootLayout);


        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonSignup.setOnClickListener(view -> openSignupPage());

        buttonLogin.setOnClickListener(view -> {
            fadeOutPageAndLogin();
        });

    }


    private void fadeOutPageAndLogin() {

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(rootView, "alpha", 1f, 0f);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, "scaleX", 1f, 0.9f);
         ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, "scaleY", 1f, 0.9f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOut, scaleX, scaleY);
        animatorSet.setDuration(500); // half a second

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                loginUser();
            }
        });

        animatorSet.start();
    }


    private void checkUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    if (!documentSnapshot.exists()) {
                        intent.putExtra("openProfile", true);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking profile!", Toast.LENGTH_SHORT).show();
                });
    }



    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // ✅ Redirect to MainActivity after login
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    checkUserProfile(user.getUid());
                } // Close LoginActivity
            } else {
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openSignupPage() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}