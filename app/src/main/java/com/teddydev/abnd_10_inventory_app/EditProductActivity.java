package com.teddydev.abnd_10_inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_CONTACT_EMAIL;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_CONTACT_PHONE;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_NAME;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_PRICE;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_QUANTITY;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.CONTENT_URI;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView textErrorMsg;
    private EditText editProductName;
    private EditText editProductQuantity;
    private EditText editProductPrice;
    private EditText editProductSupplierPhone;
    private EditText editProductSupplierEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editProductName = (EditText) findViewById(R.id.edit_product_name);
        editProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        editProductPrice = (EditText) findViewById(R.id.edit_product_price);
        editProductSupplierPhone = (EditText) findViewById(R.id.edit_product_phone_number);
        editProductSupplierEmail = (EditText) findViewById(R.id.edit_product_email);

        Uri uri = getIntent().getData();
        if (uri != null) {
            setTitle(getString(R.string.edit_product));
            // get data
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.edit_product_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (isDataValid()) {
                    if (getIntent().getData() != null) {
                        updateProduct();
                    } else {
                        saveNewProduct();
                    }
                    finish();
                }
                return true;
            case R.id.action_delete:
                // Do nothing for now
                if (getIntent().getData() == null) {
                    invalidateOptionsMenu();
                } else {
                    showDeleteConfirmationDialog();
                }
                return true;
            case R.id.order_more:
                if (getIntent().getData() != null) {
                contactAndOrderMore();
                } else {
                    invalidateOptionsMenu();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar **/
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void contactAndOrderMore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.contact_more_info_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + editProductSupplierPhone.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + editProductSupplierEmail.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "We need product called:  " +
                        editProductName.getText().toString().trim();
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Deletes the words that match the selection criteria
        getContentResolver().delete(
                getIntent().getData(),   // the user dictionary content URI
                null,                    // the column to select on
                null                      // the value to compare to
        );
    }

    private void updateProduct() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, editProductName.getText().toString().trim());
        values.put(COLUMN_PRODUCT_QUANTITY, Integer.parseInt(editProductQuantity.getText().toString()));
        values.put(COLUMN_PRODUCT_PRICE, Integer.parseInt(editProductPrice.getText().toString()));
        values.put(COLUMN_PRODUCT_CONTACT_PHONE, editProductSupplierPhone.getText().toString());
        values.put(COLUMN_PRODUCT_CONTACT_EMAIL, editProductSupplierEmail.getText().toString());

        Uri uri = getIntent().getData();
        Long id = ContentUris.parseId(uri);

        // Defines selection criteria for the rows you want to update
        String[] args = {String.valueOf(id)};
        getContentResolver().update(uri, values, ProductTable._ID + "=?", args);

        getContentResolver().update(
                getIntent().getData(),   // the user dictionary content URI
                values,                      // the columns to update
                null,
                null
        );
    }

    private void saveNewProduct() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, editProductName.getText().toString().trim());
        values.put(COLUMN_PRODUCT_QUANTITY, Integer.parseInt(editProductQuantity.getText().toString()));
        values.put(COLUMN_PRODUCT_PRICE, Integer.parseInt(editProductPrice.getText().toString()));
        values.put(COLUMN_PRODUCT_CONTACT_PHONE, editProductSupplierPhone.getText().toString());
        values.put(COLUMN_PRODUCT_CONTACT_EMAIL, editProductSupplierEmail.getText().toString());
        getContentResolver().insert(CONTENT_URI, values);
    }

    public boolean isDataValid() {
        if (TextUtils.isEmpty(editProductName.getText().toString().trim()) ||
                TextUtils.isEmpty(editProductQuantity.getText()) ||
                TextUtils.isEmpty(editProductPrice.getText())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductTable._ID,
                ProductTable.COLUMN_PRODUCT_NAME,
                ProductTable.COLUMN_PRODUCT_QUANTITY,
                ProductTable.COLUMN_PRODUCT_PRICE,
                ProductTable.COLUMN_PRODUCT_CONTACT_PHONE,
                ProductTable.COLUMN_PRODUCT_CONTACT_EMAIL
        };

        return new CursorLoader(this, getIntent().getData(), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {

            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE);
            int phoneColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_CONTACT_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_CONTACT_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String contactInfoPhone = cursor.getString(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);

            editProductName.setText(name);
            editProductQuantity.setText(Integer.toString(quantity));
            editProductPrice.setText(Integer.toString(price));
            editProductSupplierPhone.setText(contactInfoPhone);
            editProductSupplierEmail.setText(email);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // RESET ALL THE INPUTS
        editProductName.setText(null);
        editProductQuantity.setText(null);
        editProductPrice.setText(null);
        editProductSupplierPhone.setText(null);
        editProductSupplierEmail.setText(null);
    }
}
