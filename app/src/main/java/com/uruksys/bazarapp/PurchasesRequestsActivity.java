package com.uruksys.bazarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.uruksys.bazarapp.MainActivity.sharedPreferencesName;

public class PurchasesRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerPurchasesRequests;
    ImageView imgFinishThisActivity, imgOptionsForRecipeStatus;
    ArrayList<InvoiceModel> purchasesRequestsArrayList = new ArrayList<>();
    ArrayList<InvoiceModel> purchasesRequestsArrayList_ToDisplay = new ArrayList<>();
    ProgressBar progressBar_purchasesRequests;
    TextView txtInvoiceStatusOverall, txtInvoiceTotalItems, txtInvoiceTotalPaidAmount, txtInvoiceTotalCost, txtInvoiceDate, txtInvoiceId;
    ImageButton btnPaginateInvoice_arrowRight, btnPaginateInvoice_arrowLeft;

    int selectedInvoiceIndexInArrayList = 0;
    public static String shopMobileNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_requests);

        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        recyclerPurchasesRequests = findViewById(R.id.recyclerPurchasesRequests);
        progressBar_purchasesRequests = findViewById(R.id.progressBar_purchasesRequests);
        imgOptionsForRecipeStatus = findViewById(R.id.imgOptionsForRecipeStatus);
        txtInvoiceStatusOverall = findViewById(R.id.txtInvoiceStatusOverall);
        txtInvoiceTotalItems = findViewById(R.id.txtInvoiceTotalItems);
        txtInvoiceTotalPaidAmount = findViewById(R.id.txtInvoiceTotalPaidAmount);
        txtInvoiceTotalCost = findViewById(R.id.txtInvoiceTotalCost);
        txtInvoiceDate = findViewById(R.id.txtInvoiceDate);
        txtInvoiceId = findViewById(R.id.txtInvoiceId);
        btnPaginateInvoice_arrowRight = findViewById(R.id.btnPaginateInvoice_arrowRight);
        btnPaginateInvoice_arrowLeft = findViewById(R.id.btnPaginateInvoice_arrowLeft);

        imgFinishThisActivity.setOnClickListener(view -> finish());


        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")) {

            btnPaginateInvoice_arrowRight.setScaleX(-1);
            btnPaginateInvoice_arrowLeft.setScaleX(-1);
            imgFinishThisActivity.setScaleX(-1);
        }

        PaginateInvoices();
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
        int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);
        if (customerId == 0) {

            SignUserIn();
        } else {

            GetPurchasesRequests();
        }


        OnImgOptionsForRecipeStatusClicked();
    }


    private void OnImgOptionsForRecipeStatusClicked() {
        imgOptionsForRecipeStatus.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view1 = inflater.inflate(R.layout.recipe_status_options_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(PurchasesRequestsActivity.this)
                    .setView(view1);

            Button btnDisplayPendingPurchasesReq = view1.findViewById(R.id.btnFirst);
            Button btnDisplayApprovedPurchasesReq = view1.findViewById(R.id.btnSecond);
            Button btnDisplayBothTypes = view1.findViewById(R.id.btnDisplayBothTypes);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btnDisplayApprovedPurchasesReq.setOnClickListener(view2 -> {
                selectedInvoiceIndexInArrayList = 0;

                purchasesRequestsArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesRequestsArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_APPROVED)) {

                            purchasesRequestsArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesRequestsArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String5), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesRequests.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                            (PurchasesRequestsActivity.this,
                                    R.layout.purchases_requests_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                    recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
                }

                alertDialog.dismiss();
            });


            btnDisplayBothTypes.setOnClickListener(view22 -> {
                selectedInvoiceIndexInArrayList = 0;


                purchasesRequestsArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesRequestsArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_APPROVED)) {

                            purchasesRequestsArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesRequestsArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.GlobalMessage__NoInvoices), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesRequests.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                            (PurchasesRequestsActivity.this, R.layout.purchases_requests_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                    recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
                }
                alertDialog.dismiss();
            });


            btnDisplayPendingPurchasesReq.setOnClickListener(view23 -> {
                selectedInvoiceIndexInArrayList = 0;

                purchasesRequestsArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesRequestsArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_PENDING)) {

                            purchasesRequestsArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesRequestsArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String4), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesRequests.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                            (PurchasesRequestsActivity.this, R.layout.purchases_requests_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                    recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
                }

                alertDialog.dismiss();
            });

            alertDialog.show();
        });
    }


    private void PaginateInvoices() {
        btnPaginateInvoice_arrowRight.setOnClickListener(view -> {

            if (selectedInvoiceIndexInArrayList == purchasesRequestsArrayList_ToDisplay.size() - 1) {

                Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String3), Toast.LENGTH_SHORT).show();
            } else {

                selectedInvoiceIndexInArrayList++;
                InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                if (invoiceModel.getInvoiceStatusOverall() != null)
                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                        (this, R.layout.purchases_requests_recycler_item, invoiceModel.getShopInvoiceArrayList());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
            }
        });


        btnPaginateInvoice_arrowLeft.setOnClickListener(view -> {

            if (selectedInvoiceIndexInArrayList == 0) {
                Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String2), Toast.LENGTH_SHORT).show();
            } else {

                selectedInvoiceIndexInArrayList--;
                InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                if (invoiceModel.getInvoiceStatusOverall() != null)
                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                        (this, R.layout.purchases_requests_recycler_item, invoiceModel.getShopInvoiceArrayList());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
            }
        });
    }


    private void GetPurchasesRequests() {

        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_purchasesRequests.setVisibility(View.VISIBLE);

        purchasesRequestsArrayList.clear();
        purchasesRequestsArrayList_ToDisplay.clear();
        recyclerPurchasesRequests.setAdapter(null);

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);

                SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
                int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetCustomerPurchasesReqs + customerId)
                        .get()
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("pendingPurchases", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("pendingPurchases", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int invoiceId = jsonObject.getInt("id");
                                String createdAt = jsonObject.getString("createdAt");
                                try {
                                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd" , Locale.ENGLISH);
                                    Date newDate = isoFormat.parse(createdAt);
                                    createdAt = dateFormat.format(newDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                double invoiceTotalCost = 0;
                                double invoiceTotalPaidAmount = 0;
                                int invoiceTotalItems = 0;
                                boolean allInvoicesShopsStatusAreSame = true;
                                String invoiceStatusOverall = null;
                                JSONArray invoiceShopsJsonArr = jsonObject.getJSONArray("InvoiceShops");
                                ArrayList<ShopInvoiceModel> shopInvoiceArrayList = new ArrayList<>();
                                for (int j = 0; j < invoiceShopsJsonArr.length(); j++) {
                                    JSONObject invoiceShopJson = invoiceShopsJsonArr.getJSONObject(j);

                                    int shopInvoiceId = invoiceShopJson.getInt("id");
                                    int shopId = invoiceShopJson.getInt("shopId");
                                    double totalCost = invoiceShopJson.getDouble("totalCost");
                                    invoiceTotalCost += totalCost;
                                    double paidAmount = invoiceShopJson.getDouble("paidAmount");
                                    invoiceTotalPaidAmount += paidAmount;
                                    String paymentMethod = invoiceShopJson.getString("paymentMethod");
                                    String shopName = invoiceShopJson.getJSONObject("Shop").getString("name");
                                    String mobile = invoiceShopJson.getJSONObject("Shop").getString("mobile");
                                    invoiceTotalItems += invoiceShopJson.getJSONArray("InvoiceItems").
                                            getJSONObject(0).
                                            getInt("itemsCount");
                                    int itemsCount = invoiceShopJson.getJSONArray("InvoiceItems").
                                            getJSONObject(0).
                                            getInt("itemsCount");
                                    String status = invoiceShopJson.getString("status");
                                    if (invoiceStatusOverall != null && !invoiceStatusOverall.equals(status)) {
                                        allInvoicesShopsStatusAreSame = false;
                                    } else {
                                        invoiceStatusOverall = status;
                                        allInvoicesShopsStatusAreSame = true;
                                    }

                                    shopInvoiceArrayList.add(new ShopInvoiceModel(shopInvoiceId, shopId, totalCost, paidAmount,
                                            paymentMethod, status, shopName, mobile, itemsCount));
                                }

                                if (!allInvoicesShopsStatusAreSame) invoiceStatusOverall = null;

                                purchasesRequestsArrayList.add(new InvoiceModel(invoiceId, createdAt, invoiceTotalCost,
                                        invoiceTotalPaidAmount, invoiceTotalItems, invoiceStatusOverall, shopInvoiceArrayList));

                                purchasesRequestsArrayList_ToDisplay.add(new InvoiceModel(invoiceId, createdAt, invoiceTotalCost,
                                        invoiceTotalPaidAmount, invoiceTotalItems, invoiceStatusOverall, shopInvoiceArrayList));
                            }


                            if (purchasesRequestsArrayList_ToDisplay.size() == 0) {
                                Toast.makeText(this, getString(R.string.GlobalMessage__NoInvoices), Toast.LENGTH_SHORT).show();
                            } else {
                                InvoiceModel invoiceModel = purchasesRequestsArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                                if (invoiceModel.getInvoiceStatusOverall() != null)
                                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                                PurchasesRequestsRecyclerAdapter purchasesRequestsRecyclerAdapter = new PurchasesRequestsRecyclerAdapter
                                        (this, R.layout.purchases_requests_recycler_item, invoiceModel.getShopInvoiceArrayList());
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerPurchasesRequests.setLayoutManager(mLayoutManager);
                                recyclerPurchasesRequests.setItemAnimator(new DefaultItemAnimator());
                                recyclerPurchasesRequests.setAdapter(purchasesRequestsRecyclerAdapter);
                            }
//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("pendingPurchases", e.getMessage());
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_purchasesRequests.setVisibility(View.INVISIBLE);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("pendingPurchases", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void SignUserIn() {

        AtomicInteger dismissCount = new AtomicInteger(1);

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
            Intent intent = new Intent(PurchasesRequestsActivity.this, RegisterCustomerActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener((View v) -> {
            if (etxtUsername.getText().toString().trim().equals("") || etxtPassword.getText().toString().trim().equals("")) {

                Toast.makeText(PurchasesRequestsActivity.this, getString(R.string.GlobalMessage_EnterUsernameAndPassword), Toast.LENGTH_SHORT).show();
            } else {


                MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBar_purchasesRequests.setVisibility(View.VISIBLE);
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
                                            Toast.makeText(PurchasesRequestsActivity.this, getString(R.string.GlobalMessage_IncorrectUsernameOrPassword), Toast.LENGTH_SHORT).show();
                                        }

                                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                        progressBar_purchasesRequests.setVisibility(View.INVISIBLE);
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString(MainActivity.sharedPreferences_Username, jsonObject1.getString("username"));
                                        editor.putString(MainActivity.sharedPreferences_Password, jsonObject1.getString("password"));
                                        editor.putInt(MainActivity.sharedPreferences_CustomerId, jsonObject1.getInt("id"));
                                        String customerName = jsonObject1.getString("name");
                                        editor.putString(MainActivity.sharedPreferences_CustomerName, customerName);
                                        editor.apply();

                                        Toast.makeText(PurchasesRequestsActivity.this,
                                                getString(R.string.GlobalMessage_Welcome) + customerName,
                                                Toast.LENGTH_SHORT).show();

                                        alertDialog.dismiss();
                                        GetPurchasesRequests();
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();

                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBar_purchasesRequests.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PurchasesRequestsActivity.this,
                                            getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("SignUserIn", "error1 " + e.getMessage() + "");
                            runOnUiThread(() -> {

                                MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                progressBar_purchasesRequests.setVisibility(View.INVISIBLE);
                                Toast.makeText(this,
                                        getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_purchasesRequests.setVisibility(View.INVISIBLE);
                        Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                        Toast.makeText(PurchasesRequestsActivity.this,
                                getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        });

        alertDialog.setOnDismissListener(dialog -> {

            SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
            if (sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0) == 0) {
                if (dismissCount.get() > 0) {
                    Toast.makeText(this, getString(R.string.PurchasesArchiveActivity_String3), Toast.LENGTH_SHORT).show();
                    alertDialog.show();
                    dismissCount.getAndDecrement();
                } else {
                    finish();
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(this, "phone call permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + shopMobileNumber));
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String1), Toast.LENGTH_LONG).show();
            }
        }
    }
}
