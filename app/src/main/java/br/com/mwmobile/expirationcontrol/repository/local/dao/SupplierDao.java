package br.com.mwmobile.expirationcontrol.repository.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Supplier DAO
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
@Dao
public interface SupplierDao {

    @Query("select * from supplier")
    LiveData<List<Supplier>> getAll();

    @Query("select * from supplier where id = :id")
    Single<Supplier> getById(long id);

    @Query("select * from supplier where id = :id")
    Supplier getSupplier(long id);

    @Query("select * from supplier where name = :name")
    List<Supplier> getByName(String name);

    @Insert(onConflict = REPLACE)
    long insertOrUpdate(Supplier supplier);

    @Delete
    void delete(List<Supplier> suppliers);
}
