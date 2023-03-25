package com.uruksys.bazarapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class PurchasesArchiveActivity extends AppCompatActivity {

    RecyclerView recyclerPurchasesArchive;
    ImageView imgFinishThisActivity, imgOptionsForRecipeStatus;
    ArrayList<InvoiceModel> purchasesArchiveInvoicesArrayList = new ArrayList<>();
    ArrayList<InvoiceModel> purchasesArchiveInvoicesArrayList_ToDisplay = new ArrayList<>();
    ProgressBar progressBar_purchasesArchive;
    TextView txtInvoiceStatusOverall, txtInvoiceTotalItems, txtInvoiceTotalPaidAmount, txtInvoiceTotalCost, txtInvoiceDate, txtInvoiceId;
    Button btnPaginateInvoice_arrowRight, btnPaginateInvoice_arrowLeft;

    int selectedInvoiceIndexInArrayList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_archive);


        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        recyclerPurchasesArchive = findViewById(R.id.recyclerPurchasesArchive);
        progressBar_purchasesArchive = findViewById(R.id.progressBar_purchasesArchive);
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

            GetPurchasesArchive();
        }

        OnImgOptionsForRecipeStatusClicked();
    }


    private void OnImgOptionsForRecipeStatusClicked() {
        imgOptionsForRecipeStatus.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view1 = inflater.inflate(R.layout.recipe_status_options_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setView(view1);

            Button btnDisplayDeliveredPurchasesReq = view1.findViewById(R.id.btnFirst);
            btnDisplayDeliveredPurchasesReq.setText(R.string.RecipeStatusOptionsDialog_Delivered);
            Button btnDisplayRejectedPurchasesReq = view1.findViewById(R.id.btnSecond);
            btnDisplayRejectedPurchasesReq.setText(R.string.RecipeStatusOptionsDialog_Rejected);
            Button btnDisplayBothTypes = view1.findViewById(R.id.btnDisplayBothTypes);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btnDisplayDeliveredPurchasesReq.setOnClickListener(view2 -> {
                selectedInvoiceIndexInArrayList = 0;

                purchasesArchiveInvoicesArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesArchiveInvoicesArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_DELIVERED)) {

                            purchasesArchiveInvoicesArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesArchiveInvoicesArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.PurchasesArchiveActivity_String2), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesArchive.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                            (this,
                                    R.layout.purchases_archive_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                    recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);
                }

                alertDialog.dismiss();
            });


            btnDisplayBothTypes.setOnClickListener(view22 -> {
                selectedInvoiceIndexInArrayList = 0;

                purchasesArchiveInvoicesArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesArchiveInvoicesArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_DELIVERED)) {

                            purchasesArchiveInvoicesArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesArchiveInvoicesArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.GlobalMessage__NoInvoices), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesArchive.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                            (this, R.layout.purchases_archive_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                    recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);
                }
                alertDialog.dismiss();
            });


            btnDisplayRejectedPurchasesReq.setOnClickListener(view23 -> {
                selectedInvoiceIndexInArrayList = 0;

                purchasesArchiveInvoicesArrayList_ToDisplay.clear();
                for (InvoiceModel invoiceModel : purchasesArchiveInvoicesArrayList) {

                    for (ShopInvoiceModel shopInvoiceModel : invoiceModel.getShopInvoiceArrayList()) {
                        if (shopInvoiceModel.getStatus().equals(MainActivity.INVOICE_STATUS_PENDING)) {

                            purchasesArchiveInvoicesArrayList_ToDisplay.add(invoiceModel);
                            break;
                        }
                    }
                }

                if (purchasesArchiveInvoicesArrayList_ToDisplay.size() == 0) {
                    Toast.makeText(this, getString(R.string.PurchasesArchiveActivity_String1), Toast.LENGTH_SHORT).show();
                    recyclerPurchasesArchive.setAdapter(null);
                    txtInvoiceDate.setText("");
                    txtInvoiceTotalCost.setText("");
                    txtInvoiceTotalItems.setText("");
                    txtInvoiceTotalPaidAmount.setText("");
                    txtInvoiceId.setText("");
                    txtInvoiceStatusOverall.setText("");

                } else {

                    InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(0);
                    txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                    txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                    txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                    txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                    txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                    if (invoiceModel.getInvoiceStatusOverall() != null)
                        txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());
                    else
                        txtInvoiceStatusOverall.setText("");

                    PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                            (this, R.layout.purchases_archive_recycler_item,
                                    invoiceModel.getShopInvoiceArrayList());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                    recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                    recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);
                }

                alertDialog.dismiss();
            });

            alertDialog.show();
        });
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
            Intent intent = new Intent(this, RegisterCustomerActivity.class);
            startActivity(intent);
        });

        btnSignIn.setOnClickListener((View v) -> {
            if (etxtUsername.getText().toString().trim().equals("") || etxtPassword.getText().toString().trim().equals("")) {

                Toast.makeText(this, getString(R.string.GlobalMessage_EnterUsernameAndPassword), Toast.LENGTH_SHORT).show();
            } else {


                MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBar_purchasesArchive.setVisibility(View.VISIBLE);
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
                                            Toast.makeText(this, getString(R.string.GlobalMessage_IncorrectUsernameOrPassword), Toast.LENGTH_SHORT).show();
                                        }

                                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                        progressBar_purchasesArchive.setVisibility(View.INVISIBLE);
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString(MainActivity.sharedPreferences_Username, jsonObject1.getString("username"));
                                        editor.putString(MainActivity.sharedPreferences_Password, jsonObject1.getString("password"));
                                        editor.putInt(MainActivity.sharedPreferences_CustomerId, jsonObject1.getInt("id"));
                                        String customerName = jsonObject1.getString("name");
                                        editor.putString(MainActivity.sharedPreferences_CustomerName, customerName);
                                        editor.apply();

                                        Toast.makeText(this,
                                                getString(R.string.GlobalMessage_Welcome) + customerName,
                                                Toast.LENGTH_SHORT).show();

                                        alertDialog.dismiss();
                                        GetPurchasesArchive();
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();

                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBar_purchasesArchive.setVisibility(View.INVISIBLE);
                                    Toast.makeText(this,
                                            getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("SignUserIn", "error1 " + e.getMessage() + "");
                            runOnUiThread(() -> {

                                MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                progressBar_purchasesArchive.setVisibility(View.INVISIBLE);
                                Toast.makeText(this,
                                        getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_purchasesArchive.setVisibility(View.INVISIBLE);
                        Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                        Toast.makeText(this,
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


    private void PaginateInvoices() {
        btnPaginateInvoice_arrowRight.setOnClickListener(view -> {

            if (selectedInvoiceIndexInArrayList == purchasesArchiveInvoicesArrayList_ToDisplay.size() - 1) {

                Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String3), Toast.LENGTH_SHORT).show();
            } else {

                selectedInvoiceIndexInArrayList++;
                InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                if (invoiceModel.getInvoiceStatusOverall() != null)
                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                        (this, R.layout.purchases_archive_recycler_item, invoiceModel.getShopInvoiceArrayList());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);
            }
        });


        btnPaginateInvoice_arrowLeft.setOnClickListener(view -> {

            if (selectedInvoiceIndexInArrayList == 0) {
                Toast.makeText(this, getString(R.string.PurchasesRequestsActivity_String2), Toast.LENGTH_SHORT).show();
            } else {

                selectedInvoiceIndexInArrayList--;
                InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                if (invoiceModel.getInvoiceStatusOverall() != null)
                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                        (this, R.layout.purchases_archive_recycler_item, invoiceModel.getShopInvoiceArrayList());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);
            }
        });
    }


    private void GetPurchasesArchive() {

        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_purchasesArchive.setVisibility(View.VISIBLE);

        purchasesArchiveInvoicesArrayList.clear();
        purchasesArchiveInvoicesArrayList_ToDisplay.clear();
        recyclerPurchasesArchive.setAdapter(null);


        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
                int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetCustomerPurchasesArchive + customerId)
                        .get()
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getCustomerPurchasesArchive", "on background");

                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("getCustomerPurchasesArchive", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int invoiceId = jsonObject.getInt("id");
                                String createdAt = jsonObject.getString("createdAt");
                                try {
                                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"  , Locale.ENGLISH);
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

                                purchasesArchiveInvoicesArrayList.add(new InvoiceModel(invoiceId, createdAt, invoiceTotalCost,
                                        invoiceTotalPaidAmount, invoiceTotalItems, invoiceStatusOverall, shopInvoiceArrayList));

                                purchasesArchiveInvoicesArrayList_ToDisplay.add(new InvoiceModel(invoiceId, createdAt, invoiceTotalCost,
                                        invoiceTotalPaidAmount, invoiceTotalItems, invoiceStatusOverall, shopInvoiceArrayList));
                            }

                            if (purchasesArchiveInvoicesArrayList_ToDisplay.size() == 0) {
                                Toast.makeText(this, getString(R.string.GlobalMessage__NoInvoices), Toast.LENGTH_SHORT).show();
                            } else {
                                InvoiceModel invoiceModel = purchasesArchiveInvoicesArrayList_ToDisplay.get(selectedInvoiceIndexInArrayList);
                                txtInvoiceDate.setText(invoiceModel.getCreatedAt());
                                txtInvoiceTotalCost.setText(String.valueOf(invoiceModel.getInvoiceTotalCost()));
                                txtInvoiceTotalItems.setText(String.valueOf(invoiceModel.getInvoiceTotalItems()));
                                txtInvoiceTotalPaidAmount.setText(String.valueOf(invoiceModel.getInvoiceTotalPaidAmount()));
                                txtInvoiceId.setText(String.valueOf(invoiceModel.getInvoiceId()));
                                if (invoiceModel.getInvoiceStatusOverall() != null)
                                    txtInvoiceStatusOverall.setText(invoiceModel.getInvoiceStatusOverall());

                                PurchasesArchiveRecyclerAdapter purchasesArchiveRecyclerAdapter = new PurchasesArchiveRecyclerAdapter
                                        (this, R.layout.purchases_archive_recycler_item, invoiceModel.getShopInvoiceArrayList());
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerPurchasesArchive.setLayoutManager(mLayoutManager);
                                recyclerPurchasesArchive.setItemAnimator(new DefaultItemAnimator());
                                recyclerPurchasesArchive.setAdapter(purchasesArchiveRecyclerAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("getCustomerPurchasesArchive", e.getMessage());
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                        }
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_purchasesArchive.setVisibility(View.INVISIBLE);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getCustomerPurchasesArchive", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }
}