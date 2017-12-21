package br.com.mwmobile.expirationcontrol.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.fragment.MainListFragment;
import br.com.mwmobile.expirationcontrol.ui.preferences.SettingsActivity;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.ListSupplierViewModel;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Main Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */
public class MainActivity extends LifecycleAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnProductListener {

    boolean doubleBackToExitPressedOnce = false;
    private boolean settingsEnable = false;
    private ListSupplierViewModel listSupplierViewModel;
    private List<Supplier> supplierList;
    private int supplierIndex;
    private List<ExpirationStatus> expirationStatus;
    private long supplierId;
    private NavigationView navigationView;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadViewModel();

        setFrament(MainListFragment.newInstance());

        //Initialize the AdMob
        MobileAds.initialize(this, getString(R.string.admob_key));
        AdView mAdView = findViewById(R.id.adView);

        //TODO: TEST ONLY
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("42035966B2CB558C885117E9202036B4").build();
        //-------------------

        mAdView.loadAd(adRequest);

        final CollapsingToolbarLayout collapsingToolbarLayout =  findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    /**
     * Set the Fragment
     *
     * @param newFragment Fragment
     */
    private void setFrament(Fragment newFragment) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null) fragment = newFragment;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(fragment.getClass().getName()).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (fragment instanceof MainListFragment) {
                    finish();
                } else {
                    super.onBackPressed();
                }
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit_confirm_msg, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.barcode_menu) {
            startScan(this.expirationStatus, this.supplierId);
            return true;
        } else if (id == R.id.filter_menu) {
            openProductFilter();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        settingsEnable = false;

        if (id == R.id.nav_suppliers) {
            startActivity(new Intent(this, ListSupplierActivity.class));
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(this, ListProductActivity.class));
        } else if (id == R.id.nav_notifications) {
            settingsEnable = true;
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Open the filter of products
     */
    private void openProductFilter() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        View popupView = getLayoutInflater().inflate(R.layout.popup_products_filter, null);
        popupView.findViewById(R.id.inputProduct).setVisibility(View.GONE);
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
     * Execute the Filter
     *
     * @param popupView Layout of popup filter
     */
    private void doFilter(View popupView) {

        this.expirationStatus = new ArrayList<>();

        if (((CheckBox) popupView.findViewById(R.id.chkToExpire)).isChecked()) {
            this.expirationStatus.add(ExpirationStatus.VALID_PERIOD);
        }
        if (((CheckBox) popupView.findViewById(R.id.chkNextExpirations)).isChecked()) {
            this.expirationStatus.add(ExpirationStatus.WARNING);
        }
        if (((CheckBox) popupView.findViewById(R.id.chkExpireted)).isChecked()) {
            this.expirationStatus.add(ExpirationStatus.EXPIRATED);
        }

        this.supplierId = 0;

        if (supplierIndex > 0) {
            this.supplierId = this.supplierList.get(this.supplierIndex - 1).getId();
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof MainListFragment) {
            ((MainListFragment) fragment).loadProducts(expirationStatus, supplierId, null, false);
        }
    }

    /**
     * Load the Supplier's spinner
     */
    private void loadSuppliers(MaterialSpinner spinnerSupplier) {
        this.listSupplierViewModel.getAll().observe(this, list -> {

            supplierList = list;

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getSupplierList(this.supplierList, true));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerSupplier.setAdapter(arrayAdapter);

            if (list == null || list.isEmpty())
                showWarningMessage(R.string.suppliers_not_found);
            else
                supplierIndex = 0;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsEnable) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

            if (fragment instanceof MainListFragment) {
                ((MainListFragment) fragment).loadProducts(this.expirationStatus, this.supplierId, null, true);
            }
        }
        settingsEnable = false;
        unCheckAllMenuItems(navigationView.getMenu());
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.listSupplierViewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(ListSupplierViewModel.class);
    }

    @Override
    public void onClick(long position) {

    }

    @Override
    public void onLongClick(Product product) {

    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(this, RegisterProductActivity.class);

        intent.putExtra("id", product.getId());

        startActivity(intent);
    }

    @Override
    public void onRemoveItemClick(Product product) {

    }

    /**
     * Clear checked menu item
     *
     * @param menu Menu class
     */
    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }
}
