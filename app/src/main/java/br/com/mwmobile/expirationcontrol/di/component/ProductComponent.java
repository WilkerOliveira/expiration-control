package br.com.mwmobile.expirationcontrol.di.component;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.di.module.ProductModule;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListProductViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.RegisterProductViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.SummaryViewModel;
import dagger.Component;

/**
 * Product Component
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */

@Singleton
@Component(modules = {ProductModule.class})
public interface ProductComponent {

    void inject(RegisterProductViewModel viewModel);

    void inject(ListProductViewModel viewModel);

    void inject(SummaryViewModel viewModel);

    interface Injectable {
        void inject(ProductComponent productComponent);
    }
}
