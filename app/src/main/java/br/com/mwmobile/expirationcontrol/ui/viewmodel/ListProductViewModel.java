package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.ProductComponent;
import br.com.mwmobile.expirationcontrol.repository.ProductRepository;
import br.com.mwmobile.expirationcontrol.repository.SupplierProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;
import br.com.mwmobile.expirationcontrol.ui.model.SummaryVO;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.operators.completable.CompletableFromAction;
import io.reactivex.schedulers.Schedulers;

/**
 * List Product ViewModel
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class ListProductViewModel extends ViewModel implements ProductComponent.Injectable {

    ///***
    /// DI
    @Inject
    public ProductRepository productRepository;
    @Inject
    public SupplierProductRepository supplierProductRepository;
    private LiveData<List<Product>> products;
    //***

    /**
     * Get All Products
     *
     * @return List of Products
     */
    public LiveData<List<Product>> getAll() {
        if (products == null) products = productRepository.getAll();

        return products;
    }

    /**
     * Return a list of Supplier with Products
     *
     * @param expirationDays   Expirations Days to validate the Status
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier id
     * @param productName      Product name
     * @param barCode          Product Barcode
     * @return List of Supplier with Products
     */
    public LiveData<List<SupplierProduct>> getSupplierProduct(int expirationDays, List<ExpirationStatus> expirationStatus, long supplierId, String productName, String barCode) {
        if (!TextUtils.isEmpty(barCode))
            return Transformations.map(supplierProductRepository.getAll(), supplierProducts -> ListMainViewModel.doProductFilter(supplierProducts, expirationDays, expirationStatus, barCode));

        return Transformations.map(supplierProductRepository.getBySupplierIdAndProductName(supplierId, "%" + productName + "%"), supplierProducts -> ListMainViewModel.doProductFilter(supplierProducts, expirationDays, expirationStatus, barCode));
    }

    /**
     * Return the Products by Supplier
     *
     * @param supplierId Supplier id
     * @return List of Products
     */
    public LiveData<List<Product>> getBySupplier(long supplierId) {

        return LiveDataReactiveStreams.fromPublisher(productRepository.getBySupplier(supplierId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));
    }

    /**
     * Delete a Product
     *
     * @param products Product list
     *                 return Completable
     */
    public Completable deleteItem(List<Product> products) {

        return new CompletableFromAction(() -> productRepository.delete(products));
    }

    @Override
    public void inject(ProductComponent cityComponent) {
        cityComponent.inject(this);
    }

    /**
     * Calculate the Summary
     *
     * @param products       Product list
     * @param expirationDays Expiration Days
     * @return Summary
     */
    public SummaryVO calculateSummary(List<Product> products, int expirationDays) {
        return SummaryViewModel.doSummary(products, expirationDays);
    }
}