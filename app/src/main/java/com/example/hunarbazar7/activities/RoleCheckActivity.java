package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hunarbazar7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoleCheckActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue(String.class);

                if (role == null) {
                    Toast.makeText(RoleCheckActivity.this, "No role assigned", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(RoleCheckActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                if (role.equals("buyer")) {
                    String address = snapshot.child("address").getValue(String.class);
                    if (address != null && !address.isEmpty()) {
                        goTo(ProfileActivity.class);
                    } else {
                        goTo(BuyerProfileActivity.class);
                    }
                } else if (role.equals("seller")) {
                    String shopName = snapshot.child("shopName").getValue(String.class);
                    if (shopName != null && !shopName.isEmpty()) {
                        goTo(SellerProfileViewActivity.class);
                    } else {
                        goTo(SellerProfileActivity.class);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RoleCheckActivity.this, "Failed to check user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goTo(Class<?> activity) {
        Intent intent = new Intent(RoleCheckActivity.this, activity);
        startActivity(intent);
        finish();
    }
}
