package com.uruksys.bazarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class SettingsActivity extends AppCompatActivity {

    ImageView imgFinishThisActivity;
    RadioButton rBtnEnglishLang, rBtnArabicLang, rBtnDarkTheme, rBtnLightTheme;
    EditText etxtFullName, etxtUserRegion, etxtMobile, etxtEmail;
    TextView txtUsername;
    Spinner spinnerGender, spinnerProvince;
    Button btnBirthDate, btnChangePassword, btnSignIn, btnSignUp, btnSignOut, btnCallSupport, btnMessagingSupport, btnSaveChanges;
    RelativeLayout rlUserSettings, rlRegistering;
    ProgressBar progressBarSettingsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener(view -> {
            finish();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        rBtnEnglishLang = findViewById(R.id.rBtnEnglishLang);
        rBtnArabicLang = findViewById(R.id.rBtnArabicLang);
        rBtnDarkTheme = (RadioButton) findViewById(R.id.rBtnDarkTheme);
        rBtnDarkTheme.setClickable(false);
        rBtnLightTheme = (RadioButton) findViewById(R.id.rBtnLightTheme);
        etxtFullName = findViewById(R.id.etxtFullName);
        etxtUserRegion = findViewById(R.id.etxtUserRegion);
        etxtMobile = findViewById(R.id.etxtMobile);
        etxtEmail = findViewById(R.id.etxtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        btnBirthDate = findViewById(R.id.btnBirthDate);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnCallSupport = findViewById(R.id.btnCallSupport);
        btnMessagingSupport = findViewById(R.id.btnMessagingSupport);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        rlUserSettings = findViewById(R.id.rlUserSettings);
        rlRegistering = findViewById(R.id.rlRegistering);
        progressBarSettingsActivity = findViewById(R.id.progressBarSettingsActivity);


        Log.d("SettingsActivity OnCreate : ", Locale.getDefault().getLanguage());
        OnBtnSignInClicked();
        OnBtnSignUpClicked();
        OnBtnSignOutClicked();
        OnBtnMessagingSupportClicked();
        OnBtnSaveChangesClicked();
        OnBtnChangePasswordClicked();
        OnBtnBirthDateClicked();

        PopulateProvincesAndGender();
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("lifeCycleChecking", "SettingsActivity > onStart");
        SetSettingsInActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("lifeCycleChecking", "SettingsActivity > onResume");
    }


    private void SetSettingsInActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
        int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);
        RelativeLayout.LayoutParams rlRegisteringParams = (RelativeLayout.LayoutParams) rlRegistering.getLayoutParams();
        RelativeLayout.LayoutParams rlUserSettingsParams = (RelativeLayout.LayoutParams) rlUserSettings.getLayoutParams();
        if (customerId != 0) {
            rlRegisteringParams.height = 0;
            rlRegistering.setLayoutParams(rlRegisteringParams);

            rlUserSettingsParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            rlUserSettings.setLayoutParams(rlUserSettingsParams);

            GetCustomerInfoFromDb();
        } else {

            rlRegisteringParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            rlRegistering.setLayoutParams(rlRegisteringParams);

            rlUserSettingsParams.height = 0;
            rlUserSettings.setLayoutParams(rlUserSettingsParams);
        }


        Log.d("SetSettingsInActivity", Locale.getDefault().getLanguage());
        if (sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("en")) {

            rBtnEnglishLang.setChecked(true);
        } else if (sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")) {

            rBtnArabicLang.setChecked(true);
            imgFinishThisActivity.setScaleX(-1);
        }
    }


    private void SetUserSettingsInActivity(JSONObject jsonObject) {

        try {
            etxtFullName.setText(jsonObject.getString("name"));
            etxtEmail.setText(jsonObject.getString("email"));
            etxtMobile.setText(jsonObject.getString("mobile"));
            etxtUserRegion.setText(jsonObject.getString("region"));
            txtUsername.setText(jsonObject.getString("username"));
            if (!jsonObject.getString("birthDate").equals("null"))
                btnBirthDate.setText(jsonObject.getString("birthDate"));
            Resources resources = MainActivity.getLocalizedResources(this);
            if (jsonObject.getString("gender").equals(resources.getString(R.string.Gender_Male))) {
                spinnerGender.setSelection(((ArrayAdapter<String>) spinnerGender.getAdapter()).getPosition(getString(R.string.Gender_Male)));
            } else if (jsonObject.getString("gender").equals(resources.getString(R.string.Gender_Female))) {

                spinnerGender.setSelection(((ArrayAdapter<String>) spinnerGender.getAdapter()).getPosition(getString(R.string.Gender_Female)));
            }

            Log.d("SetUserSettingsInActivity",
                    "customer province: " + MainActivity.SetSelectedProvinceInSpinner(this,
                            jsonObject.getString("province")));
            spinnerProvince.setSelection(
                    ((ArrayAdapter<String>) spinnerProvince.getAdapter()).
                            getPosition(
                                    MainActivity.SetSelectedProvinceInSpinner(this,
                                            jsonObject.getString("province"))));
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }


    private void PopulateProvincesAndGender() {
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


    private void OnBtnSignInClicked() {
        btnSignIn.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.sign_in_customer_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setView(view1);

            EditText etxtUsername = view1.findViewById(R.id.etxtUsername);
            EditText etxtPassword = view1.findViewById(R.id.etxtPassword);
            TextView txtRegisterNewCustomer = view1.findViewById(R.id.txtRegisterNewCustomer);
            txtRegisterNewCustomer.setVisibility(View.INVISIBLE);
            Button btnSignIn = view1.findViewById(R.id.btnSignIn);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            btnSignIn.setOnClickListener((View v) -> {
                if (etxtUsername.getText().toString().trim().equals("") || etxtPassword.getText().toString().trim().equals("")) {

                    Toast.makeText(this, getString(R.string.GlobalMessage_EnterUsernameAndPassword), Toast.LENGTH_SHORT).show();
                } else {


                    MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                    progressBarSettingsActivity.setVisibility(View.VISIBLE);
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
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

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
                                        } else {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            editor.putString(MainActivity.sharedPreferences_Username, jsonObject1.getString("username"));
                                            editor.putString(MainActivity.sharedPreferences_Password, jsonObject1.getString("password"));
                                            editor.putInt(MainActivity.sharedPreferences_CustomerId, jsonObject1.getInt("id"));
                                            String customerName = jsonObject1.getString("name");
                                            editor.putString(MainActivity.sharedPreferences_CustomerName, customerName);
                                            editor.putString(MainActivity.sharedPreferences_LocaleLanguage, jsonObject1.getString("appLanguage"));
                                            editor.apply();

                                            Toast.makeText(this,
                                                    getString(R.string.GlobalMessage_Welcome) + customerName,
                                                    Toast.LENGTH_SHORT).show();

                                            RelativeLayout.LayoutParams rlRegisteringParams = (RelativeLayout.LayoutParams) rlRegistering.getLayoutParams();
                                            RelativeLayout.LayoutParams rlUserSettingsParams = (RelativeLayout.LayoutParams) rlUserSettings.getLayoutParams();
                                            rlRegisteringParams.height = 0;
                                            rlRegistering.setLayoutParams(rlRegisteringParams);
                                            rlUserSettingsParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                                            rlUserSettings.setLayoutParams(rlUserSettingsParams);
                                            SetUserSettingsInActivity(jsonObject1);

                                            if (alertDialog.isShowing())
                                                alertDialog.dismiss();
                                        }
                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();

                                        Toast.makeText(this,
                                                getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("SignUserIn", "error1 " + e.getMessage() + "");
                                runOnUiThread(() -> {
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                                    Toast.makeText(this,
                                            getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                            progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                            Log.d("SignUserIn", "error2 " + e.getMessage() + "");
                            Toast.makeText(this,
                                    getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                }
            });
        });
    }


    private void OnBtnSignUpClicked() {
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterCustomerActivity.class);
            startActivity(intent);
        });
    }


    private void OnBtnSignOutClicked() {
        btnSignOut.setOnClickListener(view -> {

            SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(MainActivity.sharedPreferences_Username, null);
            editor.putString(MainActivity.sharedPreferences_Password, null);
            editor.putInt(MainActivity.sharedPreferences_CustomerId, 0);
            editor.putString(MainActivity.sharedPreferences_CustomerName, null);
//            editor.putString(MainActivity.sharedPreferences_LocaleLanguage, null);
            editor.apply();
//            finish();

            SetSettingsInActivity();
        });
    }


    private void OnBtnMessagingSupportClicked() {

    }


    private void OnBtnSaveChangesClicked() {
        btnSaveChanges.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
            String lat = sharedPreferences.getString(MainActivity.sharedPreferences_Latitude, null);

            if (etxtFullName.getText().toString().trim().equals("") || etxtMobile.getText().toString().trim().equals("")) {

                Toast.makeText(this, getString(R.string.SettingsActivity_String1), Toast.LENGTH_LONG).show();
            } else if (spinnerProvince.getSelectedItem().toString().equals(getString(R.string.ChooseGovernorate))) {

                Toast.makeText(this, getString(R.string.GlobalMessage_SelectProvince), Toast.LENGTH_LONG).show();
            } else {

                MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                progressBarSettingsActivity.setVisibility(View.VISIBLE);
                UpdateAccountInfo();
            }
        });
    }


    private void UpdateAccountInfo() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            jsonObject1.put("customerId", sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0));
            jsonObject1.put("name", etxtFullName.getText().toString().trim());
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
            jsonObject1.put("province", MainActivity.SetSelectedProvinceInJson(this, spinnerProvince.getSelectedItem().toString()));
            jsonObject1.put("region", etxtUserRegion.getText().toString().trim());
            jsonObject1.put("mobile", etxtMobile.getText().toString());
            jsonObject1.put("email", etxtEmail.getText().toString().trim());
            String appLanguage = null;
            if (rBtnArabicLang.isChecked()) appLanguage = "ar";
            else if (rBtnEnglishLang.isChecked()) appLanguage = "en";

            jsonObject1.put("appLanguage", appLanguage);

            String finalAppLanguage = appLanguage;
            new Thread(() -> {
                RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject1));

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.UpdateAccountInfo)
                        .post(requestBody)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("UpdateAccountInfo", "on background");

                    String finalS = s;
                    runOnUiThread(() -> {

                        Log.d("UpdateAccountInfo", finalS);
                        try {
                            JSONObject jsonObject = new JSONObject(finalS);

                            if (jsonObject.has("updated") && jsonObject.getInt("updated") > 0) {
                                editor.putString(MainActivity.sharedPreferences_CustomerName, etxtFullName.getText().toString().trim());
                                editor.putString(MainActivity.sharedPreferences_LocaleLanguage, finalAppLanguage);
                                editor.apply();
                                MainActivity.setLocale(this, finalAppLanguage);
                                Toast.makeText(this, getString(R.string.SettingsActivity_String2), Toast.LENGTH_SHORT).show();
//                                finish();
                                recreate();
                            } else {

                                Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("UpdateAccountInfo", e.getMessage());
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                        }

                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("UpdateAccountInfo", e.getMessage());

                    runOnUiThread(() -> {
                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                        progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (JSONException e) {
            e.printStackTrace();
            MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
            progressBarSettingsActivity.setVisibility(View.INVISIBLE);
            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
        }
    }


    private void OnBtnChangePasswordClicked() {
        btnChangePassword.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.change_account_password_layout, null);
            builder.setView(view);

            EditText etxtOldPassword = view.findViewById(R.id.etxtOldPassword);
            EditText etxtNewPassword = view.findViewById(R.id.etxtNewPassword);
            EditText etxtConfirmNewPassword = view.findViewById(R.id.etxtConfirmNewPassword);
            Button btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            btnUpdatePassword.setOnClickListener(view2 -> {
                SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
                String currentPassword = sharedPreferences.getString(MainActivity.sharedPreferences_Password, null);
                if (etxtNewPassword.getText().toString().trim().equals("") ||
                        etxtConfirmNewPassword.getText().toString().trim().equals("")) {

                    Toast.makeText(this, getString(R.string.SettingsActivity_String3), Toast.LENGTH_LONG).show();
                } else if (etxtNewPassword.getText().toString().trim().length() < 6) {

                    Toast.makeText(this, getString(R.string.GlobalMessage_PasswordAtLeast6Characters), Toast.LENGTH_LONG).show();
                } else if (!etxtNewPassword.getText().toString().trim().equals(etxtConfirmNewPassword.getText().toString().trim())) {

                    Toast.makeText(this, getString(R.string.GlobalMessage_EnterPasswordTwiceIdentitcal), Toast.LENGTH_LONG).show();
                } else if (!currentPassword.equals(etxtOldPassword.getText().toString())) {

                    Toast.makeText(this, getString(R.string.SettingsActivity_String5), Toast.LENGTH_LONG).show();
                } else {
                    //update password

                    final String newPassword = etxtNewPassword.getText().toString().trim();
                    MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                    progressBarSettingsActivity.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                    new Thread(() -> {

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("customerId", sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0));
                            jsonObject.put("newPassword", newPassword);

                            RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
                            Request request = new Request.Builder()
                                    .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.UpdateAccountPassword)
                                    .post(requestBody)
                                    .build();

                            Response response = null;

                            String s = null;
                            try {

                                response = MainActivity.client.newCall(request).execute();
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);

                                s = response.body().string();

                                Log.d("OnBtnChangePasswordClicked", "on background");

                                String finalS = s;
                                runOnUiThread(() -> {
                                    Log.d("OnBtnChangePasswordClicked", finalS);

                                    try {
                                        JSONObject jsonObject1 = new JSONObject(finalS);
                                        if (jsonObject1.has("updated") && jsonObject1.getInt("updated") > 0) {

                                            Toast.makeText(this, getString(R.string.SettingsActivity_String6), Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(MainActivity.sharedPreferences_Password, newPassword);
                                            editor.apply();
                                        } else {

                                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();
                                        Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("OnBtnChangePasswordClicked", e.getMessage());

                                runOnUiThread(() -> {
                                    Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                                });
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                            progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                        }
                    }).start();
                }
            });
        });
    }


    private void OnBtnBirthDateClicked() {
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


    private void GetCustomerInfoFromDb() {

        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
        progressBarSettingsActivity.setVisibility(View.VISIBLE);

        new Thread(() -> {
            int customerId = getSharedPreferences(sharedPreferencesName, MODE_PRIVATE).getInt(MainActivity.sharedPreferences_CustomerId, 0);
            Request request = new Request.Builder()
                    .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetCustomerById + customerId)
                    .get()
                    .build();


            String s = null;
            try {
                Response response = MainActivity.client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                s = response.body().string();

                String finalS = s;
                runOnUiThread(() -> {

                    try {
                        Log.d("GetCustomerInfoFromDb", finalS);
                        JSONObject jsonObject = new JSONObject(finalS);
                        SetUserSettingsInActivity(jsonObject);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                        Toast.makeText(this,
                                getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                                Toast.LENGTH_SHORT).show();
                    }
                    MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                    progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SignUserIn", "error1 " + e.getMessage() + "");

                runOnUiThread(() -> {
//                MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
//                progressBarSettingsActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,
                            getString(R.string.GlobalMessage_ThereIsProblemTryAgain),
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }
}