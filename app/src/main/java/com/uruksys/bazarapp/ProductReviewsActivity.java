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
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductReviewsActivity extends AppCompatActivity {

    RecyclerView productReviewsRecycler;
    ImageView imgFinishThisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_reviews);

        productReviewsRecycler = findViewById(R.id.productReviewsRecycler);
        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener((View view) -> finish());

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgFinishThisActivity.setScaleX(-1);
        }

        Intent intent = getIntent();
        int itemId = intent.getIntExtra("itemId", 0);
        getItemReviews(itemId);
    }


    private void getItemReviews(int itemId) {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);
            do {
                replayThread.set(false);

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemReviews + itemId)
                        .build();

                String s = null;
                try {
                    Response response = MainActivity.client.newCall(request).execute();

                    s = response.body().string();

                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("getItemReviews", finalS);
                        ArrayList<ItemReviewsModel> itemReviewsArrayList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                                int id = jsonObject1.getInt("id");
                                double rate = jsonObject1.getDouble("rate");
                                String reviewerName = jsonObject1.getString("reviewerName");
                                String title = jsonObject1.getString("title");
                                String createdAt = jsonObject1.getString("createdAt");
                                String body = jsonObject1.getString("body");

                                itemReviewsArrayList.add(new ItemReviewsModel(id, rate, title, reviewerName, createdAt, body));


                                ProductReviewsRecyclerAdapter productReviewsRecyclerAdapter = new ProductReviewsRecyclerAdapter(
                                        ProductReviewsActivity.this,
                                        R.layout.product_reviews_recycler_item,
                                        itemReviewsArrayList);

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                                productReviewsRecycler.setLayoutManager(layoutManager);
                                productReviewsRecycler.setItemAnimator(new DefaultItemAnimator());
                                productReviewsRecycler.setAdapter(productReviewsRecyclerAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductReviewsActivity.this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }
}

