package com.uruksys.bazarapp_merchants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateProductActivity extends AppCompatActivity {

    AutoCompleteTextView etxtSearchBoxForProducts;
    public static RecyclerView recyclerProducts;
    ProgressBar progressBar_UpdateProductActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etxtSearchBoxForProducts = findViewById(R.id.etxtSearchBoxForProducts);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        progressBar_UpdateProductActivity = findViewById(R.id.progressBar_UpdateProductActivity);
        SearchProductsInSearchTextBox();

        PopulateItemsNameInTxtBox();
    }


    private void PopulateItemsNameInTxtBox() {

        AtomicBoolean replayThread = new AtomicBoolean(false);
        do {
            new Thread(() -> {
                //RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(myJsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getProductsNames.php")
                        //.post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("hello", "on background");

                    String finalS = s;

                    try {
                        runOnUiThread(() -> {

                            // OnPostExecute stuff here
                            try {
                                Log.d("hello", "onPostExecute 1");
                                Log.d("hello", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("hello", "onPostExecute 2");

                                List<String> productsNamesList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String productName = jsonObject.get("productName").toString();

                                    productsNamesList.add(productName);
                                }
                                // Create the adapter and set it to the AutoCompleteTextView
                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productsNamesList);
                                etxtSearchBoxForProducts.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("hello", "error " + e.getMessage());
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("hello", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hello", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }


    private void SearchProductsInSearchTextBox() {
        etxtSearchBoxForProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().equals("")) {

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar_UpdateProductActivity.setVisibility(View.VISIBLE);

                    getProductsFromSearchKeyWord(editable.toString());
                } else
                    recyclerProducts.setAdapter(null);
            }
        });
    }


    private void getProductsFromSearchKeyWord(String searchKeyWord) {

        AtomicBoolean replayThread = new AtomicBoolean(false);
        do {
            new Thread(() -> {

                Log.d("hello", "searchKeyWord " + searchKeyWord);

                JSONObject myJsonObject = new JSONObject();
                try {
                    myJsonObject.put("searchKeyWord", searchKeyWord);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(myJsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getProductsBySearchKeyWord.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("hello", "on background");

                    String finalS = s;

                    try {
                        runOnUiThread(() -> {

                            // OnPostExecute stuff here
                            try {
                                Log.d("hello", "onPostExecute 1");
                                Log.d("hello", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("hello", "onPostExecute 2");


                                ArrayList<ProductsModel> getProductsBySearchingArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String productCode = jsonObject.get("productCode").toString();
                                    String productName = jsonObject.get("productName").toString();
                                    byte[] productImage = Base64.decode(jsonObject.getString("productImage"), 0);

                                    getProductsBySearchingArrayList.add(new ProductsModel(id, productCode, productName, productImage));
                                }


                                SearchProductsRecyclerAdapter searchProductsRecyclerAdapter = new SearchProductsRecyclerAdapter
                                        (UpdateProductActivity.this,
                                                R.layout.products_search_recycler_item,
                                                getProductsBySearchingArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(UpdateProductActivity.this, 3);
                                recyclerProducts.setLayoutManager(mLayoutManager);
                                recyclerProducts.setItemAnimator(new DefaultItemAnimator());
                                recyclerProducts.setAdapter(searchProductsRecyclerAdapter);

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                progressBar_UpdateProductActivity.setVisibility(View.INVISIBLE);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("hello", "error " + e.getMessage());
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                progressBar_UpdateProductActivity.setVisibility(View.INVISIBLE);
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("hello", "error " + e.getMessage());
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar_UpdateProductActivity.setVisibility(View.INVISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hello", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }
}


class ProductsModel {

    int productId;
    private String productName, productCode;
    private byte[] productImage;

    public ProductsModel(int productId, String productCode, String productName, byte[] productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productCode = productCode;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public byte[] getProductImage() {
        return productImage;
    }

    public String getProductCode() {
        return productCode;
    }
}