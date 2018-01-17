package br.com.mwmobile.expirationcontrol.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import br.com.mwmobile.expirationcontrol.di.component.ProductComponent;
import br.com.mwmobile.expirationcontrol.repository.ProductRepository;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.ui.model.SummaryVO;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import io.reactivex.annotations.NonNull;

/**
 * Summary ViewModel
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class SummaryViewModel extends ViewModel implements ProductComponent.Injectable {

    ///***
    /// DI
    @Inject
    public ProductRepository productRepository;
    //***

    /**
     * Calculate the Summary
     *
     * @param products       List of Product
     * @param expirationDays Expiration Days
     * @return List of Summary
     */
    static SummaryVO doSummary(@NonNull List<Product> products, int expirationDays) {

        SummaryVO summaryVO = new SummaryVO();

        int totalProductWarning = 0;
        int totalProductExpired = 0;
        int totalProductValid = 0;

        BigDecimal amountProductWarning = BigDecimal.ZERO;
        BigDecimal amountProductExpired = BigDecimal.ZERO;
        BigDecimal amountProductValid = BigDecimal.ZERO;

        for (Product product : products) {

            if (product.getExpiration() != null) {

                DateUtil.setExpirationStatus(expirationDays, product);

                if (product.getStatus() == ExpirationStatus.WARNING) {
                    totalProductWarning++;
                    if (product.getValue() != null) {
                        amountProductWarning = amountProductWarning.add(product.getValue().multiply(product.getQuantity()));
                    }
                }
                if (product.getStatus() == ExpirationStatus.EXPIRED) {
                    totalProductExpired++;
                    if (product.getValue() != null) {
                        amountProductExpired = amountProductExpired.add(product.getValue().multiply(product.getQuantity()));
                    }
                }
                if (product.getStatus() == ExpirationStatus.VALID_PERIOD) {
                    totalProductValid++;
                    if (product.getValue() != null) {
                        amountProductValid = amountProductValid.add(product.getValue().multiply(product.getQuantity()));
                    }
                }
            }

        }

        summaryVO.setTotalProductExpired(totalProductExpired);
        summaryVO.setTotalProductValid(totalProductValid);
        summaryVO.setTotalProductWarning(totalProductWarning);

        summaryVO.setAmountProductExpired(amountProductExpired);
        summaryVO.setAmountProductValid(amountProductValid);
        summaryVO.setAmountProductWarning(amountProductWarning);

        summaryVO.setTotalProducts(totalProductExpired + totalProductValid + totalProductWarning);
        summaryVO.setTotalAmount(amountProductExpired.add(amountProductValid.add(amountProductWarning)));

        return summaryVO;
    }

    /**
     * Consult the Summary
     *
     * @param expirationDays Expiration Days
     * @return Summary result
     */
    public LiveData<SummaryVO> getSummary(int expirationDays) {
        return Transformations.map(productRepository.getAll(), productList -> doSummary(productList, expirationDays));

    }

    @Override
    public void inject(ProductComponent productComponent) {
        productComponent.inject(this);
    }

}