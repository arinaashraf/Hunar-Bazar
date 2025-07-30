package com.example.hunarbazar7.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.adaptor.categoryAdaptor;
import com.example.hunarbazar7.databinding.ActivityCategoriesMain2Binding;
import com.example.hunarbazar7.model.Category;
import androidx.core.graphics.Insets;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoriesMain2 extends AppCompatActivity {

    ActivityCategoriesMain2Binding binding;
    com.example.hunarbazar7.adaptor.categoryAdaptor categoryAdaptor;
    ArrayList<Category> categories;

    MaterialToolbar toolbar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CategoryDebug", "CategoriesMain2 loaded successfully");

        binding = ActivityCategoriesMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.categories);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(CategoriesMain2.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.categories) {
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(CategoriesMain2.this, CartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(CategoriesMain2.this, RoleCheckActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            }

            return false;
        });


        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, statusBarInsets.top, 0, 0);
            return insets;
        });

        categories = new ArrayList<>();

        categoryAdaptor = new categoryAdaptor(this, categories);




        RecyclerView recyclerView = binding.categorieslist2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdaptor);

        fetchCategoriesFromFirebase();

        bottomNavigationView.setSelectedItemId(R.id.categories);

    }

    private void fetchCategoriesFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Category category = snap.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }
                categoryAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load categories: " + error.getMessage());
            }
        });
    }


    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
