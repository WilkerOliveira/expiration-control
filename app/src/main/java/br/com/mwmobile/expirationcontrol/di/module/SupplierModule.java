package br.com.mwmobile.expirationcontrol.di.module;

import javax.inject.Singleton;

import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.repository.SupplierRepository;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.dao.SupplierDao;
import dagger.Module;
import dagger.Provides;

/**
 * Supplier Module
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 19/11/2017
 */

@Module
public class SupplierModule {

    private ExpirationControlApplication application;

    public SupplierModule(ExpirationControlApplication mApplication) {
        application = mApplication;
    }

    @Singleton
    @Provides
    SupplierDao providesSupplierDao() {
        return AppDatabase.getDatabase(application).itemSupplierDAO();
    }

    @Singleton
    @Provides
    SupplierRepository provideSupplierRepository(SupplierDao tripDao) {
        return SupplierRepository.getInstance(tripDao);
    }


}
