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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity_SearchFragment extends Fragment {

    private static final String ARG_PARAM = "searchKeyWord";
    private static final String CATEGORY_LEVEL = "categoryLevel";
    private static final String CATEGORY_ID = "categoryId";

    private ReturnToMainFragmentImgClickListener mListener;
    ImageView imgShoppingCart;

    public static TextView txtProductsCountInCart_SearchFragment;
    EditText etxtSearchBoxForProducts;
    ImageView imgReturnToMainFragment;
    SwipeRefreshLayout srlSearchFragment;
    public static RecyclerView searchedProductsRecycler;

    public static MainActivity_SearchFragment newInstance() {
        MainActivity_SearchFragment fragment = new MainActivity_SearchFragment();
        return fragment;
    }


    public static MainActivity_SearchFragment newInstance(String param) {
        MainActivity_SearchFragment fragment = new MainActivity_SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }


    public static MainActivity_SearchFragment newInstance(int categoryId) {
        MainActivity_SearchFragment fragment = new MainActivity_SearchFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    String paramText;
    int categoryId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramText = getArguments().getString(ARG_PARAM);
            categoryId = getArguments().getInt(CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_activity__search_fragment, container, false);


        imgReturnToMainFragment = view.findViewById(R.id.imgReturnToMainFragment);
        etxtSearchBoxForProducts = view.findViewById(R.id.etxtSearchBoxForProducts);
        searchedProductsRecycler = view.findViewById(R.id.searchedProductsRecycler);
        imgShoppingCart = view.findViewById(R.id.imgShoppingCart);
        txtProductsCountInCart_SearchFragment = view.findViewById(R.id.txtProductsCountInCart);
        srlSearchFragment = view.findViewById(R.id.srlSearchFragment);
        if (paramText != null) {

            etxtSearchBoxForProducts.setText(paramText);
            getProductsFromSearchKeyWord(paramText);
        } else {
            getProductsFromSelectedCategory();
        }

        mListener = (ReturnToMainFragmentImgClickListener) getActivity();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(MainActivity.sharedPreferencesName , Context.MODE_PRIVATE);
        if(sharedPreferences.getString(MainActivity.sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals("ar")){

            imgReturnToMainFragment.setScaleX(-1);
        }

        ReturnToMainFragmentImageClicked();
        SearchProductsInSearchTextBox();
        OpenShoppingCart();
        SwipeScreenToRefreshSearch();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getProductsCountInCart();
    }

    private void SwipeScreenToRefreshSearch() {
        srlSearchFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProductsFromSearchKeyWord(etxtSearchBoxForProducts.getText().toString());
            }
        });
    }


    private void OpenShoppingCart() {
        imgShoppingCart.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ShoppingCartActivity.class);
            startActivity(intent);
        });
    }


    private void getProductsCountInCart() {
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
            txtProductsCountInCart_SearchFragment.setText(totalQty + "");
            txtProductsCountInCart_SearchFragment.setVisibility(View.VISIBLE);
        } else {

            txtProductsCountInCart_SearchFragment.setVisibility(View.INVISIBLE);
        }
    }


    private void SearchProductsInSearchTextBox() {
        etxtSearchBoxForProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getProductsFromSearchKeyWord(editable.toString());
            }
        });
    }


    private void getProductsFromSearchKeyWord(String searchKeyWord) {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);
            do {
                replayThread.set(false);
//                RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(myJsonObject));
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemsBySearchKeyword + searchKeyWord)
//                        .post(requestBody)
                        .get()
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getProductsFromSearchKeyWord", "on background");


                    String finalS = s;

                    try {
                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getProductsFromSearchKeyWord", "onPostExecute 1");
                                Log.d("getProductsFromSearchKeyWord", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getProductsFromSearchKeyWord", "onPostExecute 2");


                                ArrayList<ItemsModel> getProductsBySearchingArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int itemId = jsonObject.getInt("id");
                                    String itemCode = jsonObject.get("itemCode").toString();
                                    String itemName = jsonObject.get("itemName").toString();
                                    JSONObject itemTypesJson = jsonObject.getJSONArray("ItemTypes").getJSONObject(0);
                                    int itemTypeId = itemTypesJson.getInt("id");
                                    String typeName = itemTypesJson.getString("typeName");
                                    double availableQty = itemTypesJson.getDouble("availableQty");
                                    double sellPrice = itemTypesJson.getDouble("sellPrice");
                                    double discountPrice = itemTypesJson.getDouble("discountPrice");
                                    JSONArray itemTypeImgJson = itemTypesJson.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    JSONArray itemReviewsJson = jsonObject.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    if (itemReviewsJson.length() > 0) {
                                        avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }

                                    getProductsBySearchingArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }


                                SearchProductsRecyclerAdapter searchProductsRecyclerAdapter = new SearchProductsRecyclerAdapter
                                        (getActivity(),
                                                R.layout.products_search_recycler_item,
                                                getProductsBySearchingArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                                searchedProductsRecycler.setLayoutManager(mLayoutManager);
                                searchedProductsRecycler.setItemAnimator(new DefaultItemAnimator());
                                searchedProductsRecycler.setAdapter(searchProductsRecyclerAdapter);

                                srlSearchFragment.setRefreshing(false);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getProductsFromSearchKeyWord", "error " + e.getMessage());
                                Toast.makeText(getContext(), getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getProductsFromSearchKeyWord", "error " + e.getMessage());
                        replayThread.set(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getProductsFromSearchKeyWord", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void getProductsFromSelectedCategory() {

        new Thread(() -> {
            AtomicBoolean replayThread = new AtomicBoolean(false);
            do {
                replayThread.set(false);

//                RequestBody requestBody = RequestBody.create(MainActivity.JSON, String.valueOf(myJsonObject));
                Log.d("getProductsFromSelectedCategory:", categoryId + " ");
                Request request = new Request.Builder()
                        .url(MainActivity.serverIp_NodeJs + MainActivity.HttpRequestsRoutes.GetItemsByCategory + categoryId)
//                        .post(requestBody)
                        .get()
                        .build();

                Response response = null;

                String s = null;
                try {

                    response = MainActivity.client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    s = response.body().string();

                    Log.d("getProductsFromSelectedCategory", "on background");


                    String finalS = s;

                    try {
                        requireActivity().runOnUiThread(() -> {
                            // OnPostExecute stuff here

                            try {
                                Log.d("getProductsFromSelectedCategory", "onPostExecute 1");
                                Log.d("getProductsFromSelectedCategory", finalS);
                                JSONArray jsonArray = new JSONArray(finalS);
                                Log.d("getProductsFromSelectedCategory", "onPostExecute 2");


                                ArrayList<ItemsModel> getProductsByCategoryArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int itemId = jsonObject.getInt("id");
                                    String itemCode = jsonObject.get("itemCode").toString();
                                    String itemName = jsonObject.get("itemName").toString();
                                    JSONObject itemTypesJson = jsonObject.getJSONArray("ItemTypes").getJSONObject(0);
                                    int itemTypeId = itemTypesJson.getInt("id");
                                    String typeName = itemTypesJson.getString("typeName");
                                    double availableQty = itemTypesJson.getDouble("availableQty");
                                    double sellPrice = itemTypesJson.getDouble("sellPrice");
                                    double discountPrice = itemTypesJson.getDouble("discountPrice");
                                    JSONArray itemTypeImgJson = itemTypesJson.getJSONArray("ItemTypeImages");
                                    String imageLoc = MainActivity.itemTypeImgUrl + itemTypeImgJson.getJSONObject(0).getString("imageLoc");

                                    JSONArray itemReviewsJson = jsonObject.getJSONArray("ItemReviews");
                                    double avgRate = 0;
                                    if (itemReviewsJson.length() > 0) {
                                        avgRate = itemReviewsJson.getJSONObject(0).getDouble("avgRate");
                                    }

                                    getProductsByCategoryArrayList.add(new ItemsModel(itemTypeId, itemId, typeName, availableQty,
                                            discountPrice, sellPrice, itemCode, itemName, avgRate, imageLoc));
                                }


                                SearchProductsRecyclerAdapter searchProductsRecyclerAdapter = new SearchProductsRecyclerAdapter
                                        (getActivity(),
                                                R.layout.products_search_recycler_item,
                                                getProductsByCategoryArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                                searchedProductsRecycler.setLayoutManager(mLayoutManager);
                                searchedProductsRecycler.setItemAnimator(new DefaultItemAnimator());
                                searchedProductsRecycler.setAdapter(searchProductsRecyclerAdapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("getProductsFromSelectedCategory", "error " + e.getMessage());
                                Toast.makeText(getContext(), getString(R.string.GlobalMessage_ThereIsProblemTryAgain), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IllegalStateException e) {

                        Log.d("getProductsFromSelectedCategory", "error " + e.getMessage());
                        replayThread.set(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("getProductsFromSelectedCategory", e.getMessage());
                    replayThread.set(true);
                }
            } while (replayThread.get());
        }).start();
    }


    private void ReturnToMainFragmentImageClicked() {
        imgReturnToMainFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onReturnToMainFragmentImgClickListener();
            }
        });
    }


    public interface ReturnToMainFragmentImgClickListener {
        void onReturnToMainFragmentImgClickListener();
    }
}
