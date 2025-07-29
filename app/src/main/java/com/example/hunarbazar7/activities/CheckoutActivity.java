package com.example.hunarbazar7.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {

    EditText etName, etPhone, etAddress;
    TextView tvSubtotal, tvShipping, tvDiscount, tvTotal;
    Button btnPlaceOrder;
    RadioGroup paymentGroup;
    RadioButton codOption;

    double subtotal = 0;
    double shipping = 200;
    double discount = 0;
    double total = 0;

    ArrayList<Product> cartItems;
    DatabaseReference orderRef, cartRef, userRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvDiscount = findViewById(R.id.tvItemDiscount);
        tvTotal = findViewById(R.id.tvTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        user = FirebaseAuth.getInstance().getCurrentUser();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child("guest");
        orderRef = FirebaseDatabase.getInstance().getReference("orders");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        cartItems = new ArrayList<>();

        autoFillBuyerInfo();
        loadCartAndCalculate();

        btnPlaceOrder.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderRef.push().getKey();
            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", orderId);
            orderData.put("name", name);
            orderData.put("phone", phone);
            orderData.put("address", address);
            orderData.put("subtotal", subtotal);
            orderData.put("shipping", shipping);
            orderData.put("discount", discount);
            orderData.put("total", total);
            orderData.put("status", "Pending");
            orderData.put("timestamp", System.currentTimeMillis());
            orderData.put("paymentMethod", "Cash on Delivery");

            orderRef.child(orderId).setValue(orderData).addOnSuccessListener(unused -> {
                for (Product p : cartItems) {
                    orderRef.child(orderId).child("items")
                            .child(String.valueOf(p.getId()))
                            .setValue(p);
                }

                cartRef.removeValue();
                Toast.makeText(this, "Order Placed!", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void autoFillBuyerInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);

                etName.setText(name != null ? name : "");
                etPhone.setText(phone != null ? phone : "");
                etAddress.setText(address != null ? address : "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartAndCalculate() {
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product product = snap.getValue(Product.class);
                    if (product != null) {
                        cartItems.add(product);
                        subtotal += product.getPrice() * product.getQuantity();

                        if (product.getDiscount() > 0) {
                            discount += product.getDiscount() * product.getQuantity();
                        }
                    }
                }

                total = subtotal + shipping - discount;

                tvSubtotal.setText("Subtotal: PKR " + subtotal);
                tvDiscount.setText("Item Discounts: PKR " + discount);
                tvShipping.setText("Shipping: PKR " + shipping);
                tvTotal.setText("Total: PKR " + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
