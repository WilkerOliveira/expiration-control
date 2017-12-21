package br.com.mwmobile.expirationcontrol.RoomTest;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class for working with the SQLiteDatabase.
 */
public class SqliteDatabaseTestHelper {

    public static void insertSupplier(long id, String name, SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put("id", id);
        values.put("name", name);

        db.insertWithOnConflict("supplier", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public static void insertProduct(long id, String name, long supplierId, String expiration, String quantity, SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put("id", id);
        values.put("name", name);
        values.put("supplierId", supplierId);
        values.put("expiration", expiration);
        values.put("quantity", quantity);

        db.insertWithOnConflict("product", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }

    public static void createTables(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS supplier (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT )");
        db.execSQL("CREATE TABLE IF NOT EXISTS product (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, " +
                    "expiration INTEGER , supplierId INTEGER NOT NULL, quantity TEXT, " +
                    "FOREIGN KEY (\"supplierId\") REFERENCES \"supplier\"(\"id\") ON DELETE CASCADE )");

        db.close();
    }

    public static void clearDatabase(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS supplier");

        db.close();
    }
}
