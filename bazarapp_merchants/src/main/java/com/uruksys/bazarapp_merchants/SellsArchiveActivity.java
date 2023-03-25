package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import okhttp3.Response;

public class SellsArchiveActivity extends AppCompatActivity {

    ProgressBar progressBar_SellsArchiveActivity;
    RecyclerView recyclerSellsArchive;
    public static ArrayList<PurchasesRequestsModel> sellsArchiveInvoicesArrayList = new ArrayList<>();

    public static String SelectedInvoice_CustomerMobile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sells_archive);

        recyclerSellsArchive = findViewById(R.id.recyclerSellsArchive);
        progressBar_SellsArchiveActivity = findViewById(R.id.progressBar_SellsArchiveActivity);
    }




    @Override
    protected void onStart() {
        super.onStart();

        GetSellsArchive();
    }





    private void GetSellsArchive() {


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_SellsArchiveActivity.setVisibility(View.VISIBLE);

        sellsArchiveInvoicesArrayList.clear();
        recyclerSellsArchive.setAdapter(null);


        AtomicBoolean replayThread = new AtomicBoolean(false);

        do {
            new Thread(() -> {
                //RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject1));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getSellsArchive_admin.php")
                        //.post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("SellsArchiveActivity", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("SellsArchiveActivity", finalS);
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


                                //used same model of purchases request because it's similar to it
                                sellsArchiveInvoicesArrayList.add(new PurchasesRequestsModel(invoice, recipeDate, customerName, customerMobile, province,
                                        lat, lng, recipeCost, paidAmount, paymentType, recipeStatus));
                            }

                            SellsArchiveRecyclerAdapter sellsArchiveRecyclerAdapter = new SellsArchiveRecyclerAdapter
                                    (this, R.layout.sells_archive_recycler_item, sellsArchiveInvoicesArrayList );
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerSellsArchive.setLayoutManager(mLayoutManager);
                            recyclerSellsArchive.setItemAnimator(new DefaultItemAnimator());
                            recyclerSellsArchive.setAdapter(sellsArchiveRecyclerAdapter);


                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_SellsArchiveActivity.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("SellsArchiveActivity", e.getMessage());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SellsArchiveActivity", e.getMessage());
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
