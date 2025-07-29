package com.example.hunarbazar7.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunarbazar7.R;
import com.example.hunarbazar7.activities.ProductListActivity;
import com.example.hunarbazar7.databinding.ItemCategoriesBinding;
import com.example.hunarbazar7.model.Category;

import java.util.ArrayList;

public class categoryAdaptor extends RecyclerView.Adapter<categoryAdaptor.CategoryViewHolder>{

    private final Context context;
    private final ArrayList<Category> categories;

    public categoryAdaptor(Context context, ArrayList<Category> categories){
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoriesBinding binding = ItemCategoriesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CategoryViewHolder(binding);

    }



    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.label.setText(category.getName());

        Glide.with(context)
                .load(category.getIcon())
                .into(holder.binding.image);

        holder.binding.image.setBackgroundColor(Color.parseColor(category.getColor()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductListActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        });

        Log.d("categoryAdaptor", "Binding category: " + category.getName());
        Log.d("Adapter", "Binding category: " + category.getName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoriesBinding binding;

        public CategoryViewHolder(ItemCategoriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
