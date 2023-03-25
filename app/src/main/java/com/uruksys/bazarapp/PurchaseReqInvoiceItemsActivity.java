package com.uruksys.bazarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.Response;

public class PurchaseReqInvoiceItemsActivity extends AppCompatActivity {

    ImageView imgFinishThisActivity;
    public static RecyclerView ProductsInInvoiceRecycler;
    int invoiceShopId;
    ProgressBar progressBar_PurchaseReqInvoiceItemsActivity;

    //used the same model of the shopping cart => no need to create new model
    ArrayList<ItemsInInvoiceShopModel> itemsInInvoiceArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_req_invoice_items);


        Intent intent = getIntent();
        invoiceShopId = intent.getIntExtra("invoiceShopId", 0);

        Log.d("ActivityLifeCycle", "PurchaseReqInvoiceItemsActivity_OnCreate");

        progressBar_PurchaseReqInvoiceItemsActivity = findViewById(R.id.progressBar_PurchaseReqInvoiceItemsActivity);
        ProductsInInvoiceRecycler = findViewById(R.id.ProductsInInvoiceRecycler);
        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener(view -> finish());
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgFinishThisActivity.setScaleX(-1);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d("ActivityLifeCycle", "PurchaseReqInvoiceItemsActivity_OnStart");
        GetProductsInInvoice();
    }


    private void GetProductsInInvoice() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
        progressBar_PurchaseReqInvoiceItemsActivity.setVisibility(View.VISIBLE);

        itemsInInvoiceArrayList.clear();
        ProductsInInvoiceRecycler.setAdapter(null);

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                try {

                    Request request = new Request.Builder()
                            .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemsInInvoiceShop + invoiceShopId)
                            .get()
                            .build();

                    Response response = null;
                    String s = null;

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("PurchaseReqInvoiceItemsActivity", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("PurchaseReqInvoiceItemsActivity", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int invoiceId = jsonObject.getInt("invoiceId");
                                double totalCost = jsonObject.getDouble("totalCost");
                                double paidAmount = jsonObject.getDouble("paidAmount");
                                String status = jsonObject.getString("status");
                                String paymentMethod = jsonObject.getString("paymentMethod");
                                JSONArray invoiceItemsJsonArr = jsonObject.getJSONArray("InvoiceItems");
                                for (int j = 0; j < invoiceItemsJsonArr.length(); j++) {
                                    JSONObject invoiceItemsJson = invoiceItemsJsonArr.getJSONObject(j);

                                    int invoiceItemId = invoiceItemsJson.getInt("id");
                                    double sellPrice = invoiceItemsJson.getDouble("sellPrice");
                                    double qty = invoiceItemsJson.getDouble("qty");
                                    int typeId = invoiceItemsJson.getInt("typeId");
                                    String typeName = invoiceItemsJson.getJSONObject("ItemType").getString("typeName");
                                    String imageLoc = invoiceItemsJson.getJSONObject("ItemType")
                                            .getJSONArray("ItemTypeImages").getJSONObject(0).getString("imageLoc");
                                    imageLoc = MainActivity.itemTypeImgUrl + imageLoc;
                                    String itemCode = invoiceItemsJson.getJSONObject("ItemType").getJSONObject("Item")
                                            .getString("itemCode");
                                    String itemName = invoiceItemsJson.getJSONObject("ItemType").getJSONObject("Item")
                                            .getString("itemName");

                                    itemsInInvoiceArrayList.add(new ItemsInInvoiceShopModel(invoiceItemId, typeId, itemCode,
                                            itemName, typeName, imageLoc, sellPrice, qty));
                                }
                            }

                            ProductsInInvoiceRecyclerAdapter productsInInvoiceRecyclerAdapter = new ProductsInInvoiceRecyclerAdapter
                                    (this, R.layout.products_in_invoice_recycler_item, itemsInInvoiceArrayList , PurchaseReqInvoiceItemsActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            ProductsInInvoiceRecycler.setLayoutManager(mLayoutManager);
                            ProductsInInvoiceRecycler.setItemAnimator(new DefaultItemAnimator());
                            ProductsInInvoiceRecycler.setAdapter(productsInInvoiceRecyclerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("PurchaseReqInvoiceItemsActivity", e.getMessage());
                            Toast.makeText(PurchaseReqInvoiceItemsActivity.this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                        }
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar_PurchaseReqInvoiceItemsActivity.setVisibility(View.INVISIBLE);
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hello", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}