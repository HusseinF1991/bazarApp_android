package com.uruksys.bazarapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.Response;

public class PurchasesArchiveRecyclerAdapter extends RecyclerView.Adapter<PurchasesArchiveRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    Context myContext;
    int resource;
    ArrayList<ShopInvoiceModel> shopInvoiceArrayList;

    public PurchasesArchiveRecyclerAdapter(Context context, int resource, ArrayList<ShopInvoiceModel> shopInvoiceArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.shopInvoiceArrayList = shopInvoiceArrayList;
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

        ShopInvoiceModel shopInvoiceModel = shopInvoiceArrayList.get(position);

        holder.txtPaymentMethod.setText(shopInvoiceModel.getPaymentMethod());
        holder.txtShopName.setText(shopInvoiceModel.getShopName());
        holder.txtItemsCount.setText(String.valueOf(shopInvoiceModel.getItemsCount()));
        holder.txtInvoiceCost.setText(String.valueOf(shopInvoiceModel.getTotalCost()));
        holder.txtPaidAmount.setText(String.valueOf(shopInvoiceModel.getPaidAmount()));

        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_DELIVERED)) {

            holder.imgRecipeStatusText.setTextColor(myContext.getColor(R.color.DeliveredStatus));
            holder.imgRecipeStatusText.setText(R.string.PurchasesArchiveRecyclerItem_String1);
        } else {
            holder.imgRecipeStatusText.setTextColor(myContext.getColor(R.color.colorAccent));
            holder.imgRecipeStatusText.setText(R.string.PurchasesArchiveRecyclerItem_String2);
        }

        holder.imgDisplayItemsOfInvoice.setTag(position);
        holder.imgDisplayItemsOfInvoice.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        return shopInvoiceArrayList.size();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgDisplayItemsOfInvoice) {

            int position = (int) view.getTag();
            int invoiceShopId = shopInvoiceArrayList.get(position).getShopInvoiceId();
            Intent intent = new Intent(myContext, PurchasesArchiveInvoiceItemsActivity.class);
            intent.putExtra("invoiceShopId", invoiceShopId);
            myContext.startActivity(intent);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDisplayItemsOfInvoice, imgRecipeStatusImage;
        TextView txtShopName, txtItemsCount, txtInvoiceCost, txtPaidAmount, imgRecipeStatusText, txtPaymentMethod;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDisplayItemsOfInvoice = itemView.findViewById(R.id.imgDisplayItemsOfInvoice);
            txtShopName = itemView.findViewById(R.id.txtShopName);
            txtItemsCount = itemView.findViewById(R.id.txtItemsCount);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtInvoiceCost = itemView.findViewById(R.id.txtInvoiceCost);
            txtPaidAmount = itemView.findViewById(R.id.txtPaidAmount);
            imgRecipeStatusText = itemView.findViewById(R.id.imgRecipeStatusText);
            imgRecipeStatusImage = itemView.findViewById(R.id.imgRecipeStatusImage);
            SharedPreferences sharedPreferences = myContext.getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
            if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

                imgRecipeStatusImage.setScaleX(-1);
            }
        }
    }
}
