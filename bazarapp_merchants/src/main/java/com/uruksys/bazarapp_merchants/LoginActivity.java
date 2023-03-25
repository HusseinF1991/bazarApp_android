package com.uruksys.bazarapp_merchants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etxtProviderName, etxtProviderPassword;
    Button btnLogin;
    ProgressBar progressBarLoginActivity;


    public static String providerTitle, providerPassword, logo, mobile, addressCity, addressRegion, shopName;


    public static String serverIp = "http://192.168.1.133/bazarApp/bazarApp_merchants";
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();

    public static final int MY_CAMERA_PERMISSION_CODE_IMAGE = 312;
    public static final int MY_GALLERY_PERMISSION_CODE_IMAGE = 313;
    public static final int MY_PHONE_CALL_PERMISSION_CODE_IMAGE = 314;
    public static final int CAMERA_REQUEST_IMAGE = 212;
    public static final int GALLERY_REQUEST_IMAGE = 213;
    public static final int CAMERA_REQUEST_IMAGE2 = 214;
    public static final int GALLERY_REQUEST_IMAGE2 = 215;
    public static final int CAMERA_REQUEST_IMAGE3 = 216;
    public static final int GALLERY_REQUEST_IMAGE3 = 217;
    public static final int CAMERA_REQUEST_IMAGE4 = 218;
    public static final int GALLERY_REQUEST_IMAGE4 = 219;


    public static String sharedPreferencesName = "BazarAppMerchantSharedPreferences";
    public static String sharedPreferences_Token = "BazarAppMerchantTokenSharedPreferences";
    public static String sharedPreferences_Username = "BazarAppMerchantUserNameSharedPreferences";
    public static String sharedPreferences_password = "BazarAppMerchantPasswordSharedPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etxtProviderName = findViewById(R.id.etxtProviderName);
        etxtProviderPassword = findViewById(R.id.etxtProviderPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBarLoginActivity = findViewById(R.id.progressBarLoginActivity);


        LoginBtnPressed();

        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        boolean alreadyLoggedIn = sharedPref.getBoolean("BazarAppMerchantLoggedInSharedPreferences", false);
        if (alreadyLoggedIn) {

            etxtProviderName.setVisibility(View.INVISIBLE);
            etxtProviderPassword.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            progressBarLoginActivity.setVisibility(View.VISIBLE);

            String userName = sharedPref.getString(sharedPreferences_Username, "");
            String password = sharedPref.getString(sharedPreferences_password, "");
            CheckUserValidity(userName , password);

        }
    }




    private void CheckUserValidity(String username ,String password){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {

            RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(serverIp + "/loginMerchant.php")
                    .post(requestBody)
                    .build();

            String s = null;
            try {
                Response response = client.newCall(request).execute();

                s = response.body().string();

                String finalS = s;
                runOnUiThread(() -> {

                    Log.d("Login", finalS);
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(finalS);
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        logo = jsonObject1.getString("logo");
                        providerTitle = jsonObject1.getString("providerTitle");
                        providerPassword = jsonObject1.getString("providerPassword");
                        mobile = jsonObject1.getString("mobile");
                        addressCity = jsonObject1.getString("addressCity");
                        addressRegion = jsonObject1.getString("addressRegion");
                        shopName = jsonObject1.getString("shopName");


                        finish();
                        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Wrong username or password!!!", Toast.LENGTH_SHORT).show();
                        progressBarLoginActivity.setVisibility(View.INVISIBLE);


                        etxtProviderName.setVisibility(View.VISIBLE);
                        etxtProviderPassword.setVisibility(View.VISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);

                        SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("BazarAppMerchantLoggedInSharedPreferences", false);
                        editor.apply();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Login", e.getMessage());
                runOnUiThread(() -> {

                    progressBarLoginActivity.setVisibility(View.INVISIBLE);

                    etxtProviderName.setVisibility(View.VISIBLE);
                    etxtProviderPassword.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);

                    SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("BazarAppMerchantLoggedInSharedPreferences", false);
                    editor.apply();
                });
            }
        }).start();
    }




    private void LoginBtnPressed() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtProviderName.getText().toString().trim().equals("") || etxtProviderPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "wrong username or password!!!", Toast.LENGTH_SHORT).show();
                } else {

                    progressBarLoginActivity.setVisibility(View.VISIBLE);
                    btnLogin.setEnabled(false);
                    etxtProviderName.setEnabled(false);
                    etxtProviderPassword.setEnabled(false);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", etxtProviderName.getText().toString());
                        jsonObject.put("password", etxtProviderPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new Thread(() -> {

                        RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                        Request request = new Request.Builder()
                                .url(serverIp + "/loginMerchant.php")
                                .post(requestBody)
                                .build();

                        String s = null;
                        try {
                            Response response = client.newCall(request).execute();

                            s = response.body().string();

                            String finalS = s;
                            runOnUiThread(() -> {

                                Log.d("Login", finalS);
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(finalS);
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                    logo = jsonObject1.getString("logo");
                                    providerTitle = jsonObject1.getString("providerTitle");
                                    providerPassword = jsonObject1.getString("providerPassword");
                                    mobile = jsonObject1.getString("mobile");
                                    addressCity = jsonObject1.getString("addressCity");
                                    addressRegion = jsonObject1.getString("addressRegion");
                                    shopName = jsonObject1.getString("shopName");

                                    SharedPreferences sharedPref = LoginActivity.this.getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("BazarAppMerchantLoggedInSharedPreferences", true);
                                    editor.putString("BazarAppMerchantUserNameSharedPreferences", etxtProviderName.getText().toString());
                                    editor.putString("BazarAppMerchantPasswordSharedPreferences", etxtProviderPassword.getText().toString());
                                    editor.apply();

                                    finish();
                                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "Wrong username or password!!!", Toast.LENGTH_SHORT).show();
                                    progressBarLoginActivity.setVisibility(View.INVISIBLE);
                                    btnLogin.setEnabled(true);
                                    etxtProviderName.setEnabled(true);
                                    etxtProviderPassword.setEnabled(true);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Login", e.getMessage());
                            runOnUiThread(() -> {

                                progressBarLoginActivity.setVisibility(View.INVISIBLE);
                                btnLogin.setEnabled(true);
                                etxtProviderName.setEnabled(true);
                                etxtProviderPassword.setEnabled(true);
                            });
                        }
                    }).start();
                }
            }
        });
    }
}
