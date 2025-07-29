package com.example.hunarbazar7.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.hunarbazar7.adaptor.ProductAdaptor;

import com.example.hunarbazar7.R;

import com.example.hunarbazar7.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdaptor productAdapter;
    ArrayList<Product> productList;
    int selectedCategoryId;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);


        selectedCategoryId = getIntent().getIntExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        Toolbar toolbar = findViewById(R.id.categoryToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (categoryName != null && !categoryName.isEmpty()) {
                getSupportActionBar().setTitle(categoryName);
            } else {
                getSupportActionBar().setTitle("Filtered Products");
            }
        }

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }


        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        productList = new ArrayList<>();
        productAdapter = new ProductAdaptor(this, productList);
        recyclerView.setAdapter(productAdapter);

        selectedCategoryId = getIntent().getIntExtra("categoryId", -1);
        if (selectedCategoryId != -1) {
            fetchProductsByCategory(selectedCategoryId);
        }
    }


    private void fetchProductsByCategory(int categoryId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        Query query = ref.orderByChild("categoryId").equalTo(categoryId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch products: " + error.getMessage());
            }
        });
    }
}
