package br.com.mwmobile.expirationcontrol.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.dao.ProductDao;
import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierDao;

/**
 * Migration Content Provider
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 14/02/2018
 */

public class MigrationContentProvider extends ContentProvider {

    private static final int SUPPLIER_CODE = 1;
    private static final int PRODUCT_CODE = 2;

    /**
     * The authority of this content provider.
     */
    public static final String AUTHORITY = "br.com.mwmobile.expirationcontrol.provider";

    /**
     * The URI for the Supplier table.
     */
    public static final Uri URI_SUPPLIER = Uri.parse("content://" + AUTHORITY + "/" + "supplier");
    /**
     * The URI for the Product table.
     */
    public static final Uri URI_PRODUCT = Uri.parse("content://" + AUTHORITY + "/" + "product");

    /**
     * The URI matcher.
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, "supplier", SUPPLIER_CODE);
        MATCHER.addURI(AUTHORITY, "product", PRODUCT_CODE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final int code = MATCHER.match(uri);
        if (code == SUPPLIER_CODE) {

            final Context context = getContext();
            if (context == null) {
                return null;
            }

            SupplierDao supplierDAO = AppDatabase.getDatabase(context).itemSupplierDAO();

            return supplierDAO.getAllCursor();

        } else if(code == PRODUCT_CODE) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }

            ProductDao productDAO = AppDatabase.getDatabase(context).itemProductDAO();

            return productDAO.getAllCursor();
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
