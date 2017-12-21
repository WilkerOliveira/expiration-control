package br.com.mwmobile.expirationcontrol.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.repository.SupplierRepository;
import br.com.mwmobile.expirationcontrol.ui.adapter.ListSupplierAdapter;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.ui.dialog.AlertDialog;
import br.com.mwmobile.expirationcontrol.ui.dialog.DialogType;
import br.com.mwmobile.expirationcontrol.listener.OnSupplierListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListSupplierViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_FIRST_DIVIDER;
import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_LAST_DIVIDER;

/**
 * Supplier List Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class ListSupplierActivity extends LifecycleAppCompatActivity implements OnSupplierListener {

    ListSupplierViewModel viewModel;
    RecyclerView recyclerView;
    private ListSupplierAdapter recyclerViewAdapter;
    private List<Supplier> listToRemove;
    private List<Supplier> list;
    private DividerItemDecoration itemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_supplier);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        listToRemove = new ArrayList<>();

        loadViewModel();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(ListSupplierActivity.this, RegisterSupplierActivity.class)));

        loadSuppliers();
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.viewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListSupplierViewModel.class);
    }

    /**
     * Load the Supplier's RecyclerView
     */
    private void loadSuppliers() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(null);

        recyclerViewAdapter = new ListSupplierAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.removeItemDecoration(this.itemDecoration);
        this.itemDecoration = new DividerItemDecoration(this, null, SHOW_FIRST_DIVIDER, SHOW_LAST_DIVIDER);
        recyclerView.addItemDecoration(this.itemDecoration);

        recyclerView.setAdapter(recyclerViewAdapter);

        this.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        this.viewModel.getAll().observe(this, suppliers -> {
            list = suppliers;
            recyclerViewAdapter.addItems(suppliers);
            if (list == null || list.isEmpty()) {
                showWarningMessage(R.string.registers_not_found);
                ImageView imvEmptyList = findViewById(R.id.imvEmptyList);
                imvEmptyList.setVisibility(View.VISIBLE);
                imvEmptyList.setImageResource(R.drawable.ic_supplier);
            } else {
                findViewById(R.id.imvEmptyList).setVisibility(View.GONE);
            }

            this.findViewById(R.id.progressBar).setVisibility(View.GONE);
        });

    }

    @Override
    public void onClick(long position) {
        Intent intent = new Intent(ListSupplierActivity.this, RegisterSupplierActivity.class);

        intent.putExtra("id", this.list.get((int) position).getId());

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (this.listToRemove != null && this.listToRemove.size() > 0)
            getMenuInflater().inflate(R.menu.delete_menu, menu);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Perform the action of delete
     */
    private void delete() {
        //confirm if the user really wants delete the product
        final AlertDialog alerta = new AlertDialog();

        alerta.setAlertType(DialogType.YES_NO);
        alerta.setMessage(getString(R.string.msg_confirm_delete_supplier));

        alerta.setFirstButtonEvent((dialogInterface, i) -> mDisposable.add(viewModel.delete(listToRemove)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDeleteCompleted,
                        throwable -> showWarningOrErrorMessage(getSaveError(throwable), throwable))));

        alerta.show(getSupportFragmentManager(), "dialogo");
    }

    /**
     * On Delete completed
     */
    public void onDeleteCompleted() {
        if (this.listToRemove != null) this.listToRemove.clear();
        showSuccessMessage(R.string.msg_delete_success);
    }

    @Override
    public void onLongClick(Supplier supplier) {
        //check if the product isn't in the remove list
        if (!this.listToRemove.contains(supplier)) this.listToRemove.add(supplier);

        //update de menu
        if (this.listToRemove.size() > 0) invalidateOptionsMenu();
    }

    @Override
    public void onRemoveItemClick(Supplier supplier) {
        //remove the product form list
        if (this.listToRemove != null) this.listToRemove.remove(supplier);

        //update de menu
        if (this.listToRemove != null && this.listToRemove.size() == 0) invalidateOptionsMenu();
    }
}