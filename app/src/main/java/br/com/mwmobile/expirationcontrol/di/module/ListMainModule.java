package br.com.mwmobile.expirationcontrol.di.module;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.repository.SupplierProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierProductDao;
import dagger.Module;
import dagger.Provides;

/**
 * List Main Module
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */
@Module
public class ListMainModule {

    private ExpirationControlApplication application;

    public ListMainModule(ExpirationControlApplication mApplication) {
        application = mApplication;
    }

    @Singleton
    @Provides
    SupplierProductDao providesSupplierProductDao() {
        return AppDatabase.getDatabase(application).itemSupplierProductDAO();
    }

    @Singleton
    @Provides
    SupplierProductRepository providSupplierProductRepository(SupplierProductDao supplierProductDao) {
        return SupplierProductRepository.getInstance(supplierProductDao);
    }
}
