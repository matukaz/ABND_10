package com.teddydev.abnd_10_inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_QUANTITY;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.CONTENT_URI;

/**
 * Created by Matu on 20.02.2017.
 */

public class ProductAdapter extends CursorAdapter {

    private Context context;

    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final long id = cursor.getLong(cursor.getColumnIndexOrThrow(ProductTable._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_PRICE));

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button saleButton = (Button) view.findViewById(R.id.button_sale);
        saleButton.setTag(id);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUpdateQuantityOfProduct(id, quantity);
            }
        });

        nameTextView.setText(name);
        quantityTextView.setText(context.getString(R.string.quantity, quantity));
        priceTextView.setText(context.getString(R.string.price, price));
    }

    private void getUpdateQuantityOfProduct(long id, int quantity) {
        quantity = quantity - 1;
        if(quantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_QUANTITY, quantity);
            context.getContentResolver().insert(CONTENT_URI, values);

            Uri uri = ContentUris.withAppendedId(ProductTable.CONTENT_URI, id);
            Long selectionId = ContentUris.parseId(uri);

            // Defines selection criteria for the rows you want to update
            String[] args = {String.valueOf(selectionId)};
            context.getContentResolver().update(uri, values, ProductTable._ID + "=?", args);

            context.getContentResolver().update(
                    uri,   // the user dictionary content URI
                    values,                      // the columns to update
                    null,
                    null
            );
        }
    }
}
