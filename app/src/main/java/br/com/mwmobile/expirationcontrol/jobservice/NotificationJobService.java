package br.com.mwmobile.expirationcontrol.jobservice;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

import br.com.mwmobile.expirationcontrol.notification.NotificationHelper;
import br.com.mwmobile.expirationcontrol.repository.ProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.dao.AppDatabase;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.util.Constants;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Notification Job Service
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 14/12/2017
 */

public class NotificationJobService extends JobService {

    private static final int JOB_ID = 1;
    /**
     * Represent the On Day Interval
     */
    private static final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day

    /**
     * Interval to monitoring
     */
    private static final long INTERVAL = 1;

    /**
     * Schedule the Notification Process
     *
     * @param context Context
     */
    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, NotificationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, component)
                .setPeriodic(INTERVAL * ONE_DAY_INTERVAL);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
            jobScheduler.schedule(builder.build());
            Log.d(Constants.DEBUG_TAG, "Job Scheduled");
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        processExpiration();
        Log.d(Constants.DEBUG_TAG, "Job Started");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        Log.d(Constants.DEBUG_TAG, "Job Stopped");
        return false;
    }

    /**
     * Start the process to check de expiration
     */
    private void processExpiration() {

        Log.d(Constants.DEBUG_TAG, "Processing...");

        int days = Integer.parseInt(PreferencesManager.getExpirationDays(getApplicationContext()));

        Observable.fromCallable(() -> getProductExpiring(days))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean isExpiring) {
                        if (isExpiring)
                            new NotificationHelper(getBaseContext()).startNotify();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * Get expiring products
     *
     * @param expirationDays Expirations Days
     * @return True for Expiring Products or False otherwise
     */
    private boolean getProductExpiring(int expirationDays) {

        ProductRepository repository = ProductRepository.getInstance(AppDatabase.getDatabase(getApplicationContext()).itemProductDAO());

        return processExpirationProduct(expirationDays, repository.getProductJob());
    }

    /**
     * Check if exist products that are expiring in this period of days
     *
     * @param expirationDays Expiration Days
     * @param products       Product list
     * @return True for Expiring Products or False otherwise
     */
    private boolean processExpirationProduct(int expirationDays, List<Product> products) {

        boolean isExpiring = false;

        for (Product product : products) {

            if (product.getExpiration() != null) {

                DateUtil.setExpirationStatus(expirationDays, product);

                if (product.getStatus() == ExpirationStatus.WARNING || product.getStatus() == ExpirationStatus.EXPIRED) {
                    isExpiring = true;
                    break;
                }
            }
        }

        Log.d(Constants.DEBUG_TAG, "Is Product expiring: " + isExpiring);

        return isExpiring;
    }
}