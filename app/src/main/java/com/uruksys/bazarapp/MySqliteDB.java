package com.uruksys.bazarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;


public class MySqliteDB extends SQLiteOpenHelper {

    public static final String MYSQLITE_DB_NAME = "bazarAppDb";

    public MySqliteDB(Context context) {
        super(context, MYSQLITE_DB_NAME, null, 1);


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        onCreate(sqLiteDatabase);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d("sqlite_db", "Created");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS shoppingCart");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS purchases");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS invoice");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS invoiceDetails");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `shoppingCart` (" +
                "Id INTEGER NOT NULL primary key autoIncrement, " +
                "itemTypeId INTEGER NOT NULL, " +
                "quantity double NOT NULL)");

//        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `invoice` (" +
//                "invoice INTEGER NOT NULL primary key, " +
//                "recipeDate Date NOT NULL, " +
//                "customerName varchar(80) NOT NULL, " +
//                "customerMobile varchar(50) NOT NULL, " +
//                "province varchar(50) NOT NULL, " +
//                "lat double(10,3) NOT NULL, " +
//                "lng double(10,3) NOT NULL, " +
//                "recipeCost double(10,2) NOT NULL, " +
//                "paidAmount double(10,2) default 0, " +
//                "paymentType varchar(50) NOT NULL, " +
//                "recipeStatus varchar(50) NOT NULL)");
//
//        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `invoiceDetails` (" +
//                "id INTEGER NOT NULL primary key AUTOINCREMENT, " +
//                "invoice INTEGER NOT NULL, " +
//                "itemTypeId varchar(50) NOT NULL, " +
//                "unitPrice double(10,2) NOT NULL, " +
//                "quantity double NOT NULL)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `purchases` (" +
                "id INTEGER NOT NULL primary key AUTOINCREMENT, " +
                "invoiceId INTEGER NOT NULL)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public int DeleteInvoice_invoice(int invoice) {

        Log.d("sqlite_db", "DeleteInvoice_invoice");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int result = sqLiteDatabase.delete("invoice", "WHERE invoice = ?", new String[]{String.valueOf(invoice)});
        int result2 = sqLiteDatabase.delete("invoiceDetails", "WHERE invoice = ?", new String[]{String.valueOf(invoice)});

        Log.d("sqlite_db", "DeleteInvoice_invoice" + result);

        return result;
    }


    //get number of products in the shopping cart
    public void InvoiceDelivered_invoice(int invoice, String recipeStatus) {
        Log.d("sqlite_db", "UpdateOnInvoice_invoice_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE invoice SET recipeStatus = '" + recipeStatus + "' WHERE invoice =  " + invoice + ";");


        Log.d("sqlite_db", "update db invoice read status ");
    }


    public long InsertNewInvoice_invoice(int invoice, String recipeDate, String customerName, String customerMobile, String province,
                                         double lat, double lng, String recipeCost, String paymentType, String recipeStatus) {

        Log.d("sqlite_db", "InsertNewInvoice_invoice");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("invoice", invoice);
        contentValues.put("recipeDate", recipeDate);
        contentValues.put("customerName", customerName);
        contentValues.put("customerMobile", customerMobile);
        contentValues.put("province", province);
        contentValues.put("lat", lat);
        contentValues.put("lng", lng);
        contentValues.put("recipeCost", recipeCost);
        contentValues.put("paymentType", paymentType);
        contentValues.put("recipeStatus", recipeStatus);

        Long result = sqLiteDatabase.insert("invoice", null, contentValues);

        Log.d("sqlite_db", "InsertNewInvoice_invoice" + result);

        return result;
    }


    public Cursor GetPurchasesArchive_invoice() {

        Log.d("sqlite_db", "GetPurchasesArchive_invoice_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *  FROM invoice", null);

        Log.d("sqlite_db", "row:" + cursor.getCount());
        Log.d("sqlite_db", "GetPurchasesArchive_invoice_Completed");


        return cursor;
    }


    public Cursor GetPurchasesRequests_invoice() {

        Log.d("sqlite_db", "GetPurchasesRequests_invoice_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *  FROM invoice", null);

        Log.d("sqlite_db", "row:" + cursor.getCount());
        Log.d("sqlite_db", "GetPurchasesRequests_invoice_Completed");


        return cursor;
    }


    public long InsertNewInvoiceDetails_invoiceDetails(int invoice, int productId, String productOptionTitle,
                                                       double unitPrice, int quantity) {

        Log.d("sqlite_db", "InsertNewInvoiceDetails_invoiceDetails");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("invoice", invoice);
        contentValues.put("productId", productId);
        contentValues.put("productOptionTitle", productOptionTitle);
        contentValues.put("unitPrice", unitPrice);
        contentValues.put("quantity", quantity);

        Long result = sqLiteDatabase.insert("invoiceDetails", null, contentValues);

        Log.d("sqlite_db", "InsertNewInvoiceDetails_invoiceDetails" + result);

        return result;
    }


    public long InsertNewInvoice_purchases(int invoiceId) {

        Log.d("sqlite_db", "InsertNewInvoice_purchases");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("invoiceId", invoiceId);

        Long result = sqLiteDatabase.insert("purchases", null, contentValues);

        Log.d("sqlite_db", "InsertNewInvoice_purchases" + result);

        return result;
    }


    public int DeleteAllProductsFromCart_shoppingCart() {

        Log.d("sqlite_db", "DeleteAllProductsFromCart_shoppingCart");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int result = sqLiteDatabase.delete("shoppingCart", null, null);

        Log.d("sqlite_db", "DeleteAllProductsFromCart_shoppingCart" + result);

        return result;
    }


    public Long InsertNewProduct_shoppingCart(int itemTypeId, int quantity) {

        Log.d("sqlite_db", "InsertNewProduct_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemTypeId", itemTypeId);
        contentValues.put("quantity", quantity);

        Long result = sqLiteDatabase.insert("shoppingCart", null, contentValues);

        Log.d("sqlite_db", "InsertNewProduct_shoppingCart" + result);

        return result;
    }


    //get number of products in the shopping cart
    public Cursor GetProducts_shoppingCart() {

        Log.d("sqlite_db", "GetProducts_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT *  FROM shoppingCart", null);

        Log.d("sqlite_db", "row:" + cursor.getCount());
        Log.d("sqlite_db", "GetProducts_shoppingCart_Completed");

        return cursor;
    }


    //get number of products in the shopping cart
    public Cursor GetProductsQtySum_shoppingCart() {

        Log.d("sqlite_db", "GetProductsCount_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM(quantity) AS totalQty  FROM shoppingCart", null);

        Log.d("sqlite_db", "row:" + cursor.getCount());
        Log.d("sqlite_db", "GetProductsCount_shoppingCart_Completed");

        return cursor;
    }


    //get number of products in the shopping cart
    public void AddToProduct_shoppingCart(int itemTypeId, int newQty) {
        Log.d("sqlite_db", "AddToProduct_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE shoppingCart SET quantity = quantity + " + newQty + " WHERE itemTypeId =  " + itemTypeId + ";");


        Log.d("sqlite_db", "update db shoppingCart read status ");
    }


    //get number of products in the shopping cart
    public void UpdateOnProduct_shoppingCart(int itemTypeId, int newQty) {
        Log.d("sqlite_db", "UpdateOnProduct_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE shoppingCart SET quantity = " + newQty + " WHERE itemTypeId = " + itemTypeId + ";");


        Log.d("sqlite_db", "update db shoppingCart read status ");
    }


    //get number of products in the shopping cart
    public Cursor GetSingleProductQty_shoppingCart(int itemTypeId) {

        Log.d("sqlite_db", "GetSingleProductQty_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT quantity  FROM shoppingCart WHERE itemTypeId = ?", new String[]{String.valueOf(itemTypeId)});

        Log.d("sqlite_db", "row:" + cursor.getCount());
        Log.d("sqlite_db", "GetSingleProductQty_shoppingCart_Completed");

        return cursor;
    }


    //get number of products in the shopping cart
    public void RemoveProduct_shoppingCart(int itemTypeId) {
        Log.d("sqlite_db", "RemoveProduct_shoppingCart_started");

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete("shoppingCart", "itemTypeId = ?", new String[]{String.valueOf(itemTypeId)});


        Log.d("sqlite_db", "remove product from db shoppingCart");
    }
}
