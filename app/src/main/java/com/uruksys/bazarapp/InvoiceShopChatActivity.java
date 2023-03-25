package com.uruksys.bazarapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InvoiceShopChatActivity extends AppCompatActivity {

    ProgressBar progressBar_MessagingSupportActivity;
    RecyclerView recyclerMessagingSupport;
    ImageView imgFinishThisActivity;
    EditText etxtMessageBody;
    Button btnSendMessage;

    ArrayList<InvoiceShopChatModel> invoiceSupportChatArrayList = new ArrayList<>();

    public static int CurrentMessaging_invoiceShopId = -1, CurrentMessaging_shopId = -1;

    private BroadcastReceiver MessengerNewMsgReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_shop_chat);

        progressBar_MessagingSupportActivity = findViewById(R.id.progressBar_MessagingSupportActivity);
        recyclerMessagingSupport = findViewById(R.id.recyclerMessagingSupport);
        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        etxtMessageBody = findViewById(R.id.etxtMessageBody);


        Intent intent = getIntent();
        CurrentMessaging_invoiceShopId = intent.getIntExtra("invoiceShopId", -1);
        CurrentMessaging_shopId = intent.getIntExtra("shopId", -1);

        imgFinishThisActivity.setOnClickListener(view -> finish());
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")) {

            imgFinishThisActivity.setScaleX(-1);
        }

        BtnSendMessageClicked();

        ReceiveBroadcastNewMsg();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                MessengerNewMsgReceiver, new IntentFilter("receivedToMessenger"));
    }


    //receive broadcast that new msg received
    private void ReceiveBroadcastNewMsg() {
        MessengerNewMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    String data = intent.getStringExtra("data");
                    JSONObject jsonObject = new JSONObject(data);

                    if (jsonObject.getInt("invoiceShopId") == CurrentMessaging_invoiceShopId) {

                        GetShopSupportMessaging();
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }

            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();

        GetShopSupportMessaging();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        CurrentMessaging_invoiceShopId = -1;
        CurrentMessaging_shopId = -1;
        Log.d("lifeCycleChecking", "onDestroy MessagingSupportActivity");
    }

    private void GetShopSupportMessaging() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
        progressBar_MessagingSupportActivity.setVisibility(View.VISIBLE);

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetInvoiceShopChat + CurrentMessaging_invoiceShopId)
                        .get()
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("supportMessaging", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("supportMessaging", finalS);
                        try {

                            invoiceSupportChatArrayList.clear();
                            recyclerMessagingSupport.setAdapter(null);
                            JSONArray jsonArray = new JSONArray(finalS);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                int invoiceShopId = jsonObject.getInt("invoiceShopId");
                                String createdAt = jsonObject.getString("createdAt");
                                try {
                                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                                    Date newDate = isoFormat.parse(createdAt);
                                    createdAt = dateFormat.format(newDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String msgBody = jsonObject.getString("msgBody");
                                int isRead = jsonObject.getInt("isRead");
                                String sender = jsonObject.getString("sender");


                                invoiceSupportChatArrayList.add(new InvoiceShopChatModel(id, invoiceShopId, msgBody, sender, createdAt, isRead));
                            }

                            MessagingSupportRecyclerAdapter messagingSupportRecyclerAdapter = new MessagingSupportRecyclerAdapter
                                    (this, R.layout.messaging_support_recycler_item, invoiceSupportChatArrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerMessagingSupport.setLayoutManager(mLayoutManager);
                            recyclerMessagingSupport.setItemAnimator(new DefaultItemAnimator());
                            recyclerMessagingSupport.setAdapter(messagingSupportRecyclerAdapter);

                            if (messagingSupportRecyclerAdapter.getItemCount() > 1)
                                recyclerMessagingSupport.scrollToPosition(messagingSupportRecyclerAdapter.getItemCount() - 1);

//                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("supportMessaging", e.getMessage());
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("supportMessaging", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void BtnSendMessageClicked() {
        btnSendMessage.setOnClickListener(view -> {
            if (!etxtMessageBody.getText().toString().trim().equals("")) {

//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBar_MessagingSupportActivity.setVisibility(View.VISIBLE);


                JSONObject jsonObject = new JSONObject();

                SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
                int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);
                String customerName = sharedPreferences.getString(MainActivity.sharedPreferences_CustomerName, null);

                if (customerId != 0) {
                    try {
                        final String sentMsgBody = etxtMessageBody.getText().toString().trim();
                        jsonObject.put("msgBody", sentMsgBody);
                        jsonObject.put("shopId", CurrentMessaging_shopId);
                        jsonObject.put("invoiceShopId", CurrentMessaging_invoiceShopId);
                        jsonObject.put("sender", MainActivity.CHAT_SENDER.customer.toString());
                        jsonObject.put("senderId", customerId);
                        jsonObject.put("senderName", customerName);

                        new Thread(() -> {

                            RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));

                            // do background stuff here
                            Request request = new Request.Builder()
                                    .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.AddNewMsgToChat)
                                    .post(requestBody)
                                    .build();

                            Response response = null;

                            String s = null;
                            try {

                                response = MainActivity.client.newCall(request).execute();
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                s = response.body().string();

                                Log.d("hello", "on background");


                                String finalS = s;
                                runOnUiThread(() -> {
                                    MainActivity.mSocket.emit(MainActivity.CHAT_SOCKET_MESSAGES.SEND_MESSAGE.toString(), jsonObject);

                                    Log.d("MessagingSupportActivity", finalS);

                                    JSONArray jsonArray = null;
                                    try {
                                        jsonArray = new JSONArray(finalS);

                                        invoiceSupportChatArrayList.clear();
                                        recyclerMessagingSupport.setAdapter(null);

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                            int id = jsonObject2.getInt("id");
                                            int isRead = jsonObject2.getInt("isRead");
                                            String createdAt = jsonObject2.getString("createdAt");
                                            try {
                                                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                                                Date newDate = isoFormat.parse(createdAt);
                                                createdAt = dateFormat.format(newDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            String msgBody = jsonObject2.getString("msgBody");
                                            String sender = jsonObject2.getString("sender");

                                            invoiceSupportChatArrayList.add(new InvoiceShopChatModel(id, CurrentMessaging_invoiceShopId, msgBody, sender, createdAt, isRead));
                                        }

                                        MessagingSupportRecyclerAdapter messagingSupportRecyclerAdapter = new MessagingSupportRecyclerAdapter
                                                (InvoiceShopChatActivity.this, R.layout.messaging_support_recycler_item, invoiceSupportChatArrayList);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        recyclerMessagingSupport.setLayoutManager(mLayoutManager);
                                        recyclerMessagingSupport.setItemAnimator(new DefaultItemAnimator());
                                        recyclerMessagingSupport.setAdapter(messagingSupportRecyclerAdapter);

                                        recyclerMessagingSupport.smoothScrollToPosition(messagingSupportRecyclerAdapter.getItemCount() - 1);
                                        etxtMessageBody.setText("");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, getString(R.string.String1_MessagingSupportActivity), Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("hello", e.getMessage());
                                runOnUiThread(() -> {

                                    Toast.makeText(this, getString(R.string.String1_MessagingSupportActivity), Toast.LENGTH_SHORT).show();
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                                });
                            }
                        }).start();
                    } catch (JSONException e) {
                        e.printStackTrace();

//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                        Toast.makeText(InvoiceShopChatActivity.this, getString(R.string.String1_MessagingSupportActivity), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SignUserIn();
                }
            }
        });
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

        txtRegisterNewCustomer.setVisibility(View.INVISIBLE);
//        txtRegisterNewCustomer.setOnClickListener(view -> {

//            alertDialog.dismiss();
//            Intent intent = new Intent(this, RegisterCustomerActivity.class);
//            startActivity(intent);

//        });

        btnSignIn.setOnClickListener((View v) -> {
            if (etxtUsername.getText().toString().trim().equals("") || etxtPassword.getText().toString().trim().equals("")) {

                Toast.makeText(this, getString(R.string.GlobalMessage_EnterUsernameAndPassword), Toast.LENGTH_SHORT).show();
            } else {


                MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBar_MessagingSupportActivity.setVisibility(View.VISIBLE);
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
                                        progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
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
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();

                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
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
                                progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                                Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                                Toast.makeText(this,
                                        getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBar_MessagingSupportActivity.setVisibility(View.INVISIBLE);
                        Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                        Toast.makeText(this,
                                getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        });
    }
}