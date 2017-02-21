package com.teddydev.abnd_10_inventory_app.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

/**
 * Created by Matu on 20.02.2017.
 */

public class ProductDBHelper extends SQLiteOpenHelper {

    public ProductDBHelper(Context context) {
        super(context, ProductTable.TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the product table
        String SQL_CREATE_PRODUCT_TABLE =  "CREATE TABLE " + ProductTable.TABLE_NAME + " ("
                + ProductTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductTable.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductTable.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductTable.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductTable.COLUMN_PRODUCT_IMAGE + " BLOB, "
                + ProductTable.COLUMN_PRODUCT_CONTACT_INFO + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Do nothing for now
    }
}
