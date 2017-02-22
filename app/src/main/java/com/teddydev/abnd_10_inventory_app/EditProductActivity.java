package com.teddydev.abnd_10_inventory_app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_CONTACT_EMAIL;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_CONTACT_PHONE;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_NAME;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_PRICE;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.COLUMN_PRODUCT_QUANTITY;
import static com.teddydev.abnd_10_inventory_app.Database.ProductContract.ProductTable.CONTENT_URI;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;
    private EditText editProductName;
    private EditText editProductQuantity;
    private EditText editProductPrice;
    private EditText editProductSupplierPhone;
    private EditText editProductSupplierEmail;
    private ImageView imageProduct;
    private Button buttonAddProductImage;
    private Button buttonDecreaseQuantityByOne;
    private Button buttonIncreaseQuantityByOne;

    Button.OnClickListener buttonAddProductImageOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            openImageIntent();
        }
    };

    Button.OnClickListener buttonDecreaseQuantityByOneListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            decreaseQuantityByOne();
        }
    };

    Button.OnClickListener buttonIncreaseQuantityByOneListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            increaseQuantityByOne();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        editProductName = (EditText) findViewById(R.id.edit_product_name);
        editProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        editProductPrice = (EditText) findViewById(R.id.edit_product_price);
        editProductSupplierPhone = (EditText) findViewById(R.id.edit_product_phone_number);
        editProductSupplierEmail = (EditText) findViewById(R.id.edit_product_email);
        imageProduct = (ImageView) findViewById(R.id.image_product);
        buttonAddProductImage = (Button) findViewById(R.id.button_product_image);
        buttonAddProductImage.setOnClickListener(buttonAddProductImageOnClickListener);

        buttonDecreaseQuantityByOne = (Button) findViewById(R.id.decrease_quantity);
        buttonDecreaseQuantityByOne.setOnClickListener(buttonDecreaseQuantityByOneListener);

        buttonIncreaseQuantityByOne = (Button) findViewById(R.id.increase_quantity);
        buttonIncreaseQuantityByOne.setOnClickListener(buttonIncreaseQuantityByOneListener);

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (getIntent().getData() == null) {
            MenuItem actionDelete = menu.findItem(R.id.action_delete);
            MenuItem orderMoreItem = menu.findItem(R.id.order_more);
            actionDelete.setVisible(false);
            orderMoreItem.setVisible(false);
        }
        return true;
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
        if (imageProduct.getDrawable() != null) {
            values.put(ProductTable.COLUMN_PRODUCT_IMAGE, Utility.getBytes(imageProduct));
        }

        Uri uri = getIntent().getData();
        Long id = ContentUris.parseId(uri);

        // Defines selection criteria for the rows you want to update
        String[] args = {String.valueOf(id)};
        getContentResolver().update(uri, values, ProductTable._ID + "=?", args);
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
                ProductTable.COLUMN_PRODUCT_CONTACT_EMAIL,
                ProductTable.COLUMN_PRODUCT_IMAGE
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
            int imageColumnIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String contactInfoPhone = cursor.getString(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            byte[] imageArray = cursor.getBlob(imageColumnIndex);
            ;

            editProductName.setText(name);
            editProductQuantity.setText(Integer.toString(quantity));
            editProductPrice.setText(Integer.toString(price));
            editProductSupplierPhone.setText(contactInfoPhone);
            editProductSupplierEmail.setText(email);

            if (imageArray != null) {
                if (imageArray.length > 0) {
                    imageProduct.setImageBitmap(Utility.getImage(imageArray));
                }
            }
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
        imageProduct.setImageBitmap(null);
    }

    private void openImageIntent() {
        checkPermissions();

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    openImageIntent();

                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                try {
                    Uri imageUri = resultData.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    imageProduct.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void decreaseQuantityByOne() {
        String previousValueString = editProductQuantity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty() || previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            editProductQuantity.setText(String.valueOf(previousValue - 1));
        }
    }

    private void increaseQuantityByOne() {
        String previousValueString = editProductQuantity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        editProductQuantity.setText(String.valueOf(previousValue + 1));
    }
}
