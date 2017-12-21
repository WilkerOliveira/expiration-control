package br.com.mwmobile.expirationcontrol.di.component;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.di.module.ListMainModule;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListMainViewModel;
import dagger.Component;

/**
 * List Main Component
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */

@Singleton
@Component(modules = {ListMainModule.class})
public interface ListMainComponent {

    void inject(ListMainViewModel viewModel);

    interface Injectable {
        void inject(ListMainComponent productComponent);
    }
}
