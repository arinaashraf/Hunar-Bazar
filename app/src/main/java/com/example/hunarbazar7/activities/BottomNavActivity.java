package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        container = findViewById(R.id.container);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNav();
    }

    public void setContentForChildActivity(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, container, true);
    }

    public void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d("BottomNavClick", "Item clicked: " + itemId);
            Class<?> currentActivity = getClass();

            if (itemId == R.id.home) {
                return true; // Already here
            } else if (itemId == R.id.categories) {
                startActivity(new Intent(this, CategoriesMain2.class));
                overridePendingTransition(0, 0); // no animation
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }


    public void setCurrentNavigationItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }
}
