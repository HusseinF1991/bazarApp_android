package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainMenuActivity extends AppCompatActivity {

    Button btnLogout, btnNewProduct, btnProfile, btnUpdateProduct, btnNewSellsRequests, btnSellsArchive, btnAddBrand,
            btnCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        btnLogout = findViewById(R.id.btnLogout);
        btnNewProduct = findViewById(R.id.btnNewProduct);
        btnNewSellsRequests = findViewById(R.id.btnNewSellsRequests);
        btnProfile = findViewById(R.id.btnProfile);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnSellsArchive = findViewById(R.id.btnSellsArchive);
        btnAddBrand = findViewById(R.id.btnAddBrand);
        btnCategories = findViewById(R.id.btnCategories);

        Intent intent = new Intent(this, MyFirebaseMessagingService.class);
        startService(intent);
        GetToken();


        Logout();
        AddNewProduct();
        UpdateProduct();
        SellsArchive();
        SellsRequests();
        OpenProfile();
        AddBrands();
        Categories();
    }


    private void GetToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("SaveTokenKey", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("SaveTokenKey", token);


                        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.sharedPreferencesName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(LoginActivity.sharedPreferences_Token, token);
                        editor.apply();
                        String providerTitle = sharedPreferences.getString(LoginActivity.sharedPreferences_Username, null);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("providerTitle", providerTitle);
                            jsonObject.put("token", token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new Thread(() -> {

                            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
                            Request request = new Request.Builder()
                                    .url(LoginActivity.serverIp + "/saveMerchantToken.php")
                                    .post(requestBody)
                                    .build();

                            String s = null;
                            try {
                                Response response = LoginActivity.client.newCall(request).execute();

                                s = response.body().string();

                                Log.d("SaveTokenKey" , s);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("SaveTokenKey", e.getMessage());
                                ;
                            }
                        }).start();
                    }
                });
    }


    private void Categories() {
        btnCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });
    }


    private void Logout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = MainMenuActivity.this.getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("BazarAppMerchantLoggedInSharedPreferences", false);
                editor.apply();

                finish();
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void AddBrands() {
        btnAddBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, BrandsActivity.class);
                startActivity(intent);
            }
        });
    }


    private void AddNewProduct() {
        btnNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, NewProductActivity.class);
                startActivity(intent);
            }
        });
    }


    private void UpdateProduct() {
        btnUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, UpdateProductActivity.class);
                startActivity(intent);
            }
        });
    }


    private void SellsArchive() {
        btnSellsArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainMenuActivity.this, SellsArchiveActivity.class);
                startActivity(intent);
            }
        });
    }


    private void SellsRequests() {
        btnNewSellsRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainMenuActivity.this, NewSellsRequestsActivity.class);
                startActivity(intent);
            }
        });
    }


    private void OpenProfile() {
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
