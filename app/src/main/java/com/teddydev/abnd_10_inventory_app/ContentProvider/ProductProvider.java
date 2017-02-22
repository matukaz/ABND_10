package com.teddydev.abnd_10_inventory_app.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract;
import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;
import com.teddydev.abnd_10_inventory_app.Database.ProductDBHelper;
import com.teddydev.abnd_10_inventory_app.R;

/**
 * Created by Matu on 20.02.2017.
 */

public class ProductProvider extends ContentProvider {

    private ProductDBHelper productDBHelper;

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 1;

    /**
     * URI matcher code for the content URI for a single product in product table
     */
    private static final int PRODUCT_ID = 100;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productDBHelper = new ProductDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = productDBHelper.getReadableDatabase();

        Cursor cursor;
        switch (match) {
            case PRODUCTS:
                // Return all products in table.
                cursor = db.query(ProductTable.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductTable.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                // no match found
                throw new IllegalArgumentException(getContext().getString(R.string.cannot_query_unknown_uri) + uri);
        }
        // Set notification URI on the cursor, if any data changes, then we know we need to update cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductTable.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        //TODO data validation
        SQLiteDatabase db = productDBHelper.getReadableDatabase();
        long newRowId = db.insert(ProductTable.TABLE_NAME, null, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = productDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ProductTable.TABLE_NAME, selection, selectionArgs);
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ProductTable.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Otherwise, get writeable database to update the data

        //TODO data validation
       /** if (dataValidation(values)) {
            // Returns the number of database rows affected by the update statement
        }
        throw new IllegalArgumentException("something went wrong :("); */

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductTable._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsUpdate = updateProduct(uri, contentValues, selection, selectionArgs);
                if (rowsUpdate != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = productDBHelper.getWritableDatabase();

       // if (dataValidation(values)) {
            // Returns the number of database rows affected by the update statement

            getContext().getContentResolver().notifyChange(uri, null);
            return database.update(ProductTable.TABLE_NAME, values, selection, selectionArgs);

        //}
       // throw new IllegalArgumentException("something went wrong :(");
    }


    //Validation quantity can't be -1
}
