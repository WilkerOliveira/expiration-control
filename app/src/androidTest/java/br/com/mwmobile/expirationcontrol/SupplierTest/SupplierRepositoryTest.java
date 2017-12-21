package br.com.mwmobile.expirationcontrol.SupplierTest;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.mwmobile.expirationcontrol.TestUtil.LiveDataTestUtil;
import br.com.mwmobile.expirationcontrol.repository.SupplierRepository;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 16/12/2017
 */

public class SupplierRepositoryTest {

    private AppDatabase appDatabase;
    private SupplierRepository supplierRepository;
    private long SUPPLIER_ID = 1;
    private Supplier firstSupplier;

    public SupplierRepositoryTest() {
        firstSupplier = new Supplier(SUPPLIER_ID, "First Supplier");
    }

    @Before
    public void configTest() {
        Context context = InstrumentationRegistry.getTargetContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        supplierRepository = SupplierRepository.getInstance(appDatabase.itemSupplierDAO());
    }

    @Test
    public void insertNewSupplierTest() {
        long supplierID = supplierRepository.insertOrUpdate(firstSupplier);

        assertTrue(supplierID == SUPPLIER_ID);

        supplierRepository.delete(Collections.singletonList(firstSupplier));
    }

    @Test
    public void updateSupplierTest() {

        supplierRepository.insertOrUpdate(firstSupplier);

        Supplier current = supplierRepository.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), firstSupplier.getName());

        firstSupplier.setName("First Supplier Updated");

        supplierRepository.insertOrUpdate(firstSupplier);

        current = supplierRepository.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), "First Supplier Updated");

        supplierRepository.delete(Collections.singletonList(firstSupplier));

    }

    @Test
    public void getSupplierByIdTest() {

        supplierRepository.insertOrUpdate(firstSupplier);

        Supplier current = supplierRepository.getById(SUPPLIER_ID).blockingGet();

        assertEquals(current.getName(), firstSupplier.getName());

    }

    @Test(expected = Throwable.class)
    public void deleteSupplierTest() {

        supplierRepository.insertOrUpdate(firstSupplier);

        try {
            List<Supplier> suppliers = LiveDataTestUtil.getValue(supplierRepository.getAll());

            assertTrue(suppliers.size() == 1);

        } catch (InterruptedException e) {
            assertTrue(false);
        }

        supplierRepository.delete(Collections.singletonList(firstSupplier));

        Supplier current = supplierRepository.getById(SUPPLIER_ID).blockingGet();
    }

    @Test
    public void deleteAllSupplierTest() {

        supplierRepository.insertOrUpdate(firstSupplier);
        Supplier supplier2 = new Supplier(2, "Test 2");
        supplierRepository.insertOrUpdate(supplier2);

        try {
            List<Supplier> suppliers = LiveDataTestUtil.getValue(supplierRepository.getAll());

            assertTrue(suppliers.size() == 2);

        } catch (InterruptedException e) {
            assertTrue(false);
        }

        supplierRepository.delete(Arrays.asList(firstSupplier, supplier2));

        supplierRepository.getAll().observeForever(suppliers -> {
            assert suppliers != null;
            assertTrue(suppliers.size() == 0);
        });

    }

    @Test
    public void getAllSupplierTest() {
        long id = supplierRepository.insertOrUpdate(firstSupplier);
        assertTrue(id == 1);

        firstSupplier.setId(2);
        id = supplierRepository.insertOrUpdate(firstSupplier);
        assertTrue(id == 2);

        firstSupplier.setId(3);
        id = supplierRepository.insertOrUpdate(firstSupplier);
        assertTrue(id == 3);

        try {
            List<Supplier> suppliers = LiveDataTestUtil.getValue(supplierRepository.getAll());

            assertTrue(suppliers.size() == 3);

        } catch (InterruptedException e) {
            assertTrue(false);
        }

    }

}
