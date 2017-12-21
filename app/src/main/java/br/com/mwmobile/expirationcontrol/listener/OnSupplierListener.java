package br.com.mwmobile.expirationcontrol.listener;

import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;

/**
 * Supplier Listener
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */

public interface OnSupplierListener extends OnItemClickListener {

    void onLongClick(Supplier supplier);

    void onRemoveItemClick(Supplier supplier);
}
