package com.uruksys.bazarapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class BestSellingRecyclerAdapter extends RecyclerView.Adapter<BestSellingRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<ItemsModel> bestSellingProductsArrayList;

    public BestSellingRecyclerAdapter(Context context, int resource, ArrayList<ItemsModel> bestSellingProductsArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.bestSellingProductsArrayList = bestSellingProductsArrayList;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddProductToCart) {

            int position = (int)view.getTag();
            if(bestSellingProductsArrayList.get(position).getAvailableQty() > 0){

                LayoutInflater inflater = LayoutInflater.from(myContext);
                View view1 = inflater.inflate(R.layout.alert_dialog_layout, null , false);
                AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                        .setView(view1);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView txtAlertDialogTitle = view1.findViewById(R.id.txtAlertDialogTitle);
                txtAlertDialogTitle.setText(R.string.String1_BestSellingRecyclerAdapter);
                Button btnAlertDialogConfirm = view1.findViewById(R.id.btnAlertDialogConfirm);
                Button btnAlertDialogCancel = view1.findViewById(R.id.btnAlertDialogCancel);

                btnAlertDialogCancel.setOnClickListener((View view2) -> {
                    alertDialog.dismiss();
                });

                btnAlertDialogConfirm.setOnClickListener((View view2) -> {
                    int totalQty = MainActivity.PutNewProductsInShoppingCart(myContext,
                            bestSellingProductsArrayList.get(position).getItemTypeId());


                    MainActivity_MainFragment.txtProductsCountInCart_MainFragment.setVisibility(View.VISIBLE);
                    MainActivity_MainFragment.txtProductsCountInCart_MainFragment.setText(totalQty+"");


                    Toast.makeText(myContext,  myContext.getString(R.string.String2_BestSellingRecyclerAdapter) , Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                });

                alertDialog.show();
            }
            else {
                Toast.makeText(myContext,  myContext.getString(R.string.String3_BestSellingRecyclerAdapter) , Toast.LENGTH_SHORT).show();
            }
        } else {
            int position = MainActivity_MainFragment.bestSellingRecycler.getChildLayoutPosition(view);
            Intent intent = new Intent(myContext , ProductInfoActivity.class);
            intent.putExtra("itemId" ,  bestSellingProductsArrayList.get(position).getItemId());
            intent.putExtra("itemTypeId" , bestSellingProductsArrayList.get(position).getItemTypeId());
            myContext.startActivity(intent);
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductImage, imgProductRateStar1, imgProductRateStar2, imgProductRateStar3, imgProductRateStar4,
                imgProductRateStar5, imgProductOnSale;
        TextView txtProductName;
        Button btnAddProductToCart;

        MyViewHolder(View view) {
            super(view);

            imgProductImage = view.findViewById(R.id.imgProductImage);
            imgProductRateStar1 = view.findViewById(R.id.imgProductRateStar1);
            imgProductRateStar2 = view.findViewById(R.id.imgProductRateStar2);
            imgProductRateStar3 = view.findViewById(R.id.imgProductRateStar3);
            imgProductRateStar4 = view.findViewById(R.id.imgProductRateStar4);
            imgProductRateStar5 = view.findViewById(R.id.imgProductRateStar5);
            imgProductOnSale = view.findViewById(R.id.imgProductOnSale);
            txtProductName = view.findViewById(R.id.txtProductName);
            btnAddProductToCart = view.findViewById(R.id.btnAddProductToCart);

//            SharedPreferences sharedPreferences = myContext.getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
//            if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){
//
//                imgProductOnSale.setScaleX(-1);
//            }
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

        ItemsModel itemsModel = bestSellingProductsArrayList.get(position);
        holder.txtProductName.setText(itemsModel.getItemName());

        //get items images from url
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(itemsModel.getImageLoc());
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            ((MainActivity) myContext).runOnUiThread(() -> {

                holder.imgProductImage.setImageBitmap(finalMyImg);
            });
        }).start();

        if (itemsModel.getDiscountPrice() > 0) {
            holder.imgProductOnSale.setVisibility(View.VISIBLE);
        } else {
            holder.imgProductOnSale.setVisibility(View.INVISIBLE);
        }

        switch ((int) itemsModel.getAvgRate()) {
            case 1:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 2:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 3:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 4:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                break;
            case 5:
                holder.imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                holder.imgProductRateStar5.setImageResource(R.drawable.ic_star_yellow);
                break;
        }

        holder.btnAddProductToCart.setTag(position);
        holder.btnAddProductToCart.setOnClickListener(this);

    }


    @Override
    public int getItemCount() {
        return bestSellingProductsArrayList.size();
    }
}
