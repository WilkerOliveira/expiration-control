package br.com.mwmobile.expirationcontrol.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.ui.adapter.ListProductAdapter;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.listener.OnItemClickListener;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListProductViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.RegisterSupplierViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_FIRST_DIVIDER;
import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_LAST_DIVIDER;

/**
 * Register Supplier Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class RegisterSupplierActivity extends LifecycleAppCompatActivity implements OnItemClickListener, OnProductListener {

    private final int ADD_PRODUCT = 1;
    private RegisterSupplierViewModel supplierViewModel;
    private ListProductViewModel productViewModel;
    private Supplier selectedSupplier;
    private EditText inputSupplier;
    private ListProductAdapter recyclerViewAdapter;
    private DividerItemDecoration itemDecoration;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_supplier);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        loadViewModel();

        setDataFields();

        checkEditMode();
    }

    /**
     * Load ViewModels
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.supplierViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(RegisterSupplierViewModel.class);
        this.productViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListProductViewModel.class);
    }

    /**
     * Load the views
     */
    private void setDataFields() {
        //TODO: need change to Data Binding

        findViewById(R.id.fabAddProducts).setOnClickListener(view -> {

                    Intent intent = new Intent(this, RegisterProductActivity.class);
                    intent.putExtra("supplierId", selectedSupplier.getId());
                    startActivityForResult(intent, ADD_PRODUCT);
                }
        );

        inputSupplier = findViewById(R.id.inputSupplier);
    }

    /**
     * Load data if is in edit mode
     */
    private void checkEditMode() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("id")) {
            this.supplierViewModel.getById(getIntent().getExtras().getLong("id"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onConsultCompleted,
                            throwable -> showErrorMessage(R.string.error_load_supplier, throwable));
        }
    }

    /**
     * Load data
     *
     * @param supplier Supplier data
     */
    public void onConsultCompleted(Supplier supplier) {

        selectedSupplier = supplier;

        inputSupplier.setText(supplier.getName());

        findViewById(R.id.fabAddProducts).setVisibility(View.VISIBLE);

        loadProducts();
    }

    /**
     * Load all products per supplier
     */
    private void loadProducts() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        this.recyclerViewAdapter = new ListProductAdapter(new ArrayList<>(), 0, this,
                false,
                Integer.parseInt(PreferencesManager.getExpirationDays(this)));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.removeItemDecoration(this.itemDecoration);
        this.itemDecoration = new DividerItemDecoration(this, null, SHOW_FIRST_DIVIDER, SHOW_LAST_DIVIDER);
        recyclerView.addItemDecoration(this.itemDecoration);

        recyclerView.setAdapter(this.recyclerViewAdapter);

        try {
            findViewById(R.id.contentLoading).setVisibility(View.VISIBLE);
            this.productViewModel.getBySupplier(selectedSupplier.getId()).observe(this, productList -> {

                this.productList = productList;

                recyclerViewAdapter.addItems(productList);

                if (productList == null || productList.isEmpty()) {
                    findViewById(R.id.lblProducts).setVisibility(View.GONE);
                    findViewById(R.id.lblSummary).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.lblProducts).setVisibility(View.VISIBLE);
                    findViewById(R.id.lblSummary).setVisibility(View.GONE);
                }

                findViewById(R.id.contentLoading).setVisibility(View.GONE);

            });
        } catch (Exception ex) {
            findViewById(R.id.contentLoading).setVisibility(View.GONE);
            showErrorMessage(R.string.error_load_products, ex);
        }
    }

    @Override
    public void onClick(long position) {
        Intent intent = new Intent(this, RegisterProductActivity.class);
        intent.putExtra("id", this.productList.get((int) position).getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.clear_menu:
                cleanForm();
                return true;
            case R.id.save_menu:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Clear the Form
     */
    private void cleanForm() {
        this.selectedSupplier = null;
        inputSupplier.setText("");
        findViewById(R.id.fabAddProducts).setVisibility(View.GONE);
        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.addItems(new ArrayList<>());
        }
        findViewById(R.id.lblSummary).setVisibility(View.VISIBLE);
        findViewById(R.id.lblProducts).setVisibility(View.INVISIBLE);
        if (getIntent() != null && getIntent().getExtras() != null) {
            getIntent().removeExtra("id");
        }
    }

    /**
     * Save the Supplier
     */
    private void save() {
        if (TextUtils.isEmpty(inputSupplier.getText().toString()))
            showWarningMessage(R.string.msg_required_fields);
        else {

            if (selectedSupplier == null) selectedSupplier = new Supplier();

            selectedSupplier.setName(inputSupplier.getText().toString());

            mDisposable.add(supplierViewModel.insertOrUpdate(selectedSupplier)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::supplierSaved,
                            throwable -> showWarningOrErrorMessage(getSaveError(throwable), throwable)));
        }

        hideSoftKeyboard();
    }

    /**
     * Supplier saved
     */
    private void supplierSaved() {
        showSuccessMessage(R.string.supplier_save_success);
        findViewById(R.id.fabAddProducts).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLongClick(Product product) {

    }

    @Override
    public void onClick(Product product) {

    }

    @Override
    public void onRemoveItemClick(Product product) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PRODUCT) {
            loadProducts();
        }
    }
}