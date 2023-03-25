package com.uruksys.bazarapp_merchants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessagingCustomerForInvoiceActivity extends AppCompatActivity {

    ProgressBar progressBar_MessagingCustomerForInvoiceActivity;
    RecyclerView recyclerMessagingCustomer;
    EditText etxtMessageBody;
    Button btnSendMessage;

    ArrayList<CustomerMessagingModel> customerMessagingArrayList = new ArrayList<>();

    int invoice;

    private BroadcastReceiver MessengerNewMsgReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_customer_for_invoice);

        progressBar_MessagingCustomerForInvoiceActivity = findViewById(R.id.progressBar_MessagingCustomerForInvoiceActivity);
        recyclerMessagingCustomer = findViewById(R.id.recyclerMessagingCustomer);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        etxtMessageBody = findViewById(R.id.etxtMessageBody);

        Intent intent = getIntent();
        invoice = intent.getIntExtra("invoice", 0);
        if(intent.getBooleanExtra("deliveredInvoice" , false)){

            btnSendMessage.setVisibility(View.INVISIBLE);
            etxtMessageBody.setVisibility(View.INVISIBLE);
        }

        Log.d("MessagingCustomerForInvoiceActivity", "invoice " + invoice + "");

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

                GetSupportMessaging();
            }
        };
    }




    @Override
    protected void onStart() {
        super.onStart();

        GetSupportMessaging();
    }


    private void GetSupportMessaging() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.VISIBLE);

        customerMessagingArrayList.clear();
        recyclerMessagingCustomer.setAdapter(null);

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("invoice", invoice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AtomicBoolean replayThread = new AtomicBoolean(false);

        do {
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject1));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getCustomerMessagingForInvoice.php")
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = LoginActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("customerMessaging", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("customerMessaging", finalS);
                        try {
                            JSONArray jsonArray = new JSONArray(finalS);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);


                                int id = jsonObject2.getInt("id");
                                int invoice = jsonObject2.getInt("invoice");
                                String messageDate = jsonObject2.getString("messageDate");
                                String messageBody = jsonObject2.getString("messageBody");
                                String sender = jsonObject2.getString("sender");


                                customerMessagingArrayList.add(new CustomerMessagingModel(id, invoice, messageDate, messageBody, sender));
                            }

                            MessagingCustomerRecyclerAdapter messagingCustomerRecyclerAdapter = new MessagingCustomerRecyclerAdapter
                                    (this, R.layout.messaging_customer_recycler_item, customerMessagingArrayList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerMessagingCustomer.setLayoutManager(mLayoutManager);
                            recyclerMessagingCustomer.setItemAnimator(new DefaultItemAnimator());
                            recyclerMessagingCustomer.setAdapter(messagingCustomerRecyclerAdapter);

                            if(messagingCustomerRecyclerAdapter.getItemCount() > 1)
                                recyclerMessagingCustomer.smoothScrollToPosition(messagingCustomerRecyclerAdapter.getItemCount() - 1);

                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("customerMessaging", e.getMessage());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("customerMessaging", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }


    private void BtnSendMessageClicked() {
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etxtMessageBody.getText().toString().trim().equals("")) {

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.VISIBLE);


                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String messageDateStr = sdf.format(new Date());

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("messageBody", etxtMessageBody.getText().toString().trim());
                        jsonObject.put("messageDate", messageDateStr);
                        jsonObject.put("invoice", invoice);
                        jsonObject.put("sender", "support");

                        new Thread(() -> {

                            Log.d("hello", jsonObject.toString());
                            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                            // do background stuff here
                            Request request = new Request.Builder()
                                    .url(LoginActivity.serverIp + "/sendNewMessage.php")
                                    .post(requestBody)
                                    .build();

                            Response response = null;

                            String s = null;
                            try {

                                response = LoginActivity.client.newCall(request).execute();
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                s = response.body().string();

                                Log.d("customerMessaging", "on background");

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("customerMessaging", e.getMessage());
                            }


                            String finalS = s;
                            runOnUiThread(() -> {
                                Log.d("customerMessaging", finalS);

                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(finalS);

                                    customerMessagingArrayList.clear();
                                    recyclerMessagingCustomer.setAdapter(null);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                                        int id = jsonObject2.getInt("id");
                                        int invoice = jsonObject2.getInt("invoice");
                                        String messageDate = jsonObject2.getString("messageDate");
                                        String messageBody = jsonObject2.getString("messageBody");
                                        String sender = jsonObject2.getString("sender");

                                        customerMessagingArrayList.add(new CustomerMessagingModel(id, invoice, messageDate, messageBody, sender));
                                    }
                                    Log.d("customerMessaging", "supportMessagingArrayList count = " + customerMessagingArrayList.size());

                                    MessagingCustomerRecyclerAdapter messagingCustomerRecyclerAdapter = new MessagingCustomerRecyclerAdapter
                                            (MessagingCustomerForInvoiceActivity.this,
                                                    R.layout.messaging_customer_recycler_item,
                                                    customerMessagingArrayList);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerMessagingCustomer.setLayoutManager(mLayoutManager);
                                    recyclerMessagingCustomer.setItemAnimator(new DefaultItemAnimator());
                                    recyclerMessagingCustomer.setAdapter(messagingCustomerRecyclerAdapter);

                                    recyclerMessagingCustomer.smoothScrollToPosition(messagingCustomerRecyclerAdapter.getItemCount() - 1);
                                    etxtMessageBody.setText("");


                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.INVISIBLE);

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.INVISIBLE);

                                    Toast.makeText(MessagingCustomerForInvoiceActivity.this, "فشل في الارسال", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }).start();
                    } catch (JSONException e) {
                        e.printStackTrace();

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar_MessagingCustomerForInvoiceActivity.setVisibility(View.INVISIBLE);

                        Toast.makeText(MessagingCustomerForInvoiceActivity.this, "فشل في الارسال", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}



class CustomerMessagingModel {

    private int id, invoice;
    private String messageDate;
    private String messageBody, sender;

    public CustomerMessagingModel(int id, int invoice, String messageDate, String messageBody, String sender) {
        this.id = id;
        this.invoice = invoice;
        this.messageDate = messageDate;
        this.messageBody = messageBody;
        this.sender = sender;
    }


    public int getId() {
        return id;
    }

    public int getInvoice() {
        return invoice;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getSender() {
        return sender;
    }
}