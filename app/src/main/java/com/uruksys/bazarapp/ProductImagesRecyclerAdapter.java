package com.uruksys.bazarapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.net.URL;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductImagesRecyclerAdapter extends RecyclerView.Adapter<ProductImagesRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<String> productsImagesArrayList;
    ProductInfoActivity productInfoActivity;

    public ProductImagesRecyclerAdapter(Context context, int resource, ArrayList<String> productsImagesArrayList , ProductInfoActivity productInfoActivity) {

        this.myContext = context;
        this.resource = resource;
        this.productsImagesArrayList = productsImagesArrayList;
        this.productInfoActivity = productInfoActivity;
    }


    @Override
    public void onClick(View view) {
        int position = MainActivity_MainFragment.categoriesRecyclerView.getChildLayoutPosition(view);

        productInfoActivity.zoomImageFromThumb(view, productsImagesArrayList.get(position));
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductImage;

        MyViewHolder(View view) {
            super(view);

            imgProductImage = view.findViewById(R.id.imgProductImage);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        itemView.setOnClickListener(this);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String imageLoc = productsImagesArrayList.get(position);

        //get items images from url
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(imageLoc);
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            productInfoActivity.runOnUiThread(() -> {

                holder.imgProductImage.setImageBitmap(finalMyImg);
            });
        }).start();
    }


    @Override
    public int getItemCount() {
        return productsImagesArrayList.size();
    }
}
