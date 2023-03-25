package com.uruksys.bazarapp_merchants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewSellsRequests_ItemsOfInvoiceActivity extends AppCompatActivity {

    public static RecyclerView recyclerPurchasesRequestsItemsInInvoice;
    int invoice;
    ProgressBar progressBar_newPurchasesRequests_itemsOfInvoiceActivity;

    //used the same model of the shopping cart => no need to create new model
    ArrayList<ProductsInInvoiceModel> productsInInvoiceArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sells_requests__items_of_invoice);



        Intent intent = getIntent();
        invoice = intent.getIntExtra("invoice", 0);
        Log.d("NewSellsRequests_ItemsOfInvoiceActivity", "invoice " + invoice + "");

        Log.d("ActivityLifeCycle", "NewSellsRequests_ItemsOfInvoiceActivity_OnCreate");

        progressBar_newPurchasesRequests_itemsOfInvoiceActivity = findViewById(R.id.progressBar_newPurchasesRequests_itemsOfInvoiceActivity);
        recyclerPurchasesRequestsItemsInInvoice = findViewById(R.id.recyclerPurchasesRequestsItemsInInvoice);
    }






    @Override
    protected void onStart() {
        super.onStart();

        Log.d("ActivityLifeCycle", "NewSellsRequests_ItemsOfInvoiceActivity_OnStart");

        GetProductsInInvoice();
    }




    private void GetProductsInInvoice() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_newPurchasesRequests_itemsOfInvoiceActivity.setVisibility(View.VISIBLE);

        productsInInvoiceArrayList.clear();
        recyclerPurchasesRequestsItemsInInvoice.setAdapter(null);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("invoice", invoice);
            Log.d("NewSellsRequests_ItemsOfInvoiceActivity", "invoice : "+ invoice);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AtomicBoolean replayThread = new AtomicBoolean(false);

        do {
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getProductsDetailsInInvoice.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("NewSellsRequests_ItemsOfInvoiceActivity", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("NewSellsRequests_ItemsOfInvoiceActivity", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                int id = jsonObject2.getInt("id");
                                int productId = jsonObject2.getInt("productId");
                                int invoice = jsonObject2.getInt("invoice");
                                String productCode = jsonObject2.getString("productCode");
                                String productName = jsonObject2.getString("productName");
                                String productImage = jsonObject2.getString("productImage");
                                double sellPrice = jsonObject2.getDouble("sellPrice");
                                int quantity = jsonObject2.getInt("quantity");
                                String productOptionTitle = jsonObject2.getString("productOptionTitle");


                                productsInInvoiceArrayList.add(new ProductsInInvoiceModel(id , invoice,  productId, productCode, productName,
                                        productImage, sellPrice, quantity, productOptionTitle));
                            }


                            ProductsInInvoiceRecyclerAdapter productsInInvoiceRecyclerAdapter = new ProductsInInvoiceRecyclerAdapter
                                    (this, R.layout.products_in_invoice_recycler_item, productsInInvoiceArrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerPurchasesRequestsItemsInInvoice.setLayoutManager(mLayoutManager);
                            recyclerPurchasesRequestsItemsInInvoice.setItemAnimator(new DefaultItemAnimator());
                            recyclerPurchasesRequestsItemsInInvoice.setAdapter(productsInInvoiceRecyclerAdapter);


                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_newPurchasesRequests_itemsOfInvoiceActivity.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("NewSellsRequests_ItemsOfInvoiceActivity", e.getMessage());

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_newPurchasesRequests_itemsOfInvoiceActivity.setVisibility(View.INVISIBLE);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("NewSellsRequests_ItemsOfInvoiceActivity", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }

}



class ProductsInInvoiceModel {

    private int id , invoice , productId , quantity;
    private String productCode, productName, productImage, productOptionTitle;
    private double sellPrice ;

    public ProductsInInvoiceModel(int id, int invoice, int productId, String productCode, String productName, String productImage,
                                  double sellPrice, int quantity , String productOptionTitle) {
        this.id = id;
        this.invoice = invoice;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.productImage = productImage;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.productOptionTitle = productOptionTitle;
    }

    public int getId() {
        return id;
    }

    public int getInvoice() {
        return invoice;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductOptionTitle() {
        return productOptionTitle;
    }
}



