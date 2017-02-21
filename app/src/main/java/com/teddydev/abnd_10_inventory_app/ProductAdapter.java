package com.teddydev.abnd_10_inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

/**
 * Created by Matu on 20.02.2017.
 */

public class ProductAdapter extends CursorAdapter {

    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductTable.COLUMN_PRODUCT_PRICE));

        nameTextView.setText(name);
        quantityTextView.setText(context.getString(R.string.quantity, quantity));
        priceTextView.setText(context.getString(R.string.price, price));
    }
}
