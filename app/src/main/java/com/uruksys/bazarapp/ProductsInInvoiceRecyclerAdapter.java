package com.uruksys.bazarapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;

public class ProductsInInvoiceRecyclerAdapter extends RecyclerView.Adapter<ProductsInInvoiceRecyclerAdapter.MyViewHolder> {


    Context myContext;
    int resource;
    ArrayList<ItemsInInvoiceShopModel> itemsInInvoiceArrayList;
    Activity myActivity;
    public ProductsInInvoiceRecyclerAdapter(Context context , int resource,
                                            ArrayList<ItemsInInvoiceShopModel> itemsInInvoiceArrayList , PurchasesArchiveInvoiceItemsActivity myActivity) {

        this.myContext = context;
        this.resource = resource;
        this.itemsInInvoiceArrayList = itemsInInvoiceArrayList;
        this.myActivity = myActivity;
    }

    public ProductsInInvoiceRecyclerAdapter(Context context , int resource,
                                            ArrayList<ItemsInInvoiceShopModel> itemsInInvoiceArrayList , PurchaseReqInvoiceItemsActivity myActivity) {

        this.myContext = context;
        this.resource = resource;
        this.itemsInInvoiceArrayList = itemsInInvoiceArrayList;
        this.myActivity = myActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);


        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ItemsInInvoiceShopModel itemsInInvoiceShopModel = itemsInInvoiceArrayList.get(position);

        holder.txtProductPrice.setText(String.valueOf(itemsInInvoiceShopModel.getSellPrice()));
        holder.txtProductName.setText(itemsInInvoiceShopModel.getItemName());
        //get items images from url
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(itemsInInvoiceShopModel.getImageLoc());
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            myActivity.runOnUiThread(() -> {

                holder.imgProductImage.setImageBitmap(finalMyImg);
            });
        }).start();
        holder.txtProductQtyInInvoice.setText(String.valueOf(itemsInInvoiceShopModel.getQty()));
        holder.txtProductOptionTitle.setText(itemsInInvoiceShopModel.getTypeName());

    }


    @Override
    public int getItemCount() {
        return itemsInInvoiceArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtProductPrice , txtProductQtyInInvoice , txtProductOptionTitle;
        ImageView imgProductImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQtyInInvoice = itemView.findViewById(R.id.txtProductQtyInInvoice);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtProductOptionTitle = itemView.findViewById(R.id.txtProductOptionTitle);
        }
    }
}
