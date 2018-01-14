package br.com.mwmobile.expirationcontrol.ui.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.helper.MoneyTextWatcher;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListSupplierViewModel;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.RegisterProductViewModel;
import br.com.mwmobile.expirationcontrol.util.BarcodeScanner;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;
import fr.ganfra.materialspinner.MaterialSpinner;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Register Product Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class RegisterProductActivity extends LifecycleAppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    EditText inputProduct;
    EditText txtExpirationDate;
    EditText inputQuantity;
    EditText inputProductValue;
    EditText inputBarCode;
    List<Supplier> supplierList;
    long supplierId;

    private DatePickerDialog fromDatePickerDialog;
    private ListSupplierViewModel listSupplierViewModel;
    private RegisterProductViewModel productViewModel;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        setDataFields();

        setDatePicker();

        loadViewModel();

        loadSuppliers();
    }

    /**
     * Load the Supplier's spinner
     */
    private void loadSuppliers() {
        this.listSupplierViewModel.getAll().observe(RegisterProductActivity.this, list -> {

            supplierList = list;

            MaterialSpinner cboTrip = findViewById(R.id.spnSupplier);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSupplierList(this.supplierList, false));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            cboTrip.setAdapter(arrayAdapter);

            setSupplierId();

            checkEditMode();
        });
    }

    /**
     * Set the Supplier ID
     */
    private void setSupplierId() {
        if (supplierList == null || supplierList.isEmpty())
            showWarningMessage(R.string.suppliers_not_found);
        else
            supplierId = supplierList.get(0).getId();
    }

    /**
     * Set the View's
     */
    private void setDataFields() {
        //TODO: need change to Data Binding

        inputProduct = findViewById(R.id.inputProduct);
        inputBarCode = findViewById(R.id.inputBarCode);
        txtExpirationDate = findViewById(R.id.txtExpirationDate);
        txtExpirationDate.setInputType(InputType.TYPE_NULL);

        inputQuantity = findViewById(R.id.inputQuantity);
        inputQuantity.addTextChangedListener(new MoneyTextWatcher(inputQuantity));

        inputProductValue = findViewById(R.id.inputProductValue);
        inputProductValue.addTextChangedListener(new MoneyTextWatcher(inputProductValue));

        ((MaterialSpinner) findViewById(R.id.spnSupplier)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                supplierId = supplierList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.imbBarcode).setOnClickListener(view -> startScan());
    }

    /**
     * Set the DatePicker Dialog
     */
    private void setDatePicker() {
        txtExpirationDate.setOnClickListener(this);

        txtExpirationDate.setOnFocusChangeListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            txtExpirationDate.setText(DateUtil.parseToString(newDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        if (focused) fromDatePickerDialog.show();
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.listSupplierViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListSupplierViewModel.class);
        this.productViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(RegisterProductViewModel.class);
    }

    @Override
    public void onClick(View view) {
        fromDatePickerDialog.show();
    }

    /**
     * Check if is in Edit Mode
     */
    private void checkEditMode() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("id")) {
            this.productViewModel.getById(getIntent().getExtras().getLong("id"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Product>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(Product product) {
                            onConsultCompleted(product);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showErrorMessage(R.string.error_load_product, e);
                        }
                    });
        } else if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("supplierId")) {
            setSelectedSupplier(getIntent().getExtras().getLong("supplierId"));
        }
    }

    /**
     * On Consult Completed
     *
     * @param product Product data
     */
    public void onConsultCompleted(Product product) {
        selectedProduct = product;
        inputProduct.setText(selectedProduct.getName());

        txtExpirationDate.setText(DateUtil.parseToString(selectedProduct.getExpiration()));

        inputQuantity.setText(NumberUtil.currencyToString(selectedProduct.getQuantity()));

        inputProductValue.setText(NumberUtil.currencyToString(selectedProduct.getValue()));

        inputBarCode.setText(selectedProduct.getBarCode());

        setSelectedSupplier(selectedProduct.getSupplierId());
    }

    /**
     * Set the selected Supplier
     *
     * @param supplierId Supplier ID
     */
    private void setSelectedSupplier(long supplierId) {

        if (supplierList != null && !supplierList.isEmpty()) {

            int index = 0;

            for (int i = 0; i < supplierList.size(); i++) {
                if (supplierList.get(i).getId() == supplierId) {
                    this.supplierId = supplierList.get(i).getId();
                    index = i;
                    break;
                }
            }

            ((MaterialSpinner) findViewById(R.id.spnSupplier)).setSelection(index);
        }
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
        this.selectedProduct = null;
        inputQuantity.setText("");
        inputProduct.setText("");
        txtExpirationDate.setText("");
        inputBarCode.setText("");
        inputProductValue.setText("");

        if (!(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("supplierId"))) {
            setSupplierId();
            setSelectedSupplier(this.supplierId);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            getIntent().removeExtra("id");
        }
    }

    /**
     * Save the Product
     */
    private void save() {
        if (TextUtils.isEmpty(inputProduct.getText().toString()) || TextUtils.isEmpty(txtExpirationDate.getText().toString()) ||
                this.supplierId == 0)
            showWarningMessage(R.string.msg_required_fields);
        else {
            try {
                if (selectedProduct == null) selectedProduct = new Product();

                selectedProduct.setName(inputProduct.getText().toString());

                selectedProduct.setExpiration(DateUtil.parseToDate(txtExpirationDate.getText().toString()));

                selectedProduct.setQuantity(NumberUtil.toCurrencyBigDecimal(inputQuantity.getText().toString()));
                selectedProduct.setValue(NumberUtil.toCurrencyBigDecimal(inputProductValue.getText().toString()));
                selectedProduct.setSupplierId(supplierId);
                selectedProduct.setBarCode(TextUtils.isEmpty(inputBarCode.getText().toString()) ? null : inputBarCode.getText().toString());

                mDisposable.add(productViewModel.insertOrUpdate(selectedProduct)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::productSaved,
                                throwable -> showWarningOrErrorMessage(getSaveError(throwable), throwable)));
            } catch (ParseException e) {
                showWarningOrErrorMessage(R.string.msg_invalid_expiration_date, e);
            }
        }
        hideSoftKeyboard();
    }

    /**
     * Product Saved
     */
    private void productSaved() {
        showSuccessMessage(R.string.product_save_success);
        cleanForm();
    }

    /**
     * Start the BarCode Scan
     */
    private void startScan() {
        MaterialBarcodeScannerBuilder builder = BarcodeScanner.newBuilderInstance(getString(R.string.searching), this);

        builder.withResultListener(barcode -> inputBarCode.setText(barcode.rawValue)).build().startScan();
    }
}
