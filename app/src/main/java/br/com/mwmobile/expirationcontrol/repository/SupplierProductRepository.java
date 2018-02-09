package br.com.mwmobile.expirationcontrol.repository;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierProductDao;
import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;

/**
 * Supplier Product Repository
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */

public class SupplierProductRepository {

    private static SupplierProductRepository instance;
    private final SupplierProductDao supplierProductDao;

    private SupplierProductRepository(SupplierProductDao supplierProductDao) {
        this.supplierProductDao = supplierProductDao;
    }

    /**
     * Get the Supplier Product Repository Instance
     *
     * @param supplierProductDao Supplier Product DAO
     * @return Product Repository Instance
     */
    public static SupplierProductRepository getInstance(SupplierProductDao supplierProductDao) {
        if (instance == null) {
            instance = new SupplierProductRepository(supplierProductDao);
        }
        return instance;
    }

    /**
     * Get all Suppliers
     *
     * @return List of Products
     */
    public List<SupplierProduct> getAll() {
        return supplierProductDao.getAll();
    }

    /**
     * Get by Supplier Id
     *
     * @param id Supplier id
     * @return List of Products
     */
    public List<SupplierProduct> getBySupplierId(long id) {
        return supplierProductDao.getBySupplierId(id);
    }

    /**
     * Get by Supplier Id
     *
     * @param id          Supplier id
     * @return List of Products
     */
    public List<SupplierProduct> getBySupplierIdAndProductName(long id) {
        return supplierProductDao.getBySupplierIdAndProductName(id);
    }

}
