package br.com.mwmobile.expirationcontrol.repository.local.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Supplier Model
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
@Entity
public class Supplier {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    /**
     * Default Constructor
     */
    @Ignore
    public Supplier() {
    }

    /**
     * Constructor
     *
     * @param name Supplier name
     */
    public Supplier(String name) {
        this.name = name;
    }

    /**
     * Constructor
     *
     * @param id   Supplier ID
     * @param name Supplier Name
     */
    @Ignore
    public Supplier(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Return the Supplier ID
     *
     * @return Supplier ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set the Supplier ID
     *
     * @param id Supplier ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Return the Supplier Name
     *
     * @return Supplier Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Supplier Name
     *
     * @param name Supplier Name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
