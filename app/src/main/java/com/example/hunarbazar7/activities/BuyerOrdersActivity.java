package com.example.hunarbazar7.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.model.Order;
import com.example.hunarbazar7.adaptor.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BuyerOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Order> orderList;
    OrderAdapter adapter;
    DatabaseReference orderRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_orders);

        recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getUid();
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        orderRef.orderByChild("buyerId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    orderList.add(order);
                }
                orderList.add(new Order("Test Product", "Test Seller", "https://i.pinimg.com/736x/b1/18/4a/b1184a83466ac17556a5737e658ec5ce.jpg", "999", "Pending"));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("BuyerOrders", "DB Error: " + error.getMessage());
            }
        });
    }
}
