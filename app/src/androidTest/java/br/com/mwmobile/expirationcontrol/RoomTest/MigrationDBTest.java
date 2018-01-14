package br.com.mwmobile.expirationcontrol.RoomTest;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;
import io.reactivex.Single;

import static br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase.MIGRATION_1_2;
import static br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase.MIGRATION_2_3;
import static org.junit.Assert.assertEquals;

/**
 * Test class to validate the migration of database
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 02/12/2017
 */

public class MigrationDBTest {

    private static final String TEST_DB_NAME = "migration-test_db";

    private static final long SUPPLIER_ID = 99;
    private static final long PRODUCT_ID = 99;
    private static final String BARCODE = "123456";
    private static final BigDecimal PRODUCT_VALUE = BigDecimal.valueOf(365L);


    //create a new supplier
    private static final Supplier SUPPLIER = new Supplier(SUPPLIER_ID, "New Supplier Test");

    //create a new product
    private static final Product PRODUCT = new Product("New Product Test", new Date(System.currentTimeMillis()), BARCODE, new BigDecimal(1), SUPPLIER_ID, PRODUCT_VALUE);

    // Helper for creating Room databases and migrations
    @Rule
    public MigrationTestHelper mMigrationTestHelper =
            new MigrationTestHelper(
                    InstrumentationRegistry.getInstrumentation(),
                    AppDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    private SupportSQLiteDatabase db;

    @Test
    public void migrationFrom1To2_containsCorrectData() throws IOException {

        db = mMigrationTestHelper.createDatabase(TEST_DB_NAME, 1);

        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Supplier");

        db.execSQL("CREATE TABLE " +
                "Supplier ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT " +
                ");");

        db.execSQL("CREATE TABLE " +
                "Product ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "expiration INTEGER , " +
                "supplierId INTEGER NOT NULL, " +
                "quantity TEXT," +
                " FOREIGN KEY (supplierId) REFERENCES Supplier(id) ON DELETE CASCADE );");

        insertValues();

        validInsertedValuesCursor();

        db.close();

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        db = mMigrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true,
                MIGRATION_1_2);

        Single<Product> dbProduct = validInsertedValues();

        assertEquals(dbProduct.blockingGet().getBarCode(), null);

        PRODUCT.setId(PRODUCT_ID);

        getMigratedRoomDatabase().itemProductDAO().insertOrUpdate(PRODUCT);

        dbProduct = getMigratedRoomDatabase().itemProductDAO().getById(PRODUCT_ID);

        assertEquals(dbProduct.blockingGet().getBarCode(), BARCODE);
    }

    @Test
    public void migrationFrom2To3_containsCorrectData() throws IOException {
        db = mMigrationTestHelper.createDatabase(TEST_DB_NAME, 2);

        db.execSQL("DROP TABLE IF EXISTS Product");
        db.execSQL("DROP TABLE IF EXISTS Supplier");

        db.execSQL("CREATE TABLE " +
                "Supplier ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT " +
                ");");

        db.execSQL("CREATE TABLE " +
                "Product ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "expiration INTEGER , " +
                "supplierId INTEGER NOT NULL, " +
                "quantity TEXT," +
                "barCode TEXT," +
                " FOREIGN KEY (supplierId) REFERENCES Supplier(id) ON DELETE CASCADE );");

        ContentValues values = new ContentValues();
        values.put("id", PRODUCT_ID);
        values.put("name", PRODUCT.getName());
        values.put("supplierId", PRODUCT.getSupplierId());
        values.put("expiration", DateUtil.parseToString(PRODUCT.getExpiration()));
        values.put("quantity", NumberUtil.currencyToString(PRODUCT.getQuantity()));
        values.put("barCode", PRODUCT.getBarCode());
        db.insert("product", SQLiteDatabase.CONFLICT_REPLACE, values);

        db.close();

        db = mMigrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 3, true,
                MIGRATION_1_2, MIGRATION_2_3);

        Cursor cursor = db.query("select * from product where id = " + PRODUCT_ID);
        if (cursor.moveToFirst()) {
            do {
                String barCode = cursor.getString(cursor.getColumnIndex("barCode"));

                assertEquals(barCode, PRODUCT.getBarCode());

            } while (cursor.moveToNext());
        }

        cursor.close();

        String[] selectionArgs = {Long.toString(PRODUCT_ID)};
        values.put("value", PRODUCT_VALUE.toString());

        db.update("product", SQLiteDatabase.CONFLICT_REPLACE, values, "id = ?", selectionArgs);

        AppDatabase appDatabase = getMigrated2RoomDatabase();

        //Supplier supplier = appDatabase.itemSupplierDAO().getById(SUPPLIER_ID).blockingGet();
        Product product = appDatabase.itemProductDAO().getById(PRODUCT_ID).blockingGet();

        assertEquals(product.getId(), PRODUCT_ID);
        assertEquals(product.getValue(), PRODUCT_VALUE);
    }

    private void validInsertedValuesCursor() {
        Cursor cursor = db.query("select * from product where id = " + PRODUCT_ID);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                assertEquals(id, PRODUCT_ID);

                String name = cursor.getString(cursor.getColumnIndex("name"));
                assertEquals(name, PRODUCT.getName());

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private AppDatabase getMigratedRoomDatabase() {
        return Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class, TEST_DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    private AppDatabase getMigrated2RoomDatabase() {
        AppDatabase database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class, TEST_DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();
        // close the database and release any stream resources when the test finishes
        mMigrationTestHelper.closeWhenFinished(database);
        return database;
    }

    private Single<Product> validInsertedValues() {
        Single<Product> dbProduct = getMigratedRoomDatabase().itemProductDAO().getById(PRODUCT_ID);
        assertEquals(dbProduct.blockingGet().getId(), PRODUCT_ID);
        assertEquals(dbProduct.blockingGet().getName(), PRODUCT.getName());

        return dbProduct;
    }

    private void insertValues() {
        ContentValues values = new ContentValues();
        values.put("id", SUPPLIER.getId());
        values.put("name", SUPPLIER.getName());
        db.insert("supplier", SQLiteDatabase.CONFLICT_REPLACE, values);

        values = new ContentValues();
        values.put("id", PRODUCT_ID);
        values.put("name", PRODUCT.getName());
        values.put("supplierId", PRODUCT.getSupplierId());
        values.put("expiration", DateUtil.parseToString(PRODUCT.getExpiration()));
        values.put("quantity", NumberUtil.currencyToString(PRODUCT.getQuantity()));
        db.insert("product", SQLiteDatabase.CONFLICT_REPLACE, values);
    }
}
