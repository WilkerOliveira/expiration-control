package br.com.mwmobile.expirationcontrol.ui.fragment;


import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;
import br.com.mwmobile.expirationcontrol.ui.activities.MainActivity;
import br.com.mwmobile.expirationcontrol.ui.adapter.MainListSectionAdapter;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.RecyclerViewType;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.SectionModel;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListMainViewModel;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;

/**
 * Main List Fragment
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class MainListFragment extends Fragment implements LifecycleRegistryOwner {

    //life cycle registry
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    ArrayList<SectionModel> sectionModelArrayList;
    private ListMainViewModel viewModel;
    private View view;
    private RecyclerView recyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @return A new instance of fragment MainListFragment.
     */
    public static MainListFragment newInstance() {
        return new MainListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_main_list, container, false);

        return this.view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadViewModel();

        loadProducts(null, 0, null, true);
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getActivity().getApplication();
        this.viewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListMainViewModel.class);
    }

    /**
     * Load the RecyclerView with products
     *
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier Id
     * @param barCode          Product barcode
     * @param updateTotal      Update or not the Total values
     */
    public void loadProducts(@Nullable List<ExpirationStatus> expirationStatus, long supplierId, String barCode, boolean updateTotal) {

        this.recyclerView = this.view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        String expirationDays = PreferencesManager.getExpirationDays(getActivity());

        this.view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        this.viewModel.getSupplierProduct(Integer.parseInt(expirationDays), expirationStatus, supplierId, barCode).observe(this, list -> {

            buildSections(list, updateTotal);

            if (list == null || list.isEmpty()) {
                this.view.findViewById(R.id.txtNoRegisters).setVisibility(View.VISIBLE);
                this.view.findViewById(R.id.imvEmptyList).setVisibility(View.VISIBLE);
            } else {
                boolean existProducts = this.sectionModelArrayList != null && !this.sectionModelArrayList.isEmpty();

                if (!existProducts) {
                    this.view.findViewById(R.id.txtNoRegisters).setVisibility(View.VISIBLE);
                    this.view.findViewById(R.id.imvEmptyList).setVisibility(View.VISIBLE);
                } else {
                    this.view.findViewById(R.id.txtNoRegisters).setVisibility(View.GONE);
                    this.view.findViewById(R.id.imvEmptyList).setVisibility(View.GONE);
                }
            }
            this.view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        });
    }

    /**
     * Create the Sections
     *
     * @param list        List of Suppliers and Products
     * @param updateTotal Update or not the Total values
     */
    private void buildSections(List<SupplierProduct> list, boolean updateTotal) {
        int totalWarning = 0;
        int totalExpired = 0;
        int totalValid = 0;

        this.sectionModelArrayList = new ArrayList<>();

        for (SupplierProduct supplierProduct : list) {

            if (supplierProduct.getProducts() != null && supplierProduct.getProducts().size() > 0) {

                this.sectionModelArrayList.add(new SectionModel(supplierProduct.supplier.getName(), supplierProduct.getProducts()));

                totalWarning += supplierProduct.getTotalWarning();
                totalExpired += supplierProduct.getTotalExpired();
                totalValid += supplierProduct.getTotalValid();
            }
        }

        MainListSectionAdapter sectionRecyclerViewAdapter = new MainListSectionAdapter(getActivity(),
                RecyclerViewType.LINEAR_VERTICAL, this.sectionModelArrayList, (MainActivity) getActivity());
        recyclerView.setAdapter(sectionRecyclerViewAdapter);

        if (updateTotal)
            setTotalBar(totalWarning, totalExpired, totalValid);
    }

    /**
     * Set the Max progress
     *
     * @param totalWarning Total Upcoming expiration
     * @param totalExpired Total Expired product
     * @param totalValid   Total valid product
     */
    private void setTotalBar(int totalWarning, int totalExpired, int totalValid) {
        int total = totalWarning + totalExpired + totalValid;

        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_expired)).setMax(total);
        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_expired)).setProgress(totalExpired);
        getActivity().findViewById(R.id.arc_progress_expired).setOnClickListener(view ->
                loadProducts(Collections.singletonList(ExpirationStatus.EXPIRED), 0, null, false));


        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_warning)).setMax(total);
        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_warning)).setProgress(totalWarning);
        getActivity().findViewById(R.id.arc_progress_warning).setOnClickListener(view ->
                loadProducts(Collections.singletonList(ExpirationStatus.WARNING), 0, null, false));

        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_normal)).setMax(total);
        ((ArcProgress) getActivity().findViewById(R.id.arc_progress_normal)).setProgress(totalValid);
        getActivity().findViewById(R.id.arc_progress_normal).setOnClickListener(view ->
                loadProducts(Collections.singletonList(ExpirationStatus.VALID_PERIOD), 0, null, false));
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

}
