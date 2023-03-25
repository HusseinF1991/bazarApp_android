package com.uruksys.bazarapp_merchants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewProductActivity extends AppCompatActivity {

    byte[] itemImageStr, itemImageStr2, itemImageStr3, itemImageStr4;

    EditText etxtProductName, etxtProductCode;
    Spinner spinnerProductMainCat, spinnerProductSub1Cat, spinnerProductSub2Cat, spinnerBrands;
    ImageView imgBrandLogo;
    Button btnAddProduct, btnProductExpDate;

    ImageView picbProductImage, picbProductImage2, picbProductImage3, picbProductImage4;
    Button btnProductImg_camera, btnProductImg_camera2, btnProductImg_camera3, btnProductImg_camera4;
    Button btnProductImg_gallery, btnProductImg_gallery2, btnProductImg_gallery3, btnProductImg_gallery4;
    Button btnAddProductOption;
    RecyclerView recyclerNewProductOptions;

    ProgressBar newProductActivityProgressBar;
    CheckBox cbProductExp;

    ArrayList<CategoriesModel> categoriesArrayList;
    ArrayList<BrandsModel> brandsArrayList;
    ArrayList<String> productsCodesArrayList;
    ArrayList<ProductOptionsModel> productOptionsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnProductImg_camera = (Button) findViewById(R.id.btnProductImg_camera);
        btnProductImg_camera2 = (Button) findViewById(R.id.btnProductImg_camera2);
        btnProductImg_camera3 = (Button) findViewById(R.id.btnProductImg_camera3);
        btnProductImg_camera4 = (Button) findViewById(R.id.btnProductImg_camera4);
        btnProductImg_gallery = (Button) findViewById(R.id.btnProductImg_gallery);
        btnProductImg_gallery2 = (Button) findViewById(R.id.btnProductImg_gallery2);
        btnProductImg_gallery3 = (Button) findViewById(R.id.btnProductImg_gallery3);
        btnProductImg_gallery4 = (Button) findViewById(R.id.btnProductImg_gallery4);
        picbProductImage = (ImageView) findViewById(R.id.picbProductImage);
        picbProductImage2 = (ImageView) findViewById(R.id.picbProductImage2);
        picbProductImage3 = (ImageView) findViewById(R.id.picbProductImage3);
        picbProductImage4 = (ImageView) findViewById(R.id.picbProductImage4);

        btnProductExpDate = findViewById(R.id.btnProductExpDate);
        btnAddProduct = (Button) findViewById(R.id.btnAddProduct);
        btnAddProductOption = findViewById(R.id.btnAddProductOption);

        etxtProductName = (EditText) findViewById(R.id.etxtProductName);
        etxtProductCode = (EditText) findViewById(R.id.etxtProductCode);

        spinnerBrands = findViewById(R.id.spinnerBrands);
        spinnerProductMainCat = findViewById(R.id.spinnerProductMainCat);
        spinnerProductSub1Cat = findViewById(R.id.spinnerProductSub1Cat);
        spinnerProductSub2Cat = findViewById(R.id.spinnerProductSub2Cat);

        cbProductExp = findViewById(R.id.cbProductExp);

        imgBrandLogo = findViewById(R.id.imgBrandLogo);
        newProductActivityProgressBar = findViewById(R.id.newProductActivityProgressBar);

        categoriesArrayList = new ArrayList<>();
        brandsArrayList = new ArrayList<>();
        productsCodesArrayList = new ArrayList<>();
        productOptionsArrayList = new ArrayList<>();

        recyclerNewProductOptions = findViewById(R.id.recyclerNewProductOptions);

        CameraNGalleryImageListener();

        cbProductExp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btnProductExpDate.setEnabled(true);
                    btnProductExpDate.setText((String) DateFormat.format("yyyy-MM-dd", new Date().getTime()));
                } else {
                    btnProductExpDate.setEnabled(false);
                    btnProductExpDate.setText("No expiration date");
                }
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //getCategories();     .......... called in getBrandsNames()
        GetBrandsNames();
        AddNewProduct();
        OnSelectingCatInSpinner();

        OnSelectingBrandInSpinner();
        BtnProductExpDateFromClicked();
        BtnAddProductOptionClicked();
    }


    private void BtnAddProductOptionClicked() {
        btnAddProductOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = getLayoutInflater().inflate(R.layout.add_product_option_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NewProductActivity.this)
                        .setView(view1);

                Button btnCancelDialog = view1.findViewById(R.id.btnCancelDialog);
                Button btnAddNewOption = view1.findViewById(R.id.btnAddNewOption);
                EditText etxtDescription = view1.findViewById(R.id.etxtDescription);
                EditText etxtProductOptionAvailableQty = view1.findViewById(R.id.etxtProductOptionAvailableQty);
                EditText etxtProductOptionDiscount = view1.findViewById(R.id.etxtProductOptionDiscount);
                EditText etxtProductOptionSellPrice = view1.findViewById(R.id.etxtProductOptionSellPrice);
                EditText etxtProductOptionTitle = view1.findViewById(R.id.etxtProductOptionTitle);

                AlertDialog alertDialog = builder.create();

                btnAddNewOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etxtProductOptionSellPrice.getText().toString().equals("") ||
                                etxtProductOptionAvailableQty.getText().toString().equals("")){

                            Toast.makeText(NewProductActivity.this, "ادخل سعر المادة والكمية رجاءا", Toast.LENGTH_SHORT).show();

                        }else if (!etxtProductOptionDiscount.getText().toString().equals("") &&
                                (Double.parseDouble(etxtProductOptionDiscount.getText().toString()) > Double.parseDouble(etxtProductOptionSellPrice.getText().toString()) ||
                                        Double.parseDouble(etxtProductOptionDiscount.getText().toString()) < 0 ||
                                        etxtProductOptionDiscount.getText().toString().equals("0"))) {

                            Toast.makeText(NewProductActivity.this, "ادخل قيمة صحيحة للتخفيض رجاءا", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(productOptionsArrayList.size() > 0){
                                for (ProductOptionsModel productOptionsModel: productOptionsArrayList) {

                                    if(productOptionsModel.getOptionTitle().equals(etxtProductOptionTitle.getText().toString().trim())){

                                        Toast.makeText(NewProductActivity.this, "This option title is already exist", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                            String optionTitle = etxtProductOptionTitle.getText().toString().trim();
                            String description = etxtDescription.getText().toString().trim();
                            String availableQty = etxtProductOptionAvailableQty.getText().toString();
                            String optionDiscount = etxtProductOptionDiscount.getText().toString();
                            String optionSellPrice = etxtProductOptionSellPrice.getText().toString();

                            AddNewOption(optionTitle, optionSellPrice, optionDiscount, availableQty, description);

                            alertDialog.dismiss();
                        }
                    }
                });


                btnCancelDialog.setOnClickListener(view2 -> {
                    alertDialog.dismiss();
                    Log.d("dialog", "dismissed");
                });

                alertDialog.show();
            }
        });
    }


    private void AddNewOption(String optionTitle, String optionSellPrice, String optionDiscount, String availableQty, String description) {

        if (optionTitle.equals("") || optionSellPrice.equals("") || availableQty.equals("")) {

            Toast.makeText(NewProductActivity.this, "please fill the required fields", Toast.LENGTH_SHORT).show();
        } else {

            productOptionsArrayList.add(new ProductOptionsModel(optionTitle, description, optionSellPrice, optionDiscount, availableQty));
            ProductOptionsRecyclerAdapter productOptionsRecyclerAdapter = new ProductOptionsRecyclerAdapter(NewProductActivity.this,
                    R.layout.product_option_recycler_item, productOptionsArrayList, recyclerNewProductOptions);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerNewProductOptions.setLayoutManager(mLayoutManager);
            recyclerNewProductOptions.setItemAnimator(new DefaultItemAnimator());
            recyclerNewProductOptions.setAdapter(productOptionsRecyclerAdapter);

            productOptionsRecyclerAdapter.notifyDataSetChanged();

            Log.d("NewProductActivity", "AddNewOption");

        }
    }



    private void CameraNGalleryImageListener() {

        /////////////1////////////////
        //open camera to set item image
        btnProductImg_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
                }
            }
        });


        //open gallery to set item  image
        btnProductImg_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
                }
            }
        });


        ////////////2//////////////////
        //open camera to set item image
        btnProductImg_camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE2);
                }
            }
        });


        //open gallery to set item  image
        btnProductImg_gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE2);
                }
            }
        });


        ////////////3///////////////
        //open camera to set item image
        btnProductImg_camera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE3);
                }
            }
        });


        //open gallery to set item  image
        btnProductImg_gallery3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE3);
                }
            }
        });


        ///////////////4//////////////////
        //open camera to set item image
        btnProductImg_camera4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE4);
                }
            }
        });


        //open gallery to set item  image
        btnProductImg_gallery4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE4);
                }
            }
        });
    }


    private void BtnProductExpDateFromClicked() {
        btnProductExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = Integer.parseInt((String) DateFormat.format("MM", new Date().getTime())) - 1;
                int day = Integer.parseInt((String) DateFormat.format("dd", new Date().getTime()));
                int year = Integer.parseInt((String) DateFormat.format("yyyy", new Date().getTime()));
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        String fromDateStr = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                        btnProductExpDate.setText(fromDateStr);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }


    private void OnSelectingBrandInSpinner() {
        spinnerBrands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spinnerBrands.getSelectedItem().toString().equals("Choose brand name") &&
                        !spinnerBrands.getSelectedItem().toString().equals("No brand")) {
                    for (BrandsModel brandsModel : brandsArrayList) {
                        if (brandsModel.getBrandName().equals(spinnerBrands.getSelectedItem().toString())) {

                            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imgBrandLogo.getLayoutParams();
                            Resources r = NewProductActivity.this.getResources();
                            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, r.getDisplayMetrics());
                            params.height = px;
//                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(180, 180);
//                            imgBrandLogo.setLayoutParams(layoutParams);
                            imgBrandLogo.setVisibility(View.VISIBLE);

                            imgBrandLogo.setImageBitmap(decodeBase64Profile(brandsModel.getBrandImage()));
                        }
                    }
                } else {

                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imgBrandLogo.getLayoutParams();
                    params.height = 0;
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
//                    imgBrandLogo.setLayoutParams(layoutParams);
                    imgBrandLogo.setVisibility(View.INVISIBLE);

                    imgBrandLogo.setImageBitmap(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void GetBrandsNames() {
        new Thread(() -> {
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
                Log.d("fetchingData", s);
                String finalS = s;
                runOnUiThread(() -> {

                    try {
                        JSONArray jsonArray = new JSONArray(finalS);
                        brandsArrayList.clear();
                        brandsArrayList.add(new BrandsModel("Choose brand name", null));
                        brandsArrayList.add(new BrandsModel("No brand", null));
                        ArrayList<String> brandsNamesArrayList = new ArrayList<>();
                        brandsNamesArrayList.add("Choose brand name");
                        brandsNamesArrayList.add("No brand");
                        for (int r = 0; r < jsonArray.length(); r++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(r);

                            brandsArrayList.add(new BrandsModel(jsonObject1.getString("brandName"), jsonObject1.getString("brandImage")));
                            brandsNamesArrayList.add(jsonObject1.getString("brandName"));
                        }

                        // Create the adapter and set brands names in the spinner
                        spinnerBrands.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, brandsNamesArrayList) {
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
                        getCategories();

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


    private void OnSelectingCatInSpinner() {
        spinnerProductMainCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PopulateSub1Cat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerProductSub1Cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PopulateSub2Cat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void getCategories() {
        new Thread(() -> {

            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/getCategories.php")
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            runOnUiThread(() -> {

                try {
                    JSONArray jsonArray = new JSONArray(finalS);
                    categoriesArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String categoryId = jsonObject.getString("categoryId");
                        String categoryTitle = jsonObject.getString("categoryTitle");
                        String categoryLevel = jsonObject.getString("categoryLevel");
                        String parentCategoryId = jsonObject.getString("parentCategoryId");
                        String parentCatTitle = jsonObject.getString("parentCatTitle");

                        categoriesArrayList.add(new CategoriesModel(categoryId, categoryTitle, categoryLevel, parentCategoryId, parentCatTitle));
                    }

                    PopulateMainCat();
                    getProductsCodes();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    newProductActivityProgressBar.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("hello", "JSONException error " + e.getMessage());
                }
            });
        }).start();
    }


    private void getProductsCodes() {
        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/getProductsCodes.php")
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("hello", "IOException error " + e.getMessage());
            }

            String finalS = s;
            runOnUiThread(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(finalS);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String productCode = jsonObject.getString("productCode");
                        productsCodesArrayList.add(productCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("hello", "JSONException error " + e.getMessage());
                }
            });
        }).start();
    }


    private void PopulateMainCat() {

        ArrayList<String> mainCategoriesArrayList = new ArrayList<>();
        mainCategoriesArrayList.add("Choose main category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("1")) {
                mainCategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        spinnerProductMainCat.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, mainCategoriesArrayList) {
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
        PopulateSub1Cat();
    }


    private void PopulateSub1Cat() {

        ArrayList<String> sub1CategoriesArrayList = new ArrayList<>();
        sub1CategoriesArrayList.add("Choose sub 1 category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("2") && spinnerProductMainCat.getSelectedItem().toString().equals(categoriesModel.getParentCatTitle())) {
                sub1CategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        spinnerProductSub1Cat.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sub1CategoriesArrayList) {
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
        PopulateSub2Cat();
    }


    private void PopulateSub2Cat() {

        ArrayList<String> sub2CategoriesArrayList = new ArrayList<>();
        sub2CategoriesArrayList.add("Choose sub 2 category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("3") && spinnerProductSub1Cat.getSelectedItem().toString().equals(categoriesModel.getParentCatTitle())) {
                sub2CategoriesArrayList.add(categoriesModel.getCategoryTitle());
            }
        }

        spinnerProductSub2Cat.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, sub2CategoriesArrayList) {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "gallery permission granted", Toast.LENGTH_LONG).show();
                //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //photoPickerIntent.setType("image/*");
                //startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
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

//            Bitmap resizedBitmap = ResizeImage(photo);
//            picbProductImage.setImageBitmap(resizedBitmap);
//            itemImageStr = getBytesFromBitmap(resizedBitmap);
            //
            //
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            itemImageStr = outputStream.toByteArray();
            picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

//                Bitmap resizedBitmap = ResizeImage(selectedImage);
//                picbProductImage.setImageBitmap(resizedBitmap);
//                itemImageStr = getBytesFromBitmap(resizedBitmap);


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                itemImageStr = outputStream.toByteArray();
                picbProductImage.setImageBitmap(selectedImage);
//                itemImageStr = getBytesFromBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE2 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

//            Bitmap resizedBitmap = ResizeImage(photo);
//            picbProductImage2.setImageBitmap(resizedBitmap);
//            itemImageStr2 = getBytesFromBitmap(resizedBitmap);
            //
            //

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            itemImageStr2 = outputStream.toByteArray();
            picbProductImage2.setImageBitmap(photo);
//            itemImageStr2 = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE2 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

//                Bitmap resizedBitmap = ResizeImage(selectedImage);
//                picbProductImage2.setImageBitmap(resizedBitmap);
//                itemImageStr2 = getBytesFromBitmap(resizedBitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                itemImageStr2 = outputStream.toByteArray();
                picbProductImage2.setImageBitmap(selectedImage);
//                itemImageStr2 = getBytesFromBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE3 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

//            Bitmap resizedBitmap = ResizeImage(photo);
//            picbProductImage3.setImageBitmap(resizedBitmap);
//            itemImageStr3 = getBytesFromBitmap(resizedBitmap);
            //
            //

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            itemImageStr3 = outputStream.toByteArray();
            picbProductImage3.setImageBitmap(photo);
//            itemImageStr3 = getBytesFromBitmap(photo);
        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE3 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

//                Bitmap resizedBitmap = ResizeImage(selectedImage);
//                picbProductImage3.setImageBitmap(resizedBitmap);
//                itemImageStr3 = getBytesFromBitmap(resizedBitmap);


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                itemImageStr3 = outputStream.toByteArray();
                picbProductImage3.setImageBitmap(selectedImage);
//                itemImageStr3 = getBytesFromBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE4 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

//            Bitmap resizedBitmap = ResizeImage(photo);
//            picbProductImage4.setImageBitmap(resizedBitmap);
//            itemImageStr4 = getBytesFromBitmap(resizedBitmap);
            //
            //

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            itemImageStr4 = outputStream.toByteArray();
            picbProductImage4.setImageBitmap(photo);
//            itemImageStr4 = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE4 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

//                Bitmap resizedBitmap = ResizeImage(selectedImage);
//                picbProductImage4.setImageBitmap(resizedBitmap);
//                itemImageStr4 = getBytesFromBitmap(resizedBitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                itemImageStr4 = outputStream.toByteArray();
                picbProductImage4.setImageBitmap(selectedImage);
//                itemImageStr4 = getBytesFromBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private Bitmap ResizeImage(Bitmap selectedImage) {

        //
        //
        //originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
        int width = selectedImage.getWidth();
        Log.i("Old width................", width + "");
        int height = selectedImage.getHeight();
        Log.i("Old height................", height + "");

        Matrix matrix = new Matrix();
        int newWidth = 150;
        int newHeight = 150;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(selectedImage, 0, 0, width, height, matrix, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        width = resizedBitmap.getWidth();
        Log.i("new width................", width + "");
        height = resizedBitmap.getHeight();
        Log.i("new height................", height + "");

        return resizedBitmap;
    }


    private void AddNewProduct() {
        btnAddProduct.setOnClickListener(view -> {

            if (etxtProductName.getText().toString().trim().equals("") ||
                    etxtProductCode.getText().toString().trim().equals("") ||
                    spinnerProductMainCat.getSelectedItem().toString().equals("Choose main category") ||
                    itemImageStr == null ||
                    itemImageStr2 == null ||
                    itemImageStr3 == null ||
                    itemImageStr4 == null ||
                    productOptionsArrayList.size() == 0){

                Toast.makeText(NewProductActivity.this, "ادخل معلومات المنتج رجاءا", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    for (String code : productsCodesArrayList) {
                        if (code.equals(etxtProductCode.getText().toString().trim())) {
                            Toast.makeText(NewProductActivity.this, "الرمز موجود مسبقا", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    newProductActivityProgressBar.setVisibility(View.VISIBLE);


                    String productCode = etxtProductCode.getText().toString().trim();
                    String productName = etxtProductName.getText().toString().trim();

                    String brand = null;
                    if (!spinnerBrands.getSelectedItem().toString().equals("no brand") && !spinnerBrands.getSelectedItem().toString().equals("Choose brand name"))
                        brand = spinnerBrands.getSelectedItem().toString().trim();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String ProductDate = sdf.format(new Date());

                    JSONObject jsonObject = new JSONObject();
                    Log.d("hello", "productImage = " + Base64.encodeToString(itemImageStr, Base64.DEFAULT).length());

                    try {
                        //
                        jsonObject.put("productCode", productCode);
                        jsonObject.put("productName", productName);
                        jsonObject.put("productImage", Base64.encodeToString(itemImageStr, Base64.DEFAULT));
                        jsonObject.put("productImage2", Base64.encodeToString(itemImageStr2, Base64.DEFAULT));
                        jsonObject.put("productImage3", Base64.encodeToString(itemImageStr3, Base64.DEFAULT));
                        jsonObject.put("productImage4", Base64.encodeToString(itemImageStr4, Base64.DEFAULT));

                        String expDate = null;
                        if (!btnProductExpDate.getText().toString().toUpperCase().equals("NO EXPIRATION DATE")) {

                            expDate = btnProductExpDate.getText().toString();
                        }
                        jsonObject.put("expDate", expDate);
                        jsonObject.put("productDate", ProductDate);
                        jsonObject.put("brand", brand);

                        String mainCat = null;
                        for (CategoriesModel categoriesModel : categoriesArrayList) {
                            if (categoriesModel.getCategoryLevel().equals("1") &&
                                    categoriesModel.getCategoryTitle().equals(spinnerProductMainCat.getSelectedItem().toString())) {
                                mainCat = categoriesModel.getCategoryId();
                            }
                        }
                        jsonObject.put("mainCat", mainCat);


                        String sub1Cat = null;
                        if (!spinnerProductSub1Cat.getSelectedItem().toString().equals("Choose sub 1 category")) {
                            for (CategoriesModel categoriesModel : categoriesArrayList) {
                                if (categoriesModel.getCategoryLevel().equals("2") &&
                                        categoriesModel.getCategoryTitle().equals(spinnerProductSub1Cat.getSelectedItem().toString())) {
                                    sub1Cat = categoriesModel.getCategoryId();
                                }
                            }
                        }
                        jsonObject.put("sub1Cat", sub1Cat);


                        String sub2Cat = null;
                        if (!spinnerProductSub2Cat.getSelectedItem().toString().equals("Choose sub 2 category")) {
                            for (CategoriesModel categoriesModel : categoriesArrayList) {
                                if (categoriesModel.getCategoryLevel().equals("3") &&
                                        categoriesModel.getCategoryTitle().equals(spinnerProductSub2Cat.getSelectedItem().toString())) {
                                    sub2Cat = categoriesModel.getCategoryId();
                                }
                            }
                        }
                        jsonObject.put("sub2Cat", sub2Cat);


                        SharedPreferences sharedPref = NewProductActivity.this.getSharedPreferences("BazarAppMerchantSharedPreferences", Context.MODE_PRIVATE);
                        String providerUserName = sharedPref.getString("BazarAppMerchantUserNameSharedPreferences", "");
                        jsonObject.put("provider", providerUserName);


                        if (productOptionsArrayList.size() > 1) {
                            for (ProductOptionsModel productOptionsModel: productOptionsArrayList) {
                                if(productOptionsModel.getOptionTitle().trim().equals("")){

                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    newProductActivityProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(NewProductActivity.this, "You need to enter option title if your product has multiple options", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        } else {
                            ProductOptionsModel productOptionsModel = productOptionsArrayList.get(0);
                            productOptionsModel.setOptionTitle("");
                        }

                            JSONArray jsonArray = new JSONArray();
                            for (ProductOptionsModel productOptionsModel: productOptionsArrayList) {
                                JSONObject optionsJsonObj = new JSONObject();
                                optionsJsonObj.put("optionTitle" , productOptionsModel.getOptionTitle());
                                optionsJsonObj.put("optionSellPrice" , productOptionsModel.getSellPrice());

                                String discount = "0";
                                if (!productOptionsModel.getDiscount().equals(""))
                                    discount = productOptionsModel.getDiscount();
                                optionsJsonObj.put("optionDiscount" , discount);

                                optionsJsonObj.put("optionAvailableQty" , productOptionsModel.getAvailableQty());
                                optionsJsonObj.put("optionDescription" , productOptionsModel.getDescription());
                                jsonArray.put(optionsJsonObj);
                            }

                            jsonObject.put("options" , jsonArray);



                        Log.d("hello", "productCode " + productCode + ",productName " + productName + ",productDate " + ProductDate
                                + ",brand " + brand + ",mainCat " + mainCat + ",sub1Cat " + sub1Cat + ",sub2Cat " + sub2Cat + ",provider "
                                + providerUserName + ",expDate " + btnProductExpDate.getText().toString());

                        new Thread(() -> {

                            //Log.d("hello", jsonObject.toString());
                            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                            // do background stuff here
                            Request request = new Request.Builder()
                                    .url(LoginActivity.serverIp + "/addNewProduct.php")
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

                                Log.d("hello", finalS);
                                Toast.makeText(NewProductActivity.this, finalS, Toast.LENGTH_SHORT).show();
                                if (finalS.contains("new product added")) {

                                    productsCodesArrayList.add(productCode);
                                } else {

                                }

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                newProductActivityProgressBar.setVisibility(View.INVISIBLE);
                            });
                        }).start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("hello", e.getMessage());

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        newProductActivityProgressBar.setVisibility(View.INVISIBLE);
                    }
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                    Log.d("hello", e1.getMessage());

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    newProductActivityProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    //convert byte[] to bitmap
    public static Bitmap decodeBase64Profile(String input) {
        Bitmap bitmap = null;
        if (input != null) {
            try {

                byte[] decodedByte = Base64.decode(input, 0);
                bitmap = BitmapFactory
                        .decodeByteArray(decodedByte, 0, decodedByte.length);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    //convert byte[] to bitmap
    public static Bitmap decodeBase64Profile(byte[] input) {
        Bitmap bitmap = null;
        if (input != null) {
            try {

                bitmap = BitmapFactory
                        .decodeByteArray(input, 0, input.length);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    //convert bitmap to byte[] to set it in db in blob variable
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        return null;
    }
}


class CategoriesModel {
    private String categoryId, categoryTitle, categoryLevel, parentCategoryId, parentCatTitle;

    public CategoriesModel(String categoryId, String categoryTitle, String categoryLevel, String parentCategoryId, String parentCatTitle) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.categoryLevel = categoryLevel;
        this.parentCategoryId = parentCategoryId;
        this.parentCatTitle = parentCatTitle;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getCategoryLevel() {
        return categoryLevel;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public String getParentCatTitle() {
        return parentCatTitle;
    }
}


class BrandsModel {
    private String brandName, brandImage;

    public BrandsModel(String brandName, String brandImage) {
        this.brandName = brandName;
        this.brandImage = brandImage;
    }


    public String getBrandName() {
        return brandName;
    }

    public String getBrandImage() {
        return brandImage;
    }
}


class ProductOptionsModel {
    private String optionId , description, optionTitle;
    private String sellPrice, discount, availableQty;

    public ProductOptionsModel(String optionTitle, String description, String sellPrice, String discount, String availableQty) {
        this.description = description;
        this.sellPrice = sellPrice;
        this.discount = discount;
        this.availableQty = availableQty;
        this.optionTitle = optionTitle;
    }
    public ProductOptionsModel(String optionId, String optionTitle, String description, String sellPrice, String discount, String availableQty) {

        this.optionId = optionId;
        this.description = description;
        this.sellPrice = sellPrice;
        this.discount = discount;
        this.availableQty = availableQty;
        this.optionTitle = optionTitle;
    }

    public String getOptionId() {
        return optionId;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public String getAvailableQty() {
        return availableQty;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }
}