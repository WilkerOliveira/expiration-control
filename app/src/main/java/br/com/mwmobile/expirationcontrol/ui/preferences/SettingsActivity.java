package br.com.mwmobile.expirationcontrol.ui.preferences;

import android.app.job.JobScheduler;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.jobservice.NotificationJobService;
import br.com.mwmobile.expirationcontrol.util.Constants;

/**
 * Settings Activity
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 27/11/2017
 */

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs != null) {
            prefs.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefs != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preferenceName) {

        if (getString(R.string.notification_alert).equals(preferenceName)) {
            boolean notificationAlert = Boolean.parseBoolean(sharedPreferences.getAll().get(getString(R.string.notification_alert)).toString());

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            if (notificationAlert) {

                //NotificationJobService.createJob(period, serviceName, scheduler);
                NotificationJobService.schedule(this);

            } else {
                cancelJobs(scheduler);
            }
        }
    }

    /**
     * Cancel the Job
     *
     * @param scheduler JobScheduler to cancel
     */
    private void cancelJobs(JobScheduler scheduler) {
        if (scheduler != null) {
            scheduler.cancelAll();
            Log.d(Constants.DEBUG_TAG, "JobScheduler canceled");
        }
    }

    /**
     * Settings Fragment
     */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
