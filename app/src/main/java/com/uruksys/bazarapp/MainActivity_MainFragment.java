package com.uruksys.bazarapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity_MainFragment extends Fragment {

    private static final String ARG_PARAM = "";

    private SearchTextChangedClickListener mListener;
    public static RecyclerView categoriesRecyclerView;
    NavigationView categoriesNavView;
    ImageView imgOpenNavigationMenu, imgShoppingCart;
    public static ImageView imgCancelCategoriesMenu, imgReturnToPreviousCat;
    TextView txtNavigationBarTitle, txtNavViewPendingRequests, txtNavViewPurchasesArchive, txtNavViewSettings, txtMainActivityBarTitle;
    DrawerLayout mainActivityDrawer;
    public static ArrayList<CategoriesMenuModel> categoriesMenuModelArrayList;
    EditText etxtSearchBoxForProducts;
    public static TextView txtProductsCountInCart_MainFragment;

    ProgressBar progressBar_MainActivityMainFragment;

    public static RecyclerView bestSellingRecycler, newlyAddedProductsRecycler, trendingProductsRecycler, productsInDiscountRecycler, brandsRecycler, providersNShopsRecycler;

    public static int selectedParentCategoryId;
    public static int selectCategoryLevel = 1;

    public static MainActivity_MainFragment newInstance() {
        MainActivity_MainFragment fragment = new MainActivity_MainFragment();
        return fragment;
    }

    public static MainActivity_MainFragment newInstance(String param) {
        MainActivity_MainFragment fragment = new MainActivity_MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    String paramText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramText = getArguments().getString(ARG_PARAM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_activity_main_fragment, container, false);


        imgOpenNavigationMenu = view.findViewById(R.id.imgOpenNavigationMenu);
        mainActivityDrawer = view.findViewById(R.id.mainActivityDrawer);
        imgCancelCategoriesMenu = view.findViewById(R.id.imgCancelCategoriesMenu);
        txtNavigationBarTitle = view.findViewById(R.id.txtNavigationBarTitle);
        imgReturnToPreviousCat = view.findViewById(R.id.imgReturnToPreviousCat);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        bestSellingRecycler = view.findViewById(R.id.bestSellingRecycler);
        newlyAddedProductsRecycler = view.findViewById(R.id.newlyAddedProductsRecycler);
        trendingProductsRecycler = view.findViewById(R.id.trendingProductsRecycler);
        productsInDiscountRecycler = view.findViewById(R.id.productsInDiscountRecycler);
        brandsRecycler = view.findViewById(R.id.brandsRecycler);
        providersNShopsRecycler = view.findViewById(R.id.providersNShopsRecycler);
        etxtSearchBoxForProducts = view.findViewById(R.id.etxtSearchBoxForProducts);
        txtProductsCountInCart_MainFragment = view.findViewById(R.id.txtProductsCountInCart);
        imgShoppingCart = view.findViewById(R.id.imgShoppingCart);
        txtNavViewPendingRequests = view.findViewById(R.id.txtNavViewPendingRequests);
        txtNavViewSettings = view.findViewById(R.id.txtNavViewSettings);
        txtNavViewPurchasesArchive = view.findViewById(R.id.txtNavViewPurchasesArchive);
        progressBar_MainActivityMainFragment = view.findViewById(R.id.progressBar_MainActivityMainFragment);
        txtMainActivityBarTitle = view.findViewById(R.id.txtMainActivityBarTitle);

        mListener = (SearchTextChangedClickListener) getActivity();

        categoriesMenuModelArrayList = new ArrayList<>();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgReturnToPreviousCat.setScaleX(-1);
        }

        OpenCategoriesNavMenu();
        CloseCategoriesNavMenu();
        ReturnToPreviousCategoriesLevel();
        OpenShoppingCart();

        PopulateProductsRecyclersSequentially();
        OnTextChangedInSearchBox();

        OnTxtNavViewPendingRequestsClicked();
        OnTxtNavViewPurchasesArchiveClicked();
        OnTxtNavViewSettingsClicked();

        OnHoverOnNavigationMenuTxts();
        OnActionBarTitleClicked();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("lifeCycleChecking", "MainActivity_MainFragment > onStart");
        getProductsCountInCart();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("lifeCycleChecking", "MainActivity_MainFragment > OnStop");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("lifeCycleChecking", "MainActivity_MainFragment > onDestroyView");
    }


    private void OpenShoppingCart() {
        imgShoppingCart.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
            startActivity(intent);
        });
    }


    private void getProductsCountInCart() {

        Log.d("lifeCycleChecking", "MainActivity_MainFragment > getProductsCountInCart");
        MySqliteDB mySqliteDB = new MySqliteDB(getContext());
        Cursor cursor = mySqliteDB.GetProductsQtySum_shoppingCart();

        int totalQty = 0;
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    totalQty = cursor.getInt(cursor.getColumnIndex("totalQty"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (totalQty > 0) {

            txtProductsCountInCart_MainFragment.setText(String.valueOf(totalQty));
            txtProductsCountInCart_MainFragment.setVisibility(View.VISIBLE);
        } else {

            txtProductsCountInCart_MainFragment.setVisibility(View.INVISIBLE);
        }
    }


    private void OpenCategoriesNavMenu() {

        imgOpenNavigationMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDrawer.open();
                getCategoriesFromServer();
            }
        });
    }


    private void ReturnToPreviousCategoriesLevel() {
        imgReturnToPreviousCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectCategoryLevel--;
                categoriesMenuModelArrayList.forEach(categoriesMenuModel -> {
                    if (categoriesMenuModel.getCategoryId() == selectedParentCategoryId) {
                        selectedParentCategoryId = categoriesMenuModel.getParentCategoryId();
                    }
                });

//                if (selectCategoryLevel == 2) {
                if (selectCategoryLevel > 1) {
                    ArrayList<CategoriesMenuModel> oneLevelCategoryList = new ArrayList<>();

                    oneLevelCategoryList.add(new CategoriesMenuModel
                            (0, selectCategoryLevel, getString(R.string.ViewAll), selectedParentCategoryId, null));

                    for (CategoriesMenuModel categoriesMenuModel : categoriesMenuModelArrayList) {
                        if (categoriesMenuModel.getParentCategoryId() == selectedParentCategoryId) {
                            oneLevelCategoryList.add(categoriesMenuModel);
                        }
                    }
                    CategoriesMenuAdapter adapter = new CategoriesMenuAdapter(getActivity(), R.layout.categories_menu_recycler_item, oneLevelCategoryList);
                    categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    categoriesRecyclerView.setAdapter(adapter);

                } else if (selectCategoryLevel == 1) {

                    imgCancelCategoriesMenu.setVisibility(View.VISIBLE);
                    imgReturnToPreviousCat.setVisibility(View.INVISIBLE);

                    setNavDrawerMenuForLvlOne();
                }
            }
        });
    }


    private void CloseCategoriesNavMenu() {
        imgCancelCategoriesMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityDrawer.close();
            }
        });
    }


    private void getCategoriesFromServer() {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetCategories)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getCategoriesFromServer", "on background");


                    String finalS = s;
                    getActivity().runOnUiThread(() -> {
                        // OnPostExecute stuff here

                        try {
                            Log.d("getCategoriesFromServer", "onPostExecute 1");
                            Log.d("getCategoriesFromServer", finalS);
                            JSONArray jsonArray = new JSONArray(finalS);
                            Log.d("getCategoriesFromServer", "onPostExecute 2");

                            categoriesMenuModelArrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int categoryId = Integer.parseInt(jsonObject.get("id").toString());
                                int categoryLevel = Integer.parseInt(jsonObject.get("catLevel").toString());
                                String categoryTitle = jsonObject.get("catName").toString();

                                try {

                                    int parentCategoryId = Integer.parseInt(jsonObject.get("parentCatId").toString());
                                    categoriesMenuModelArrayList.add(new CategoriesMenuModel
                                            (categoryId, categoryLevel, categoryTitle, parentCategoryId, null));
                                } catch (NumberFormatException ex) {

                                    categoriesMenuModelArrayList.add(new CategoriesMenuModel
                                            (categoryId, categoryLevel, categoryTitle, 0, null));
                                }

                            }

                            selectCategoryLevel = 1;
                            imgCancelCategoriesMenu.setVisibility(View.VISIBLE);
                            imgReturnToPreviousCat.setVisibility(View.INVISIBLE);

                            setNavDrawerMenuForLvlOne();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("getCategoriesFromServer", "error " + e.getMessage());
                            replayThread.set(true);
                        }
                    });
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                    Log.d("getCategoriesFromServer", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void setNavDrawerMenuForLvlOne() {
        ArrayList<CategoriesMenuModel> oneLevelCategoryList = new ArrayList<>();
        for (CategoriesMenuModel categoriesMenuModel : categoriesMenuModelArrayList) {
            if (categoriesMenuModel.getCategoryLevel() == 1) {
                oneLevelCategoryList.add(categoriesMenuModel);
            }
        }
        CategoriesMenuAdapter adapter = new CategoriesMenuAdapter(getActivity(), R.layout.categories_menu_recycler_item, oneLevelCategoryList);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoriesRecyclerView.setAdapter(adapter);

//        INITIATE SELECT MENU
        // adapter.selectedItemParent = menu.get(0).menuTitle;
        // onMenuItemClick(adapter.selectedItemParent);
        // adapter.notifyDataSetChanged();
    }


    private void OnTxtNavViewPendingRequestsClicked() {
        txtNavViewPendingRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), PurchasesRequestsActivity.class);
                startActivity(intent);

