package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class ProfileActivity extends AppCompatActivity {

    TextView welcomeText, profileName;
    Button viewOrdersBtn, logoutBtn;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure this layout is for BUYER

        welcomeText = findViewById(R.id.welcomeText);
        profileName = findViewById(R.id.profileName);
        viewOrdersBtn = findViewById(R.id.viewOrdersBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);

                profileName.setText(name != null ? name : "Username");
                welcomeText.setText("Welcome, " + (name != null ? name : email));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                welcomeText.setText("Failed to load user");
            }
        });

        // View Orders
        viewOrdersBtn.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, BuyerOrdersActivity.class))
        );

        // Logout
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // Bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (id == R.id.categories) {
                startActivity(new Intent(this, CategoriesMain2.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (id == R.id.cart) {
                startActivity(new Intent(this, CartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (id == R.id.profile) {
                return true;
            }

            return false;
        });
    }
}
