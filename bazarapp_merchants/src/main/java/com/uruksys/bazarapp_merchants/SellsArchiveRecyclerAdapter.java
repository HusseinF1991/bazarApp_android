package com.uruksys.bazarapp_merchants;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SellsArchiveRecyclerAdapter extends RecyclerView.Adapter<SellsArchiveRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    Context myContext;
    int resource;
    ArrayList<PurchasesRequestsModel> sellsArchiveArrayList;

    public SellsArchiveRecyclerAdapter(Context context, int resource, ArrayList<PurchasesRequestsModel> sellsArchiveArrayList) {

        this.myContext = context;
        this.resource = resource;
        this.sellsArchiveArrayList = sellsArchiveArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        //itemView.setOnClickListener(this);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        PurchasesRequestsModel sellsArchiveModel = sellsArchiveArrayList.get(position);

        holder.txtInvoiceDate.setText(sellsArchiveModel.getRecipeDate());
        holder.txtInvoiceNumber.setText(sellsArchiveModel.getInvoice() + "");
        holder.txtInvoiceCost.setText(sellsArchiveModel.getRecipeCost() + "");
        holder.txtPaidAmount.setText(sellsArchiveModel.getPaidAmount() + "");
        holder.txtCustomerName.setText(sellsArchiveModel.getCustomerName());
        holder.txtCustomerMobile.setText(sellsArchiveModel.getCustomerMobile());
        holder.txtCustomerProvince.setText(sellsArchiveModel.getProvince());


        holder.imgDisplayItemsOfInvoice.setTag(position);
        holder.imgDisplayItemsOfInvoice.setOnClickListener(this);

        holder.imgDisplayMessagingOfInvoice.setTag(position);
        holder.imgDisplayMessagingOfInvoice.setOnClickListener(this);

        holder.imgCallCustomer.setTag(position);
        holder.imgCallCustomer.setOnClickListener(this);

        holder.btnDisplayCustomerLocInMap.setTag(position);
        holder.btnDisplayCustomerLocInMap.setOnClickListener(this);

        Log.d("hello", sellsArchiveModel.getCustomerName());

    }


    @Override
    public int getItemCount() {
        return sellsArchiveArrayList.size();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgDisplayMessagingOfInvoice) {

            int position = (int) view.getTag();
            int invoice = sellsArchiveArrayList.get(position).getInvoice();
            Intent intent = new Intent(myContext, MessagingCustomerForInvoiceActivity.class);
            intent.putExtra("invoice", invoice);
            intent.putExtra("deliveredInvoice" , true);
            myContext.startActivity(intent);
//            notifyDataSetChanged();
        }
        else if (view.getId() == R.id.imgDisplayItemsOfInvoice) {

            int position = (int) view.getTag();
            int invoice = sellsArchiveArrayList.get(position).getInvoice();
            Intent intent = new Intent(myContext, NewSellsRequests_ItemsOfInvoiceActivity.class);
            intent.putExtra("invoice", invoice);
            myContext.startActivity(intent);
        }
        else if (view.getId() == R.id.imgCallCustomer) {

            int position = (int) view.getTag();
            String mobile  = sellsArchiveArrayList.get(position).getCustomerMobile();

            if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                NewSellsRequestsActivity.SelectedInvoice_CustomerMobile = mobile;
                ActivityCompat.requestPermissions(
                        ((NewSellsRequestsActivity)myContext),
                        new String[]{Manifest.permission.CALL_PHONE},
                        LoginActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE);
            }
            else{

                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + mobile));
                myContext.startActivity(intent);
            }

        }
        else if (view.getId() == R.id.btnDisplayCustomerLocInMap){

            int position = (int) view.getTag();
            double lat = sellsArchiveArrayList.get(position).getLat();
            double lng = sellsArchiveArrayList.get(position).getLng();

            Intent intent = new Intent(myContext , MapsActivity.class);
            intent.putExtra("lat" , lat);
            intent.putExtra("lng" , lng);
            myContext.startActivity(intent);
        }
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDisplayItemsOfInvoice;
        TextView txtInvoiceDate, txtInvoiceNumber, txtInvoiceCost, txtPaidAmount, txtCustomerName, txtCustomerMobile,
                txtCustomerProvince, txtRecipeStatusText;
        ImageView imgDisplayMessagingOfInvoice, imgCallCustomer;
        Button btnDisplayCustomerLocInMap;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDisplayItemsOfInvoice = itemView.findViewById(R.id.imgDisplayItemsOfInvoice);
            txtInvoiceDate = itemView.findViewById(R.id.txtInvoiceDate);
            txtInvoiceNumber = itemView.findViewById(R.id.txtInvoiceNumber);
            txtInvoiceCost = itemView.findViewById(R.id.txtInvoiceCost);
            imgDisplayMessagingOfInvoice = itemView.findViewById(R.id.imgDisplayMessagingOfInvoice);
            txtRecipeStatusText = itemView.findViewById(R.id.txtRecipeStatusText);
            txtPaidAmount = itemView.findViewById(R.id.txtPaidAmount);
            imgCallCustomer = itemView.findViewById(R.id.imgCallCustomer);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtCustomerMobile = itemView.findViewById(R.id.txtCustomerMobile);
            txtCustomerProvince = itemView.findViewById(R.id.txtCustomerProvince);
            btnDisplayCustomerLocInMap = itemView.findViewById(R.id.btnDisplayCustomerLocInMap);
        }
    }
}
