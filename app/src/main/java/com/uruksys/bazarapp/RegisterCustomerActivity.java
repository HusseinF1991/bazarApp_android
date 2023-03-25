package com.uruksys.bazarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.uruksys.bazarapp.MainActivity.sharedPreferencesName;

public class RegisterCustomerActivity extends AppCompatActivity {
    ImageView imgFinishThisActivity;
    EditText etxtMobile, etxtEmail, etxtCustomerName, etxtRegion, etxtUsername, etxtPassword, etxtConfirmPassword;
    Spinner spinnerProvince, spinnerGender;
    Button btnRegister, btnBirthDate;
    ProgressBar registerCustomerProgressBar;

    private GoogleApiClient googleApiClient;

    private FusedLocationProviderClient fusedLocationClient;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etxtMobile = findViewById(R.id.etxtMobile);
        etxtEmail = findViewById(R.id.etxtEmail);
        etxtCustomerName = findViewById(R.id.etxtCustomerName);
        etxtRegion = findViewById(R.id.etxtRegion);
        etxtUsername = findViewById(R.id.etxtUsername);
        etxtPassword = findViewById(R.id.etxtPassword);
        etxtConfirmPassword = findViewById(R.id.etxtConfirmPassword);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnBirthDate = findViewById(R.id.btnBirthDate);
        registerCustomerProgressBar = findViewById(R.id.registerCustomerProgressBar);


