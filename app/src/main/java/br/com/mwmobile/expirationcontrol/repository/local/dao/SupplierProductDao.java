package br.com.mwmobile.expirationcontrol.repository.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;

/**
 * Supplier Product DAO
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */
@Dao
public interface SupplierProductDao {

    @Query("SELECT s.* FROM supplier s " +
            "INNER JOIN product p ON p.supplierId = s.id " +
            "GROUP BY s.id " +
            "ORDER BY s.id ASC")
    LiveData<List<SupplierProduct>> getAll();

    @Query("SELECT * FROM supplier WHERE id = :id ORDER BY id")
    LiveData<List<SupplierProduct>> getBySupplierId(long id);

    @Query("SELECT s.* FROM supplier s " +
            "INNER JOIN product p ON p.supplierId = s.id " +
            "WHERE (:id = 0 OR s.id = :id) " +
            "GROUP BY s.id " +
            "ORDER BY p.expiration ASC")
    LiveData<List<SupplierProduct>> getBySupplierIdAndProductName(long id);

}
