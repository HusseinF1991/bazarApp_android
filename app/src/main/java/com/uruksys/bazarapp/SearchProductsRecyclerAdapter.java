package com.uruksys.bazarapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;

public class SearchProductsRecyclerAdapter extends RecyclerView.Adapter<SearchProductsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    Context myContext;
    int resource;
    ArrayList<ItemsModel> searchItemsByCatArrayList;

    public SearchProductsRecyclerAdapter(Context context, int resource , ArrayList<ItemsModel> searchItemsByCatArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.searchItemsByCatArrayList = searchItemsByCatArrayList;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnAddProductToCart){

            int position = (int)view.getTag();
            if(searchItemsByCatArrayList.get(position).getAvailableQty() > 0){

                LayoutInflater inflater = LayoutInflater.from(myContext);
                View view1 = inflater.inflate(R.layout.alert_dialog_layout, null , false);
                AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                        .setView(view1);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                TextView txtAlertDialogTitle = view1.findViewById(R.id.txtAlertDialogTitle);
                txtAlertDialogTitle.setText(R.string.String1_SearchProductsRecyclerAdapter);
                Button btnAlertDialogConfirm = view1.findViewById(R.id.btnAlertDialogConfirm);
                Button btnAlertDialogCancel = view1.findViewById(R.id.btnAlertDialogCancel);

                btnAlertDialogCancel.setOnClickListener((View view2) -> {
                    alertDialog.dismiss();
                });

                btnAlertDialogConfirm.setOnClickListener((View view2) -> {

                    int totalQty = MainActivity.PutNewProductsInShoppingCart(myContext,
                            searchItemsByCatArrayList.get(position).getItemTypeId());


                    MainActivity_SearchFragment.txtProductsCountInCart_SearchFragment.setVisibility(View.VISIBLE);
                    MainActivity_SearchFragment.txtProductsCountInCart_SearchFragment.setText(totalQty+"");

                    Toast.makeText(myContext,  myContext.getString(R.string.String2_SearchProductsRecyclerAdapter) , Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                });

                alertDialog.show();
            }
            else{
                Toast.makeText(myContext,  myContext.getString(R.string.String3_SearchProductsRecyclerAdapter) , Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int position = MainActivity_SearchFragment.searchedProductsRecycler.getChildLayoutPosition(view);
            Intent intent = new Intent(myContext , ProductInfoActivity.class);
            intent.putExtra("itemId" , searchItemsByCatArrayList.get(position).getItemId());
            intent.putExtra("itemTypeId" , searchItemsByCatArrayList.get(position).getItemTypeId());
            myContext.startActivity(intent);
        }
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductImage, imgProductRateStar1 , imgProductRateStar2 , imgProductRateStar3 , imgProductRateStar4 ,
                imgProductRateStar5 , imgProductOnSale;
        TextView txtProductName;
        Button btnAddProductToCart;
        RelativeLayout rlProductsRecyclerLayoutItem;

        MyViewHolder(View view){
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
            rlProductsRecyclerLayoutItem = view.findViewById(R.id.rlProductsRecyclerLayoutItem);
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

        ItemsModel itemsModel = searchItemsByCatArrayList.get(position);

        holder.txtProductName.setText(itemsModel.getItemName());
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

        if(itemsModel.getDiscountPrice() > 0){
            holder.imgProductOnSale.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgProductOnSale.setVisibility(View.INVISIBLE);
        }

        switch ((int)itemsModel.getAvgRate()){
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
        return searchItemsByCatArrayList.size();
    }
}