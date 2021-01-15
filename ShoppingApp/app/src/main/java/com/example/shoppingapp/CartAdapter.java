package com.example.shoppingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    ArrayList<Item> items;

    public CartAdapter(Context context, ArrayList<Item> list) {
        items=list;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItemName,tvItemDesc, tvItemPrice, tvItemCount;
        ImageView ivItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDesc = itemView.findViewById(R.id.tvItemDesc);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemCount = itemView.findViewById(R.id.tvCartCount);

            ivItemImage = itemView.findViewById(R.id.ivItemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        }

    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(items.get(position));
        holder.tvItemName.setText(items.get(position).getName());
        holder.tvItemDesc.setText(items.get(position).getDescription()); //optional, maybe remove later
        holder.tvItemPrice.setText(String.format("$%.2f",Double.parseDouble(items.get(position).getPrice())));
        holder.tvItemCount.setText("Quantity: " +String.valueOf(items.get(position).getQuantity()));

        //TODO if we use images, set the image resource here
    }



    @Override
    public int getItemCount() {
        return items.size();
    }
}
