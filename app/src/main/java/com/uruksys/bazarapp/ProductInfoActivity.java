package com.uruksys.bazarapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductInfoActivity extends AppCompatActivity {

    TextView txtProductCode, txtProductName, txtNumOfReviews, txtProductReviews, txtAddReviewToProduct, txtProductPrice,
            txtQtyToBuy, txtProductAvailability, txtProductDescription, txtProductDiscountPrice, txtProviderName, txtItemBrand,
            txtItemDate, txtItemExpDate, txtItemSellsCount, txtItemLastSellDate;
    ImageView imgProductImage;
    ImageView imgProductRateStar1, imgProductRateStar2, imgProductRateStar3, imgProductRateStar4,
            imgProductRateStar5, imgIncrementQty, imgDecrementQty, imgFinishThisActivity;
    Button btnAddProductToCart;
    ProgressBar productInfoActivityProgressBar;
    public static RecyclerView productOptionsRecycler, itemImagesRecycler;


    ItemDetailsModel selectedItemDetails;
    public static ItemTypeDetailsModel selectedItemTypeModel;


    private Animator currentAnimator;
    private int shortAnimationDuration;


    public static int selectedItemTypePositionInArrayList = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        imgFinishThisActivity = findViewById(R.id.imgFinishThisActivity);
        imgFinishThisActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtProductCode = findViewById(R.id.txtProductCode);
        txtProductName = findViewById(R.id.txtProductName);
        txtNumOfReviews = findViewById(R.id.txtNumOfReviews);
        txtProductReviews = findViewById(R.id.txtProductReviews);
        txtAddReviewToProduct = findViewById(R.id.txtAddReviewToProduct);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtQtyToBuy = findViewById(R.id.txtQtyToBuy);
        txtProductAvailability = findViewById(R.id.txtProductAvailability);
        txtProductDescription = findViewById(R.id.txtProductDescription);

        itemImagesRecycler = findViewById(R.id.itemImagesRecycler);
        imgProductImage = findViewById(R.id.imgProductImage);

        imgProductRateStar1 = findViewById(R.id.imgProductRateStar1);
        imgProductRateStar2 = findViewById(R.id.imgProductRateStar2);
        imgProductRateStar3 = findViewById(R.id.imgProductRateStar3);
        imgProductRateStar4 = findViewById(R.id.imgProductRateStar4);
        imgProductRateStar5 = findViewById(R.id.imgProductRateStar5);
        imgIncrementQty = findViewById(R.id.imgIncrementQty);
        imgDecrementQty = findViewById(R.id.imgDecrementQty);
        btnAddProductToCart = findViewById(R.id.btnAddProductToCart);
        productInfoActivityProgressBar = findViewById(R.id.productInfoActivityProgressBar);

        txtProviderName = findViewById(R.id.txtProviderName);
        txtItemBrand = findViewById(R.id.txtItemBrand);
        txtItemDate = findViewById(R.id.txtItemDate);
        txtItemExpDate = findViewById(R.id.txtItemExpDate);
        txtItemSellsCount = findViewById(R.id.txtItemSellsCount);
        txtItemLastSellDate = findViewById(R.id.txtItemLastSellDate);
        txtProductDiscountPrice = findViewById(R.id.txtProductDiscountPrice);

        productOptionsRecycler = findViewById(R.id.productOptionsRecycler);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgFinishThisActivity.setScaleX(-1);
        }

        Intent intent = getIntent();
        int selectedItemId = intent.getIntExtra("itemId", 0);
        int selectedItemTypeId = intent.getIntExtra("itemTypeId", 0);

        GetProductDetails(selectedItemId, selectedItemTypeId);
        incrementOrDecrementDesiredQty();
        AddReviewToProduct();
        DisplayProductReviews();
        AddToCart();

        EnlargeProductImagesOnClick();

    }


    private void GetProductDetails(int selectedItemId, int selectedItemTypeId) {

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        MainActivity.disableEnableControls(false, findViewById(android.R.id.content));
        productInfoActivityProgressBar.setVisibility(View.VISIBLE);

        AtomicBoolean replayThread = new AtomicBoolean(false);

        new Thread(() -> {
            do {
                replayThread.set(false);

                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetOneItemDetailsForCustomer + selectedItemId)
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
                            JSONArray jsonArray = new JSONArray(finalS);
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            int id = jsonObject1.getInt("id");
                            String itemCode = jsonObject1.getString("itemCode");
                            String itemName = jsonObject1.getString("itemName");
                            int shopId = jsonObject1.getInt("shopId");
                            String shopName = jsonObject1.getJSONObject("Shop").getString("name");
                            String shopLogo = jsonObject1.getJSONObject("Shop").getString("logo");
                            String shopSpecialty = jsonObject1.getJSONObject("Shop").getString("specialty");
                            int categoryId = jsonObject1.getInt("categoryId");
                            String catName = jsonObject1.getJSONObject("Category").getString("catName");
                            String itemCreatedAt = jsonObject1.getString("createdAt");
                            String itemUpdatedAt = jsonObject1.getString("updatedAt");
                            String brandId = null, brandName = null, brandLogo = null, brandDescription = null;
                            try {

                                brandId = String.valueOf(jsonObject1.getInt("brandId"));
                                brandName = jsonObject1.getJSONObject("Brand").getString("brandName");
                                brandLogo = jsonObject1.getJSONObject("Brand").getString("brandLogo");
                                brandDescription = jsonObject1.getJSONObject("Brand").getString("description");
                            } catch (JSONException jsonException) {
                                Log.d("GetProductDetails", "GetProductDetails: " + jsonException);
                            }

                            JSONArray itemTypes = jsonObject1.getJSONArray("ItemTypes");
                            ArrayList<ItemTypeDetailsModel> itemTypeDetailsArrayList = new ArrayList<>();
                            for (int i = 0; i < itemTypes.length(); i++) {
                                JSONObject jsonObject = itemTypes.getJSONObject(i);
                                int typeId = jsonObject.getInt("id");
                                int itemId = jsonObject.getInt("itemId");
                                String typeName = jsonObject.getString("typeName");
                                double availableQty = jsonObject.getDouble("availableQty");
                                double sellPrice = jsonObject.getDouble("sellPrice");
                                double discountPrice = jsonObject.getDouble("discountPrice");
                                String description = jsonObject.getString("description");
                                String expDate = jsonObject.getString("expDate");
                                String typeCreatedAt = jsonObject.getString("createdAt");
                                String typeUpdatedAt = jsonObject.getString("updatedAt");
                                String lastSellDate = null;
                                int numOfSells = 0;
                                if (jsonObject.has("InvoiceItems") && jsonObject.getJSONArray("InvoiceItems").length() > 0) {

                                    JSONObject itemSellsDetails = jsonObject.getJSONArray("InvoiceItems").getJSONObject(0);
                                    Log.d("productInfoActivity", "GetProductDetails: " + itemSellsDetails);
                                    lastSellDate = itemSellsDetails.getString("lastSellDate");
                                    numOfSells = itemSellsDetails.getInt("numOfSells");
                                }

                                ArrayList<String> itemTypeImagesArrayList = new ArrayList<>();
                                JSONArray itemTypeImgJson = jsonObject.getJSONArray("ItemTypeImages");
                                for (int j = 0; j < itemTypeImgJson.length(); j++) {
                                    JSONObject typeImgJsonObject = itemTypeImgJson.getJSONObject(j);
                                    itemTypeImagesArrayList.add(MainActivity.itemTypeImgUrl + typeImgJsonObject.getString("imageLoc"));
                                }

                                ItemTypeDetailsModel itemTypeDetailsModel = new ItemTypeDetailsModel(typeId, itemId, typeName, availableQty,
                                        sellPrice, discountPrice, description, expDate, typeCreatedAt, typeUpdatedAt, lastSellDate,
                                        numOfSells, itemTypeImagesArrayList);
                                itemTypeDetailsArrayList.add(itemTypeDetailsModel);

                                if (typeId == selectedItemTypeId) {

                                    selectedItemTypePositionInArrayList = itemTypeDetailsArrayList.size() - 1;
                                    selectedItemTypeModel = itemTypeDetailsModel;
                                }
                            }


                            JSONArray itemReviewsJson = jsonObject1.getJSONArray("ItemReviews");
                            ArrayList<ItemReviews> itemReviewsArrayList = new ArrayList<>();
                            double totalRates = 0;
                            for (int i = 0; i < itemReviewsJson.length(); i++) {
                                JSONObject jsonObject = itemReviewsJson.getJSONObject(i);
                                int reviewId = jsonObject.getInt("id");
                                int itemId = jsonObject.getInt("itemId");
                                String reviewerName = jsonObject.getString("reviewerName");
                                String customerId = jsonObject.getString("customerId");
                                String title = jsonObject.getString("title");
                                String body = jsonObject.getString("body");
                                double rate = jsonObject.getDouble("rate");
                                String reviewCreatedAt = jsonObject.getString("createdAt");

                                totalRates += rate;
                                itemReviewsArrayList.add(new ItemReviews(reviewId, itemId, reviewerName, customerId, title, body, rate, reviewCreatedAt));
                            }
                            double avgRate = totalRates / itemReviewsJson.length();

                            selectedItemDetails = new ItemDetailsModel(id, itemCode, itemName, shopId, shopName, shopLogo, shopSpecialty,
                                    categoryId, catName, itemCreatedAt, itemUpdatedAt, brandId, brandName,
                                    brandLogo, brandDescription, itemTypeDetailsArrayList,
                                    itemReviewsArrayList, avgRate);

                            txtProductCode.setText(itemCode);
                            txtProductName.setText(itemName);

                            if (brandName.equals("null"))
                                txtItemBrand.setText(getString(R.string.String6_ProductInfoActivity));
                            else
                                txtItemBrand.setText(brandName);

                            txtProviderName.setText(shopName);

                            switch ((int) avgRate) {
                                case 1:
                                    imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                                    break;
                                case 2:
                                    imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                                    break;
                                case 3:
                                    imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                                    break;
                                case 4:
                                    imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                                    break;
                                case 5:
                                    imgProductRateStar1.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar2.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar3.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar4.setImageResource(R.drawable.ic_star_yellow);
                                    imgProductRateStar5.setImageResource(R.drawable.ic_star_yellow);
                                    break;
                                default:
                                    break;
                            }
                            txtNumOfReviews.setText((String.valueOf(itemReviewsJson.length())));


                            //setting selected item type details
                            setSelectedItemTypeDetails();

                            ItemTypesRecyclerAdapter itemTypesRecyclerAdapter = new ItemTypesRecyclerAdapter(
                                    getApplicationContext(),
                                    R.layout.item_type_recycler_item,
                                    itemTypeDetailsArrayList,
                                    this
                            );
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            productOptionsRecycler.setLayoutManager(mLayoutManager);
                            productOptionsRecycler.setItemAnimator(new DefaultItemAnimator());
                            productOptionsRecycler.setAdapter(itemTypesRecyclerAdapter);

                            GetProductQtyInCart();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("productInfoActivity", "error2 " + e.getMessage() + "");
                            Toast.makeText(this, getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("productInfoActivity", "error1 " + e.getMessage() + "");
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    public void setSelectedItemTypeDetails(){

        txtProductPrice.setText(String.valueOf(selectedItemTypeModel.getSellPrice()));
        txtProductDiscountPrice.setText(String.valueOf(selectedItemTypeModel.getDiscountPrice()));

        if (selectedItemTypeModel.getDiscountPrice() > 0) {
            txtProductPrice.setForeground(getDrawable(R.drawable.curved_line_on_text_view));
            txtProductDiscountPrice.setVisibility(View.VISIBLE);
        } else {
            txtProductPrice.setForeground(null);
            txtProductDiscountPrice.setVisibility(View.INVISIBLE);
        }

        if (!selectedItemTypeModel.getDescription().trim().equals("null"))
            txtProductDescription.setText(selectedItemTypeModel.getDescription());
        else
            txtProductDescription.setText("");

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = isoFormat.parse(selectedItemTypeModel.getTypeCreatedAt());
            String date = dateFormat.format(newDate);
            txtItemDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
            txtItemDate.setText(selectedItemTypeModel.getTypeCreatedAt());
        }

        if (selectedItemTypeModel.getExpDate().equals("null"))
            txtItemExpDate.setText(getString(R.string.String5_ProductInfoActivity));
        else {
            try {
                Date newDate = isoFormat.parse(selectedItemTypeModel.getExpDate());
                String date = dateFormat.format(newDate);
                txtItemExpDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
                txtItemExpDate.setText(selectedItemTypeModel.getExpDate());
            }
        }


        txtItemSellsCount.setText(String.valueOf(selectedItemTypeModel.getNumOfSells()));
        if (selectedItemTypeModel.getLastSellDate() == null)
            txtItemLastSellDate.setText(getString(R.string.String7_ProductInfoActivity));
        else {
            try {
                Date newDate = isoFormat.parse(selectedItemTypeModel.getLastSellDate());
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                String date = dateTimeFormat.format(newDate);
                txtItemLastSellDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
                txtItemLastSellDate.setText(selectedItemTypeModel.getLastSellDate());
            }
        }

        //get first img from url
        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(selectedItemTypeModel.getItemTypeImagesArrayList().get(0));
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            runOnUiThread(() -> {

                imgProductImage.setImageBitmap(finalMyImg);
            });
        }).start();

        ProductImagesRecyclerAdapter productImagesRecyclerAdapter = new ProductImagesRecyclerAdapter(
                getApplicationContext(),
                R.layout.product_image_recycler_item,
                selectedItemTypeModel.getItemTypeImagesArrayList(),
                this
        );
        LinearLayoutManager productImgAdapterLayoutManager = new LinearLayoutManager(getApplicationContext());
        productImgAdapterLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemImagesRecycler.setLayoutManager(productImgAdapterLayoutManager);
        itemImagesRecycler.setItemAnimator(new DefaultItemAnimator());
        itemImagesRecycler.setAdapter(productImagesRecyclerAdapter);
    }


    private void EnlargeProductImagesOnClick() {
        imgProductImage.setOnClickListener(view -> zoomImageFromThumb(imgProductImage, selectedItemTypeModel.getItemTypeImagesArrayList().get(0)));
    }


    private void AddToCart() {
        btnAddProductToCart.setOnClickListener(view -> {
            if (!txtQtyToBuy.getText().toString().equals("0")) {
                PutNewProductsInShoppingCart(Integer.parseInt(txtQtyToBuy.getText().toString()));

                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.alert_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setView(v);

                TextView txtAlertDialogTitle = v.findViewById(R.id.txtAlertDialogTitle);
                txtAlertDialogTitle.setText(R.string.String1_ProductInfoActivity);
                Button btnAlertDialogConfirm = v.findViewById(R.id.btnAlertDialogConfirm);
                btnAlertDialogConfirm.setText(R.string.String2_ProductInfoActivity);
                Button btnAlertDialogCancel = v.findViewById(R.id.btnAlertDialogCancel);
                btnAlertDialogCancel.setText(R.string.String3_ProductInfoActivity);

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                btnAlertDialogConfirm.setOnClickListener((View view1) -> {

                    alertDialog.dismiss();
                    finish();
                    Intent intent = new Intent(this, ShoppingCartActivity.class);
                    startActivity(intent);
                });


                btnAlertDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            } else {
                Toast.makeText(this, getString(R.string.String4_ProductInfoActivity), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void PutNewProductsInShoppingCart(int qtyToBuy) {

        MySqliteDB mySqliteDB = new MySqliteDB(this);
        Cursor cursor = mySqliteDB.GetProducts_shoppingCart();

        boolean productAlreadyInCart = false;

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int itemTypeId = cursor.getInt(cursor.getColumnIndex("itemTypeId"));

                    if (itemTypeId == selectedItemTypeModel.getTypeId()) {

                        productAlreadyInCart = true;
                        mySqliteDB.UpdateOnProduct_shoppingCart(itemTypeId, qtyToBuy);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }


        if (!productAlreadyInCart) {
            mySqliteDB.InsertNewProduct_shoppingCart(selectedItemTypeModel.getTypeId(), qtyToBuy);
        }
    }


    public void GetProductQtyInCart() {

        Log.d("lifeCycleChecking", "ProductInfoActivity > GetProductQtyInCart");
        MySqliteDB mySqliteDB = new MySqliteDB(this);
        Cursor cursor = mySqliteDB.GetSingleProductQty_shoppingCart(selectedItemTypeModel.getTypeId());

        int quantity = 0;
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }

        txtQtyToBuy.setText(String.valueOf(quantity));

        if (selectedItemTypeModel.getAvailableQty() >= quantity) {

            btnAddProductToCart.setEnabled(true);
            txtProductAvailability.setVisibility(View.INVISIBLE);
//            txtProductAvailability.setText(R.string.txtProductAvailability_ProductInfoActivity);
//            txtProductAvailability.setTextColor(Color.parseColor("#669900"));
        } else {

            btnAddProductToCart.setEnabled(false);

            txtProductAvailability.setVisibility(View.VISIBLE);
            txtProductAvailability.setText(R.string.String8_ProductInfoActivity);
            txtProductAvailability.setTextColor(getColor(R.color.colorAccent));
        }

//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        MainActivity.disableEnableControls(true, findViewById(android.R.id.content));
        productInfoActivityProgressBar.setVisibility(View.INVISIBLE);
    }


    private void incrementOrDecrementDesiredQty() {
        imgIncrementQty.setOnClickListener(view -> {
            txtQtyToBuy.setText(String.valueOf(Integer.parseInt(txtQtyToBuy.getText().toString()) + 1));
            if (selectedItemTypeModel.getAvailableQty() >= Integer.parseInt(txtQtyToBuy.getText().toString())) {

//                txtProductAvailability.setText(R.string.txtProductAvailability_ProductInfoActivity);
//                txtProductAvailability.setTextColor(Color.parseColor("#669900"));
                txtProductAvailability.setVisibility(View.INVISIBLE);
                btnAddProductToCart.setEnabled(true);
            } else {

                txtProductAvailability.setText(R.string.String8_ProductInfoActivity);
                txtProductAvailability.setTextColor(getColor(R.color.colorAccent));

                txtProductAvailability.setVisibility(View.VISIBLE);
                btnAddProductToCart.setEnabled(false);
            }
        });


        imgDecrementQty.setOnClickListener(view -> {
            if (Integer.parseInt(txtQtyToBuy.getText().toString()) > 0) {

                txtQtyToBuy.setText(String.valueOf(Integer.parseInt(txtQtyToBuy.getText().toString()) - 1));
                if (selectedItemTypeModel.getAvailableQty() >= Integer.parseInt(txtQtyToBuy.getText().toString())) {

//                    txtProductAvailability.setText(R.string.txtProductAvailability_ProductInfoActivity);
//                    txtProductAvailability.setTextColor(Color.parseColor("#669900"));
                    txtProductAvailability.setVisibility(View.INVISIBLE);
                    btnAddProductToCart.setEnabled(true);
                } else {

                    txtProductAvailability.setText(R.string.String8_ProductInfoActivity);
                    txtProductAvailability.setTextColor(getColor(R.color.colorAccent));

                    txtProductAvailability.setVisibility(View.VISIBLE);
                    btnAddProductToCart.setEnabled(false);
                }
            }
        });
    }


    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }


    private void AddReviewToProduct() {
        txtAddReviewToProduct.setOnClickListener(view -> {

            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.add_review_to_product_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductInfoActivity.this)
                    .setView(view1);


            ImageView imgRatingStar1 = view1.findViewById(R.id.imgRatingStar1);
            ImageView imgRatingStar2 = view1.findViewById(R.id.imgRatingStar2);
            ImageView imgRatingStar3 = view1.findViewById(R.id.imgRatingStar3);
            ImageView imgRatingStar4 = view1.findViewById(R.id.imgRatingStar4);
            ImageView imgRatingStar5 = view1.findViewById(R.id.imgRatingStar5);
            TextView txtProductName = view1.findViewById(R.id.txtProductName);
            txtProductName.setText(this.txtProductName.getText().toString());
            EditText etxtReviewerNickname = view1.findViewById(R.id.etxtReviewerNickname);
            EditText etxtReviewTitle = view1.findViewById(R.id.etxtReviewTitle);
            EditText etxtReviewBody = view1.findViewById(R.id.etxtReviewBody);
            Button btnSubmitReview = view1.findViewById(R.id.btnSubmitReview);
            AtomicInteger selectedRate = new AtomicInteger();

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            imgRatingStar1.setOnClickListener((View v) -> {
                imgRatingStar1.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar2.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar3.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar4.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar5.setImageResource(R.drawable.ic_star_grey);
                selectedRate.set(1);
            });

            imgRatingStar2.setOnClickListener((View v) -> {
                imgRatingStar1.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar2.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar3.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar4.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar5.setImageResource(R.drawable.ic_star_grey);
                selectedRate.set(2);
            });

            imgRatingStar3.setOnClickListener((View v) -> {
                imgRatingStar1.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar2.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar3.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar4.setImageResource(R.drawable.ic_star_grey);
                imgRatingStar5.setImageResource(R.drawable.ic_star_grey);
                selectedRate.set(3);
            });

            imgRatingStar4.setOnClickListener((View v) -> {
                imgRatingStar1.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar2.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar3.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar4.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar5.setImageResource(R.drawable.ic_star_grey);
                selectedRate.set(4);
            });

            imgRatingStar5.setOnClickListener((View v) -> {
                imgRatingStar1.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar2.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar3.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar4.setImageResource(R.drawable.ic_star_yellow);
                imgRatingStar5.setImageResource(R.drawable.ic_star_yellow);
                selectedRate.set(5);
            });


            btnSubmitReview.setOnClickListener((View v) -> {
                if (etxtReviewerNickname.getText().toString().trim().equals("")
                        || etxtReviewBody.getText().toString().trim().equals("")
                        || etxtReviewTitle.getText().toString().trim().equals("")
                        || selectedRate.get() == 0) {

                    Toast.makeText(ProductInfoActivity.this, getString(R.string.String9_ProductInfoActivity), Toast.LENGTH_SHORT).show();
                } else {

                    new Thread(() -> {

                        JSONObject jsonObject = new JSONObject();
                        try {


                            jsonObject.put("shopId", selectedItemDetails.getShopId());
                            jsonObject.put("itemId", selectedItemDetails.getItemId());
                            jsonObject.put("rate", selectedRate.get());
                            jsonObject.put("reviewerName", etxtReviewerNickname.getText().toString().trim());
                            jsonObject.put("body", etxtReviewBody.getText().toString().trim());
                            jsonObject.put("title", etxtReviewTitle.getText().toString().trim());
                            jsonObject.put("readByManager", 0);
                            RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
                            Request request = new Request.Builder()
                                    .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.AddNewReview)
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

                                    Log.d("AddReviewToProduct", finalS + "");
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(finalS);

                                        MainActivity.disableEnableControls(false, (ViewGroup) findViewById(android.R.id.content));
                                        productInfoActivityProgressBar.setVisibility(View.VISIBLE);

                                        alertDialog.dismiss();
                                        Toast.makeText(this, getString(R.string.String10_ProductInfoActivity), Toast.LENGTH_SHORT).show();

                                        GetProductDetails(selectedItemDetails.getItemId(), selectedItemTypeModel.getTypeId());

                                        MainActivity.disableEnableControls(true, (ViewGroup) findViewById(android.R.id.content));
                                    } catch (JSONException jsonException) {
                                        jsonException.printStackTrace();
                                        Toast.makeText(this, getString(R.string.String11_ProductInfoActivity), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("AddReviewToProduct", "error1 " + e.getMessage() + "");
                                Toast.makeText(this, getString(R.string.String11_ProductInfoActivity), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("AddReviewToProduct", "error2 " + e.getMessage() + "");
                            Toast.makeText(this, getString(R.string.String11_ProductInfoActivity), Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                }
            });
        });
    }


    private void DisplayProductReviews() {
        txtProductReviews.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProductReviewsActivity.class);
            intent.putExtra("itemId", selectedItemDetails.getItemId());
            startActivity(intent);
        });
    }


    public void zoomImageFromThumb(final View thumbView, String imageStringUrl) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        new Thread(() -> {
            Bitmap myImg = null;
            try {
                URL newUrl = new URL(imageStringUrl);
                myImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bitmap finalMyImg = myImg;
            runOnUiThread(() -> {

                // Load the high-resolution "zoomed-in" image.
                final ImageView expandedImageView = (ImageView) findViewById(
                        R.id.expanded_image);
                expandedImageView.setImageBitmap(finalMyImg);

                // Calculate the starting and ending bounds for the zoomed-in image.
                // This step involves lots of math. Yay, math.
                final Rect startBounds = new Rect();
                final Rect finalBounds = new Rect();
                final Point globalOffset = new Point();

                // The start bounds are the global visible rectangle of the thumbnail,
                // and the final bounds are the global visible rectangle of the container
                // view. Also set the container view's offset as the origin for the
                // bounds, since that's the origin for the positioning animation
                // properties (X, Y).
                thumbView.getGlobalVisibleRect(startBounds);
                findViewById(R.id.ProductInfoActivityContainer).getGlobalVisibleRect(finalBounds, globalOffset);
                startBounds.offset(-globalOffset.x, -globalOffset.y);
                finalBounds.offset(-globalOffset.x, -globalOffset.y);

                // Adjust the start bounds to be the same aspect ratio as the final
                // bounds using the "center crop" technique. This prevents undesirable
                // stretching during the animation. Also calculate the start scaling
                // factor (the end scaling factor is always 1.0).
                float startScale;
                if ((float) finalBounds.width() / finalBounds.height()
                        > (float) startBounds.width() / startBounds.height()) {
                    // Extend start bounds horizontally
                    startScale = (float) startBounds.height() / finalBounds.height();
                    float startWidth = startScale * finalBounds.width();
                    float deltaWidth = (startWidth - startBounds.width()) / 2;
                    startBounds.left -= deltaWidth;
                    startBounds.right += deltaWidth;
                } else {
                    // Extend start bounds vertically
                    startScale = (float) startBounds.width() / finalBounds.width();
                    float startHeight = startScale * finalBounds.height();
                    float deltaHeight = (startHeight - startBounds.height()) / 2;
                    startBounds.top -= deltaHeight;
                    startBounds.bottom += deltaHeight;
                }

                // Hide the thumbnail and show the zoomed-in view. When the animation
                // begins, it will position the zoomed-in view in the place of the
                // thumbnail.
                thumbView.setAlpha(0f);
                expandedImageView.setVisibility(View.VISIBLE);

                // Set the pivot point for SCALE_X and SCALE_Y transformations
                // to the top-left corner of the zoomed-in view (the default
                // is the center of the view).
                expandedImageView.setPivotX(0f);
                expandedImageView.setPivotY(0f);

                // Construct and run the parallel animation of the four translation and
                // scale properties (X, Y, SCALE_X, and SCALE_Y).
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                                startBounds.left, finalBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                                startBounds.top, finalBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                                startScale, 1f))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_Y, startScale, 1f));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;

                // Upon clicking the zoomed-in image, it should zoom back down
                // to the original bounds and show the thumbnail instead of
                // the expanded image.
                final float startScaleFinal = startScale;
                expandedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentAnimator != null) {
                            currentAnimator.cancel();
                        }

                        // Animate the four positioning/sizing properties in parallel,
                        // back to their original values.
                        AnimatorSet set = new AnimatorSet();
                        set.play(ObjectAnimator
                                .ofFloat(expandedImageView, View.X, startBounds.left))
                                .with(ObjectAnimator
                                        .ofFloat(expandedImageView,
                                                View.Y, startBounds.top))
                                .with(ObjectAnimator
                                        .ofFloat(expandedImageView,
                                                View.SCALE_X, startScaleFinal))
                                .with(ObjectAnimator
                                        .ofFloat(expandedImageView,
                                                View.SCALE_Y, startScaleFinal));
                        set.setDuration(shortAnimationDuration);
                        set.setInterpolator(new DecelerateInterpolator());
                        set.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                thumbView.setAlpha(1f);
                                expandedImageView.setVisibility(View.GONE);
                                currentAnimator = null;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                thumbView.setAlpha(1f);
                                expandedImageView.setVisibility(View.GONE);
                                currentAnimator = null;
                            }
                        });
                        set.start();
                        currentAnimator = set;
                    }
                });
            });
        }).start();
    }
}
