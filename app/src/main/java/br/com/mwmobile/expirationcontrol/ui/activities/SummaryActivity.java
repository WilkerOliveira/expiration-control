package br.com.mwmobile.expirationcontrol.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.application.ExpirationControlApplication;
import br.com.mwmobile.expirationcontrol.di.ViewModelFactory;
import br.com.mwmobile.expirationcontrol.ui.activities.base.LifecycleAppCompatActivity;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.ui.viewmodel.SummaryViewModel;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;

public class SummaryActivity extends LifecycleAppCompatActivity {

    private SummaryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        loadViewModel();
        loadSummary();
    }

    /**
     * Load ViewModel
     */
    private void loadViewModel() {
        ExpirationControlApplication application = (ExpirationControlApplication) getApplication();
        this.viewModel = ViewModelProviders.of(this, new ViewModelFactory(application)).get(SummaryViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Load the Summary
     */
    private void loadSummary() {
        String expirationDays = PreferencesManager.getExpirationDays(this);

        this.viewModel.getSummary(Integer.parseInt(expirationDays)).observe(this, summaryVO -> {
            if (summaryVO != null) {
                ((TextView) findViewById(R.id.lblSummaryProducts)).setText(String.format("%s", summaryVO.getTotalProducts()));
                ((TextView) findViewById(R.id.lblSummaryTotal)).setText(NumberUtil.currencyToString(summaryVO.getTotalAmount()));

                ((TextView) findViewById(R.id.lblExpProducts)).setText(String.format("%s", summaryVO.getTotalProductExpired()));
                ((TextView) findViewById(R.id.lblExpAmount)).setText(NumberUtil.currencyToString(summaryVO.getAmountProductExpired()));

                ((TextView) findViewById(R.id.lblNextProducts)).setText(String.format("%s", summaryVO.getTotalProductWarning()));
                ((TextView) findViewById(R.id.lblNextAmount)).setText(NumberUtil.currencyToString(summaryVO.getAmountProductWarning()));

                ((TextView) findViewById(R.id.lblValidProducts)).setText(String.format("%s", summaryVO.getTotalProductValid()));
                ((TextView) findViewById(R.id.lblValidAmount)).setText(NumberUtil.currencyToString(summaryVO.getAmountProductValid()));
            }
        });
    }
}
