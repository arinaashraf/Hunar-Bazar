package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;

import com.example.hunarbazar7.databinding.ActivityProductdetailBinding;
import com.example.hunarbazar7.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ProductDetail extends AppCompatActivity {

    ActivityProductdetailBinding binding;
    Product currentProduct;

    ArrayList<Product> products;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        products = new ArrayList<>();

        double discount = getIntent().getDoubleExtra("discount", 0);
        int stock = getIntent().getIntExtra("stock", 0);
        double price = getIntent().getDoubleExtra("price", 0);

        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        int id = getIntent().getIntExtra("id", 0);
        String description = getIntent().getStringExtra("description");

        if (description == null || description.isEmpty()) {
            String[] defaultDescriptions = {
                    "This handcrafted piece is a beautiful blend of artistry and passion. Carefully made by skilled hands, it carries the warmth and dedication of its creator. Every thread, every detail, is a reflection of time, love, and patience. Perfect for adding a personal touch to your life or gifting someone something truly one-of-a-kind.",
                    "A timeless creation that embraces the charm of traditional craftsmanship. This product isn’t just made—it’s nurtured. Its textures, colors, and feel tell a story of dedication and heart. Whether for you or a loved one, it promises to deliver joy, comfort, and a connection to something made with genuine intention.",
                    "Carefully crafted by hand, this item brings together creativity and care in every stitch and shape. Unlike factory-made products, it radiates character and warmth. It's designed to last, inspire, and stand out in your home, wardrobe, or heart. Ideal for those who value authenticity and soulful design.",
                    "There’s something magical about holding a product that someone made with their own hands. This item is not just useful—it’s meaningful. Made from quality materials and infused with personal care, it offers more than function; it offers a connection to real artistry and slow, intentional living.",
                    "This item is a celebration of handmade beauty—where no two pieces are ever exactly alike. The thoughtful design and gentle finish make it ideal for gifting or cherishing. Whether it’s for cozy decor, everyday style, or heartfelt memories, this piece carries stories, care, and tradition within it.",
                    "Made in small batches with big love, this product stands apart with its personal touch and attention to detail. It reflects the hands that shaped it and the care that went into every choice. If you appreciate products with a soul, this one was made just for you.",
                    "Inspired by everyday moments and natural beauty, this handcrafted item brings warmth into your world. It isn’t just an object—it’s an expression. Whether it rests in your home or travels with you, it brings a bit of joy, intention, and handmade magic to your life.",
                    "This product captures the essence of handmade living. The soft imperfections and charming finish remind us that real beauty lies in things made slowly and sincerely. Let this piece be a reminder of the stories hands can tell when guided by heart and creativity.",
                    "From the very first thread to the final touch, this item was made with care. It’s not just something to use—it’s something to experience. A wonderful addition to your home or wardrobe, this handcrafted treasure adds warmth, meaning, and timeless appeal.",
                    "This isn't just a product—it’s a personal expression, born from hours of dedication and heart. Meant for those who admire the human touch in the digital age, it’s full of comfort, personality, and handmade charm. You'll feel the difference every time you use it."
            };
            int randomIndex = (int) (Math.random() * defaultDescriptions.length);
            description = defaultDescriptions[randomIndex];
        }



        binding.productDescription.setText(description);
        binding.productPrice.setText("Price: PKR " + price);
        binding.productDiscount.setText("Discount: " + discount + "%");
        binding.productStock.setText("In Stock: " + stock + " items");


        Glide.with(this)
                .load(image)
                .into(binding.productimage);


        currentProduct = new Product(
                name,
                image,
                "Available",
                price,
                0,
                10,
                id,
                1
        );

        getSupportActionBar().setTitle(name);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("cart")
                .child("guest")
                .child(String.valueOf(currentProduct.getId()));


        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (snap.getValue() instanceof Map) {
                        Product product = snap.getValue(Product.class);
                        products.add(product);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        binding.button.setOnClickListener(v -> {

            currentProduct.setQuantity(1);
            cartRef.setValue(currentProduct)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProductDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        binding.addtocartbtn.setEnabled(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProductDetail.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    });
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}