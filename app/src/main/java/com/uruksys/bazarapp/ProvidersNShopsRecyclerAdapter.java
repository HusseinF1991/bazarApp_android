package com.uruksys.bazarapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;

public class ProvidersNShopsRecyclerAdapter extends RecyclerView.Adapter<ProvidersNShopsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<ProvidersNShopsModel> providersNShopsArrayList;


    public ProvidersNShopsRecyclerAdapter(Context context, int resource, ArrayList<ProvidersNShopsModel> providersNShopsArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.providersNShopsArrayList = providersNShopsArrayList;
    }


    @Override
    public void onClick(View view) {
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProviderImage;
        TextView txtProviderName;

        MyViewHolder(View view) {
            super(view);

            imgProviderImage = view.findViewById(R.id.imgProviderOrBrandImage);
            txtProviderName = view.findViewById(R.id.txtProviderOrBrandName);
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

        ProvidersNShopsModel providersNShopsModel = providersNShopsArrayList.get(position);

        holder.txtProviderName.setText(providersNShopsModel.getName());
        if (providersNShopsModel.getLogo() != null && !providersNShopsModel.getLogo().equals("")) {

            new Thread(() -> {
                Bitmap myImg = null;
                try {
                    URL newUrl = new URL(providersNShopsModel.getLogo());
                    myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bitmap finalMyImg = myImg;
                ((MainActivity) myContext).runOnUiThread(() -> {

                    holder.imgProviderImage.setImageBitmap(finalMyImg);
                });
            }).start();
        }
    }


    @Override
    public int getItemCount() {
        return providersNShopsArrayList.size();
    }
}