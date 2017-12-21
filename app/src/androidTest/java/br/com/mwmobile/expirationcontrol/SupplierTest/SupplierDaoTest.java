package br.com.mwmobile.expirationcontrol.SupplierTest;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import br.com.mwmobile.expirationcontrol.TestUtil.LiveDataTestUtil;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierDao;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 16/12/2017
 */

@RunWith(AndroidJUnit4.class)
public class SupplierDaoTest {

    private AppDatabase appDatabase;
    private SupplierDao supplierDao;
    private long SUPPLIER_ID = 1;
    private Supplier firstSupplier;

    public SupplierDaoTest() {
        firstSupplier = new Supplier(SUPPLIER_ID, "First Supplier");
    }

    @Before
    public void configTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        supplierDao = appDatabase.itemSupplierDAO();
    }

    @After
    public void closeDataBase() {
        appDatabase.close();
    }

    @Test
    public void insertNewSupplierTest() {

        long supplierID = supplierDao.insertOrUpdate(firstSupplier);

        assertTrue(supplierID == SUPPLIER_ID);
    }

    @Test
    public void updateSupplierTest() {

        supplierDao.insertOrUpdate(firstSupplier);

        Supplier current = supplierDao.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), firstSupplier.getName());

        firstSupplier.setName("First Supplier Updated");

        supplierDao.insertOrUpdate(firstSupplier);

        current = supplierDao.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), "First Supplier Updated");

    }

    @Test
    public void getSupplierByIdTest() {

        supplierDao.insertOrUpdate(firstSupplier);

        Supplier current = supplierDao.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), firstSupplier.getName());

    }

    @Test(expected = Throwable.class)
    public void deleteSupplierTest() {

        supplierDao.insertOrUpdate(firstSupplier);

        Supplier current = supplierDao.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), firstSupplier.getName());

        supplierDao.delete(Collections.singletonList(firstSupplier));

        supplierDao.getById(SUPPLIER_ID).blockingGet();
    }

    @Test
    public void getAllSupplierTest() {
        long id = supplierDao.insertOrUpdate(firstSupplier);
        assertTrue(id == 1);

        firstSupplier.setId(2);
        id = supplierDao.insertOrUpdate(firstSupplier);
        assertTrue(id == 2);

        firstSupplier.setId(3);
        id = supplierDao.insertOrUpdate(firstSupplier);
        assertTrue(id == 3);

        try {
            List<Supplier> suppliers = LiveDataTestUtil.getValue(supplierDao.getAll());

            assertTrue(suppliers.size() == 3);

        } catch (InterruptedException e) {
            assertTrue(false);
        }

    }

}
