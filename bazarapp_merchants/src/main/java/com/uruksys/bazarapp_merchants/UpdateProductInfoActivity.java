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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
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
import android.widget.RatingBar;
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
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateProductInfoActivity extends AppCompatActivity {

    int productId;
    EditText etxtProductName,   etxtProvider , txtSellsCount;
    RatingBar productRateBar;
    Button btnProductExpDate, btnShowProductReviews;
    TextView txtProductCode, txtLastSellDate;
    Spinner spinnerProductMainCat, spinnerProductSub1Cat, spinnerProductSub2Cat, spinnerBrands;
    Button btnUpdateProductInfo, btnProductDate, btnDeleteProduct;

    Button btnProductImg_camera, btnProductImg_camera2, btnProductImg_camera3, btnProductImg_camera4;
    Button btnProductImg_gallery, btnProductImg_gallery2, btnProductImg_gallery3, btnProductImg_gallery4;
    ImageView picbProductImage, picbProductImage2, picbProductImage3, picbProductImage4;
    byte[] productImageStr, productImageStr2, productImageStr3, productImageStr4;
    RecyclerView recyclerProductOptions;
    Button btnAddProductOption;

    ProgressBar progressBar_updateProductInfoActivity;
    CheckBox cbProductExp ;

    String mainCatId, sub1CatId, sub2CatId, productBrand;
    ArrayList<CategoriesModel> categoriesArrayList = new ArrayList<>();
    ArrayList<BrandsModel> brandsArrayList = new ArrayList<>();
    ArrayList<ProductOptionsModel> productOptionsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_info);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etxtProvider = findViewById(R.id.etxtProvider);
        txtProductCode = findViewById(R.id.txtProductCode);
        btnProductExpDate = findViewById(R.id.btnProductExpDate);
        etxtProductName = findViewById(R.id.etxtProductName);
        spinnerProductMainCat = findViewById(R.id.spinnerProductMainCat);
        spinnerProductSub1Cat = findViewById(R.id.spinnerProductSub1Cat);
        spinnerProductSub2Cat = findViewById(R.id.spinnerProductSub2Cat);
        btnUpdateProductInfo = findViewById(R.id.btnUpdateProductInfo);
        progressBar_updateProductInfoActivity = findViewById(R.id.progressBar_updateProductInfoActivity);
        spinnerBrands = findViewById(R.id.spinnerBrands);
        btnProductDate = findViewById(R.id.btnProductDate);
        txtLastSellDate = findViewById(R.id.txtLastSellDate);
        btnShowProductReviews = findViewById(R.id.btnShowProductReviews);
        productRateBar = findViewById(R.id.productRateBar);
        txtSellsCount = findViewById(R.id.txtSellsCount);
        cbProductExp = findViewById(R.id.cbProductExp);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        recyclerProductOptions = findViewById(R.id.recyclerProductOptions);
        btnAddProductOption = findViewById(R.id.btnAddProductOption);


        btnProductImg_camera = findViewById(R.id.btnProductImg_camera);
        btnProductImg_camera2 = findViewById(R.id.btnProductImg_camera2);
        btnProductImg_camera3 = findViewById(R.id.btnProductImg_camera3);
        btnProductImg_camera4 = findViewById(R.id.btnProductImg_camera4);
        btnProductImg_gallery = findViewById(R.id.btnProductImg_gallery);
        btnProductImg_gallery2 = findViewById(R.id.btnProductImg_gallery2);
        btnProductImg_gallery3 = findViewById(R.id.btnProductImg_gallery3);
        btnProductImg_gallery4 = findViewById(R.id.btnProductImg_gallery4);
        picbProductImage = findViewById(R.id.picbProductImage);
        picbProductImage2 = findViewById(R.id.picbProductImage2);
        picbProductImage3 = findViewById(R.id.picbProductImage3);
        picbProductImage4 = findViewById(R.id.picbProductImage4);


        CameraNGalleryImageListener();

        BtnProductExpDateClicked();
        ProductExpirationCheckBoxClicked();
        OnSelectingCatInSpinner();
        UpdateProductInfo();
        DeleteProductBtnClicked();
        BtnAddProductOptionClicked();

        Intent intent = getIntent();
        productId = intent.getIntExtra("productId", 0);
        if (productId != 0) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            GetProductInfo();
        }
    }


    private void BtnAddProductOptionClicked() {
        btnAddProductOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = getLayoutInflater().inflate(R.layout.add_product_option_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductInfoActivity.this)
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
                                etxtProductOptionAvailableQty.getText().toString().equals("") ||
                                etxtProductOptionTitle.getText().toString().trim().equals("")){

                            Toast.makeText(UpdateProductInfoActivity.this, "ادخل سعر المادة والكمية رجاءا", Toast.LENGTH_SHORT).show();

                        }else if (!etxtProductOptionDiscount.getText().toString().equals("") &&
                                (Double.parseDouble(etxtProductOptionDiscount.getText().toString()) > Double.parseDouble(etxtProductOptionSellPrice.getText().toString()) ||
                                        Double.parseDouble(etxtProductOptionDiscount.getText().toString()) < 0 ||
                                        etxtProductOptionDiscount.getText().toString().equals("0"))) {

                            Toast.makeText(UpdateProductInfoActivity.this, "ادخل قيمة صحيحة للتخفيض رجاءا", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(productOptionsArrayList.size() > 0){
                                for (ProductOptionsModel productOptionsModel: productOptionsArrayList) {

                                    if(productOptionsModel.getOptionTitle().equals(etxtProductOptionTitle.getText().toString().trim())){

                                        Toast.makeText(UpdateProductInfoActivity.this, "This option title is already exist", Toast.LENGTH_SHORT).show();
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


            productOptionsArrayList.add(new ProductOptionsModel(optionTitle, description, optionSellPrice, optionDiscount, availableQty));
            ProductOptionsRecyclerAdapter productOptionsRecyclerAdapter = new ProductOptionsRecyclerAdapter(UpdateProductInfoActivity.this,
                    R.layout.product_option_recycler_item, productOptionsArrayList, recyclerProductOptions);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerProductOptions.setLayoutManager(mLayoutManager);
            recyclerProductOptions.setItemAnimator(new DefaultItemAnimator());
            recyclerProductOptions.setAdapter(productOptionsRecyclerAdapter);

            //productOptionsRecyclerAdapter.notifyDataSetChanged();

            Log.d("UpdateProductInfoActivity", "AddNewOption");

    }


    private void CameraNGalleryImageListener() {

        //open camera to set item image
        btnProductImg_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE);
                }
            }
        });


        //open gallery to set item attachment image
        btnProductImg_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE);
                }
            }
        });


        //open camera to set item image
        btnProductImg_camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE2);
                }
            }
        });


        //open gallery to set item attachment image
        btnProductImg_gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE2);
                }
            }
        });


        //open camera to set item image
        btnProductImg_camera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE3);
                }
            }
        });


        //open gallery to set item attachment image
        btnProductImg_gallery3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE3);
                }
            }
        });


        //open camera to set item image
        btnProductImg_camera4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.CAMERA}, LoginActivity.MY_CAMERA_PERMISSION_CODE_IMAGE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, LoginActivity.CAMERA_REQUEST_IMAGE4);
                }
            }
        });


        //open gallery to set item attachment image
        btnProductImg_gallery4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UpdateProductInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateProductInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, LoginActivity.MY_GALLERY_PERMISSION_CODE_IMAGE);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, LoginActivity.GALLERY_REQUEST_IMAGE4);
                }
            }
        });
    }


    private void DeleteProductBtnClicked() {
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductInfoActivity.this)
                        .setMessage("هل انت متاكد من حذف المادة")
                        .setNegativeButton("كلا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DeleteProduct();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    private void DeleteProduct() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar_updateProductInfoActivity.setVisibility(View.VISIBLE);


        JSONObject jsonObject = new JSONObject();
        Log.d("hello", productImageStr.length + "");

        try {
            jsonObject.put("productId", productId);
            jsonObject.put("productCode", txtProductCode.getText().toString());

            new Thread(() -> {

                Log.d("hello", jsonObject.toString());
                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                // do background stuff here
                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/deleteProduct.php")
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
                    Toast.makeText(UpdateProductInfoActivity.this, finalS, Toast.LENGTH_SHORT).show();


                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar_updateProductInfoActivity.setVisibility(View.INVISIBLE);

                    if (finalS.contains("deleted successfully")) {
                        UpdateProductActivity.recyclerProducts.setAdapter(null);
                        finish();
                    }
                });
            }).start();
        } catch (JSONException e) {
            e.printStackTrace();

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar_updateProductInfoActivity.setVisibility(View.INVISIBLE);
        }

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


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            productImageStr = outputStream.toByteArray();
            //Bitmap resizedImage = ResizeImage(photo);
            picbProductImage.setImageBitmap(photo);
            //productImageStr = NewProductActivity.getBytesFromBitmap(resizedImage);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                productImageStr = outputStream.toByteArray();
