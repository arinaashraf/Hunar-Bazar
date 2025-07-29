package com.example.hunarbazar7.activities;

import android.os.Bundle;
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
        orderRef.orderByChild("sellerId").equalTo(sellerUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            Order order = orderSnap.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
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
                        // Optional: Handle error
                    }
                });
    }
}
