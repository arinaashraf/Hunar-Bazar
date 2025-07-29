package com.example.hunarbazar7.adaptor;

import static java.lang.Character.getName;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;
import com.example.hunarbazar7.activities.ProductDetail;
import com.example.hunarbazar7.databinding.ItemProductBinding;
import com.example.hunarbazar7.model.Product;

import java.util.ArrayList;

public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.ProductViewHolder> {

    Context context;
    ArrayList<Product> products;
    private ArrayList<Product> originalList;

    public ProductAdaptor(Context context, ArrayList<Product> products){
        this.context = context;
        this.products = products;
        this.originalList = new ArrayList<>(products);
    }

    public void filter(String query) {
        ArrayList<Product> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (Product product : originalList) {
                if (product.getName() != null &&
                        product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }

        products.clear();
        products.addAll(filteredList);
        notifyDataSetChanged();
    }


    public void updateData(ArrayList<Product> newProducts) {
        this.originalList = new ArrayList<>(newProducts);
        this.products = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.binding.productImage);
        holder.binding.productLabel.setText(product.getName());

        holder.binding.productPrice.setText("PKR " + product.getPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("name", product.getName());
                intent.putExtra("image", product.getImage());
                intent.putExtra("id", product.getId());
                intent.putExtra("discount", product.getDiscount());
                intent.putExtra("stock", product.getStock());
                intent.putExtra("description", product.getDescription());
                intent.putExtra("price", product.getPrice());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        ItemProductBinding binding;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);
        }
    }


}
