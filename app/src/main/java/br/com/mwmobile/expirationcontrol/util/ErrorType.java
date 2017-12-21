package br.com.mwmobile.expirationcontrol.util;

import br.com.mwmobile.expirationcontrol.R;

/**
 * Enum Error Type
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 20/11/2017
 */

public enum ErrorType {

    SUPPLIER_NAME_EXIST(R.string.msg_supplier_name_exist),
    INVALID_EXPIRATION_DATE(R.string.msg_invalid_expiration_date);

    Integer errorType = 0;

    ErrorType(int error) {
        this.errorType = error;
    }

    public String getStringErrorType() {
        return this.errorType.toString();
    }

    public int getErrorType() {
        return this.errorType;
    }
}
