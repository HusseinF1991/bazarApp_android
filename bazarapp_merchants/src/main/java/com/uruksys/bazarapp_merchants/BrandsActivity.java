package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BrandsActivity extends AppCompatActivity {

    Button  btnAddNewBrand, btnBrandImg_camera, btnBrandImg_gallery, btnRemoveBrand;
    AutoCompleteTextView etxtBrandName;
    ImageView imgBrandImage;
    ProgressBar brandActivityProgressBar;
    byte[] brandImageStr;

    ArrayList<BrandsModel> brandsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        btnAddNewBrand = findViewById(R.id.btnAddNewBrand);
        etxtBrandName = findViewById(R.id.etxtBrandName);
        imgBrandImage = findViewById(R.id.imgBrandImage);
        btnBrandImg_camera = findViewById(R.id.btnBrandImg_camera);
        btnBrandImg_gallery = findViewById(R.id.btnBrandImg_gallery);
        brandActivityProgressBar = findViewById(R.id.brandActivityProgressBar);
        btnRemoveBrand = findViewById(R.id.btnRemoveBrand);

        //open camera to set brand image
        btnBrandImg_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(BrandsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BrandsActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
                }
            }
        });


        //open gallery to set brand  image
        btnBrandImg_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(BrandsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BrandsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
                }
            }
        });

        FillAutoCompletionWithBrands();
        AddNewBrandBtnClicked();
        OnBrandNameSelected();
        OnBrandDeleteBtnPressed();
    }


    private void OnBrandDeleteBtnPressed() {
        btnRemoveBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                brandActivityProgressBar.setVisibility(View.VISIBLE);
                etxtBrandName.setEnabled(false);
                btnAddNewBrand.setEnabled(false);
                btnRemoveBrand.setEnabled(false);
                btnBrandImg_camera.setEnabled(false);
                btnBrandImg_gallery.setEnabled(false);

                new Thread(() -> {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("brandName", etxtBrandName.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
                    Request request = new Request.Builder()
                            .url(LoginActivity.serverIp + "/removeBrand.php")
                            .post(requestBody)
                            .build();

                    String s = null;
                    try {
                        Response response = LoginActivity.client.newCall(request).execute();

                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);

                        s = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {

                        Log.d("fetchingData", s + "");
                        String finalS = s;
                        runOnUiThread(() -> {

                            if (finalS.trim().equals("successful")) {

                                FillAutoCompletionWithBrands();

                                Toast.makeText(BrandsActivity.this, "deleted successfully", Toast.LENGTH_SHORT).show();

                                etxtBrandName.setText("");
                                brandImageStr = null;
                            }
                            else{

                                Toast.makeText(BrandsActivity.this, "failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("hello", "IllegalStateException error " + e.getMessage());
                    } catch (NullPointerException e) {

                        Log.d("hello", "NullPointerException error " + e.getMessage());
                    }
                }).start();
            }
        });
    }


    private void OnBrandNameSelected() {
        etxtBrandName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean brandExisted = false;
                for (BrandsModel brandsModel : brandsArrayList) {
                    if (etxtBrandName.getText().toString().equals(brandsModel.getBrandName())) {
                        imgBrandImage.setImageBitmap(NewProductActivity.decodeBase64Profile(brandsModel.getBrandImage()));

                        btnAddNewBrand.setEnabled(false);
                        btnRemoveBrand.setEnabled(true);

                        brandExisted = true;
                        break;
                    }
                }

                if (!brandExisted) {
                    imgBrandImage.setImageBitmap(null);
                    btnAddNewBrand.setEnabled(true);
                    btnRemoveBrand.setEnabled(false);
                }
            }
        });
    }


    private void FillAutoCompletionWithBrands() {
        new Thread(() -> {

            Log.d("fetchingData",  "FillAutoCompletionWithBrands");
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/getBrands.php")
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                Log.d("fetchingData", s + "");
                String finalS = s;
                runOnUiThread(() -> {

                    try {
                        JSONArray jsonArray = new JSONArray(finalS);
                        List<String> brandsNamesList = new ArrayList<>();
                        brandsArrayList.clear();
                        for (int r = 0; r < jsonArray.length(); r++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(r);

                            brandsArrayList.add(new BrandsModel(jsonObject1.getString("brandName"), jsonObject1.getString("brandImage")));
                            brandsNamesList.add(jsonObject1.getString("brandName"));
                        }
                        // Create the adapter and set it to the AutoCompleteTextView
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, brandsNamesList);
                        etxtBrandName.setAdapter(adapter);

                        brandActivityProgressBar.setVisibility(View.INVISIBLE);
                        etxtBrandName.setEnabled(true);
                        btnAddNewBrand.setEnabled(true);
                        btnBrandImg_camera.setEnabled(true);
                        btnBrandImg_gallery.setEnabled(true);

                    } catch (JSONException e) {
                        e.printStackTrace();

                        Log.d("hello", "JSONException error " + e.getMessage());
                    }
                });
            } catch (IllegalStateException e) {

                Log.d("hello", "IllegalStateException error " + e.getMessage());
            } catch (NullPointerException e) {

                Log.d("hello", "NullPointerException error " + e.getMessage());
            }
        }).start();
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
            imgBrandImage.setImageBitmap(resizedBitmap);
            brandImageStr = NewProductActivity.getBytesFromBitmap(resizedBitmap);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgBrandImage.setImageBitmap(selectedImage);

                brandImageStr = NewProductActivity.getBytesFromBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(BrandsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void AddNewBrandBtnClicked() {
        btnAddNewBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (BrandsModel brandsModel : brandsArrayList) {

                    if (brandsModel.getBrandName().equals(etxtBrandName.getText().toString().trim())) {
                        Toast.makeText(BrandsActivity.this, "brand name already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (!etxtBrandName.getText().toString().trim().equals("") && brandImageStr != null) {

                    brandActivityProgressBar.setVisibility(View.VISIBLE);
                    etxtBrandName.setEnabled(false);
                    btnAddNewBrand.setEnabled(false);
                    btnRemoveBrand.setEnabled(false);
                    btnBrandImg_camera.setEnabled(false);
                    btnBrandImg_gallery.setEnabled(false);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("brandName", etxtBrandName.getText().toString().trim());
                        jsonObject.put("brandImage", Base64.encodeToString(brandImageStr, Base64.DEFAULT));

                        new Thread(() -> {

                            Log.d("hello", jsonObject.toString());
                            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                            // do background stuff here
                            Request request = new Request.Builder()
                                    .url(LoginActivity.serverIp + "/addNewBrand.php")
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


                            String finalS = s;
                            Log.d("hello", finalS);
                            runOnUiThread(() -> {

                                if (finalS.contains("successful")) {

                                    FillAutoCompletionWithBrands();

                                    etxtBrandName.setText("");
                                    brandImageStr = null;
                                    Toast.makeText(BrandsActivity.this, "successfully added", Toast.LENGTH_SHORT).show();
                                }
                                else{

                                    Toast.makeText(BrandsActivity.this, "failed to add", Toast.LENGTH_SHORT).show();

                                    etxtBrandName.setEnabled(true);
                                    btnAddNewBrand.setEnabled(true);
                                    btnBrandImg_camera.setEnabled(true);
                                    btnBrandImg_gallery.setEnabled(true);
                                    etxtBrandName.setText("");
                                    brandImageStr = null;
                                    brandActivityProgressBar.setVisibility(View.INVISIBLE);
                                }

                            });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("hello", "IOException "+e.getMessage());
                                brandActivityProgressBar.setVisibility(View.INVISIBLE);
                                etxtBrandName.setEnabled(true);
                                btnAddNewBrand.setEnabled(true);
                                btnBrandImg_camera.setEnabled(true);
                                btnBrandImg_gallery.setEnabled(true);
                            }
                        }).start();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        brandActivityProgressBar.setVisibility(View.INVISIBLE);
                        etxtBrandName.setEnabled(true);
                        btnAddNewBrand.setEnabled(true);
                        btnBrandImg_camera.setEnabled(true);
                        btnBrandImg_gallery.setEnabled(true);
                    }
                } else {
                    Toast.makeText(BrandsActivity.this, "ادخل المعلومات كاملة رجاءا", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}