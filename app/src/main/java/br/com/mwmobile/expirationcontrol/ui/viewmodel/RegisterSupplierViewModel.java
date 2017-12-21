package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.SupplierComponent;
import br.com.mwmobile.expirationcontrol.exception.CustomError;
import br.com.mwmobile.expirationcontrol.repository.SupplierRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.util.ErrorType;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.internal.operators.completable.CompletableFromAction;

/**
 * View Model for {@link br.com.mwmobile.expirationcontrol.ui.activities.RegisterSupplierActivity}
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
public class RegisterSupplierViewModel extends ViewModel implements SupplierComponent.Injectable {

    ///***
    /// DI
    @Inject
    public SupplierRepository supplierRepository;
   //***

    /**
     * Insert or Update a Supplier
     *
     * @param newSupplier Supplier data
     */
    public Completable insertOrUpdate(final Supplier newSupplier) {

        return new CompletableFromAction(() -> {

            String name = newSupplier.getName();

            //check if name already exist
            List<Supplier> oldSupplier = supplierRepository.getByName(name);
            if (oldSupplier != null && oldSupplier.size() > 0 && oldSupplier.get(0).getId() != newSupplier.getId())
                throw new CustomError(ErrorType.SUPPLIER_NAME_EXIST.getStringErrorType());

            newSupplier.setId(supplierRepository.insertOrUpdate(newSupplier));
        });
    }

    /**
     * Get a Supplier by Id
     *
     * @param id Supplier's id
     * @return Supplier data
     */
    public Single<Supplier> getById(long id) {

        //Add bussines logic here

        return supplierRepository.getById(id);
    }

    @Override
    public void inject(SupplierComponent supplierComponent) {
        supplierComponent.inject(this);
    }
}