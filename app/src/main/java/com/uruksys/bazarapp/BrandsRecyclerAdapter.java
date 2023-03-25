package com.uruksys.bazarapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class BrandsRecyclerAdapter  extends RecyclerView.Adapter<BrandsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<BrandsModel> brandsArrayList;


    public BrandsRecyclerAdapter(Context context, int resource , ArrayList<BrandsModel> brandsArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.brandsArrayList = brandsArrayList;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imgProviderOrBrandImage){

        }
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBrandImage;
        TextView txtBrandName;

        MyViewHolder(View view){
            super(view);

            imgBrandImage = view.findViewById(R.id.imgProviderOrBrandImage);
            txtBrandName = view.findViewById(R.id.txtProviderOrBrandName);
        }
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

        BrandsModel brandsModel = brandsArrayList.get(position);

        holder.txtBrandName.setText(brandsModel.brandName);
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(brandsModel.getBrandLogo());
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            ((MainActivity) myContext).runOnUiThread(() -> {

                holder.imgBrandImage.setImageBitmap(finalMyImg);
            });
        }).start();
    }


    @Override
    public int getItemCount() {
        return brandsArrayList.size();
    }
}
