package br.com.mwmobile.expirationcontrol.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierDao;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import io.reactivex.Single;

/**
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 16/11/2017
 */

public class SupplierRepository {

    private static SupplierRepository instance;
    final SupplierDao supplierDao;

    private SupplierRepository(SupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }

    /**
     * Get the Supplier Repository Instance
     *
     * @param supplierDao Supplier DAO
     * @return Supplier Repository Instance
     */
    public static SupplierRepository getInstance(SupplierDao supplierDao) {
        if (instance == null) {
            instance = new SupplierRepository(supplierDao);
        }
        return instance;
    }

    /**
     * Insert or Update a Supplier
     *
     * @param supplierModel Supplier data
     */
    public long insertOrUpdate(final Supplier supplierModel) {
        return supplierDao.insertOrUpdate(supplierModel);
    }

    /**
     * Get a Supplier by name
     *
     * @param name Supplier's name
     * @return Supplier data
     */
    public List<Supplier> getByName(String name) {
        return supplierDao.getByName(name);
    }

    /**
     * Get a Supplier by Id
     *
     * @param id Supplier's id
     * @return Supplier data
     */
    public Single<Supplier> getById(long id) {
        return supplierDao.getById(id);
    }

    /**
     * Get all Suppliers
     *
     * @return List of Suppliers
     */
    public LiveData<List<Supplier>> getAll() {
        return supplierDao.getAll();
    }

    /**
     * Delete a Supplier
     *
     * @param suppliers Supplier list
     */
    public void delete(List<Supplier> suppliers) {
        supplierDao.delete(suppliers);
    }

    /**
     * Get a Supplier
     *
     * @param supplierId Supplier id
     * @return Supplier data
     */
    public Supplier getSupplier(long supplierId) {
        return supplierDao.getSupplier(supplierId);
    }
}
