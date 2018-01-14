package br.com.mwmobile.expirationcontrol.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.dao.ProductDao;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Product Repository
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */
public class ProductRepository {

    private static ProductRepository instance;
    private final ProductDao productDao;

    private ProductRepository(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Get the Instance of Product Repository
     *
     * @param productDao Product Dao
     * @return Product Repository instance
     */
    public static ProductRepository getInstance(ProductDao productDao) {
        if (instance == null) {
            instance = new ProductRepository(productDao);
        }
        return instance;
    }

    /**
     * Insert or Update a Product
     *
     * @param product Product data
     */
    public void insertOrUpdate(final Product product) {
        productDao.insertOrUpdate(product);
    }

    /**
     * Get a Product by Id
     *
     * @param id Product's id
     * @return Product data
     */
    public Single<Product> getById(long id) {
        return productDao.getById(id);
    }

    /**
     * Get all Products
     *
     * @return List of Products
     */
    public LiveData<List<Product>> getAll() {
        return productDao.getAll();
    }

    /**
     * Get all Products to be process in JobService
     *
     * @return List of Products
     */
    public List<Product> getProductJob() {
        return productDao.getProductJob();
    }

    /**
     * Get all Products by Supplier
     *
     * @return List of Products
     */
    public Flowable<List<Product>> getBySupplier(long supplierId) {
        return productDao.getBySupplier(supplierId);
    }

    /**
     * Delete a product
     *
     * @param products Products to delete
     */
    public void delete(List<Product> products) {
        productDao.delete(products);
    }

    /**
     * Get a Product by name
     *
     * @param name Product's name
     * @return Product data
     */
    public List<Product> getByName(String name) {
        return productDao.getByName(name);
    }

    /**
     * Get a Product by Id
     *
     * @param id Product's id
     * @return Product data
     */
    public Product getByIdSync(long id) {
        return productDao.getByIdSync(id);
    }

}
