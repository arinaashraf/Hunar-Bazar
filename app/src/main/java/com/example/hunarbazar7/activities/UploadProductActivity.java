package com.example.hunarbazar7.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.model.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadProductActivity extends AppCompatActivity {

    EditText nameInput, imageInput, priceInput, discountInput, stockInput, categoryIdInput, descriptionInput;
    Button uploadButton;
    Spinner categorySpinner;
    ArrayList<Category> categoryList;
    ArrayAdapter<String> spinnerAdapter;
    int selectedCategoryId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());


        nameInput = findViewById(R.id.productName);
        imageInput = findViewById(R.id.imageUrl);
        priceInput = findViewById(R.id.price);
        discountInput = findViewById(R.id.discount);
        stockInput = findViewById(R.id.stock);
        uploadButton = findViewById(R.id.uploadButton);
        descriptionInput = findViewById(R.id.productDescription);

        categorySpinner = findViewById(R.id.categorySpinner);
        categoryList = new ArrayList<>();
        ArrayList<String> categoryNames = new ArrayList<>();

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Category cat = snap.getValue(Category.class);
                    categoryList.add(cat);
                    categoryNames.add(cat.getName());
                }

                spinnerAdapter = new ArrayAdapter<>(UploadProductActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(spinnerAdapter);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryId = categoryList.get(position).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        uploadButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String image = imageInput.getText().toString();
            String description = descriptionInput.getText().toString();
            double price = Double.parseDouble(priceInput.getText().toString());
            double discount = Double.parseDouble(discountInput.getText().toString());
            int stock = Integer.parseInt(stockInput.getText().toString());
            int categoryId = selectedCategoryId;
            int id = (int) (System.currentTimeMillis() / 1000);

            String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();



            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("image", image);
            product.put("description", description);
            product.put("price", price);
            product.put("discount", discount);
            product.put("stock", stock);
            product.put("categoryId", categoryId);
            product.put("id", id);
            product.put("quantity", 1);
            product.put("status", "available");
            product.put("sellerId", sellerId);

            FirebaseDatabase.getInstance().getReference("products")
                    .push()
                    .setValue(product)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Product Uploaded", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
