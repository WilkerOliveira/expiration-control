package br.com.mwmobile.expirationcontrol.di.module;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.repository.ProductRepository;
import br.com.mwmobile.expirationcontrol.repository.SupplierProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.dao.ProductDao;
import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierProductDao;
import dagger.Module;
import dagger.Provides;

/**
 * Product Module
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */
@Module
public class ProductModule {

    private ExpirationControlApplication application;

    public ProductModule(ExpirationControlApplication mApplication) {
        application = mApplication;
    }

    @Singleton
    @Provides
    ProductDao providesProductDao() {
        return AppDatabase.getDatabase(application).itemProductDAO();
    }

    @Singleton
    @Provides
    SupplierProductDao providesSupplierProductDao() {
        return AppDatabase.getDatabase(application).itemSupplierProductDAO();
    }

    @Singleton
    @Provides
    ProductRepository providProductRepository(ProductDao productDao) {
        return ProductRepository.getInstance(productDao);
    }

    @Singleton
    @Provides
    SupplierProductRepository providSupplierProductRepository(SupplierProductDao supplierProductDao) {
        return SupplierProductRepository.getInstance(supplierProductDao);
    }
}
