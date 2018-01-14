package br.com.mwmobile.expirationcontrol.repository.local.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.math.BigDecimal;
import java.util.Date;

import br.com.mwmobile.expirationcontrol.repository.converter.BigDecimalConverter;
import br.com.mwmobile.expirationcontrol.repository.converter.DateConverter;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Product Model
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 28/09/2017
 */

@Entity(foreignKeys = @ForeignKey(entity = Supplier.class, parentColumns = "id", childColumns = "supplierId", onDelete = CASCADE))
public class Product {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    @TypeConverters(DateConverter.class)
    private Date expiration;

    private String barCode;

    private long supplierId;

    @TypeConverters(BigDecimalConverter.class)
    private BigDecimal quantity;

    @TypeConverters(BigDecimalConverter.class)
    private BigDecimal value;

    @Ignore
    private ExpirationStatus status;

    /**
     * Default Constructor
     */
    @Ignore
    public Product() {
    }

    /**
     * Constructor
     *
     * @param name       Product name
     * @param expiration Product expiration
     * @param barCode    Product barcode
     * @param quantity   Product quantity
     * @param supplierId Supplier ID
     */
    @Ignore
    public Product(String name, Date expiration, String barCode,  BigDecimal quantity, long supplierId) {
        this.name = name;
        this.expiration = expiration;
        this.barCode = barCode;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }


    /**
     * Constructor
     *
     * @param name       Product name
     * @param expiration Product expiration
     * @param barCode    Product barcode
     * @param quantity   Product quantity
     * @param supplierId Supplier ID
     * @param value      Product Value
     */
    public Product(String name, Date expiration, String barCode, BigDecimal quantity, long supplierId, BigDecimal value) {
        this.name = name;
        this.expiration = expiration;
        this.barCode = barCode;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.value = value;
    }

    /**
     * Return the Product ID
     *
     * @return Product ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set the Product ID
     *
     * @param id Product ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Return Product Name
     *
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Product Name
     *
     * @param name Product Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return Product Expiration
     *
     * @return Product Expiration
     */
    public Date getExpiration() {
        return expiration;
    }

    /**
     * Set the Product Expiration
     *
     * @param expiration Product Expiration
     */
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    /**
     * Return the Supplier ID
     *
     * @return Supplier ID
     */
    public long getSupplierId() {
        return supplierId;
    }

    /**
     * Set the Supplier ID
     *
     * @param supplierId Supplier ID
     */
    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Return the Product Quantity
     *
     * @return Product Quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Set the Product Quantity
     *
     * @param quantity Product Quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Return the Status of Expiration
     *
     * @return Status of Expiration
     */
    public ExpirationStatus getStatus() {
        return status;
    }

    /**
     * Set the Status of Expiration
     *
     * @param status Status of Expiration
     */
    public void setStatus(ExpirationStatus status) {
        this.status = status;
    }

    /**
     * Return the BarCode
     *
     * @return BarCode
     */
    public String getBarCode() {
        return barCode;
    }

    /**
     * Set the BarCode
     *
     * @param barCode BarCode
     */
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    /**
     * Return the Product Value
     * @return Product Value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Set the Product Value
     * @param value Product Value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
