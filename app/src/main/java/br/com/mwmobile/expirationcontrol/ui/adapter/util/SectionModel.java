package br.com.mwmobile.expirationcontrol.ui.adapter.util;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.model.Product;

/**
 * Section model
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */
public class SectionModel {

    private String sectionLabel;
    private List<Product> productList;

    /**
     * Constructor
     *
     * @param sectionLabel Section Label
     * @param productList  List of products
     */
    public SectionModel(String sectionLabel, List<Product> productList) {
        this.sectionLabel = sectionLabel;
        this.productList = productList;
    }

    /**
     * Return the Section label
     *
     * @return Section label
     */
    public String getSectionLabel() {
        return sectionLabel;
    }

    /**
     * Return the Product list
     *
     * @return Product list
     */
    public List<Product> getProductList() {
        return productList;
    }
}