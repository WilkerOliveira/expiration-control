package br.com.mwmobile.expirationcontrol.RoomTest;

import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
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
import static org.junit.Assert.assertEquals;

/**
 * Test class to validate the migration of database
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 02/12/2017
 */

public class MigrationDaaBaseTest {

    private static final String TEST_DB_NAME = "test-db";

    private static final long SUPPLIER_ID = 99;
    private static final long PRODUCT_ID = 99;

    //create a new supplier
    private static final Supplier SUPPLIER = new Supplier(SUPPLIER_ID, "New Supplier Test");

    //create a new product
    private static final Product PRODUCT = new Product(PRODUCT_ID, "New Product Test", new Date(System.currentTimeMillis()), new BigDecimal(1), SUPPLIER_ID);

    // Helper for creating Room databases and migrations
    @Rule
    public MigrationTestHelper mMigrationTestHelper =
            new MigrationTestHelper(
                    InstrumentationRegistry.getInstrumentation(),
                    AppDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    // Helper for creating SQLite database in version 1
    private SqliteTestDbOpenHelper mSqliteTestDbHelper;

    @Before
    public void setUp() throws Exception {
        // To test migrations from version 1 of the database, we need to create the database
        // with version 1 using SQLite API
        mSqliteTestDbHelper = new SqliteTestDbOpenHelper(InstrumentationRegistry.getTargetContext(),
                TEST_DB_NAME);
        // We're creating the table for every test, to ensure that the table is in the correct state
        SqliteDatabaseTestHelper.createTables(mSqliteTestDbHelper);
    }

    @After
    public void tearDown() throws Exception {
        // Clear the database after every test
        SqliteDatabaseTestHelper.clearDatabase(mSqliteTestDbHelper);
    }

    @Test
    public void migrationFrom1To2_containsCorrectData() throws IOException {
        // Create the database with the initial version 1 schema and insert a supplier and product
        SqliteDatabaseTestHelper.insertSupplier(SUPPLIER.getId(), SUPPLIER.getName(), mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.insertProduct(PRODUCT.getId(), PRODUCT.getName(), PRODUCT.getSupplierId(),
                DateUtil.parseToString(PRODUCT.getExpiration()),
                NumberUtil.currencyToString(PRODUCT.getQuantity()), mSqliteTestDbHelper);

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        mMigrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true,
                MIGRATION_1_2);

        // Get the latest, migrated, version of the database
        // Check that the correct data is in the database
        Single<Product> dbProduct = getMigratedRoomDatabase().itemProductDAO().getById(PRODUCT_ID);
        assertEquals(dbProduct.blockingGet().getId(), PRODUCT_ID);
        assertEquals(dbProduct.blockingGet().getName(), PRODUCT.getName());

        // The date was missing in version 2, so it should be null in version 3
        assertEquals(dbProduct.blockingGet().getBarCode(), null);
    }

    private AppDatabase getMigratedRoomDatabase() {
        AppDatabase database = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
                AppDatabase.class, TEST_DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
        // close the database and release any stream resources when the test finishes
        mMigrationTestHelper.closeWhenFinished(database);
        return database;
    }
}