        PopulateIraqiProvinces();
        PopulateGenderSpinner();
        BtnBirthDateClicked();
        BtnRegisterClicked();
    }


    private void PopulateIraqiProvinces() {

        ArrayList<String> iraqProvinces = new ArrayList<>(Arrays.asList(
                getString(R.string.ChooseGovernorate),
                getString(R.string.IraqProvince_Baghdad),
                getString(R.string.IraqProvince_Basrah),
                getString(R.string.IraqProvince_Nineveh),
                getString(R.string.IraqProvince_Diwaniyah),
                getString(R.string.IraqProvince_Babel),
                getString(R.string.IraqProvince_Dhi_Qar),
                getString(R.string.IraqProvince_Najaf),
                getString(R.string.IraqProvince_Karbala),
                getString(R.string.IraqProvince_Wasit),
                getString(R.string.IraqProvince_Muthana),
                getString(R.string.IraqProvince_Anbar),
                getString(R.string.IraqProvince_Salah_Aldeen),
                getString(R.string.IraqProvince_Diyala),
                getString(R.string.IraqProvince_Sulaymaniyah),
                getString(R.string.IraqProvince_Dhok),
                getString(R.string.IraqProvince_Erbil),
                getString(R.string.IraqProvince_Missan),
                getString(R.string.IraqProvince_Kirkok)));

        spinnerProvince.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout_item, iraqProvinces) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView TV = (TextView) view;
                if (position == 0) {
                    TV.setTextColor(Color.GRAY);
                } else {
                    TV.setTextColor(Color.RED);
                }
                return view;
            }
        });
    }


    private void PopulateGenderSpinner() {

        ArrayList<String> genderList = new ArrayList<>(Arrays.asList(
                getString(R.string.ChooseGender),
                getString(R.string.Gender_Male),
                getString(R.string.Gender_Female)));

        spinnerGender.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout_item, genderList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView TV = (TextView) view;
                if (position == 0) {
                    TV.setTextColor(Color.GRAY);
                } else {
                    TV.setTextColor(Color.RED);
                }
                return view;
            }
        });
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


    private void BtnRegisterClicked() {
        btnRegister.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
            String lat = sharedPreferences.getString(MainActivity.sharedPreferences_Latitude, null);

            if (etxtUsername.getText().toString().trim().equals("") ||
                    etxtCustomerName.getText().toString().trim().equals("") ||
                    etxtMobile.getText().toString().trim().equals("") ||
                    etxtPassword.getText().toString().trim().equals("") ||
                    etxtConfirmPassword.getText().toString().trim().equals("")) {

                Toast.makeText(this, getString(R.string.RegisterCustomerActivity_String1), Toast.LENGTH_LONG).show();
            } else if (etxtPassword.getText().toString().trim().length() < 6) {

                Toast.makeText(this, getString(R.string.GlobalMessage_PasswordAtLeast6Characters), Toast.LENGTH_LONG).show();
            } else if (!etxtPassword.getText().toString().trim().equals(etxtConfirmPassword.getText().toString().trim())) {

                Toast.makeText(this, getString(R.string.GlobalMessage_EnterPasswordTwiceIdentitcal), Toast.LENGTH_LONG).show();
            } else if (spinnerProvince.getSelectedItem().toString().equals(getString(R.string.ChooseGovernorate))) {

                Toast.makeText(this, getString(R.string.GlobalMessage_SelectProvince), Toast.LENGTH_LONG).show();
            } else if (lat == null) {

                TurnGPS_On();
            } else {

                disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                registerCustomerProgressBar.setVisibility(View.VISIBLE);
                RegisterUser();
            }
        });
    }


    private void RegisterUser() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String token = sharedPreferences.getString(MainActivity.sharedPreferences_Token, null);
            String lat = sharedPreferences.getString(MainActivity.sharedPreferences_Latitude, null);
            String lng = sharedPreferences.getString(MainActivity.sharedPreferences_Longitude, null);

            jsonObject1.put("username", etxtUsername.getText().toString().trim());
            jsonObject1.put("password", etxtPassword.getText().toString().trim());
            jsonObject1.put("name", etxtCustomerName.getText().toString().trim());
            Resources resources = MainActivity.getLocalizedResources(this);
            if (spinnerGender.getSelectedItem().toString().equals(getString(R.string.Gender_Male))) {
                jsonObject1.put("gender", resources.getString(R.string.Gender_Male));
            } else if (spinnerGender.getSelectedItem().toString().equals(getString(R.string.Gender_Female))) {

                jsonObject1.put("gender", resources.getString(R.string.Gender_Female));
            } else {
                jsonObject1.put("gender", null);
            }

            jsonObject1.put("birthDate",
                    !btnBirthDate.getText().toString().equals(getString(R.string.GlobalMessage_SelectBirthDate)) ?
                            btnBirthDate.getText().toString() : null);
            jsonObject1.put("token", token);
            jsonObject1.put("lat", lat);
            jsonObject1.put("lng", lng);
            jsonObject1.put("province", MainActivity.SetSelectedProvinceInJson(this, spinnerProvince.getSelectedItem().toString()));
            jsonObject1.put("region", etxtRegion.getText().toString().trim());
            jsonObject1.put("mobile", etxtMobile.getText().toString());
            jsonObject1.put("email", etxtEmail.getText().toString().trim());
            jsonObject1.put("appLanguage", Locale.getDefault().getLanguage());

            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject1));

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.RegisterNewCustomer)
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("RegisterNewCustomer", "on background");


                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("RegisterNewCustomer", finalS);
                        try {
                            JSONObject jsonObject = new JSONObject(finalS);

                            if (jsonObject.has("err")) {
                                if (jsonObject.getString("err").equals("usernameDuplicated")) {

                                    Toast.makeText(this, getString(R.string.RegisterCustomerActivity_String4), Toast.LENGTH_LONG).show();
                                }
                            } else {

                                editor.putString(MainActivity.sharedPreferences_Username, jsonObject.getString("username"));
                                editor.putString(MainActivity.sharedPreferences_Password, jsonObject.getString("password"));
                                editor.putString(MainActivity.sharedPreferences_CustomerName, jsonObject.getString("name"));
                                editor.putInt(MainActivity.sharedPreferences_CustomerId, jsonObject.getInt("id"));
                                editor.putString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage());
                                editor.apply();
                                Toast.makeText(RegisterCustomerActivity.this, getString(R.string.RegisterCustomerActivity_String5), Toast.LENGTH_SHORT).show();

                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("RegisterNewCustomer", e.getMessage());
                            Toast.makeText(this, getString(R.string.RegisterCustomerActivity_String6), Toast.LENGTH_SHORT).show();
                        }

                        disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        registerCustomerProgressBar.setVisibility(View.INVISIBLE);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("RegisterNewCustomer", e.getMessage());

                    runOnUiThread(() -> {
                        disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        registerCustomerProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterCustomerActivity.this, getString(R.string.RegisterCustomerActivity_String6), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (JSONException e) {
            e.printStackTrace();
            disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
            registerCustomerProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterCustomerActivity.this, getString(R.string.RegisterCustomerActivity_String6), Toast.LENGTH_SHORT).show();
        }
    }


    private void BtnBirthDateClicked() {
        btnBirthDate.setOnClickListener(view -> {
            int month = Integer.parseInt((String) DateFormat.format("MM", new Date().getTime())) - 1;
            int day = Integer.parseInt((String) DateFormat.format("dd", new Date().getTime()));
            int year = Integer.parseInt((String) DateFormat.format("yyyy", new Date().getTime()));
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                    String fromDateStr = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                    btnBirthDate.setText(fromDateStr);
                }
            }, year, month, day);
            datePickerDialog.show();
        });
    }


    //promote the user to turn on GPS (if closed) on the activity created
    private void TurnGPS_On() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();

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
                            status.startResolutionForResult(RegisterCustomerActivity.this, 1000);

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
                ActivityCompat.requestPermissions(RegisterCustomerActivity.this
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
                                SharedPreferences sharedPref = RegisterCustomerActivity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
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

    //if user didn't grant the application access to the GPS then close the application
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