//                Bitmap resizedImage = ResizeImage(selectedImage);
                picbProductImage.setImageBitmap(selectedImage);
//                productImageStr = NewProductActivity.getBytesFromBitmap(resizedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UpdateProductInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE2 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            productImageStr2 = outputStream.toByteArray();
//            Bitmap resizedImage = ResizeImage(photo);
            picbProductImage2.setImageBitmap(photo);
//            productImageStr2 = NewProductActivity.getBytesFromBitmap(resizedImage);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE2 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                productImageStr2 = outputStream.toByteArray();
//                Bitmap resizedImage = ResizeImage(selectedImage);
                picbProductImage2.setImageBitmap(selectedImage);
//                productImageStr2 = NewProductActivity.getBytesFromBitmap(resizedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UpdateProductInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE3 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            productImageStr3 = outputStream.toByteArray();
//            Bitmap resizedImage = ResizeImage(photo);
            picbProductImage3.setImageBitmap(photo);
//            productImageStr3 = NewProductActivity.getBytesFromBitmap(resizedImage);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE3 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                productImageStr3 = outputStream.toByteArray();
//                Bitmap resizedImage = ResizeImage(selectedImage);
                picbProductImage3.setImageBitmap(selectedImage);
//                productImageStr3 = NewProductActivity.getBytesFromBitmap(resizedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UpdateProductInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == LoginActivity.CAMERA_REQUEST_IMAGE4 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            productImageStr4 = outputStream.toByteArray();
//            Bitmap resizedImage = ResizeImage(photo);
            picbProductImage4.setImageBitmap(photo);
