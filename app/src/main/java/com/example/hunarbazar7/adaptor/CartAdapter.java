package com.example.hunarbazar7.adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;
import com.example.hunarbazar7.databinding.ItemCartBinding;
import com.example.hunarbazar7.databinding.QuantityDialogBinding;
import com.example.hunarbazar7.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Product> products;
    CartListener cartListener;
    Runnable onCartChanged;
    DatabaseReference cartRef;

    public interface CartListener{
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<Product> products, Runnable onCartChanged, CartListener cartListener){
        this.context = context;
        this.products = products;
        this.onCartChanged = onCartChanged;
        this.cartListener = cartListener;
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child("guest");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);

        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.cartimage);

        holder.binding.name.setText(product.getName());
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");
        holder.binding.price.setText("PKR " + product.getPrice());

        holder.binding.deleteBtn.setOnClickListener(v -> {
            cartRef.child(String.valueOf(product.getId())).removeValue()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                        onCartChanged.run();
                    });
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuantityDialogBinding quantityDialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(quantityDialogBinding.getRoot())
                        .create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                quantityDialogBinding.productName.setText(product.getName());
                quantityDialogBinding.productStock.setText("Stock: " + product.getStock());
                quantityDialogBinding.quantity.setText(String.valueOf(product.getQuantity()));

                int stock = product.getStock();

                quantityDialogBinding.plusbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = product.getQuantity();
                        quantity++;

                        if(quantity>product.getStock()){
                            Toast.makeText(context, "Max stock available: " + product.getStock(), Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            product.setQuantity(quantity);
                            quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                        }
                    }
                });

                quantityDialogBinding.minusbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = product.getQuantity();
                        if(quantity > 1)
                            quantity--;
                        product.setQuantity(quantity);
                        quantityDialogBinding.quantity.setText(String.valueOf(quantity));
                    }
                });


                quantityDialogBinding.savebtn.setOnClickListener(view -> {
                    dialog.dismiss();
                    notifyDataSetChanged();

                    cartRef.child(String.valueOf(product.getId()))
                            .child("quantity")
                            .setValue(product.getQuantity())
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show());


                    onCartChanged.run();
                    cartListener.onQuantityChanged();
                });

                dialog.show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{

        ItemCartBinding binding;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);
        }
    }
}
