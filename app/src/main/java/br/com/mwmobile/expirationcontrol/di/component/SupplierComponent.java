package br.com.mwmobile.expirationcontrol.di.component;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.di.module.SupplierModule;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListSupplierViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.RegisterSupplierViewModel;
import dagger.Component;

/**
 * Supplier Component
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 17/11/2017
 */

@Singleton
@Component(modules = {SupplierModule.class})
public interface SupplierComponent {

    void inject(RegisterSupplierViewModel viewModel);

    void inject(ListSupplierViewModel viewModel);

    interface Injectable {
        void inject(SupplierComponent supplierComponent);
    }
}
