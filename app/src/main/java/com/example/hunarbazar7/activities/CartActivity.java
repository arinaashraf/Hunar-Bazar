package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.adaptor.CartAdapter;
import com.example.hunarbazar7.databinding.ActivityCartBinding;
import com.example.hunarbazar7.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    CartAdapter adapter;
    ArrayList<Product> products;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(CartActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.categories) {
                startActivity(new Intent(CartActivity.this, CategoriesMain2.class));
                return true;
            } else if (itemId == R.id.cart) {
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(CartActivity.this, RoleCheckActivity.class));
                return true;
            }

            return false;
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        products = new ArrayList<>();

        adapter = new CartAdapter(this, products, this::updateSubtotal, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                updateSubtotal();
            }
        });


        binding.cartlist.setLayoutManager(new LinearLayoutManager(this));
        binding.cartlist.setAdapter(adapter);
        binding.cartlist.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child("guest");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    products.add(product);
                }
                adapter.notifyDataSetChanged();
                updateSubtotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });


        binding.continueBtn.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()
                            && snapshot.child("address").exists()
                            && snapshot.child("phone").exists()) {
                        startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                    } else {
                        startActivity(new Intent(CartActivity.this, BuyerProfileActivity.class));
                        Toast.makeText(CartActivity.this, "Please complete your profile first", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateSubtotal() {
        double subtotal = 0.0;
        for (Product p : products) {
            subtotal += p.getPrice() * p.getQuantity();
        }
        binding.subtotal.setText("PKR " + subtotal);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}