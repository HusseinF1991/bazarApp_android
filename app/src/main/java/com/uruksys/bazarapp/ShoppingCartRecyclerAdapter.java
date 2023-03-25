package com.uruksys.bazarapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class ShoppingCartRecyclerAdapter extends RecyclerView.Adapter<ShoppingCartRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    Context  myContext;
    int resource;
    ArrayList<ItemsInCartModel> itemsInCartArrayList;
    public ShoppingCartRecyclerAdapter(Context context , int resource, ArrayList<ItemsInCartModel> itemsInCartArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.itemsInCartArrayList = itemsInCartArrayList;
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

        ItemsInCartModel itemsInCartModel = itemsInCartArrayList.get(position);
        if(itemsInCartModel.getDiscountPrice() > 0){

            holder.txtProductPrice.setText(String.valueOf(itemsInCartModel.getDiscountPrice()* itemsInCartModel.getQtyInCart()));
        }
        else{

            holder.txtProductPrice.setText(String.valueOf(itemsInCartModel.getSellPrice()* itemsInCartModel.getQtyInCart()));
        }
        holder.txtProductName.setText(itemsInCartModel.getItemName());
        holder.txtProductOptionTitle.setText(itemsInCartModel.getTypeName());
        holder.txtProductQtyInCart.setText(String.valueOf(itemsInCartModel.getQtyInCart()));
        if(itemsInCartModel.getAvailableQty() < itemsInCartModel.getQtyInCart()){
            holder.txtProductQtyUnavailable.setVisibility(View.VISIBLE);
        }
        else{
            holder.txtProductQtyUnavailable.setVisibility(View.INVISIBLE);
        }

        //get items images from url
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(itemsInCartModel.getImageLoc());
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            ((ShoppingCartActivity) myContext).runOnUiThread(() -> {

                holder.imgProductImage.setImageBitmap(finalMyImg);
            });
        }).start();

        holder.imgRemoveProductFromCart.setTag(position);
        holder.imgRemoveProductFromCart.setOnClickListener(this);


    }


    @Override
    public int getItemCount() {
        return itemsInCartArrayList.size();
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imgRemoveProductFromCart){

            int position = (int)view.getTag();

            LayoutInflater inflater = LayoutInflater.from(myContext);
            View view1 = inflater.inflate(R.layout.alert_dialog_layout, null , false);
            AlertDialog.Builder builder = new AlertDialog.Builder(myContext)
                    .setView(view1);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            TextView txtAlertDialogTitle = view1.findViewById(R.id.txtAlertDialogTitle);
            txtAlertDialogTitle.setText(R.string.String1_ShoppingCartRecyclerAdapter);
            Button btnAlertDialogConfirm = view1.findViewById(R.id.btnAlertDialogConfirm);
            Button btnAlertDialogCancel = view1.findViewById(R.id.btnAlertDialogCancel);

            btnAlertDialogCancel.setOnClickListener((View view2) -> {
                alertDialog.dismiss();
            });

            btnAlertDialogConfirm.setOnClickListener((View view3) ->{

                MySqliteDB mySqliteDB = new MySqliteDB(myContext);
                mySqliteDB.RemoveProduct_shoppingCart(itemsInCartArrayList.get(position).getItemTypeId());

                itemsInCartArrayList.remove(itemsInCartArrayList.get(position));
                alertDialog.dismiss();

                notifyDataSetChanged();
            });

            alertDialog.show();
        }
        else{
            int position = ShoppingCartActivity.ProductsInShoppingCartRecycler.getChildLayoutPosition(view);
            Intent intent = new Intent(myContext , ProductInfoActivity.class);
            intent.putExtra("itemId" , itemsInCartArrayList.get(position).getItemId());
            intent.putExtra("itemTypeId" , itemsInCartArrayList.get(position).getItemTypeId());
            myContext.startActivity(intent);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEditProductInCart;
        TextView txtProductName, txtProductPrice , txtProductQtyInCart , txtProductQtyUnavailable , txtProductOptionTitle;
        ImageView imgProductImage, imgRemoveProductFromCart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEditProductInCart = itemView.findViewById(R.id.imgEditProductInCart);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductQtyInCart = itemView.findViewById(R.id.txtProductQtyInCart);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            imgRemoveProductFromCart = itemView.findViewById(R.id.imgRemoveProductFromCart);
            txtProductQtyUnavailable = itemView.findViewById(R.id.txtProductQtyUnavailable);
            txtProductOptionTitle = itemView.findViewById(R.id.txtProductOptionTitle);
        }
    }
}
