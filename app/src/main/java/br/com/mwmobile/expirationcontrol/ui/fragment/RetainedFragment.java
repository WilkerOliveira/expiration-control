package br.com.mwmobile.expirationcontrol.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;

/**
 * Retained Fragment
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 07/02/2018
 */

public class RetainedFragment extends Fragment {

    // data object we want to retain
    private List<SupplierProduct> supplierProductList;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public List<SupplierProduct> getSupplierProductList() {
        return supplierProductList;
    }

    public void setSupplierProductList(List<SupplierProduct> supplierProductList) {
        this.supplierProductList = supplierProductList;
    }
}