package com.uruksys.bazarapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PurchasesRequestsRecyclerAdapter extends RecyclerView.Adapter<PurchasesRequestsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    Context myContext;
    int resource;
    ArrayList<ShopInvoiceModel> shopInvoiceArrayList;

    public PurchasesRequestsRecyclerAdapter(Context context, int resource, ArrayList<ShopInvoiceModel> shopInvoiceArrayList) {

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

        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_PENDING)) {

            holder.imgRecipeStatusText.setTextColor(myContext.getColor(R.color.colorPrimaryDark));
            holder.imgRecipeStatusText.setText(R.string.String1_PurchasesRequestsRecyclerAdapter);
        } else {
            holder.imgRecipeStatusText.setTextColor(myContext.getColor(R.color.colorAccent));
            holder.imgRecipeStatusText.setText(R.string.String2_PurchasesRequestsRecyclerAdapter);
        }


        holder.imgDisplayItemsOfInvoice.setTag(position);
        holder.imgDisplayItemsOfInvoice.setOnClickListener(this);

        holder.imgDisplayMessagingOfInvoice.setTag(position);
        holder.imgDisplayMessagingOfInvoice.setOnClickListener(this);

        holder.imgCallSupport.setTag(position);
        holder.imgCallSupport.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        return shopInvoiceArrayList.size();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgDisplayMessagingOfInvoice) {

            int position = (int) view.getTag();
            Intent intent = new Intent(myContext, InvoiceShopChatActivity.class);
            intent.putExtra("invoiceShopId", shopInvoiceArrayList.get(position).getShopInvoiceId());
            intent.putExtra("shopId", shopInvoiceArrayList.get(position).getShopId());
            myContext.startActivity(intent);
            notifyDataSetChanged();

        } else if (view.getId() == R.id.imgDisplayItemsOfInvoice) {


            int position = (int) view.getTag();
            Intent intent = new Intent(myContext, PurchaseReqInvoiceItemsActivity.class);
            intent.putExtra("invoiceShopId", shopInvoiceArrayList.get(position).getShopInvoiceId());
            myContext.startActivity(intent);
        } else if (view.getId() == R.id.imgCallSupport) {

            int position = (int) view.getTag();
            PurchasesRequestsActivity.shopMobileNumber = shopInvoiceArrayList.get(position).getMobile();
            GetSupportMobileNumber();
        }
    }


    private void GetSupportMobileNumber() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    ((PurchasesRequestsActivity) myContext),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MainActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE);
        } else {

            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + PurchasesRequestsActivity.shopMobileNumber));
            myContext.startActivity(intent);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imgDisplayItemsOfInvoice;
        TextView txtShopName, txtItemsCount, txtInvoiceCost, txtPaidAmount, imgRecipeStatusText, txtPaymentMethod;
        ImageView imgDisplayMessagingOfInvoice, imgCallSupport , imgRecipeStatusImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDisplayItemsOfInvoice = itemView.findViewById(R.id.imgDisplayItemsOfInvoice);
            txtShopName = itemView.findViewById(R.id.txtShopName);
            txtItemsCount = itemView.findViewById(R.id.txtItemsCount);
            txtPaymentMethod = itemView.findViewById(R.id.txtPaymentMethod);
            txtInvoiceCost = itemView.findViewById(R.id.txtInvoiceCost);
            imgDisplayMessagingOfInvoice = itemView.findViewById(R.id.imgDisplayMessagingOfInvoice);
            txtPaidAmount = itemView.findViewById(R.id.txtPaidAmount);
            imgCallSupport = itemView.findViewById(R.id.imgCallSupport);
            imgRecipeStatusText = itemView.findViewById(R.id.imgRecipeStatusText);
            imgRecipeStatusImage = itemView.findViewById(R.id.imgRecipeStatusImage);


            SharedPreferences sharedPreferences = myContext.getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
            if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

                imgRecipeStatusImage.setScaleX(-1);
            }
        }
    }
}
