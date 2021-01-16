package com.example.shoppingapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class Item {

    /**
     * Basic holder class for the items from database
     * Probably have to wait for Tony to send backend API so we can know how to name fields,etc
     */

    private String name;
    private String price;
    private String description;
    private int quantity;
    private String image;



    public Item(String name, String price, String description,String image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image=image;
        quantity=1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(@Nullable Item item) {
        if(this.getName().equals(item.getName())&&this.getPrice().equals(item.getPrice())&&
                this.getDescription().equals(item.getDescription())&&this.getImage().equals(item.getImage())){
            return true;
        }
        else return false;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
