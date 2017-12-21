package br.com.mwmobile.expirationcontrol.application;

import android.app.Application;

import br.com.mwmobile.expirationcontrol.di.component.DaggerListMainComponent;
import br.com.mwmobile.expirationcontrol.di.component.DaggerProductComponent;
import br.com.mwmobile.expirationcontrol.di.component.DaggerSupplierComponent;
import br.com.mwmobile.expirationcontrol.di.component.ListMainComponent;
import br.com.mwmobile.expirationcontrol.di.component.ProductComponent;
import br.com.mwmobile.expirationcontrol.di.component.SupplierComponent;
import br.com.mwmobile.expirationcontrol.di.module.ListMainModule;
import br.com.mwmobile.expirationcontrol.di.module.ProductModule;
import br.com.mwmobile.expirationcontrol.di.module.SupplierModule;

/**
 * Custom Expiration Control Application
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 17/11/2017
 */

public class ExpirationControlApplication extends Application {

    private SupplierComponent supplierComponent;
    private ProductComponent productComponent;
    private ListMainComponent listMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        supplierComponent = DaggerSupplierComponent.builder().supplierModule(new SupplierModule(this)).build();
        productComponent = DaggerProductComponent.builder().productModule(new ProductModule(this)).build();
        listMainComponent = DaggerListMainComponent.builder().listMainModule(new ListMainModule(this)).build();
    }

    /**
     * Return the Supplier Component DI
     *
     * @return Supplier Component
     */
    public SupplierComponent getSupplierComponent() {
        return supplierComponent;
    }

    /**
     * Return the Product Component DI
     *
     * @return Product Component
     */
    public ProductComponent getProductComponent() {
        return productComponent;
    }

    /**
     * Return the List Main Component DI
     *
     * @return List Main Component
     */
    public ListMainComponent getListMainComponent() {
        return listMainComponent;
    }
}