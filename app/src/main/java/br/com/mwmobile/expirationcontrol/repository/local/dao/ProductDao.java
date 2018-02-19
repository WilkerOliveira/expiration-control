package br.com.mwmobile.expirationcontrol.repository.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.converter.BigDecimalConverter;
import br.com.mwmobile.expirationcontrol.repository.converter.DateConverter;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Product DAO
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
@Dao
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public interface ProductDao {

    @Query("select * from Product order by supplierId")
    LiveData<List<Product>> getAll();

    @Query("select * from Product order by supplierId")
    Cursor getAllCursor();

    @Query("select * from Product order by supplierId")
    List<Product> getProductJob();

    @Query("select * from Product where supplierId = :supplierId")
    Flowable<List<Product>> getBySupplier(Long supplierId);

    @Query("select * from Product where name = :name")
    List<Product> getByName(String name);

    @Query("select * from Product where id = :id")
    Single<Product> getById(long id);

    @Query("select * from Product where id = :id")
    Product getByIdSync(long id);

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(List<Product> products);

    @Delete
    void delete(Product product);

}
