package com.uruksys.bazarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.uruksys.bazarapp.MainActivity.sharedPreferencesName;

public class ShoppingCartActivity extends AppCompatActivity {

    ImageView imgFinishThisActivity;
    public static RecyclerView ProductsInShoppingCartRecycler;
    ArrayList<ItemsInCartModel> itemsInCartArrayList = new ArrayList<>();
    Button btnProceedToPurchase;
    TextView txtTotalPrice, txtTotalPriceTitle;
    ProgressBar progressBarShoppingCartActivity;
    View viewLineSeparator;

    private GoogleApiClient googleApiClient;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnCreate");


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        progressBarShoppingCartActivity = findViewById(R.id.progressBarShoppingCartActivity);
        btnProceedToPurchase = findViewById(R.id.btnProceedToPurchase);
        ProductsInShoppingCartRecycler = findViewById(R.id.ProductsInShoppingCartRecycler);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtTotalPriceTitle = findViewById(R.id.txtTotalPriceTitle);
        viewLineSeparator = findViewById(R.id.viewLineSeparator);
        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener(view -> {
            finish();

            Intent intent = new Intent(ShoppingCartActivity.this, MainActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgFinishThisActivity.setScaleX(-1);
        }

        OnBuyBtnClicked();
    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnStart");

        GetProductsInShoppingCart();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnRestart");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnDestroy");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLifeCycle", "ShoppingCartActivity_OnResume");
    }


    private void OnBuyBtnClicked() {
        btnProceedToPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (itemsInCartArrayList.size() > 0) {

                    for (ItemsInCartModel itemsInCartModel : itemsInCartArrayList) {
                        if (itemsInCartModel.getAvailableQty() < itemsInCartModel.getQtyInCart()) {
                            Toast.makeText(ShoppingCartActivity.this, getString(R.string.String1_ShoppingCartActivity), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    ShowPaymentMethodDialog();
                }
            }
        });
    }


