package br.com.mwmobile.expirationcontrol.repository.local.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Supplier Product Model
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */
public class SupplierProduct {

    @Embedded
    public Supplier supplier;

    @Relation(parentColumn = "id",
            entityColumn = "supplierId")

    private List<Product> products;
    @Ignore
    private int totalExpired;
    @Ignore
    private int totalWarning;
    @Ignore
    private int totalValid;

    /**
     * Return the Product list
     *
     * @return Product list
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Set the Product list
     *
     * @param products Product list
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Return the Total of expired product
     * @return Total
     */
    public int getTotalExpired() {
        return totalExpired;
    }

    /**
     * Set the Total of expired product
     * @param totalExpired Total
     */
    public void setTotalExpired(int totalExpired) {
        this.totalExpired = totalExpired;
    }

    /**
     * Return the Total of upcoming expiration product
     * @return Total
     */
    public int getTotalWarning() {
        return totalWarning;
    }

    /**
     * Set the Total of upcoming expiration product
     * @param totalWarning Total
     */
    public void setTotalWarning(int totalWarning) {
        this.totalWarning = totalWarning;
    }

    /**
     * Return the Total of Valid expiration
     * @return Total
     */
    public int getTotalValid() {
        return totalValid;
    }

    /**
     * Set the Total of Valid expiration
     * @param totalValid Total
     */
    public void setTotalValid(int totalValid) {
        this.totalValid = totalValid;
    }
}
