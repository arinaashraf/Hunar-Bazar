package com.example.hunarbazar7.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;
import com.example.hunarbazar7.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    List<Order> orderList;
    Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_buyer, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        if (order == null) return;

        holder.productName.setText("Product: " + order.getName());
        holder.sellerName.setText("Seller ID: " + order.getSellerId());
        holder.orderPrice.setText("Total: Rs. " + order.getPrice());
        holder.orderStatus.setText("Status: " + order.getStatus());

//        Glide.with(context).load(order.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.orderImage);

        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("orders")
                .child(order.getOrderId())
                .child("items");

        itemRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String imageUrl = itemSnap.child("image").getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(context)
                                .load(imageUrl)
                                .placeholder(R.drawable.placeholder)
                                .into(holder.orderImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, sellerName, orderPrice, orderStatus;
        ImageView orderImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            sellerName = itemView.findViewById(R.id.sellerName);
            orderPrice = itemView.findViewById(R.id.orderPrice);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderImage = itemView.findViewById(R.id.orderProductImage);
        }
    }
}
