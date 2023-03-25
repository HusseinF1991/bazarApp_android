package com.uruksys.bazarapp_merchants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchProductsRecyclerAdapter extends RecyclerView.Adapter<SearchProductsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    int resource;
    ArrayList<ProductsModel> productsModelArrayList;
    Context myContext;

    public SearchProductsRecyclerAdapter(Context myContext,int resource,  ArrayList<ProductsModel> productsModelArrayList) {
        this.resource = resource;
        this.productsModelArrayList = productsModelArrayList;
        this.myContext = myContext;
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


        ProductsModel productsModel = productsModelArrayList.get(position);

        Log.d("hello" , productsModel.getProductName());
        holder.txtProductName.setText(productsModel.getProductName());
        holder.imgProductImage.setImageBitmap(NewProductActivity.decodeBase64Profile(productsModel.getProductImage()));
        holder.txtProductCode.setText(productsModel.getProductCode());
    }

    @Override
    public int getItemCount() {
        return productsModelArrayList.size();
    }

    @Override
    public void onClick(View view) {

        int position = UpdateProductActivity.recyclerProducts.getChildLayoutPosition(view);
        ProductsModel productsModel = productsModelArrayList.get(position);
        Intent intent = new Intent(myContext , UpdateProductInfoActivity.class);
        intent.putExtra("productId" , productsModel.productId);
        myContext.startActivity(intent);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProductImage;
        TextView txtProductName , txtProductCode;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductCode = itemView.findViewById(R.id.txtProductCode);
        }
    }
}
