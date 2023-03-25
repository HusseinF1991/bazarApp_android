package com.uruksys.bazarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import io.socket.client.IO;
import io.socket.client.Socket;


public class MainActivity extends AppCompatActivity implements MainActivity_MainFragment.SearchTextChangedClickListener,
        MainActivity_SearchFragment.ReturnToMainFragmentImgClickListener,
        CategoriesMenuAdapter.SearchForCategoryClickListener {


    //    public static String serverIp = "http://192.168.1.133/bazarApp/bazarApp_customers";
//    public static String serverIp = "http://23.239.203.134/bazarApp/bazarApp_customers";
    //public static String serverIp = "http://192.168.1.9:81/bazarApp";
    public static String serverIp_NodeJs = "http://192.168.0.114:5000";
    public static String itemTypeImgUrl = serverIp_NodeJs + "/api/getItemImage/";
    public static String shopImgUrl = serverIp_NodeJs + "/api/getShopImage/";
    public static String brandImgUrl = serverIp_NodeJs + "/api/getBrandImage/";

    public enum CHAT_SENDER {
        customer,
        manager,
    }
    public enum CHAT_SOCKET_MESSAGES {
        SEND_MESSAGE,
        RECEIVE_MESSAGE_BY_CUSTOMER,
        RECEIVE_MESSAGE_BY_MANAGER,
    }


//    public static String INVOICE_CHAT_CUSTOMER_SENDER = "customer";
//    public static String INVOICE_CHAT_MANAGER_SENDER = "manager";
    public enum HttpRequestsRoutes {
        UpdateCustomerToken("/updateCustomerToken"),
        SignInCustomer("/signInCustomer"),
        GetCategories("/getCategories"),
        GetBestSellingItems("/getBestSellingItems"),
        GetNewlyAddedItems("/getNewlyAddedItems"),
        GetTrendingItems("/getTrendingItems"),
        GetItemsInDiscount("/getItemsInDiscount"),
        GetBrands("/getBrands"),
        GetShopsForCustomer("/getShopsForCustomer"),
        GetItemsBySearchKeyword("/getItemsBySearchKeyword?itemName="),
        GetItemsByCategory("/getItemsByCategory?categoryId="),
        GetInvoiceShopChat("/getInvoiceShopChat?id="),
        AddNewMsgToChat("/addNewMsgToChat"),
        AddNewReview("/addNewReview"),
        GetOneItemDetailsForCustomer("/getOneItemDetailsForCustomer?id="),
        GetItemReviews("/getItemReviews?itemId="),
        GetItemsInInvoiceShop("/getItemsInInvoiceShop?invoiceShopId="),
        GetCustomerPurchasesArchive("/getCustomerPurchasesArchive?customerId="),
        GetCustomerPurchasesReqs("/getCustomerPurchasesReqs?customerId="),
        RegisterNewCustomer("/registerNewCustomer"),
        UpdateAccountInfo("/updateAccountInfo"),
        UpdateAccountPassword("/updateAccountPassword"),
        GetCustomerById("/getCustomerById?id="),
        NewPurchaseRequest("/newPurchaseRequest"),
        GetItemsDetailsInCart("/getItemsDetailsInCart");

        private String stringValue;

        HttpRequestsRoutes(String toString) {
            stringValue = toString;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    ;

    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();

    public static String sharedPreferencesName = "BazarAppSharedPreferences";
    public static String sharedPreferences_Token = "BazarApp_Token_SharedPreferences";
    public static String sharedPreferences_Username = "BazarApp_Username_SharedPreferences";
    public static String sharedPreferences_Password = "BazarApp_Password_SharedPreferences";
    public static String sharedPreferences_CustomerId = "BazarApp_CustomerId_SharedPreferences";
    public static String sharedPreferences_Latitude = "BazarApp_Latitude_SharedPreferences";
    public static String sharedPreferences_Longitude = "BazarApp_Longitude_SharedPreferences";
    public static String sharedPreferences_CustomerName = "BazarApp_CustomerName_SharedPreferences";
    public static String sharedPreferences_LocaleLanguage = "BazarApp_LocaleLanguage_SharedPreferences";

    public static String PAYMENT_METHOD_CASH = "CASH";


    public static String INVOICE_STATUS_DELIVERED = "delivered";
    public static String INVOICE_STATUS_REJECTED = "rejected";
    public static String INVOICE_STATUS_APPROVED = "approved";
    public static String INVOICE_STATUS_PENDING = "pending";

    public static String CUSTOMER_NOTIFICATION_INVOICE_MODIFIED = "INVOICE_MODIFIED";
    public static String CUSTOMER_NOTIFICATION_INVOICE_REJECTED = "INVOICE_REJECTED";
    public static String CUSTOMER_NOTIFICATION_INVOICE_DELIVERED = "INVOICE_DELIVERED";
    public static String CUSTOMER_NOTIFICATION_NEW_CHAT_MESSAGE = "NEW_CHAT_MESSAGE";

    public static final int MY_PHONE_CALL_PERMISSION_CODE_IMAGE = 314;

    public static final int PERMISSIONS_REQUEST_LOCATION = 128;


    private GoogleApiClient googleApiClient;

    private FusedLocationProviderClient fusedLocationClient;

    public static Socket mSocket;

    {
        try {
            mSocket = IO.socket(serverIp_NodeJs);
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
        if (!sharedPreferences.getString(sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()).equals(Locale.getDefault().getLanguage()))
            setLocale(MainActivity.this, sharedPreferences.getString(sharedPreferences_LocaleLanguage, Locale.getDefault().getLanguage()));


        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Log.d("lifeCycleChecking", "onCreate MainActivity");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, MainActivity_MainFragment.newInstance())
                .commit();


        Intent intent = new Intent(this, MyFirebaseMessagingService.class);
        startService(intent);

        //getting token and setting it in database if the customer is registered
        String currentToken = sharedPreferences.getString(sharedPreferences_Token, null);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {

            if (currentToken == null || currentToken != token) {
                Log.d("getToken", token);
                sharedPreferences.edit().putString(sharedPreferences_Token, token).apply();
                int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);
                if (customerId != 0) {
                    HttpRequests.UpdateCustomerToken(customerId, token);
                }
            }
        });


        if(!mSocket.connected())
            mSocket.connect();

        mSocket.on(CHAT_SOCKET_MESSAGES.RECEIVE_MESSAGE_BY_CUSTOMER.toString(), (data) -> {

            Log.d("receive_message", "onCreate: " + data[0].toString());
            Intent intent1 = new Intent("receivedToMessenger");
            intent1.putExtra("data", data[0].toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
        });

        TurnGPS_On();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("lifeCycleChecking", "onRestart MainActivity");
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    //convert byte[] to bitmap
    public static Bitmap decodeBase64Profile(String input) {
        Bitmap bitmap = null;
        if (input != null) {
            byte[] decodedByte = Base64.decode(input, 0);
            bitmap = BitmapFactory
                    .decodeByteArray(decodedByte, 0, decodedByte.length);
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


    //when search editText text changed event invoked change from main fragment to search fragment
    @Override
    public void onSearchTextChangedClickListener(String itemString) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, MainActivity_SearchFragment.newInstance(itemString))
                .commit();
    }


    //on return btn clicked in search fragment switch to main fragment
    @Override
    public void onReturnToMainFragmentImgClickListener() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, MainActivity_MainFragment.newInstance())
                .commit();
    }


    @Override
    public void onSearchForCategoryClickListener(int categoryId) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, MainActivity_SearchFragment.newInstance(categoryId))
                .commit();
    }


    public static int PutNewProductsInShoppingCart(Context context, int selectedItemTypeId) {

        MySqliteDB mySqliteDB = new MySqliteDB(context);
        Cursor cursor = mySqliteDB.GetProducts_shoppingCart();

        boolean productAlreadyInCart = false;
        int totalQty = 0;

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                    int itemTypeId = cursor.getInt(cursor.getColumnIndex("itemTypeId"));
                    totalQty = totalQty + quantity;

                    if (selectedItemTypeId == itemTypeId) {
                        productAlreadyInCart = true;
                        mySqliteDB.AddToProduct_shoppingCart(selectedItemTypeId, 1);

                        totalQty = totalQty + 1;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }


        if (!productAlreadyInCart) {

            mySqliteDB.InsertNewProduct_shoppingCart(selectedItemTypeId, 1);
            totalQty = totalQty + 1;
        }

        return totalQty;
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
                            status.startResolutionForResult(MainActivity.this, 1000);

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
                ActivityCompat.requestPermissions(MainActivity.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.d("sendMyLocation", location + "");
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object

                                SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(sharedPreferences_Longitude, String.valueOf(location.getLongitude()));
                                editor.putString(sharedPreferences_Latitude, String.valueOf(location.getLatitude()));
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
                            .setMessage(R.string.String5_BuyingRequestActivity)
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
                            .setMessage(R.string.String5_BuyingRequestActivity)
                            .create()
                            .show();
                }
                break;
        }
    }


    public static void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child.getId() != R.id.imgFinishThisActivity)
                child.setEnabled(enable);

            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }


    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @NonNull
    public static Resources getLocalizedResources(Context context) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(new Locale("en"));
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }


    public static String SetSelectedProvinceInJson(Context context, String s) {
        Resources resources = getLocalizedResources(context);
        if (context.getString(R.string.IraqProvince_Baghdad).equals(s)) {
            return resources.getString(R.string.IraqProvince_Baghdad);
        } else if (context.getString(R.string.IraqProvince_Babel).equals(s)) {
            return resources.getString(R.string.IraqProvince_Babel);
        } else if (context.getString(R.string.IraqProvince_Basrah).equals(s)) {
            return resources.getString(R.string.IraqProvince_Basrah);
        } else if (context.getString(R.string.IraqProvince_Najaf).equals(s)) {
            return resources.getString(R.string.IraqProvince_Najaf);
        } else if (context.getString(R.string.IraqProvince_Nineveh).equals(s)) {
            return resources.getString(R.string.IraqProvince_Nineveh);
        } else if (context.getString(R.string.IraqProvince_Missan).equals(s)) {
            return resources.getString(R.string.IraqProvince_Missan);
        } else if (context.getString(R.string.IraqProvince_Muthana).equals(s)) {
            return resources.getString(R.string.IraqProvince_Muthana);
        } else if (context.getString(R.string.IraqProvince_Salah_Aldeen).equals(s)) {
            return resources.getString(R.string.IraqProvince_Salah_Aldeen);
        } else if (context.getString(R.string.IraqProvince_Anbar).equals(s)) {
            return resources.getString(R.string.IraqProvince_Anbar);
        } else if (context.getString(R.string.IraqProvince_Diwaniyah).equals(s)) {
            return resources.getString(R.string.IraqProvince_Diwaniyah);
        } else if (context.getString(R.string.IraqProvince_Dhi_Qar).equals(s)) {
            return resources.getString(R.string.IraqProvince_Dhi_Qar);
        } else if (context.getString(R.string.IraqProvince_Diyala).equals(s)) {
            return resources.getString(R.string.IraqProvince_Diyala);
        } else if (context.getString(R.string.IraqProvince_Dhok).equals(s)) {
            return resources.getString(R.string.IraqProvince_Dhok);
        } else if (context.getString(R.string.IraqProvince_Erbil).equals(s)) {
            return resources.getString(R.string.IraqProvince_Erbil);
        } else if (context.getString(R.string.IraqProvince_Sulaymaniyah).equals(s)) {
            return resources.getString(R.string.IraqProvince_Sulaymaniyah);
        } else if (context.getString(R.string.IraqProvince_Karbala).equals(s)) {
            return resources.getString(R.string.IraqProvince_Karbala);
        } else if (context.getString(R.string.IraqProvince_Kirkok).equals(s)) {
            return resources.getString(R.string.IraqProvince_Kirkok);
        } else if (context.getString(R.string.IraqProvince_Wasit).equals(s)) {
            return resources.getString(R.string.IraqProvince_Wasit);
        } else {
            return null;
        }
    }


    public static String SetSelectedProvinceInSpinner(Context context, String s) {
        Resources resources = getLocalizedResources(context);
        if (resources.getString(R.string.IraqProvince_Baghdad).equals(s)) {
            return context.getString(R.string.IraqProvince_Baghdad);
        } else if (resources.getString(R.string.IraqProvince_Babel).equals(s)) {
            return context.getString(R.string.IraqProvince_Babel);
        } else if (resources.getString(R.string.IraqProvince_Basrah).equals(s)) {
            return context.getString(R.string.IraqProvince_Basrah);
        } else if (resources.getString(R.string.IraqProvince_Najaf).equals(s)) {
            return context.getString(R.string.IraqProvince_Najaf);
        } else if (resources.getString(R.string.IraqProvince_Nineveh).equals(s)) {
            return context.getString(R.string.IraqProvince_Nineveh);
        } else if (resources.getString(R.string.IraqProvince_Missan).equals(s)) {
            return context.getString(R.string.IraqProvince_Missan);
        } else if (resources.getString(R.string.IraqProvince_Muthana).equals(s)) {
            return context.getString(R.string.IraqProvince_Muthana);
        } else if (resources.getString(R.string.IraqProvince_Salah_Aldeen).equals(s)) {
            return context.getString(R.string.IraqProvince_Salah_Aldeen);
        } else if (resources.getString(R.string.IraqProvince_Anbar).equals(s)) {
            return context.getString(R.string.IraqProvince_Anbar);
        } else if (resources.getString(R.string.IraqProvince_Diwaniyah).equals(s)) {
            return context.getString(R.string.IraqProvince_Diwaniyah);
        } else if (resources.getString(R.string.IraqProvince_Dhi_Qar).equals(s)) {
            return context.getString(R.string.IraqProvince_Dhi_Qar);
        } else if (resources.getString(R.string.IraqProvince_Diyala).equals(s)) {
            return context.getString(R.string.IraqProvince_Diyala);
        } else if (resources.getString(R.string.IraqProvince_Dhok).equals(s)) {
            return context.getString(R.string.IraqProvince_Dhok);
        } else if (resources.getString(R.string.IraqProvince_Erbil).equals(s)) {
            return context.getString(R.string.IraqProvince_Erbil);
        } else if (resources.getString(R.string.IraqProvince_Sulaymaniyah).equals(s)) {
            return context.getString(R.string.IraqProvince_Sulaymaniyah);
        } else if (resources.getString(R.string.IraqProvince_Karbala).equals(s)) {
            return context.getString(R.string.IraqProvince_Karbala);
        } else if (resources.getString(R.string.IraqProvince_Kirkok).equals(s)) {
            return context.getString(R.string.IraqProvince_Kirkok);
        } else if (resources.getString(R.string.IraqProvince_Wasit).equals(s)) {
            return context.getString(R.string.IraqProvince_Wasit);
        } else {
            return null;
        }
    }
}



