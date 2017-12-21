package br.com.mwmobile.expirationcontrol.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.component.ListMainComponent;
import br.com.mwmobile.expirationcontrol.di.component.ProductComponent;
import br.com.mwmobile.expirationcontrol.di.component.SupplierComponent;

/**
 * View Model Factory to load ViewModel in Activities or Fragments
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 17/11/2017
 */

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private ExpirationControlApplication application;

    public ViewModelFactory(ExpirationControlApplication application) {
        this.application = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T t = super.create(modelClass);
        if (t instanceof SupplierComponent.Injectable) {
            ((SupplierComponent.Injectable) t).inject(application.getSupplierComponent());
        } else if (t instanceof ProductComponent.Injectable) {
            ((ProductComponent.Injectable) t).inject(application.getProductComponent());
        } else if (t instanceof ListMainComponent.Injectable) {
            ((ListMainComponent.Injectable) t).inject(application.getListMainComponent());
        }

        return t;
    }
}