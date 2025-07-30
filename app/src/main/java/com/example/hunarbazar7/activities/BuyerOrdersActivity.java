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
                    String orderId = orderSnap.getKey();
                    String status = orderSnap.child("status").getValue(String.class);

                    DataSnapshot itemsSnapshot = orderSnap.child("items");
                    for (DataSnapshot itemSnap : itemsSnapshot.getChildren()) {
                        String name = itemSnap.child("name").getValue(String.class);
                        String image = itemSnap.child("image").getValue(String.class);
                        String price = String.valueOf(itemSnap.child("price").getValue());
                        String sellerId = itemSnap.child("sellerId").getValue(String.class);

                        Order order = new Order();
                        order.setOrderId(orderId);
                        order.setName(name);
                        order.setImageUrl(image);
                        order.setPrice(price);
                        order.setStatus(status);
                        order.setSellerId(sellerId);

                        orderList.add(order);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("BuyerOrders", "DB Error: " + error.getMessage());
            }
        });
    }
}
