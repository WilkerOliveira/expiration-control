package br.com.mwmobile.expirationcontrol.ui.model;

import java.math.BigDecimal;

/**
 * Summary Vo
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 07/01/2018
 */

public class SummaryVO {

    private int totalProductWarning;
    private int totalProductExpired;
    private int totalProductValid;
    private int totalProducts;

    private BigDecimal amountProductWarning;
    private BigDecimal amountProductExpired;
    private BigDecimal amountProductValid;
    private BigDecimal totalAmount;

    public int getTotalProductWarning() {
        return totalProductWarning;
    }

    public void setTotalProductWarning(int totalProductWarning) {
        this.totalProductWarning = totalProductWarning;
    }

    public int getTotalProductExpired() {
        return totalProductExpired;
    }

    public void setTotalProductExpired(int totalProductExpired) {
        this.totalProductExpired = totalProductExpired;
    }

    public int getTotalProductValid() {
        return totalProductValid;
    }

    public void setTotalProductValid(int totalProductValid) {
        this.totalProductValid = totalProductValid;
    }

    public BigDecimal getAmountProductWarning() {
        return amountProductWarning;
    }

    public void setAmountProductWarning(BigDecimal amountProductWarning) {
        this.amountProductWarning = amountProductWarning;
    }

    public BigDecimal getAmountProductExpired() {
        return amountProductExpired;
    }

    public void setAmountProductExpired(BigDecimal amountProductExpired) {
        this.amountProductExpired = amountProductExpired;
    }

    public BigDecimal getAmountProductValid() {
        return amountProductValid;
    }

    public void setAmountProductValid(BigDecimal amountProductValid) {
        this.amountProductValid = amountProductValid;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
