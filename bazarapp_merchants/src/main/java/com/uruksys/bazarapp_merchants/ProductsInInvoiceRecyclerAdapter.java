package com.uruksys.bazarapp_merchants;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductsInInvoiceRecyclerAdapter  extends RecyclerView.Adapter<ProductsInInvoiceRecyclerAdapter.MyViewHolder> {


    Context myContext;
    int resource;
    ArrayList<ProductsInInvoiceModel> productsInInvoiceArrayList;
    public ProductsInInvoiceRecyclerAdapter(Context context , int resource, ArrayList<ProductsInInvoiceModel> productsInInvoiceArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.productsInInvoiceArrayList = productsInInvoiceArrayList;
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

        ProductsInInvoiceModel productsInInvoiceModel = productsInInvoiceArrayList.get(position);

        holder.txtProductPrice.setText(productsInInvoiceModel.getSellPrice()+"");
        holder.txtProductName.setText(productsInInvoiceModel.getProductName());
        holder.imgProductImage.setImageBitmap(NewProductActivity.decodeBase64Profile(productsInInvoiceModel.getProductImage()));
        holder.txtProductQtyInInvoice.setText(productsInInvoiceModel.getQuantity()+"");
        holder.txtProductOptionTitle.setText(productsInInvoiceModel.getProductOptionTitle());

        Log.d("hello", productsInInvoiceModel.getProductName());

    }


    @Override
    public int getItemCount() {
        return productsInInvoiceArrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtProductPrice , txtProductQtyInInvoice,txtProductOptionTitle;
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
