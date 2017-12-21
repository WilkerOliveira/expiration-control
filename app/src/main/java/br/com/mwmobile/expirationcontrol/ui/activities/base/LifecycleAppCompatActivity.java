package br.com.mwmobile.expirationcontrol.ui.activities.base;

import android.app.Activity;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.exception.CustomError;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.fragment.MainListFragment;
import br.com.mwmobile.expirationcontrol.util.BarcodeScanner;
import br.com.mwmobile.expirationcontrol.util.Constants;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Activity base class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/09/2017
 */

public abstract class LifecycleAppCompatActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    //Composite Disposable to async process
    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    //Control if the callback is about load image
    protected final int RESULT_LOAD_IMAGE = 1;
    //Control if the callback is about access external storage
    protected final int READ_EXTERNAL_STORAGE = 1;
    //life cycle registry
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    //variable to show SnackBar
    protected CoordinatorLayout coordinatorLayout;

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Open a popup gallery to pick up an image
     */
    protected void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    /**
     * Show success message
     *
     * @param msg message id
     */
    protected void showSuccessMessage(int msg) {
        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.GREEN);
            snackbar.show();
        }
    }

    /**
     * Show warning or error message
     *
     * @param msg       message id
     * @param throwable Some error
     */
    protected void showWarningOrErrorMessage(int msg, Throwable throwable) {
        if (throwable instanceof CustomError)
            showWarningMessage(msg);
        else {
            showErrorMessage(msg, throwable);
        }
    }

    /**
     * Show error message
     *
     * @param msg       message id
     * @param throwable Some error
     */
    protected void showErrorMessage(int msg, Throwable throwable) {
        Log.e(Constants.ERROR_TAG, getString(msg), throwable);

        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
    }

    /**
     * Show error message
     *
     * @param msg       message
     * @param throwable Some error
     */
    protected void showErrorMessage(String msg, Throwable throwable) {
        Log.e(Constants.ERROR_TAG, msg, throwable);

        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
    }

    /**
     * Show warning message
     *
     * @param msg message id
     */
    protected void showWarningMessage(int msg) {
        if (coordinatorLayout != null) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    /**
     * Get the error
     *
     * @param throwable Throwable
     * @return error id
     */
    protected int getSaveError(Throwable throwable) {
        if (throwable instanceof CustomError) {
            return Integer.parseInt(throwable.getMessage());
        }

        return R.string.save_error;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // clear all the subscriptions
        mDisposable.clear();
    }

    /**
     * Hide the Keyboard
     */
    protected void hideSoftKeyboard() {

        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && this.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Get the Supplier' name
     *
     * @param supplierList Supplier list
     * @param addAll       Add the item ALL
     * @return List with Supplier's name
     */
    protected String[] getSupplierList(List<Supplier> supplierList, boolean addAll) {
        List<String> names = new ArrayList<>();

        if(addAll) names.add(getString(R.string.all));

        if (supplierList != null) {
            for (Supplier trip : supplierList) {
                names.add(trip.getName());
            }
        }

        return names.toArray(new String[names.size()]);
    }

    /**
     * Start the Barcode Scan
     *
     * @param expirationStatus Expiration Status
     * @param supplierId       Supplier ID
     */
    protected void startScan(List<ExpirationStatus> expirationStatus, long supplierId) {

        MaterialBarcodeScannerBuilder builder = BarcodeScanner.newBuilderInstance(getString(R.string.searching), this);

        builder.withResultListener(barcode -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment instanceof MainListFragment) {
                ((MainListFragment) fragment).loadProducts(expirationStatus, supplierId, barcode.rawValue, false);
            }
        }).build().startScan();
    }
}