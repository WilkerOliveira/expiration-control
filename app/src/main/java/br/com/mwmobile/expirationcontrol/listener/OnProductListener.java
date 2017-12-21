package br.com.mwmobile.expirationcontrol.listener;

import br.com.mwmobile.expirationcontrol.repository.local.model.Product;

/**
 * Product Listener
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */

public interface OnProductListener extends OnItemClickListener {

    void onLongClick(Product product);

    void onClick(Product product);

    void onRemoveItemClick(Product product);
}
