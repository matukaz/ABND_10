package com.teddydev.abnd_10_inventory_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;
import com.teddydev.abnd_10_inventory_app.Database.ProductDBHelper;

import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_CONTACT_INFO;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_NAME;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_PRICE;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_QUANTITY;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.TABLE_NAME;

//TODO delete me

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView productListView;
    private FloatingActionButton addProductFloatingBtn;
    private ProductAdapter productAdapter;

    private FloatingActionButton.OnClickListener addProductFloatingBtnOnClickListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Add product
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addProductFloatingBtn = (FloatingActionButton) findViewById(R.id.fab_add_product);
        addProductFloatingBtn.setOnClickListener(addProductFloatingBtnOnClickListener);

        // Find the ListView which will be populated with the pet data
        productListView = (ListView) findViewById(R.id.products_list);
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        productAdapter = new ProductAdapter(this, null);
        productListView.setAdapter(productAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
        //TODO delete me
        insertMockDataForTesting();
    }

    private void insertMockDataForTesting() {


        ProductDBHelper productDBHelper = new ProductDBHelper(this);
        SQLiteDatabase db = productDBHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, "TEST PRODUCT NAME");
        values.put(COLUMN_PRODUCT_QUANTITY, 10);
        values.put(COLUMN_PRODUCT_PRICE, 100);
        values.put(COLUMN_PRODUCT_CONTACT_INFO, "www.google.ee");
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        Log.d("this", newRowId + "?????");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductTable._ID,
                ProductTable.COLUMN_PRODUCT_NAME,
                ProductTable.COLUMN_PRODUCT_QUANTITY,
                ProductTable.COLUMN_PRODUCT_PRICE
        };
        return new CursorLoader(this, ProductTable.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productAdapter.swapCursor(null);
    }
}
