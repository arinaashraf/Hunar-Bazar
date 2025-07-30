package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellerProfileViewActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String uid;

    TextView sellerName, sellerShopName;
    ImageView sellerProfileImage;
    Button btnUploadProduct, btnViewMyProducts, btnSellerOrders, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile_view);


        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        sellerName = findViewById(R.id.sellerName);
        sellerShopName = findViewById(R.id.sellerShopName);
        sellerProfileImage = findViewById(R.id.sellerProfileImage);
        btnUploadProduct = findViewById(R.id.btnUploadProduct);
        btnViewMyProducts = findViewById(R.id.btnViewMyProducts);
        btnSellerOrders = findViewById(R.id.btnSellerOrders);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Load seller data
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("name").getValue(String.class);
                String shop = snapshot.child("shopName").getValue(String.class);
                String imageUrl = snapshot.child("profileImage").getValue(String.class);

                sellerName.setText(name);
                sellerShopName.setText(shop);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(sellerProfileImage);
                }else {
                    sellerProfileImage.setImageResource(R.drawable.placeholder);
                }
            }
        });

        btnUploadProduct.setOnClickListener(v ->
                startActivity(new Intent(this, UploadProductActivity.class)));

        btnViewMyProducts.setOnClickListener(v ->
                startActivity(new Intent(this, MyProductsActivity.class)));

        btnSellerOrders.setOnClickListener(v ->
                startActivity(new Intent(this, SellerOrdersActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(SellerProfileViewActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.categories) {
                startActivity(new Intent(SellerProfileViewActivity.this, CategoriesMain2.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(SellerProfileViewActivity.this, CartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.profile) {
                return true;
            }

            return false;
        });

    }

}
