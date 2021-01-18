package com.example.shoppingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    private ArrayList<Item> items;
    private CartAdapter.ViewHolder holder;
    private Context activity;

    public CartAdapter(Context context, ArrayList<Item> list) {
        items=list;
        activity=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItemName,tvItemDesc, tvItemPrice, tvItemCount;
        private ImageView ivItemImage;

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
        this.holder=holder;
        holder.itemView.setTag(items.get(position));
        holder.tvItemName.setText(items.get(position).getName());
        holder.tvItemDesc.setText(items.get(position).getDescription()); //optional, maybe remove later
        holder.tvItemPrice.setText(String.format("$%.2f",Double.parseDouble(items.get(position).getPrice())));
        holder.tvItemCount.setText("Quantity: " +String.valueOf(items.get(position).getQuantity()));

        ImageView iv = holder.ivItemImage;
        try{
            URL url = new URL(items.get(position).getImage());
            new ImageSyncInBackground(iv).execute(url);
        }
        catch(Exception e){
            Log.i("shopLog",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ImageSyncInBackground extends AsyncTask<URL,Integer, Bitmap> {

        private ImageView ivItemImage;

        public ImageSyncInBackground(ImageView iv) {
            this.ivItemImage=iv;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap image = null;
            try {
                URL url = params[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                image = BitmapFactory.decodeStream(bufferedInputStream);
                bufferedInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return image;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ivItemImage.setImageBitmap(bitmap);
            } else {
                Toast.makeText((Context)activity, "Failed to Download Image", Toast.LENGTH_LONG).show();
            }
        }
    }
}
