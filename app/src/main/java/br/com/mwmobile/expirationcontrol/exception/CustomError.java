package br.com.mwmobile.expirationcontrol.exception;

/**
 * Custom Error
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 20/11/2017
 */

public class CustomError extends Exception {

    /**
     * Customer error constructor
     *
     * @param error Error message
     */
    public CustomError(String error) {
        super(error);
    }

}
