package com.example.hunarbazar7.activities;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.adaptor.ProductAdaptor;
import com.example.hunarbazar7.databinding.ActivityMainBinding;
import com.example.hunarbazar7.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ProductAdaptor productAdaptor;
    ArrayList<Product> products;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.categories) {
                startActivity(new Intent(MainActivity.this, CategoriesMain2.class));
                return true;
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.profile) {
                startActivity(new Intent(MainActivity.this, RoleCheckActivity.class));
                return true;
            }

            return false;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        products = new ArrayList<>();

        initProducts();

        productAdaptor = new ProductAdaptor(this, products);
        binding.productlist.setLayoutManager(new GridLayoutManager(this, 2));
        binding.productlist.setAdapter(productAdaptor);

        initSliderFromDiscountedProducts();

        fetchAllProducts();

        binding.searchBar.setOnSearchActionListener(new com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    productAdaptor.filter("");
                    binding.carousel.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                productAdaptor.filter(text.toString());
                binding.carousel.setVisibility(View.GONE);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });


    }

    void initSliderFromDiscountedProducts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Product> allProducts = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null && product.getImage() != null) {
                        allProducts.add(product);
                    }
                }

                Collections.sort(allProducts, (p1, p2) -> Double.compare(p2.getDiscount(), p1.getDiscount()));

                for (int i = 0; i < Math.min(4, allProducts.size()); i++) {
                    Product product = allProducts.get(i);
                    CarouselItem item = new CarouselItem(product.getImage(), product.getName());
                    binding.carousel.addData(item);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Carousel fetch failed: " + error.getMessage());
            }
        });
    }

    private void openProductDetailById(String productId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products").child(productId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    Intent intent = new Intent(MainActivity.this, ProductDetail.class);
                    intent.putExtra("name", product.getName());
                    intent.putExtra("image", product.getImage());
                    intent.putExtra("id", product.getId());
                    intent.putExtra("price", product.getPrice());
                    intent.putExtra("discount", product.getDiscount());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CarouselClick", "Error fetching product: " + error.getMessage());
            }
        });
    }



    void initProducts(){
        products = new ArrayList<>();

        productAdaptor = new ProductAdaptor(this, products);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
        );
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        binding.productlist.setLayoutManager(layoutManager);

        binding.productlist.setHasFixedSize(true);
        binding.productlist.setItemViewCacheSize(20);

        binding.productlist.setAdapter(productAdaptor);
    }

    private void fetchAllProducts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Product> allProducts = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    try {
                        Log.d("ProductData", snap.getValue().toString());
                        Product product = snap.getValue(Product.class);
                        if (product != null && product.getName() != null) {
                            allProducts.add(product);
                        } else {
                            Log.e("ProductFetch", "Product is null or missing name: " + snap.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("ProductFetch", "Error parsing product: " + e.getMessage());
                    }
                }


                Collections.shuffle(allProducts);

                productAdaptor.updateData(allProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch all products: " + error.getMessage());
            }
        });
    }


}



