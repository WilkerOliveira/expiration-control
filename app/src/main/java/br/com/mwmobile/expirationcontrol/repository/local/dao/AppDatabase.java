package br.com.mwmobile.expirationcontrol.repository.local.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;

/**
 * Class responsible to maintain the DataBase
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

@Database(entities = {Supplier.class, Product.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Migration to add a barcode column to table Product
     *
     * @since 02/12/2017
     */
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'product' ADD COLUMN 'barCode' TEXT NULL");
        }
    };
    private static AppDatabase INSTANCE;

    /**
     * Return the AppDatabase Instance
     *
     * @param context Context
     * @return AppDatabase instance
     */
    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "expControl_db")
                            .addMigrations(MIGRATION_1_2) //ADD MIGRATION TO VERSION 2
                            .build();
        }
        return INSTANCE;
    }

    /**
     * Return the Instance of Supplier DAO
     *
     * @return Supplier DAO
     */
    public abstract SupplierDao itemSupplierDAO();

    /**
     * Return the Instance of Product DAO
     *
     * @return Product DAO
     */
    public abstract ProductDao itemProductDAO();

    /**
     * Return the Instance of SupplierProduct DAO
     *
     * @return SupplierProduct DAO
     */
    public abstract SupplierProductDao itemSupplierProductDAO();


}