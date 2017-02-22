package com.teddydev.abnd_10_inventory_app;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

//TODO delete me

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView productListView;
    private FloatingActionButton addProductFloatingBtn;
    private ProductAdapter productAdapter;

    private FloatingActionButton.OnClickListener addProductFloatingBtnOnClickListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
            startActivity(intent);
        }
    };

    private ListView.OnItemClickListener productItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, EditProductActivity.class);
            Uri uri = ContentUris.withAppendedId(ProductTable.CONTENT_URI, id);
            intent.setData(uri);
            startActivity(intent);
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
        productListView.setOnItemClickListener(productItemClickListener);

        productAdapter = new ProductAdapter(this, null);
        productListView.setAdapter(productAdapter);

        getSupportLoaderManager().initLoader(0, null, this);
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
