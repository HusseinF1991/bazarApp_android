package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewSellsRequestsActivity extends AppCompatActivity {

    ProgressBar progressBar_newPurchasesRequestsActivity;
    RecyclerView recyclerPurchasesRequests;
    public static ArrayList<PurchasesRequestsModel> PurchasesRequestsInvoicesArrayList = new ArrayList<>();
    ImageView imgOptionsForRecipeStatus;

    public static String SelectedInvoice_CustomerMobile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_purchases_requests);

        recyclerPurchasesRequests = findViewById(R.id.recyclerPurchasesRequests);
        progressBar_newPurchasesRequestsActivity = findViewById(R.id.progressBar_newPurchasesRequestsActivity);
        imgOptionsForRecipeStatus = findViewById(R.id.imgOptionsForRecipeStatus);

        OnImgOptionsForRecipeStatusClicked();
    }



    @Override
    protected void onStart() {
        super.onStart();

        GetPurchasesRequests();
    }



    private void OnImgOptionsForRecipeStatusClicked(){
        imgOptionsForRecipeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(NewSellsRequestsActivity.this);
                View view1 = inflater.inflate(R.layout.recipe_status_options_dialog , null);

                AlertDialog.Builder builder = new AlertDialog.Builder(NewSellsRequestsActivity.this)
                        .setView(view1);

                Button btnDisplayPendingPurchasesReq = view1.findViewById(R.id.btnDisplayPendingPurchasesReq);
                Button btnDisplayApprovedPurchasesReq = view1.findViewById(R.id.btnDisplayApprovedPurchasesReq);
                Button btnDisplayBothTypes = view1.findViewById(R.id.btnDisplayBothTypes);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnDisplayApprovedPurchasesReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<PurchasesRequestsModel> PurchasesRequestsInvoices_ApprovedArrayList = new ArrayList<>();
                        for (PurchasesRequestsModel purchasesRequestsModel: PurchasesRequestsInvoicesArrayList) {
                            if(purchasesRequestsModel.getRecipeStatus().equals("approved")){
                                PurchasesRequestsInvoices_ApprovedArrayList.add(purchasesRequestsModel);
                            }
                        }

                        PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                                (NewSellsRequestsActivity.this
                                        , R.layout.purchases_requests_recycler_item,
                                        PurchasesRequestsInvoices_ApprovedArrayList,
                                        "approved");
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                        recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                        recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);

                        alertDialog.dismiss();
                    }
                });


                btnDisplayBothTypes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                                (NewSellsRequestsActivity.this,
                                        R.layout.purchases_requests_recycler_item,
                                        PurchasesRequestsInvoicesArrayList,
                                        "all");
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                        recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                        recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);

                        alertDialog.dismiss();
                    }
                });

                btnDisplayPendingPurchasesReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ArrayList<PurchasesRequestsModel> PurchasesRequestsInvoices_PendingArrayList = new ArrayList<>();
                        for (PurchasesRequestsModel purchasesRequestsModel: PurchasesRequestsInvoicesArrayList) {
                            if(purchasesRequestsModel.getRecipeStatus().equals("pending")){
                                PurchasesRequestsInvoices_PendingArrayList.add(purchasesRequestsModel);
                            }
                        }

                        PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                                (NewSellsRequestsActivity.this,
                                        R.layout.purchases_requests_recycler_item,
                                        PurchasesRequestsInvoices_PendingArrayList,
                                        "pending");
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                        recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                        recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);

                        alertDialog.dismiss();
                    }
                });


                alertDialog.show();
            }
        });
    }



    private void GetPurchasesRequests() {


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_newPurchasesRequestsActivity.setVisibility(View.VISIBLE);

        PurchasesRequestsInvoicesArrayList.clear();
        recyclerPurchasesRequests.setAdapter(null);


        AtomicBoolean replayThread = new AtomicBoolean(false);

        do {
            new Thread(() -> {
                //RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject1));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getPurchasesRequests_admin.php")
                        //.post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("newSellsRequestActivity", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("newSellsRequestActivity", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                int invoice = Integer.parseInt(jsonObject2.getString("invoice"));
                                String recipeDate = jsonObject2.getString("recipeDate");
                                String customerName = jsonObject2.getString("customerName");
                                String customerMobile = jsonObject2.getString("customerMobile");
                                String province = jsonObject2.getString("province");
                                double lat = jsonObject2.getDouble("lat");
                                double lng = jsonObject2.getDouble("lng");
                                double recipeCost = jsonObject2.getDouble("recipeCost");
                                double paidAmount = jsonObject2.getDouble("paidAmount");
                                String paymentType = jsonObject2.getString("paymentType");
                                String recipeStatus = jsonObject2.getString("recipeStatus");


                                PurchasesRequestsInvoicesArrayList.add(new PurchasesRequestsModel(invoice, recipeDate, customerName, customerMobile, province,
                                        lat, lng, recipeCost, paidAmount, paymentType, recipeStatus));
                            }

                            PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                                    (this, R.layout.purchases_requests_recycler_item, PurchasesRequestsInvoicesArrayList , "all");
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                            recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                            recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);


                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_newPurchasesRequestsActivity.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("newSellsRequestActivity", e.getMessage());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("newSellsRequestActivity", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LoginActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "phone call permission granted", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + SelectedInvoice_CustomerMobile));
                startActivity(intent);
            } else {
                Toast.makeText(this, "phone call permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}



class PurchasesRequestsModel {
    private int invoice;
    private String recipeDate, customerName, customerMobile, province;
    private double paidAmount, recipeCost, lat, lng;
    private String paymentType, recipeStatus;

    public PurchasesRequestsModel(int invoice, String recipeDate, String customerName, String customerMobile, String province,
                                  double lat, double lng, double recipeCost, double paidAmount, String paymentType, String recipeStatus) {
        this.invoice = invoice;
        this.recipeDate = recipeDate;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.province = province;
        this.lat = lat;
        this.lng = lng;
        this.recipeCost = recipeCost;
        this.paidAmount = paidAmount;
        this.paymentType = paymentType;
        this.recipeStatus = recipeStatus;
    }


    public int getInvoice() {
        return invoice;
    }

    public String getRecipeDate() {
        return recipeDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public String getProvince() {
        return province;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getRecipeCost() {
        return recipeCost;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getRecipeStatus() {
        return recipeStatus;
    }


    public void setInvoice(int invoice) {
        this.invoice = invoice;
    }

    public void setRecipeDate(String recipeDate) {
        this.recipeDate = recipeDate;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setRecipeCost(double recipeCost) {
        this.recipeCost = recipeCost;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setRecipeStatus(String recipeStatus) {
        this.recipeStatus = recipeStatus;
    }
}