//            productImageStr4 = NewProductActivity.getBytesFromBitmap(resizedImage);
            //
            //
            //picbProductImage.setImageBitmap(photo);
            //itemImageStr = getBytesFromBitmap(photo);

        } else if (requestCode == LoginActivity.GALLERY_REQUEST_IMAGE4 && resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                productImageStr4 = outputStream.toByteArray();
//                Bitmap resizedImage = ResizeImage(selectedImage);
                picbProductImage4.setImageBitmap(selectedImage);
//                productImageStr4 = NewProductActivity.getBytesFromBitmap(resizedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(UpdateProductInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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


    private void OnSelectingCatInSpinner() {
        spinnerProductMainCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CategoriesModel categoriesModel : categoriesArrayList) {
                    if (categoriesModel.getCategoryTitle().equals
                            (spinnerProductMainCat.getSelectedItem().toString())
                            && categoriesModel.getCategoryLevel().equals("1")) {

                        mainCatId = categoriesModel.getCategoryId();
                    }
                }
                PopulateSub1Cat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerProductSub1Cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CategoriesModel categoriesModel : categoriesArrayList) {
                    if (categoriesModel.getCategoryTitle().equals
                            (spinnerProductSub1Cat.getSelectedItem().toString())
                            && categoriesModel.getCategoryLevel().equals("2")) {

                        sub1CatId = categoriesModel.getCategoryId();
                    }
                }
                PopulateSub2Cat();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinnerProductSub2Cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CategoriesModel categoriesModel : categoriesArrayList) {
                    if (categoriesModel.getCategoryTitle().equals
                            (spinnerProductSub2Cat.getSelectedItem().toString())
                            && categoriesModel.getCategoryLevel().equals("3")) {

                        sub2CatId = categoriesModel.getCategoryId();
                    }
                }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }


    private void PopulateMainCat() {

        String selectedProductMainCat = null;
        ArrayList<String> mainCategoriesArrayList = new ArrayList<>();
        mainCategoriesArrayList.add("Choose main category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("1")) {
                mainCategoriesArrayList.add(categoriesModel.getCategoryTitle());
                if (categoriesModel.getCategoryId().equals(mainCatId)) {
                    selectedProductMainCat = categoriesModel.getCategoryTitle();
                }
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
        Log.d("populateMAinCat", selectedProductMainCat);
        spinnerProductMainCat.setSelection(mainCategoriesArrayList.indexOf(selectedProductMainCat));
        PopulateSub1Cat();
    }


    private void PopulateSub1Cat() {

        String selectedProductSub1Cat = null;
        ArrayList<String> sub1CategoriesArrayList = new ArrayList<>();
        sub1CategoriesArrayList.add("Choose sub 1 category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("2") && mainCatId.equals(categoriesModel.getParentCategoryId())) {
                sub1CategoriesArrayList.add(categoriesModel.getCategoryTitle());

                if (sub1CatId != null && categoriesModel.getCategoryId().equals(sub1CatId)) {
                    selectedProductSub1Cat = categoriesModel.getCategoryTitle();
                }
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
        spinnerProductSub1Cat.setSelection(sub1CategoriesArrayList.indexOf(selectedProductSub1Cat));
        PopulateSub2Cat();
    }


    private void PopulateSub2Cat() {

        String selectedProductSub2Cat = null;
        ArrayList<String> sub2CategoriesArrayList = new ArrayList<>();
        sub2CategoriesArrayList.add("Choose sub 2 category");
        for (CategoriesModel categoriesModel : categoriesArrayList) {
            if (categoriesModel.getCategoryLevel().equals("3") && sub1CatId.equals(categoriesModel.getParentCategoryId())) {
                sub2CategoriesArrayList.add(categoriesModel.getCategoryTitle());
                if (sub2CatId != null && categoriesModel.getCategoryId().equals(sub2CatId)) {
                    selectedProductSub2Cat = categoriesModel.getCategoryTitle();
                }
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
        spinnerProductSub2Cat.setSelection(sub2CategoriesArrayList.indexOf(selectedProductSub2Cat));
    }


    private void GetProductInfo() {

        AtomicBoolean replayThread = new AtomicBoolean(false);
        do {
            new Thread(() -> {

                Log.d("hello", "productId  " + productId);

                JSONObject myJsonObject = new JSONObject();
                try {
                    myJsonObject.put("productId", productId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(myJsonObject));

                Request request = new Request.Builder()
                        .url(LoginActivity.serverIp + "/getSelectedProduct.php")
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

                    try {
                        runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("hello", "onPostExecute 1");
                                Log.d("hello", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("hello", "onPostExecute 2");


                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                int id = jsonObject.getInt("id");
                                String productCode = jsonObject.get("productCode").toString();
                                txtProductCode.setText(productCode);
                                String productName = jsonObject.get("productName").toString();
                                etxtProductName.setText(productName);
                                productImageStr = Base64.decode(jsonObject.getString("productImage"), 0);
                                picbProductImage.setImageBitmap(NewProductActivity.decodeBase64Profile(productImageStr));

                                productImageStr2 = Base64.decode(jsonObject.getString("productImage2"), Base64.DEFAULT);
                                picbProductImage2.setImageBitmap(NewProductActivity.decodeBase64Profile(productImageStr2));

                                productImageStr3 = Base64.decode(jsonObject.getString("productImage3"), Base64.DEFAULT);
                                picbProductImage3.setImageBitmap(NewProductActivity.decodeBase64Profile(productImageStr3));

                                productImageStr4 = Base64.decode(jsonObject.getString("productImage4"), Base64.DEFAULT);
                                picbProductImage4.setImageBitmap(NewProductActivity.decodeBase64Profile(productImageStr4));

                                mainCatId = jsonObject.getString("mainCatId");
                                sub1CatId = jsonObject.getString("sub1CatId");
                                sub2CatId = jsonObject.getString("sub2CatId");
                                String productDate = jsonObject.getString("productDate");
                                btnProductDate.setText(productDate);

                                String expDate = jsonObject.getString("expDate");
                                if (expDate == null || expDate.equals("null")) {
                                    cbProductExp.setChecked(false);
                                } else {
                                    cbProductExp.setChecked(true);
                                    btnProductExpDate.setText(expDate);
                                }

                                productBrand = jsonObject.getString("brand");
                                String provider = jsonObject.getString("provider");
                                etxtProvider.setText(provider);


                                Log.d("hello", finalS);
                                String rate = jsonObject.getString("rate");
                                productRateBar.setRating(Float.parseFloat(rate));
                                productRateBar.setIsIndicator(true);

                                String numOfSells = jsonObject.getString("numOfSells");
                                txtSellsCount.setText(numOfSells);

                                String lastSellDate = jsonObject.getString("lastSellDate");
                                if (lastSellDate != null && !lastSellDate.trim().equals("null"))
                                    txtLastSellDate.setText(lastSellDate);
                                else
                                    txtLastSellDate.setText("No sells");


                                    String productOptions = jsonObject.getString("options");
                                    Log.d("hello", "product options : " + productOptions);
                                    JSONArray jsonArrayOptions = new JSONArray(productOptions);
                                    for (int i = 0; i < jsonArrayOptions.length(); i++) {

                                        JSONObject jsonObjectOption = jsonArrayOptions.getJSONObject(i);
                                        String optionId = jsonObjectOption.getString("id");
                                        String optionTitle = jsonObjectOption.getString("optionTitle");
                                        String description = jsonObjectOption.getString("description");
                                        String sellPrice = jsonObjectOption.getString("sellPrice");
                                        String discount = jsonObjectOption.getString("discount");
                                        String availableQty = jsonObjectOption.getString("availableQty");

                                        Log.d("hello", "product options ids : " + optionId);
                                        productOptionsArrayList.add(new ProductOptionsModel(
                                                optionTitle, description, sellPrice, discount, availableQty
                                        ));
                                    }


                                    ProductOptionsRecyclerAdapter productOptionsRecyclerAdapter = new ProductOptionsRecyclerAdapter(
                                            UpdateProductInfoActivity.this,
                                            R.layout.product_option_recycler_item, productOptionsArrayList, recyclerProductOptions);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerProductOptions.setLayoutManager(mLayoutManager);
                                    recyclerProductOptions.setItemAnimator(new DefaultItemAnimator());
                                    recyclerProductOptions.setAdapter(productOptionsRecyclerAdapter);



                                getCategories();
                                getBrandsNames();


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("hello", "error " + e.getMessage());
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("hello", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hello", e.getMessage());
                    replayThread.set(true);
                }
            }).start();
        } while (replayThread.get());
    }


    private void getBrandsNames() {
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

                        Log.d("hello", "updated product brand = " + productBrand);
                        if (productBrand != null && !productBrand.trim().equals("null"))
                            spinnerBrands.setSelection(brandsNamesArrayList.indexOf(productBrand));
                        else
                            spinnerBrands.setSelection(1);

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        progressBar_updateProductInfoActivity.setVisibility(View.INVISIBLE);

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


    private void UpdateProductInfo() {
        btnUpdateProductInfo.setOnClickListener(view -> {

            if (txtProductCode.getText().toString().trim().equals("") ||
                    spinnerProductMainCat.getSelectedItem().toString().equals("Choose main category") ||
                    etxtProductName.getText().toString().trim().equals("") ||
                    productImageStr == null ||
                    productImageStr2 == null ||
                    productImageStr3 == null ||
                    productImageStr4 == null ||
                    productOptionsArrayList.size() == 0) {

                Toast.makeText(UpdateProductInfoActivity.this, "ادخل معلومات المنتج رجاءا", Toast.LENGTH_SHORT).show();
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progressBar_updateProductInfoActivity.setVisibility(View.VISIBLE);


                JSONObject jsonObject = new JSONObject();
                Log.d("hello", productImageStr.length + "");

                try {
                    jsonObject.put("productId", productId);
                    jsonObject.put("productCode", txtProductCode.getText().toString());
                    jsonObject.put("productName", etxtProductName.getText().toString().trim());

                    String brand = null;
                    if (!spinnerBrands.getSelectedItem().toString().trim().equals("no brand") && !spinnerBrands.getSelectedItem().toString().equals("Choose brand name"))
                        brand = spinnerBrands.getSelectedItem().toString().trim();
                    jsonObject.put("brand", brand);


                    if (!btnProductExpDate.getText().toString().toUpperCase().equals("NO EXPIRATION DATE")) {

                        jsonObject.put("expDate", btnProductExpDate.getText().toString());
                        Log.d("hello", "expDate " + btnProductExpDate.getText().toString());
                    } else {

                        jsonObject.put("expDate", null);
                    }

                    String mainCat = null;
                    for (CategoriesModel categoriesModel : categoriesArrayList) {
                        if (categoriesModel.getCategoryLevel().equals("1") &&
                                categoriesModel.getCategoryTitle().equals(spinnerProductMainCat.getSelectedItem().toString())) {
                            mainCat = categoriesModel.getCategoryId();
                        }
                    }
                    jsonObject.put("mainCat", mainCat);
                    Log.d("hello", "mainCat " + mainCat);

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
                    Log.d("hello", "sub1Cat " + sub1Cat);

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
                    Log.d("hello", "sub2Cat " + sub2Cat);

                    jsonObject.put("productImage", Base64.encodeToString(productImageStr, Base64.DEFAULT));
                    jsonObject.put("productImage2", Base64.encodeToString(productImageStr2, Base64.DEFAULT));
                    jsonObject.put("productImage3", Base64.encodeToString(productImageStr3, Base64.DEFAULT));
                    jsonObject.put("productImage4", Base64.encodeToString(productImageStr4, Base64.DEFAULT));


                    JSONArray jsonArray = new JSONArray();
                    for (ProductOptionsModel productOptionsModel : productOptionsArrayList) {
                        JSONObject optionsJsonObj = new JSONObject();
                        optionsJsonObj.put("optionTitle", productOptionsModel.getOptionTitle());
                        optionsJsonObj.put("optionSellPrice", productOptionsModel.getSellPrice());

                        String discount = "0";
                        if (!productOptionsModel.getDiscount().equals(""))
                            discount = productOptionsModel.getDiscount();
                        optionsJsonObj.put("optionDiscount", discount);

                        optionsJsonObj.put("optionAvailableQty", productOptionsModel.getAvailableQty());
                        optionsJsonObj.put("optionDescription", productOptionsModel.getDescription());
                        jsonArray.put(optionsJsonObj);
                    }
                    jsonObject.put("options", jsonArray);

                    new Thread(() -> {

                        Log.d("hello", jsonObject.toString());
                        RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));

                        // do background stuff here
                        Request request = new Request.Builder()
                                .url(LoginActivity.serverIp + "/UpdateProductInfo.php")
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
                            Toast.makeText(UpdateProductInfoActivity.this, finalS, Toast.LENGTH_SHORT).show();


                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            progressBar_updateProductInfoActivity.setVisibility(View.INVISIBLE);
                        });
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();


                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar_updateProductInfoActivity.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    private void BtnProductExpDateClicked() {
        btnProductExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = Integer.parseInt((String) DateFormat.format("MM", new Date().getTime())) - 1;
                int day = Integer.parseInt((String) DateFormat.format("dd", new Date().getTime()));
                int year = Integer.parseInt((String) DateFormat.format("yyyy", new Date().getTime()));
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProductInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
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


    private void ProductExpirationCheckBoxClicked() {
        cbProductExp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    btnProductExpDate.setText("NO EXPIRATION DATE");
                    btnProductExpDate.setEnabled(false);
                } else {
                    btnProductExpDate.setEnabled(true);
                    btnProductExpDate.setText((String) DateFormat.format("yyyy-MM-dd", new Date().getTime()));
                }
            }
        });
    }
}
