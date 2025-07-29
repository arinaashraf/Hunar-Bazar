package com.example.hunarbazar7.activities;

import com.example.hunarbazar7.activities.BuyerOrdersActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button btnUploadProduct, viewOrdersBtn;
    Button logoutBtn, btnViewMyProducts, btnSellerOrders;
    TextView welcomeText;
    String userId;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String userRole = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout sellerDetailsLayout = findViewById(R.id.sellerDetailsCard);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvShopName = findViewById(R.id.tvShopName);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvCity = findViewById(R.id.tvCity);
        TextView tvExperience = findViewById(R.id.tvExperience);
        TextView tvCategories = findViewById(R.id.tvCategories);


        welcomeText = findViewById(R.id.welcomeText);
        viewOrdersBtn = findViewById(R.id.viewOrdersBtn);
        btnUploadProduct = findViewById(R.id.btnUploadProduct);
        logoutBtn = findViewById(R.id.logoutBtn);
        btnViewMyProducts = findViewById(R.id.btnViewMyProducts);
        btnSellerOrders = findViewById(R.id.btnSellerOrders);

        TextView profileName = findViewById(R.id.profileName);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String role = snapshot.child("role").getValue(String.class);

                profileName.setText(name != null ? name : "Username");
                welcomeText.setText("Welcome, " + (name != null ? name : email));

                if ("buyer".equals(role)) {
                    viewOrdersBtn.setVisibility(View.VISIBLE);
                    btnUploadProduct.setVisibility(View.GONE);
                    btnViewMyProducts.setVisibility(View.GONE);
                    btnSellerOrders.setVisibility(View.GONE);
                    sellerDetailsLayout.setVisibility(View.GONE);
                } else if ("seller".equals(role)) {
                    btnUploadProduct.setVisibility(View.VISIBLE);
                    btnViewMyProducts.setVisibility(View.VISIBLE);
                    btnSellerOrders.setVisibility(View.VISIBLE);
                    viewOrdersBtn.setVisibility(View.GONE);
                    sellerDetailsLayout.setVisibility(View.VISIBLE);

                    tvName.setText("Name: " + snapshot.child("name").getValue(String.class));
                    tvShopName.setText("Shop Name: " + snapshot.child("shopName").getValue(String.class));
                    tvPhone.setText("Phone: " + snapshot.child("phone").getValue(String.class));
                    tvCity.setText("City: " + snapshot.child("city").getValue(String.class));
                    tvExperience.setText("Experience: " + snapshot.child("experience").getValue(String.class));

                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot catSnap : snapshot.child("categories").getChildren()) {
                        categories.add(catSnap.getValue(String.class));
                    }
                    tvCategories.setText("Categories: " + TextUtils.join(", ", categories));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                welcomeText.setText("Failed to load user");
            }
        });


        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        viewOrdersBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, BuyerOrdersActivity.class));
        });

        btnViewMyProducts.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, MyProductsActivity.class)));

        btnSellerOrders.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, SellerOrdersActivity.class)));



        btnUploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UploadProductActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.categories) {
                startActivity(new Intent(ProfileActivity.this, CategoriesMain2.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(ProfileActivity.this, CartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.profile) {
                return true;
            }

            return false;
        });

    }

    private void setupBuyerProfile() {
        findViewById(R.id.btnUploadProduct).setVisibility(View.GONE);
    }

    private void setupSellerProfile() {
        findViewById(R.id.btnUploadProduct).setVisibility(View.VISIBLE);
    }

}