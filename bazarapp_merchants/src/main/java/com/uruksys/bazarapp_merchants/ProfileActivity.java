package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    ProgressBar progressBar_ProfileActivity;
    Button btnUpdateProviderInfo, btnLogo_camera, btnLogo_gallery, btnChangeProviderPassword;
    EditText etxtAddressRegion, etxtShopMobile, etxtShopName;
    Spinner spinnerAddressCity;
    ImageView imgProfileLogo;
    TextView txtMerchantName;
    List<String> iraqiProvinces = new ArrayList<>();

    String logoImageStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar_ProfileActivity = findViewById(R.id.progressBar_ProfileActivity);
        btnUpdateProviderInfo = findViewById(R.id.btnUpdateProviderInfo);
        etxtShopMobile = findViewById(R.id.etxtShopMobile);
        etxtAddressRegion = findViewById(R.id.etxtAddressRegion);
        spinnerAddressCity = findViewById(R.id.spinnerAddressCity);
        imgProfileLogo = findViewById(R.id.imgProfileLogo);
        txtMerchantName = findViewById(R.id.txtMerchantName);
        etxtShopName = findViewById(R.id.etxtShopName);
        btnLogo_gallery = findViewById(R.id.btnLogo_gallery);
        btnLogo_camera = findViewById(R.id.btnLogo_camera);
        btnChangeProviderPassword = findViewById(R.id.btnChangeProviderPassword);

        PopulateIraqiProvinces();

        spinnerAddressCity.setSelection(iraqiProvinces.indexOf(LoginActivity.addressCity));
        etxtShopMobile.setText(LoginActivity.mobile);
        etxtAddressRegion.setText(LoginActivity.addressRegion);
        imgProfileLogo.setImageBitmap(NewProductActivity.decodeBase64Profile(LoginActivity.logo));
        logoImageStr = LoginActivity.logo;
        txtMerchantName.setText(LoginActivity.providerTitle);
        etxtShopName.setText(LoginActivity.shopName);


        //open camera to set item image
        btnLogo_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
                }
            }
        });


        //open gallery to set item attachment image
        btnLogo_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
                }
            }
        });

        UpdateProfileInfo();
        ChangeProviderPassBtnClicked();
    }


    private void ChangeProviderPassBtnClicked() {
        btnChangeProviderPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View view1 = getLayoutInflater().inflate(R.layout.change_password_dialog_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                        .setView(view1);

                Button btnCancelDialog = view1.findViewById(R.id.btnCancelDialog);
                Button btnConfirmPasswordDialog = view1.findViewById(R.id.btnConfirmPasswordDialog);
                EditText etxtOldPasswordDialog = view1.findViewById(R.id.etxtOldPasswordDialog);
                EditText etxtNewPasswordDialog = view1.findViewById(R.id.etxtNewPasswordDialog);
                EditText etxtConfirmNewPasswordDialog = view1.findViewById(R.id.etxtConfirmNewPasswordDialog);

                AlertDialog alertDialog = builder.create();

                btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btnConfirmPasswordDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!etxtOldPasswordDialog.getText().toString().equals(LoginActivity.providerPassword)) {

                            Toast.makeText(ProfileActivity.this, "الرمز السري غير صحيح", Toast.LENGTH_LONG).show();
                        }
                        else if (!etxtNewPasswordDialog.getText().toString().trim().equals(etxtConfirmNewPasswordDialog.getText().toString().trim())) {

                            Toast.makeText(ProfileActivity.this, "تاكد من ادخال الرمز السري مرتين صحيحا", Toast.LENGTH_LONG).show();
                        }
                        else if (etxtNewPasswordDialog.getText().toString().trim().length() < 6) {

                            Toast.makeText(ProfileActivity.this, "الرمز السري يجب ان لا يقل عن 6 مراتب", Toast.LENGTH_LONG).show();
                        }
                        else {

                            final String newPassword = etxtNewPasswordDialog.getText().toString().trim();

                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_ProfileActivity.setVisibility(View.VISIBLE);


                            JSONObject jsonObject = new JSONObject();
                            Log.d("hello", logoImageStr.length() + "");

                            try {
                                jsonObject.put("providerTitle", LoginActivity.providerTitle);
                                jsonObject.put("providerPassword", newPassword);

                                new Thread(() -> {

                                    Log.d("hello", jsonObject.toString());
                                    RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                                    // do background stuff here
                                    Request request = new Request.Builder()
                                            .url(LoginActivity.serverIp + "/ChangeProviderPassword.php")
                                            .post(requestBody)
                                            .build();

                                    Response response = null;

                                    String s = null;
                                    try {

                                        response = LoginActivity.client.newCall(request).execute();
                                        if (!response.isSuccessful())
                                            throw new IOException("Unexpected code " + response);

                                        s = response.body().string();

                                        Log.d("hello", "on background");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("hello", e.getMessage());
                                    }


                                    String finalS = s;
                                    runOnUiThread(() -> {
                                        Toast.makeText(ProfileActivity.this, finalS, Toast.LENGTH_SHORT).show();

                                        if (finalS.contains("Changed successfully")) {

                                            SharedPreferences sharedPreferences = getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("BazarAppMerchantPasswordSharedPreferences", newPassword);
                                            editor.commit();

                                            LoginActivity.providerPassword = newPassword;
                                        }


                                        alertDialog.dismiss();


                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        progressBar_ProfileActivity.setVisibility(View.INVISIBLE);
                                    });
                                }).start();
                            } catch (JSONException e) {
                                e.printStackTrace();


                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                progressBar_ProfileActivity.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

                alertDialog.show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "gallery permission granted", Toast.LENGTH_LONG).show();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
            } else {
                Toast.makeText(this, "gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");


            //
            //
            //originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
            int width = photo.getWidth();
            Log.i("Old width................", width + "");
            int height = photo.getHeight();
            Log.i("Old height................", height + "");

            Matrix matrix = new Matrix();
            int newWidth = 150;
            int newHeight = 150;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, width, height, matrix, true);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            width = resizedBitmap.getWidth();
            Log.i("new width................", width + "");
            height = resizedBitmap.getHeight();
            Log.i("new height................", height + "");
            imgProfileLogo.setImageBitmap(resizedBitmap);
            logoImageStr = Base64.encodeToString(NewProductActivity.getBytesFromBitmap(resizedBitmap), Base64.DEFAULT);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgProfileLogo.setImageBitmap(selectedImage);

                logoImageStr = Base64.encodeToString(NewProductActivity.getBytesFromBitmap(selectedImage), Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void PopulateIraqiProvinces() {

        String[] iraqiProvincesArray = new String[]{"اختر المحافظة", "بغداد", "بابل", "الديوانية", "واسط", "البصرة", "ميسان", "ذي قار"
                , "صلاح الدين", "النجف", "كربلاء", "الانبار", "ديالى", "سليمانية", "كركوك", "المثنى", "دهوك", "اربيل", "نينوى"};
        iraqiProvinces = Arrays.asList(iraqiProvincesArray);

        spinnerAddressCity.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, iraqiProvinces) {
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


    private void UpdateProfileInfo() {
        btnUpdateProviderInfo.setOnClickListener(view -> {

            if (etxtShopName.getText().toString().trim().equals("") ||
                    etxtShopMobile.getText().toString().equals("") ||
                    etxtAddressRegion.getText().toString().trim().equals("")) {

                Toast.makeText(ProfileActivity.this, "ادخل المعلومات كاملة رجاءا", Toast.LENGTH_SHORT).show();
            } else {

                final String logo = logoImageStr;
                final String shopName = etxtShopName.getText().toString().trim();
                final String mobile = etxtShopMobile.getText().toString();
                final String addressRegion = etxtAddressRegion.getText().toString().trim();
                final String addressCity = spinnerAddressCity.getSelectedItem().toString();

                View view1 = getLayoutInflater().inflate(R.layout.confirm_password_dialog_layout, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this)
                        .setView(view1);

                Button btnCancelDialog = view1.findViewById(R.id.btnCancelDialog);
                Button btnConfirmPasswordDialog = view1.findViewById(R.id.btnConfirmPasswordDialog);
                EditText etxtProviderPasswordDialog = view1.findViewById(R.id.etxtProviderPasswordDialog);

                AlertDialog alertDialog = builder.create();

                btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btnConfirmPasswordDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etxtProviderPasswordDialog.getText().toString().equals(LoginActivity.providerPassword)) {

                            alertDialog.dismiss();

                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_ProfileActivity.setVisibility(View.VISIBLE);


                            JSONObject jsonObject = new JSONObject();
                            Log.d("hello", logoImageStr.length() + "");

                            try {
                                jsonObject.put("providerTitle", LoginActivity.providerTitle);
                                jsonObject.put("logo", logoImageStr);
                                jsonObject.put("shopName", etxtShopName.getText().toString().trim());
                                jsonObject.put("mobile", etxtShopMobile.getText().toString());
                                jsonObject.put("addressRegion", etxtAddressRegion.getText().toString().trim());
                                jsonObject.put("addressCity", spinnerAddressCity.getSelectedItem().toString());

                                new Thread(() -> {

                                    Log.d("hello", jsonObject.toString());
                                    RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                                    // do background stuff here
                                    Request request = new Request.Builder()
                                            .url(LoginActivity.serverIp + "/UpdateProviderInfo.php")
                                            .post(requestBody)
                                            .build();

                                    Response response = null;

                                    String s = null;
                                    try {

                                        response = LoginActivity.client.newCall(request).execute();
                                        if (!response.isSuccessful())
                                            throw new IOException("Unexpected code " + response);

                                        s = response.body().string();

                                        Log.d("hello", "on background");

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("hello", e.getMessage());
                                    }


                                    String finalS = s;
                                    runOnUiThread(() -> {
                                        Toast.makeText(ProfileActivity.this, finalS, Toast.LENGTH_SHORT).show();

                                        if (finalS.contains("profile updated")) {

                                            LoginActivity.logo = logo;
                                            LoginActivity.shopName = shopName;
                                            LoginActivity.mobile = mobile;
                                            LoginActivity.addressRegion = addressRegion;
                                            LoginActivity.addressCity = addressCity;
                                        }

                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        progressBar_ProfileActivity.setVisibility(View.INVISIBLE);
                                    });
                                }).start();
                            } catch (JSONException e) {
                                e.printStackTrace();


                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                progressBar_ProfileActivity.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "الرمز السري غير صحيح", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alertDialog.show();
            }
        });
    }
}