//                getActivity().finish();

            }
        });
    }


    private void OnTxtNavViewPurchasesArchiveClicked() {

        txtNavViewPurchasesArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), PurchasesArchiveActivity.class);
                startActivity(intent);

//                getActivity().finish();

            }
        });
    }


    private void OnTxtNavViewSettingsClicked() {
        txtNavViewSettings.setOnClickListener(view -> {


            getActivity().finish(); // finish this to reset activity in case user changes app language
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
//                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                progressBar_MainActivityMainFragment.setVisibility(View.VISIBLE);
//
//                AtomicBoolean replayThread = new AtomicBoolean(false);
//
//                do {
//                    new Thread(() -> {
//                        //RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(jsonObject));
//
//                        Request request = new Request.Builder()
//                                .url(MainActivity.serverIp + "/getSupportNumber.php")
//                                //.post(requestBody)
//                                .build();
//
//                        Response response = null;
//
//                        String s = null;
//                        try {
//
//                            response = MainActivity.client.newCall(request).execute();
//                            if (!response.isSuccessful())
//                                throw new IOException("Unexpected code " + response);
//
//                            s = response.body().string();
//
//                            Log.d("getSupportNumber", "on background");
//
//
//                            String finalS = s;
//                            getActivity().runOnUiThread(() -> {
//
//                                Log.d("getSupportNumber", finalS);
//
//                                if (!finalS.contains("failed")) {
//
//                                    MainActivity.supportMobileNumber = finalS.trim();
//
//                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//
//                                        ActivityCompat.requestPermissions(
//                                                getActivity(),
//                                                new String[]{Manifest.permission.CALL_PHONE},
//                                                MainActivity.MY_PHONE_CALL_PERMISSION_CODE_IMAGE);
//                                    } else {
//
//                                        Intent intent = new Intent(Intent.ACTION_CALL);
//
//                                        intent.setData(Uri.parse("tel:" + MainActivity.supportMobileNumber));
//                                        getActivity().startActivity(intent);
//                                    }
//
//                                } else {
//                                    Toast.makeText(getContext(), getString(R.string.String1_MainActivityMainFragment), Toast.LENGTH_SHORT).show();
//                                }
//
//                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                progressBar_MainActivityMainFragment.setVisibility(View.INVISIBLE);
//                            });
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.d("getSupportNumber", e.getMessage());
//                            replayThread.set(true);
//                        }
//                    }).start();
//                } while (replayThread.get());
        });
    }


    private void OnHoverOnNavigationMenuTxts() {
        txtNavViewPendingRequests.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {

                return false;
            }
        });
    }


    private void OnActionBarTitleClicked() {
        txtMainActivityBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OnActionBarTitleClicked", "true");
                bestSellingRecycler.setAdapter(null);
                newlyAddedProductsRecycler.setAdapter(null);
                trendingProductsRecycler.setAdapter(null);
                productsInDiscountRecycler.setAdapter(null);
                brandsRecycler.setAdapter(null);
                providersNShopsRecycler.setAdapter(null);
                PopulateProductsRecyclersSequentially();
                //txtNavigationBarTitle.setBackground(null);
            }
        });
    }


    //populates main activity recyclers with product
    //1)bestSellingProducts
    private void PopulateProductsRecyclersSequentially() {

        //first get the best selling products
        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetBestSellingItems)
                        .build();

                Response response = null;

                String s = null;


                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("PopulateProductsRecyclersSequentially", "on background");


                    String finalS = s;
                    try {

                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("PopulateProductsRecyclersSequentially", "onPostExecute 1");
                                Log.d("PopulateProductsRecyclersSequentially", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("PopulateProductsRecyclersSequentially", "onPostExecute 2");


                                ArrayList<ItemsModel> bestSellingProductsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONObject ItemTypeJson = jsonObject.getJSONObject("ItemType");
                                    int itemTypeId = ItemTypeJson.getInt("id");
                                    int itemId = ItemTypeJson.getInt("itemId");
                                    String typeName = ItemTypeJson.getString("typeName");
                                    double availableQty = ItemTypeJson.getDouble("availableQty");
                                    double discountPrice = ItemTypeJson.getDouble("discountPrice");
                                    double sellPrice = ItemTypeJson.getDouble("sellPrice");
                                    JSONObject itemJson = ItemTypeJson.getJSONObject("Item");
                                    String itemCode = itemJson.getString("itemCode");
                                    String itemName = itemJson.getString("itemName");
                                    JSONArray itemReviewsJson = itemJson.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    Log.d("PopulateProductsRecyclersSequentially", "" + itemReviewsJson);
                                    if (itemReviewsJson.length() > 0) {
                                            avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }
                                    JSONArray itemTypeImgJson = ItemTypeJson.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    bestSellingProductsArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }


                                BestSellingRecyclerAdapter bestSellingRecyclerAdapter = new BestSellingRecyclerAdapter(getActivity(),
                                        R.layout.products_recycler_layout_item, bestSellingProductsArrayList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                bestSellingRecycler.setLayoutManager(mLayoutManager);
                                bestSellingRecycler.setItemAnimator(new DefaultItemAnimator());
                                bestSellingRecycler.setAdapter(bestSellingRecyclerAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("PopulateProductsRecyclersSequentially", "jsonException: " + e.getMessage());
                            }

                            //step 2:
                            PopulateNewlyAddedProductsRecycler();
                        });
                    } catch (IllegalStateException e) {

                        Log.d("PopulateProductsRecyclersSequentially", "IllegalStateException: " + e.getMessage());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("PopulateProductsRecyclersSequentially", "IOException: " + e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    //2) newlyAddedProducts
    private void PopulateNewlyAddedProductsRecycler() {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetNewlyAddedItems)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getNewlyAddedItems", "on background");


                    String finalS = s;
                    try {
                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getNewlyAddedItems", "onPostExecute 1");
                                Log.d("getNewlyAddedItems", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getNewlyAddedItems", "onPostExecute 2");


                                ArrayList<ItemsModel> newlyAddedProductsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int itemTypeId = jsonObject.getInt("id");
                                    int itemId = jsonObject.getInt("itemId");
                                    String typeName = jsonObject.getString("typeName");
                                    double availableQty = jsonObject.getDouble("availableQty");
                                    double discountPrice = jsonObject.getDouble("discountPrice");
                                    double sellPrice = jsonObject.getDouble("sellPrice");

                                    JSONObject itemJson = jsonObject.getJSONObject("Item");
                                    String itemCode = itemJson.getString("itemCode");
                                    String itemName = itemJson.getString("itemName");
                                    JSONArray itemReviewsJson = itemJson.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    if (itemReviewsJson.length() > 0) {
                                        avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }
                                    JSONArray itemTypeImgJson = jsonObject.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    newlyAddedProductsArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }


                                NewlyAddedProductsRecyclerAdapter newlyAddedProductsRecyclerAdapter = new NewlyAddedProductsRecyclerAdapter
                                        (getActivity(),
                                                R.layout.products_recycler_layout_item,
                                                newlyAddedProductsArrayList);

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                newlyAddedProductsRecycler.setLayoutManager(mLayoutManager);
                                newlyAddedProductsRecycler.setItemAnimator(new DefaultItemAnimator());
                                newlyAddedProductsRecycler.setAdapter(newlyAddedProductsRecyclerAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getNewlyAddedItems", "error " + e.getMessage());
                            }

                            //step 3
                            PopulateTrendingProductsRecycler();
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getNewlyAddedItems", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getNewlyAddedItems", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    //3) trendingProducts
    private void PopulateTrendingProductsRecycler() {
        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);
            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetTrendingItems)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getTrendingItems", "on background");


                    String finalS = s;
                    try {

                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getTrendingItems", "onPostExecute 1");
                                Log.d("getTrendingItems", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getTrendingItems", "onPostExecute 2");


                                ArrayList<ItemsModel> trendingProductsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONObject ItemTypeJson = jsonObject.getJSONObject("ItemType");
                                    int itemTypeId = ItemTypeJson.getInt("id");
                                    int itemId = ItemTypeJson.getInt("itemId");
                                    String typeName = ItemTypeJson.getString("typeName");
                                    double availableQty = ItemTypeJson.getDouble("availableQty");
                                    double discountPrice = ItemTypeJson.getDouble("discountPrice");
                                    double sellPrice = ItemTypeJson.getDouble("sellPrice");
                                    JSONObject itemJson = ItemTypeJson.getJSONObject("Item");
                                    String itemCode = itemJson.getString("itemCode");
                                    String itemName = itemJson.getString("itemName");
                                    JSONArray itemReviewsJson = itemJson.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    if (itemReviewsJson.length() > 0) {
                                        avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }
                                    JSONArray itemTypeImgJson = ItemTypeJson.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    trendingProductsArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }


                                TrendingProductsRecyclerAdapter trendingProductsRecyclerAdapter = new TrendingProductsRecyclerAdapter
                                        (getActivity(),
                                                R.layout.products_recycler_layout_item, trendingProductsArrayList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                trendingProductsRecycler.setLayoutManager(mLayoutManager);
                                trendingProductsRecycler.setItemAnimator(new DefaultItemAnimator());
                                trendingProductsRecycler.setAdapter(trendingProductsRecyclerAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getTrendingItems", "error " + e.getMessage());
                            }

                            //step 4
                            PopulateProductsInDiscountRecycler();
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getTrendingItems", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getTrendingItems", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    //4) productsInDiscount
    private void PopulateProductsInDiscountRecycler() {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemsInDiscount)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getItemsInDiscount", "on background");


                    String finalS = s;
                    try {

                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getItemsInDiscount", "step4 discount ,onPostExecute 1");
                                Log.d("getItemsInDiscount", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getItemsInDiscount", "onPostExecute 2");


                                ArrayList<ItemsModel> productsInDiscountArrayList = new ArrayList<>();

                                Log.d("getItemsInDiscount", "jsonArray.length() " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int itemTypeId = jsonObject.getInt("id");
                                    int itemId = jsonObject.getInt("itemId");
                                    String typeName = jsonObject.getString("typeName");
                                    double availableQty = jsonObject.getDouble("availableQty");
                                    double discountPrice = jsonObject.getDouble("discountPrice");
                                    double sellPrice = jsonObject.getDouble("sellPrice");

                                    JSONObject itemJson = jsonObject.getJSONObject("Item");
                                    String itemCode = itemJson.getString("itemCode");
                                    String itemName = itemJson.getString("itemName");
                                    JSONArray itemReviewsJson = itemJson.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    if (itemReviewsJson.length() > 0) {
                                        avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }
                                    JSONArray itemTypeImgJson = jsonObject.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    productsInDiscountArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }

                                ProductsInDiscountRecyclerAdapter productsInDiscountRecyclerAdapter = new ProductsInDiscountRecyclerAdapter
                                        (getActivity(),
                                                R.layout.products_recycler_layout_item,
                                                productsInDiscountArrayList);

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                productsInDiscountRecycler.setLayoutManager(mLayoutManager);
                                productsInDiscountRecycler.setItemAnimator(new DefaultItemAnimator());
                                productsInDiscountRecycler.setAdapter(productsInDiscountRecyclerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getItemsInDiscount", "error " + e.getMessage());

                            }


                            //step 5
                            PopulateBrandsRecycler();
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getItemsInDiscount", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("hello", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    //5) brands
    private void PopulateBrandsRecycler() {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetBrands)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getBrands", "on background");


                    String finalS = s;
                    try {

                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getBrands", "onPostExecute 1");
                                Log.d("getBrands", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getBrands", "onPostExecute 2");


                                ArrayList<BrandsModel> brandsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String brandName = jsonObject.getString("brandName");
                                    String brandLogo = jsonObject.getString("brandLogo");
                                    brandLogo = MainActivity.brandImgUrl + brandLogo;
                                    String description = jsonObject.getString("description");


                                    brandsArrayList.add(new BrandsModel(id, brandName, brandLogo, description));
                                }


                                BrandsRecyclerAdapter brandsRecyclerAdapter = new BrandsRecyclerAdapter(getActivity(),
                                        R.layout.providers_recycler_layout_item, brandsArrayList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                brandsRecycler.setLayoutManager(mLayoutManager);
                                brandsRecycler.setItemAnimator(new DefaultItemAnimator());
                                brandsRecycler.setAdapter(brandsRecyclerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getBrands", "error " + e.getMessage());

                            }


                            //step 6
                            PopulateProvidersRecycler();
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getBrands", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getBrands", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    //6) providers and shops
    private void PopulateProvidersRecycler() {
        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);

            do {
                replayThread.set(false);
                // do background stuff here
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetShopsForCustomer)
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getShopsForCustomer", "on background");

                    String finalS = s;
                    try {


                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getShopsForCustomer", "onPostExecute 1");
                                Log.d("getShopsForCustomer", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getShopsForCustomer", "onPostExecute 2");


                                ArrayList<ProvidersNShopsModel> providersNShopsArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int id = jsonObject.getInt("id");
                                    String name = jsonObject.getString("name");
                                    String logo = null;
                                    if (jsonObject.getString("logo") != null && !jsonObject.getString("logo").equals("")) {
                                        logo = MainActivity.shopImgUrl + jsonObject.getString("logo");
                                    }
                                    String specialty = jsonObject.getString("specialty");
                                    String email = jsonObject.getString("email");
                                    String location = jsonObject.getString("location");
                                    String lat = jsonObject.getString("lat");
                                    String lng = jsonObject.getString("lng");

                                    providersNShopsArrayList.add(new ProvidersNShopsModel(id, name, logo, specialty, email, location, lat, lng));
                                }


                                ProvidersNShopsRecyclerAdapter providersNShopsRecyclerAdapter = new ProvidersNShopsRecyclerAdapter
                                        (getActivity(),
                                                R.layout.providers_recycler_layout_item, providersNShopsArrayList);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                providersNShopsRecycler.setLayoutManager(mLayoutManager);
                                providersNShopsRecycler.setItemAnimator(new DefaultItemAnimator());
                                providersNShopsRecycler.setAdapter(providersNShopsRecyclerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getShopsForCustomer", "error " + e.getMessage());

                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getShopsForCustomer", "error " + e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getShopsForCustomer", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void OnTextChangedInSearchBox() {
        etxtSearchBoxForProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals(""))
                    mListener.onSearchTextChangedClickListener(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals(""))
                    etxtSearchBoxForProducts.setText("");
            }
        });
    }


    public interface SearchTextChangedClickListener {
        void onSearchTextChangedClickListener(String itemString);
    }
}
