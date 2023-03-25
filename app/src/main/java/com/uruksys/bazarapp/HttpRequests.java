package com.uruksys.bazarapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequests {


    public enum HttpRequestsRoutes {
        UpdateCustomerToken("/updateCustomerToken"),
        SignInCustomer("/signInCustomer"),
        GetCategories("/getCategories"),
        GetBestSellingItems("/getBestSellingItems"),
        GetNewlyAddedItems("/getNewlyAddedItems"),
        GetTrendingItems("/getTrendingItems"),
        GetItemsInDiscount("/getItemsInDiscount"),
        GetBrands("/getBrands"),
        GetShopsForCustomer("/getShopsForCustomer"),
        GetItemsBySearchKeyword("/getItemsBySearchKeyword?itemName="),
        GetItemsByCategory("/getItemsByCategory?categoryId="),
        GetInvoiceShopChat("/getInvoiceShopChat?id="),
        AddNewMsgToChat("/addNewMsgToChat"),
        AddNewReview("/addNewReview"),
        GetOneItemDetailsForCustomer("/getOneItemDetailsForCustomer?id="),
        GetItemReviews("/getItemReviews?itemId="),
        GetItemsInInvoiceShop("/getItemsInInvoiceShop?invoiceShopId="),
        GetCustomerPurchasesArchive("/getCustomerPurchasesArchive?customerId="),
        GetCustomerPurchasesReqs("/getCustomerPurchasesReqs?customerId="),
        RegisterNewCustomer("/registerNewCustomer"),
        UpdateAccountInfo("/updateAccountInfo"),
        UpdateAccountPassword("/updateAccountPassword"),
        GetCustomerById("/getCustomerById?id="),
        NewPurchaseRequest("/newPurchaseRequest"),
        GetItemsDetailsInCart("/getItemsDetailsInCart");

        private String stringValue;

        HttpRequestsRoutes(String toString) {
            stringValue = toString;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    ;

    public static String SignUserIn(JSONObject jsonObject) {

        RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(MainActivity.serverIp_NodeJs + HttpRequestsRoutes.SignInCustomer)
                .post(requestBody)
                .build();

        String s = null;
        try {
            Response response = MainActivity.client.newCall(request).execute();

            s = response.body().string();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SignUserIn", "error1 " + e.getMessage() + "");
            return null;
        }
    }


    public static void UpdateCustomerToken(int customerId, String token) {
        new Thread(() -> {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customerId", customerId);
                jsonObject.put("token", token);

                RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + HttpRequestsRoutes.UpdateCustomerToken)
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("newToken", "on background");


                    String finalS = s;
                    Log.d("newToken", finalS);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("newToken", e.getMessage());
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }).start();
    }
}
