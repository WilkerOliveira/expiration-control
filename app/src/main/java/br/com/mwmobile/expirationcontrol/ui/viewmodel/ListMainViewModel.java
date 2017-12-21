package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.ListMainComponent;
import br.com.mwmobile.expirationcontrol.repository.SupplierProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;
import br.com.mwmobile.expirationcontrol.util.Constants;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import io.reactivex.annotations.NonNull;

/**
 * List Main ViewModel
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
public class ListMainViewModel extends ViewModel implements ListMainComponent.Injectable {

    ///***
    /// DI
    @Inject
    public SupplierProductRepository supplierProductRepository;
    //***

    /**
     * Filter the Product List
     *
     * @param supplierProducts List of Supplier with Product list
     * @param expirationDays   Expiration Days
     * @param expirationStatus Expiration Status to filter
     * @param barCode          BarCode
     * @return List of Supplier with Products filtered
     */
    static List<SupplierProduct> doProductFilter(@NonNull List<SupplierProduct> supplierProducts, int expirationDays, List<ExpirationStatus> expirationStatus, String barCode) {

        int totalWarning;
        int totalExpired;
        int totalValid;

        for (SupplierProduct supplierProduct : supplierProducts) {

            totalWarning = 0;
            totalExpired = 0;
            totalValid = 0;

            List<Product> processedProducts = new ArrayList<>();

            Collections.sort(supplierProduct.getProducts(), (a, b) -> a.getExpiration().compareTo(b.getExpiration()));

            for (Product product : supplierProduct.getProducts()) {

                if (product.getExpiration() != null) {

                    if (!TextUtils.isEmpty(barCode) && !barCode.equals(product.getBarCode()))
                        continue;

                    DateUtil.setExpirationStatus(expirationDays, product);

                    if (expirationStatus != null && expirationStatus.size() > 0 && expirationStatus.size() != Constants.TOTAL_EXPIRATION_STATUS) {

                        if (expirationStatus.contains(ExpirationStatus.WARNING) && product.getStatus() == ExpirationStatus.WARNING) {
                            processedProducts.add(product);
                        }
                        if (expirationStatus.contains(ExpirationStatus.EXPIRATED) && product.getStatus() == ExpirationStatus.EXPIRATED) {
                            processedProducts.add(product);
                        }
                        if (expirationStatus.contains(ExpirationStatus.VALID_PERIOD) && product.getStatus() == ExpirationStatus.VALID_PERIOD) {
                            //VALID_PERIOD
                            processedProducts.add(product);
                        }

                    } else {
                        processedProducts.add(product);
                    }

                    if (product.getStatus() == ExpirationStatus.WARNING) {
                        totalWarning++;
                    } else if (product.getStatus() == ExpirationStatus.EXPIRATED) {
                        totalExpired++;
                    } else if (product.getStatus() == ExpirationStatus.VALID_PERIOD) {
                        totalValid++;
                    }
                }
            }

            supplierProduct.setTotalExpired(totalExpired);
            supplierProduct.setTotalWarning(totalWarning);
            supplierProduct.setTotalValid(totalValid);

            supplierProduct.setProducts(processedProducts);
        }

        return supplierProducts;
    }

    /**
     * Return a list of Supplier with Products
     *
     * @param expirationDays   Expirations days to validate
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier Id
     * @param barCode          Product barcode
     * @return List of Supplier with Products
     */
    public LiveData<List<SupplierProduct>> getSupplierProduct(int expirationDays, List<ExpirationStatus> expirationStatus, long supplierId, String barCode) {
        return consultSupplierProducts(expirationDays, expirationStatus, supplierId, barCode);
    }

    /**
     * Consult the Supplier Products
     *
     * @param expirationDays   Expiration Days
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier ID
     * @param barCode          Product BarCode
     * @return List of Supplier Product
     */
    private LiveData<List<SupplierProduct>> consultSupplierProducts(int expirationDays, List<ExpirationStatus> expirationStatus, long supplierId, String barCode) {

        if (!TextUtils.isEmpty(barCode))
            return Transformations.map(supplierProductRepository.getAll(), supplierProducts -> doProductFilter(supplierProducts, expirationDays, expirationStatus, barCode));

        if (supplierId > 0) {
            return Transformations.map(supplierProductRepository.getBySupplierId(supplierId), supplierProducts -> doProductFilter(supplierProducts, expirationDays, expirationStatus, barCode));
        } else {

            return Transformations.map(supplierProductRepository.getAll(), supplierProducts -> doProductFilter(supplierProducts, expirationDays, expirationStatus, barCode));
        }
    }

    @Override
    public void inject(ListMainComponent listMainComponent) {
        listMainComponent.inject(this);
    }

}