    private void ShowPaymentMethodDialog() {

        View view = getLayoutInflater().inflate(R.layout.payment_method_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);


        LinearLayout llPayWithCash = view.findViewById(R.id.llPayWithCash);
        LinearLayout llPayWithZainCash = view.findViewById(R.id.llPayWithZainCash);


        AlertDialog alertDialog = builder.create();

        llPayWithCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                SharedPreferences sharedPref = ShoppingCartActivity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
                String username = sharedPref.getString(MainActivity.sharedPreferences_Username, null);
                String lat = sharedPref.getString(MainActivity.sharedPreferences_Latitude, null);
                if (username == null) {

                    SignUserIn();
                } else if (lat == null) {

                    TurnGPS_On();
                } else {
                    SendRecipeToDb();
                }
            }
        });


        llPayWithZainCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                Toast.makeText(ShoppingCartActivity.this, getString(R.string.String2_ShoppingCartActivity), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }


    private void SendRecipeToDb() {

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
        progressBarShoppingCartActivity.setVisibility(View.VISIBLE);

        try {

            MySqliteDB mySqliteDB = new MySqliteDB(this);
            JSONObject jsonObject = new JSONObject();
            SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
            int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);
            if (customerId != 0) {
                jsonObject.put("customerId", customerId);
                JSONArray invoiceShopsJsonArr = new JSONArray();
                ArrayList<Integer> loopedShops = new ArrayList<>();
                for (ItemsInCartModel itemsInCartModel : itemsInCartArrayList) {

                    if (!loopedShops.contains(itemsInCartModel.getShopId())) {
                        loopedShops.add(itemsInCartModel.getShopId());

                        int shopId = itemsInCartModel.getShopId();
                        JSONObject oneShopJson = new JSONObject();
                        JSONArray oneShopItemsJsonArr = new JSONArray();
                        double totalCost = 0;
                        for (ItemsInCartModel itemsInCartModel2 : itemsInCartArrayList) {

                            if (itemsInCartModel2.getShopId() == shopId) {
                                JSONObject oneShopItemsJson = new JSONObject();
                                oneShopItemsJson.put("typeId", itemsInCartModel2.getItemTypeId());
                                oneShopItemsJson.put("purchasePrice", itemsInCartModel2.getShopPurchasePrice());
                                oneShopItemsJson.put("sellPrice", itemsInCartModel2.getSellPrice());
                                oneShopItemsJson.put("qty", itemsInCartModel2.getQtyInCart());

                                if (itemsInCartModel2.getDiscountPrice() > 0)
                                    totalCost = totalCost + (itemsInCartModel2.getQtyInCart() * itemsInCartModel2.getDiscountPrice());
                                else
                                    totalCost = totalCost + (itemsInCartModel2.getQtyInCart() * itemsInCartModel2.getSellPrice());

                                oneShopItemsJsonArr.put(oneShopItemsJson);
                            }
                        }

                        oneShopJson.put("shopId", itemsInCartModel.getShopId());
                        oneShopJson.put("totalCost", totalCost);
                        oneShopJson.put("paidAmount", 0);
                        oneShopJson.put("paymentMethod", MainActivity.PAYMENT_METHOD_CASH);
//                  oneShopJson.put("status" , "PENDING");  ......IN BACKEND
//                  oneShopJson.put("deleteFlag" , 0);   ......IN BACKEND
                        oneShopJson.put("shopInvoiceItems", oneShopItemsJsonArr);

                        invoiceShopsJsonArr.put(oneShopJson);
                    }
                }
                jsonObject.put("shopsInvoices", invoiceShopsJsonArr);


                new Thread(() -> {
                    AtomicBoolean replayThread = new AtomicBoolean(false);

                    do {
                        replayThread.set(false);
                        RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));

                        Request request = new Request.Builder()
                                .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.NewPurchaseRequest)
                                .post(requestBody)
                                .build();

                        Response response = null;

                        String s = null;
                        try {

                            response = MainActivity.client.newCall(request).execute();
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);

                            s = response.body().string();

                            Log.d("newPurchaseRequest", "on background");


                            String finalS = s;
                            runOnUiThread(() -> {

                                Log.d("newPurchaseRequest", finalS);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(finalS);
                                    if (jsonObject1.getBoolean("error")) {

                                        Toast.makeText(ShoppingCartActivity.this, getString(R.string.ShoppingCartActivity_String7), Toast.LENGTH_SHORT).show();
                                    } else {
                                        mySqliteDB.InsertNewInvoice_purchases(jsonObject1.getInt("invoiceId"));
                                        mySqliteDB.DeleteAllProductsFromCart_shoppingCart();


                                        Toast.makeText(ShoppingCartActivity.this, getString(R.string.ShoppingCartActivity_String6), Toast.LENGTH_SHORT).show();

                                        Thread.sleep(500); //for sqlite to finish its deletion and insertion
                                        finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("newPurchaseRequest", e.getMessage());
                                    Toast.makeText(ShoppingCartActivity.this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Log.d("newPurchaseRequest", e.getMessage());
                                }
                                disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);

                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("newPurchaseRequest", e.getMessage());
                            replayThread.set(true);
                        }
                    } while (replayThread.get());
                }).start();
            } else {

                Toast.makeText(ShoppingCartActivity.this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ShoppingCartActivity.this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
            disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
            progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
        }
    }


    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child.getId() != R.id.imgFinishThisActivity)
                child.setEnabled(enable);

            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }


    private void SignUserIn() {


        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.sign_in_customer_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view1);

        EditText etxtUsername = view1.findViewById(R.id.etxtUsername);
        EditText etxtPassword = view1.findViewById(R.id.etxtPassword);
        TextView txtRegisterNewCustomer = view1.findViewById(R.id.txtRegisterNewCustomer);
        Button btnSignIn = view1.findViewById(R.id.btnSignIn);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        txtRegisterNewCustomer.setOnClickListener(view -> {

            alertDialog.dismiss();
            Intent intent = new Intent(ShoppingCartActivity.this, RegisterCustomerActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener((View v) -> {
            if (etxtUsername.getText().toString().trim().equals("") || etxtPassword.getText().toString().trim().equals("")) {

                Toast.makeText(ShoppingCartActivity.this, getString(R.string.GlobalMessage_EnterUsernameAndPassword), Toast.LENGTH_SHORT).show();
            } else {


                disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBarShoppingCartActivity.setVisibility(View.VISIBLE);
                new Thread(() -> {

                    try {

                        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("username", etxtUsername.getText().toString().trim());
                        jsonObject.put("password", etxtPassword.getText().toString().trim());
                        jsonObject.put("token", sharedPreferences.getString(MainActivity.sharedPreferences_Token, null));
                        RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
                        Request request = new Request.Builder()
                                .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.SignInCustomer)
                                .post(requestBody)
                                .build();

                        String s = null;
                        try {
                            Response response = MainActivity.client.newCall(request).execute();

                            s = response.body().string();

                            String finalS = s;
                            runOnUiThread(() -> {

                                Log.d("SignUserIn", finalS + "");
                                try {
                                    JSONObject jsonObject1 = new JSONObject(finalS);
                                    if (jsonObject1.has("ERROR") && jsonObject1.has("ERROR_TYPE")) {
                                        if (jsonObject1.getString("ERROR_TYPE").equals("USER_NOT_AUTHORIZED")) {
                                            Toast.makeText(ShoppingCartActivity.this, getString(R.string.GlobalMessage_IncorrectUsernameOrPassword), Toast.LENGTH_SHORT).show();
                                        }

                                        disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                        progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putInt(MainActivity.sharedPreferences_CustomerId, jsonObject1.getInt("id"));
                                        editor.putString(MainActivity.sharedPreferences_Username, jsonObject1.getString("username"));
                                        editor.putString(MainActivity.sharedPreferences_Password, jsonObject1.getString("password"));
                                        String customerName = jsonObject1.getString("name");
                                        editor.putString(MainActivity.sharedPreferences_CustomerName, customerName);
                                        editor.putString(MainActivity.sharedPreferences_LocaleLanguage, jsonObject1.getString("appLanguage"));
                                        editor.apply();

                                        Toast.makeText(this, getString(R.string.GlobalMessage_Welcome) + customerName, Toast.LENGTH_SHORT).show();

                                        alertDialog.dismiss();
                                        SendRecipeToDb();
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();

                                    disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
                                    Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("SignUserIn", "error1 " + e.getMessage() + "");
                            runOnUiThread(() -> {

                                disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
                                Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBarShoppingCartActivity.setVisibility(View.INVISIBLE);
                        Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                        Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        });
    }


    private void GetProductsInShoppingCart() {

        itemsInCartArrayList.clear();
        ProductsInShoppingCartRecycler.setAdapter(null);
        JSONObject jsonObject = new JSONObject();
        ArrayList<Integer> typeIdsInSqliteArrayList = new ArrayList<>();

        MySqliteDB mySqliteDB = new MySqliteDB(this);
        Cursor cursor = mySqliteDB.GetProducts_shoppingCart();
        if (cursor.getCount() > 0) {
            Dictionary<Integer, Double> quantitiesInCartDict = new Hashtable<>();

            try {
                if (cursor.moveToFirst()) {
                    do {

                        int itemTypeId = cursor.getInt(cursor.getColumnIndex("itemTypeId"));
                        double quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
                        quantitiesInCartDict.put(itemTypeId, quantity);

                        typeIdsInSqliteArrayList.add(itemTypeId);
                    } while (cursor.moveToNext());
                }


                jsonObject.put("typesIds", typeIdsInSqliteArrayList);


                new Thread(() -> {
                    AtomicBoolean replayThread = new AtomicBoolean(false);
                    do {
                        replayThread.set(false);
                        RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));

                        Request request = new Request.Builder()
                                .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemsDetailsInCart)
                                .post(requestBody)
                                .build();

                        Response response = null;

                        String s = null;
                        try {

                            response = MainActivity.client.newCall(request).execute();
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);

                            s = response.body().string();

                            Log.d("shoppingCart", "on background");


                            String finalS = s;
                            runOnUiThread(() -> {

                                double totalCost = 0;

                                Log.d("shoppingCart", finalS);
                                try {
                                    JSONArray jsonArray = new JSONArray(finalS);
                                    Log.d("shoppingCart", jsonArray.length() + "");
                                    //check if the manager deleted the products that the user put them previously in the cart
                                    if (jsonArray.length() == 0) {

                                        mySqliteDB.DeleteAllProductsFromCart_shoppingCart();
                                    } else {

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                            int itemTypeId = jsonObject2.getInt("id");
                                            int itemId = jsonObject2.getInt("itemId");
                                            String typeName = jsonObject2.getString("typeName");
                                            int availableQty = jsonObject2.getInt("availableQty");
                                            double shopPurchasePrice = jsonObject2.getDouble("purchasePrice");
                                            double sellPrice = jsonObject2.getDouble("sellPrice");
                                            double discountPrice = jsonObject2.getDouble("discountPrice");
                                            JSONObject itemJson = jsonObject2.getJSONObject("Item");
                                            String itemCode = itemJson.getString("itemCode");
                                            String itemName = itemJson.getString("itemName");
                                            int shopId = itemJson.getInt("shopId");
                                            JSONArray itemTypeImgJson = jsonObject2.getJSONArray("ItemTypeImages");
                                            String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");


                                            try {
                                                double qtyInCart = quantitiesInCartDict.get(itemTypeId);

                                                itemsInCartArrayList.add(new ItemsInCartModel(itemId, itemTypeId, itemCode, itemName, shopId,
                                                        imageLoc, typeName, shopPurchasePrice, sellPrice, discountPrice, availableQty, qtyInCart));


                                                if (discountPrice > 0)
                                                    totalCost = totalCost + (qtyInCart * discountPrice);
                                                else
                                                    totalCost = totalCost + (qtyInCart * sellPrice);
                                            } catch (NullPointerException e) {
                                                Log.d("GetProductsInShoppingCart: ", e.getMessage());
                                                mySqliteDB.RemoveProduct_shoppingCart(itemTypeId);
                                            }
                                        }
                                        txtTotalPrice.setText(String.valueOf(totalCost));

                                        ShoppingCartRecyclerAdapter shoppingCartRecyclerAdapter = new ShoppingCartRecyclerAdapter
                                                (this, R.layout.shopping_cart_recycler_item, itemsInCartArrayList);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        ProductsInShoppingCartRecycler.setLayoutManager(mLayoutManager);
                                        ProductsInShoppingCartRecycler.setItemAnimator(new DefaultItemAnimator());
                                        ProductsInShoppingCartRecycler.setAdapter(shoppingCartRecyclerAdapter);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("shoppingCart", e.getMessage());
                                    Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("hello", e.getMessage());
                            replayThread.set(true);
                        }
                    } while (replayThread.get());
                }).start();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
            }
        } else {
            btnProceedToPurchase.setVisibility(View.INVISIBLE);
            txtTotalPrice.setVisibility(View.INVISIBLE);
            txtTotalPriceTitle.setVisibility(View.INVISIBLE);
            viewLineSeparator.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

        Intent intent = new Intent(ShoppingCartActivity.this, MainActivity.class);
        startActivity(intent);
    }


    //promote the user to turn on GPS (if closed) on the activity created
    private void TurnGPS_On() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).build();

        }

        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback() {

            @Override
            public void onResult(@NonNull Result result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("openGps", "case success");
                        GetCurrentLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {

                            Log.d("openGps", "case resolution required");
                            status.startResolutionForResult(ShoppingCartActivity.this, 1000);

                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;

                }
            }
        });
        //googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void GetCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(ShoppingCartActivity.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , MainActivity.PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

            Log.d("sendMyLocation", "start");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                SharedPreferences sharedPref = ShoppingCartActivity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(MainActivity.sharedPreferences_Longitude, String.valueOf(location.getLongitude()));
                                editor.putString(MainActivity.sharedPreferences_Latitude, String.valueOf(location.getLatitude()));
                                editor.apply();
                            }
                        }
                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                if (resultCode == Activity.RESULT_OK) {

                    Log.d("openGps", "gps opened");
                    GetCurrentLocation();
                    return;
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.Notice)
                            .setMessage(R.string.String3_BuyingRequestActivity)
                            .create()
                            .show();
                }
                break;
        }
    }

    //if user didn't grant the application access to the GPS then purchase request cant be completed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MainActivity.PERMISSIONS_REQUEST_LOCATION:   //checking location access permission
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GetCurrentLocation();
                    return;
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.Notice)
                            .setMessage(R.string.String3_BuyingRequestActivity)
                            .create()
                            .show();
                }
                break;
        }
    }
}
