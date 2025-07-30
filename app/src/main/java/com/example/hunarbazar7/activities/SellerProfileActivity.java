package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SellerProfileActivity extends AppCompatActivity {

    private EditText etSellerName, etShopName, etPhone, etCity, etExperience;
    private Button btnSaveSeller;
    private LinearLayout categoryContainer;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    private final List<String> selectedCategories = new ArrayList<>();
    private final List<String> categoryList = Arrays.asList(
            "Crochet and Knitting", "Sewing", "Wood Work", "Crafts",
            "Pottery", "Jewelry", "Decor", "Embroidery"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        initViews();

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(auth.getUid());

        populateCategories();

        btnSaveSeller.setOnClickListener(v -> {
            String name = etSellerName.getText().toString().trim();
            String shopName = etShopName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String experience = etExperience.getText().toString().trim();

            if (name.isEmpty() || shopName.isEmpty() || phone.isEmpty() || city.isEmpty() || experience.isEmpty() || selectedCategories.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> sellerData = new HashMap<>();
            sellerData.put("name", name);
            sellerData.put("shopName", shopName);
            sellerData.put("phone", phone);
            sellerData.put("city", city);
            sellerData.put("experience", experience);
            sellerData.put("categories", selectedCategories);
            sellerData.put("role", "seller");

            userRef.updateChildren(sellerData).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Seller Profile Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SellerProfileViewActivity.class));
                finish();
            });
        });
    }

    private void initViews() {
        etSellerName = findViewById(R.id.etSellerName);
        etShopName = findViewById(R.id.etShopName);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etExperience = findViewById(R.id.etExperience);
        btnSaveSeller = findViewById(R.id.btnSaveSeller);
        categoryContainer = findViewById(R.id.categoryContainer);
    }

    private void populateCategories() {
        for (String category : categoryList) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(category);
            checkBox.setPadding(8, 8, 8, 8);
            checkBox.setTextSize(16);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
            });

            categoryContainer.addView(checkBox);
        }
    }
}
