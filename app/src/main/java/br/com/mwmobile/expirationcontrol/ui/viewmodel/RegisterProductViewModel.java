package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.ProductComponent;
import br.com.mwmobile.expirationcontrol.repository.ProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.internal.operators.completable.CompletableFromAction;

/**
 * View Model for {@link br.com.mwmobile.expirationcontrol.ui.activities.RegisterProductActivity}
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class RegisterProductViewModel extends ViewModel implements ProductComponent.Injectable {

    ///***
    /// DI
    @SuppressWarnings("WeakerAccess")
    @Inject
    public ProductRepository productRepository;
    //***

    /**
     * Insert or Update the Product
     *
     * @param product Product Data
     * @return Completable
     */
    public Completable insertOrUpdate(final Product product) {
        return new CompletableFromAction(() -> {

            //validation of start and end date
            DateUtil.validateDate(product.getExpiration());

            productRepository.insertOrUpdate(product);
        });
    }

    /**
     * Return the Product by ID
     *
     * @param id Product ID
     * @return Product data
     */
    public Single<Product> getById(long id) {
        //Add bussines logic here

        return productRepository.getById(id);
    }

    @Override
    public void inject(ProductComponent productComponent) {
        productComponent.inject(this);
    }


}