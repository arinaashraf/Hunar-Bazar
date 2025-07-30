package com.example.hunarbazar7.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunarbazar7.R;
import com.example.hunarbazar7.adaptor.OrderAdapter;
import com.example.hunarbazar7.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SellerOrdersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Order> orderList;
    OrderAdapter adapter;
    DatabaseReference orderRef;
    String sellerUid;
    TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);

        recyclerView = findViewById(R.id.sellerOrdersRecyclerView);
        emptyMessage = findViewById(R.id.noOrdersMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        sellerUid = FirebaseAuth.getInstance().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference("orders");

        fetchSellerOrders();


    }

    private void fetchSellerOrders() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            String orderId = orderSnap.getKey();
                            String status = orderSnap.child("status").getValue(String.class);

                            DataSnapshot itemsSnapshot = orderSnap.child("items");
                            for (DataSnapshot itemSnap : itemsSnapshot.getChildren()) {
                                String productSellerId = itemSnap.child("sellerId").getValue(String.class);

                                if (sellerUid.equals(productSellerId)) {

                                    String name = itemSnap.child("name").getValue(String.class);
                                    String image = itemSnap.child("image").getValue(String.class);
                                    Object priceObj = itemSnap.child("price").getValue();
                                    String price = priceObj != null ? String.valueOf(priceObj) : "0";


                                    Order order = new Order();
                                    order.setOrderId(orderId);
                                    order.setName(name);
                                    order.setImageUrl(image);
                                    order.setPrice(price);
                                    order.setStatus(status);
                                    order.setSellerId(productSellerId);

                                    orderList.add(order);
                                }
                            }
                            Log.d("ORDER_FETCH", orderSnap.toString());
                        }

                        if (orderList.isEmpty()) {
                            emptyMessage.setVisibility(View.VISIBLE);
                        } else {
                            emptyMessage.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }


        });
    }
}
