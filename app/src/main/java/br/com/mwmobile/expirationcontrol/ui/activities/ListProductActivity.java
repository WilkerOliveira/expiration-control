package br.com.mwmobile.expirationcontrol.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.repository.local.model.SupplierProduct;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.adapter.ProductSectionAdapter;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.RecyclerViewType;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.SectionModel;
import br.com.mwmobile.expirationcontrol.ui.dialog.AlertDialog;
import br.com.mwmobile.expirationcontrol.ui.dialog.DialogType;
import br.com.mwmobile.expirationcontrol.ui.fragment.RetainedFragment;
import br.com.mwmobile.expirationcontrol.ui.model.SummaryVO;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListProductViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListSupplierViewModel;
import br.com.mwmobile.expirationcontrol.util.BarcodeScanner;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Product List Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class ListProductActivity extends LifecycleAppCompatActivity implements OnProductListener {

    ListProductViewModel viewModel;
    RecyclerView recyclerView;
    ArrayList<SectionModel> sectionModelArrayList;
    private ListSupplierViewModel listSupplierViewModel;
    private List<Product> listToRemove;
    private List<Supplier> supplierList;
    private int supplierIndex;
    private RetainedFragment dataFragment;
    private List<SupplierProduct> supplierProducts;
    private boolean barcodeSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadViewModel();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        listToRemove = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            supplierProducts = null;
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(dataFragment).commit();
            startActivity(new Intent(ListProductActivity.this, RegisterProductActivity.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("supplierProductList");

        // create the fragment and data the first time
        if (!barcodeSearching && (dataFragment == null || (dataFragment.getSupplierProductList() == null || dataFragment.getSupplierProductList().isEmpty()))) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "supplierProductList").commit();
            // load the data from the web
            dataFragment.setSupplierProductList(supplierProducts);

            loadProducts(null, 0, "", null);
        } else if(!barcodeSearching) {
            loadProducts(dataFragment.getSupplierProductList());
        }

        this.barcodeSearching = false;
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.viewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListProductViewModel.class);
        this.listSupplierViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListSupplierViewModel.class);
    }

    /**
     * Open the filter of products
     */
    private void openProductFilter() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // Get the layout inflater
        View popupView = getLayoutInflater().inflate(R.layout.popup_products_filter, null);
        loadSuppliers(popupView.findViewById(R.id.spnSupplier));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(popupView)
                // Add action buttons
                .setPositiveButton(R.string.filter, (dialog, id) -> doFilter(popupView))
                .setNegativeButton(R.string.cancel_button, (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    /**
     * Load the Supplier's spinner
     */
    private void loadSuppliers(MaterialSpinner spinnerSupplier) {
        this.listSupplierViewModel.getAll().observe(this, list -> {

            this.supplierList = list;

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSupplierList(this.supplierList, true));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerSupplier.setAdapter(arrayAdapter);

            if (list == null || list.isEmpty()) {
                showWarningMessage(R.string.suppliers_not_found);
            } else {
                this.supplierIndex = 0;
            }
        });

        spinnerSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                supplierIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Execute the Filter
     *
     * @param popupView Layout of popup filter
     */
    private void doFilter(View popupView) {

        List<ExpirationStatus> expirationStatus = new ArrayList<>();

        if (((CheckBox) popupView.findViewById(R.id.chkToExpire)).isChecked()) {
            expirationStatus.add(ExpirationStatus.VALID_PERIOD);
        }
        if (((CheckBox) popupView.findViewById(R.id.chkNextExpirations)).isChecked()) {
            expirationStatus.add(ExpirationStatus.WARNING);
        }
        if (((CheckBox) popupView.findViewById(R.id.chkExpired)).isChecked()) {
            expirationStatus.add(ExpirationStatus.EXPIRED);
        }

        long supplierId = 0;
        if (supplierIndex > 0) {
            supplierId = this.supplierList.get(this.supplierIndex - 1).getId();
        }

        String productName = ((EditText) popupView.findViewById(R.id.inputProduct)).getText().toString();

        loadProducts(expirationStatus, supplierId, productName, null);
    }

    /**
     * Load RecyclerView products
     *
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier ID
     * @param productName      Product Name
     * @param barCode          Product barcode
     */
    private void loadProducts(@Nullable List<ExpirationStatus> expirationStatus, long supplierId, String productName, String barCode) {

        this.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        //I had to do this because LiveData was "bugging" my search after deleted a register
        Observable<List<SupplierProduct>> resultObservable = Observable.fromCallable(() -> viewModel.getSupplierProduct(
                Integer.parseInt(PreferencesManager.getExpirationDays(ListProductActivity.this)),
                expirationStatus, supplierId, productName, barCode));

        resultObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::loadProducts);
    }

    /**
     * Load the Products
     *
     * @param supplierProducts List to load
     */
    private void loadProducts(List<SupplierProduct> supplierProducts) {

        this.supplierProducts = supplierProducts;

        if (!buildSections(supplierProducts)) {
            showWarningMessage(R.string.registers_not_found);
            ImageView imvEmptyList = findViewById(R.id.imvEmptyList);
            imvEmptyList.setVisibility(View.VISIBLE);
            imvEmptyList.setImageResource(R.drawable.ic_product);
        } else {
            findViewById(R.id.imvEmptyList).setVisibility(View.GONE);
        }

        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    /**
     * Create the Sections
     *
     * @param list List of Suppliers and Products
     * @return True or False
     */
    private boolean buildSections(List<SupplierProduct> list) {

        this.sectionModelArrayList = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        for (SupplierProduct supplierProduct : list) {

            if (supplierProduct.getProducts() != null && supplierProduct.getProducts().size() > 0) {
                products.addAll(supplierProduct.getProducts());
                this.sectionModelArrayList.add(new SectionModel(supplierProduct.supplier.getName(), supplierProduct.getProducts()));
            }
        }

        ProductSectionAdapter sectionRecyclerViewAdapter = new ProductSectionAdapter(this, RecyclerViewType.LINEAR_VERTICAL, this.sectionModelArrayList, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionRecyclerViewAdapter);

        SummaryVO summaryVO = viewModel.calculateSummary(products, Integer.parseInt(PreferencesManager.getExpirationDays(this)));
        if (summaryVO != null) {
            ((TextView) findViewById(R.id.lblSummaryProducts)).setText(String.format("%s", summaryVO.getTotalProducts()));
            ((TextView) findViewById(R.id.lblSummaryTotal)).setText(NumberUtil.currencyToString(summaryVO.getTotalAmount()));
        }

        return !products.isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (this.listToRemove != null && this.listToRemove.size() > 0)
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        else
            getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.delete_menu:
                delete();
                return true;
            case R.id.barcode_menu:
                startScan();
                return true;
            case R.id.filter_menu:
                openProductFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Perform the action of delete
     */
    private void delete() {
        //confirm if the user really wants delete the product
        final AlertDialog alert = new AlertDialog();

        alert.setAlertType(DialogType.YES_NO);
        alert.setMessage(getString(R.string.msg_confirm_delete_product));

        alert.setFirstButtonEvent((dialogInterface, i) -> mDisposable.add(viewModel.deleteItem(listToRemove)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDeleteCompleted,
                        throwable -> showWarningOrErrorMessage(getSaveError(throwable), throwable))));

        alert.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onClick(long position) {
    }

    @Override
    public void onLongClick(Product product, boolean immediately) {

        //check if the product isn't in the remove list
        if (!this.listToRemove.contains(product)) this.listToRemove.add(product);

        if (immediately) {
            delete();
        } else if (this.listToRemove.size() > 0) {
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(this, RegisterProductActivity.class);

        intent.putExtra("id", product.getId());

        startActivity(intent);
    }

    @Override
    public void onRemoveItemClick(Product product) {

        //remove the product form list
        if (this.listToRemove != null) this.listToRemove.remove(product);

        //update de menu
        if (this.listToRemove != null && this.listToRemove.size() == 0) invalidateOptionsMenu();
    }

    /**
     * On Delete completed
     */
    public void onDeleteCompleted() {
        if (this.listToRemove != null) this.listToRemove.clear();
        showSuccessMessage(R.string.msg_delete_success);
        invalidateOptionsMenu();
        loadProducts(null, 0, "", null);
    }

    /**
     * Start the Scan of Barcode
     */
    private void startScan() {

        this.barcodeSearching = true;

        MaterialBarcodeScannerBuilder builder = BarcodeScanner.newBuilderInstance(getString(R.string.searching), this)
                .withBarcodeFormats(Barcode.ALL_FORMATS);

        builder.withResultListener(barcode -> loadProducts(null, 0, null, barcode.rawValue)).build().startScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        dataFragment.setSupplierProductList(this.supplierProducts);
    }

}
