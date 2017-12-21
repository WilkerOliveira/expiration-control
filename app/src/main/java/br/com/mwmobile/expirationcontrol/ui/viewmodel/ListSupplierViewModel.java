package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.SupplierComponent;
import br.com.mwmobile.expirationcontrol.repository.SupplierRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import io.reactivex.Completable;
import io.reactivex.internal.operators.completable.CompletableFromAction;

/**
 * List Supplier ViewModel
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class ListSupplierViewModel extends ViewModel implements SupplierComponent.Injectable {

    ///***
    /// DI
    @Inject
    public SupplierRepository supplierRepository;
    //***

    private LiveData<List<Supplier>> supplierList;

    /**
     * Get all list of trip
     *
     * @return List of Supplier
     */
    public LiveData<List<Supplier>> getAll() {
        if (supplierList == null)
            supplierList = supplierRepository.getAll();

        return supplierList;
    }

    /**
     * Delete a list of Supplier
     *
     * @param suppliers Supplier list
     */
    public Completable delete(List<Supplier> suppliers) {

        return new CompletableFromAction(() -> supplierRepository.delete(suppliers));
    }

    @Override
    public void inject(SupplierComponent tripComponent) {
        tripComponent.inject(this);
    }

}