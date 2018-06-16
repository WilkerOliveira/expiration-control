package br.com.mwmobile.expirationcontrol.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.util.Constants;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import br.com.mwmobile.expirationcontrol.util.ImageUtil;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;
import br.com.mwmobile.expirationcontrol.util.Utility;
import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Product Adapter
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.RecyclerViewHolder> {

    private final int sectionPosition;
    private final int expirationDays;
    private final Context context;
    private final OnProductListener listener;
    private final boolean fullList;
    private List<Product> productList;
    private List<Product> productListToDelete;
    private boolean tooltip;

    /**
     * Constructor
     *
     * @param context         Context
     * @param productList     List of Products
     * @param sectionPosition Section Position
     * @param listener        Event listener
     * @param fullList        Show or not the full designer
     * @param expirationDays  Expiration Day
     * @param tooltipAdded    Add or not Tooltip
     */
    public ListProductAdapter(Context context, List<Product> productList, int sectionPosition, OnProductListener listener,
                              boolean fullList, int expirationDays, boolean tooltipAdded,
                              List<Product> productListToDelete) {
        this.productList = productList;
        this.listener = listener;
        this.fullList = fullList;
        this.sectionPosition = sectionPosition;
        this.expirationDays = expirationDays;
        this.context = context;
        this.tooltip = tooltipAdded;
        this.productListToDelete = productListToDelete;

    }

    /**
     * Constructor
     *
     * @param context         Context
     * @param productList     List of Products
     * @param sectionPosition Section Position
     * @param listener        Event listener
     * @param fullList        Show or not the full designer
     * @param expirationDays  Expiration Day
     * @param tooltipAdded    Add or not Tooltip
     */
    public ListProductAdapter(Context context, List<Product> productList, int sectionPosition, OnProductListener listener,
                              boolean fullList, int expirationDays, boolean tooltipAdded) {
        this.productList = productList;
        this.listener = listener;
        this.fullList = fullList;
        this.sectionPosition = sectionPosition;
        this.expirationDays = expirationDays;
        this.context = context;
        this.tooltip = tooltipAdded;
        this.productListToDelete = new ArrayList<>();

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        Product product = productList.get(position);

        DateUtil.setExpirationStatus(this.expirationDays, product);

        holder.name.setText(Utility.limitText(product.getName(), Constants.TEXT_LIMIT));

        holder.expirationDate.setText(DateUtil.parseToString(product.getExpiration()));

        //check if the product has status
        if (product.getStatus() != null) {
            if (product.getStatus() == ExpirationStatus.EXPIRED) {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.expired));
            } else if (product.getStatus() == ExpirationStatus.WARNING) {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.warning_expiration));
            } else {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.normal_expiration));
            }
        }

        if (holder.quantity != null)
            holder.quantity.setText(NumberUtil.currencyToString(product.getQuantity()));

        if (holder.value != null)
            holder.value.setText(NumberUtil.currencyToString(product.getValue()));

        if (holder.amount != null && product.getValue() != null)
            holder.amount.setText(NumberUtil.currencyToString(product.getValue().multiply(product.getQuantity())));

        holder.itemView.setTag(product.getId());

        holder.sectionPosition = this.sectionPosition;

        if (holder.imgLetter != null) {

            ImageUtil.setLetter(holder.imgLetter, product.getName().toUpperCase());

            holder.imgLetter.setOnLongClickListener(view -> {

                if (!this.productListToDelete.contains(product)) {
                    this.productListToDelete.add(product);
                }

                holder.imgLetter.setImageResource(R.drawable.ic_check_circle_outline);
                listener.onLongClick(product, false);
                return true;
            });

            holder.imgLetter.setOnClickListener(view -> {

                if (this.productListToDelete.contains(product)) {
                    this.productListToDelete.remove(product);
                    ImageUtil.setLetter(holder.imgLetter, product.getName().toUpperCase());
                    listener.onRemoveItemClick(product);
                } else if (this.productListToDelete.size() > 0) {
                    this.productListToDelete.add(product);
                    holder.imgLetter.setImageResource(R.drawable.ic_check_circle_outline);
                    listener.onLongClick(product, false);
                } else {
                    ImageUtil.setLetter(holder.imgLetter, product.getName().toUpperCase());
                    listener.onRemoveItemClick(product);
                }

            });

            if (!fullList) {
                holder.imgLetter.setVisibility(View.GONE);
                holder.imgContextMenu.setVisibility(View.GONE);
            }else {

                holder.imgContextMenu.setOnClickListener(view -> {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, holder.imgContextMenu);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.edit_delete);
                    //adding click listener
                    popup.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.edit_menu:
                                listener.onClick(product);
                                break;
                            case R.id.delete_menu:
                                listener.onLongClick(product, true);
                                break;
                        }
                        return false;
                    });
                    //displaying the popup
                    popup.show();

                });
            }
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(product));

        if (!tooltip && PreferencesManager.getTipStatus(context)) {
            this.tooltip = true;

            Utility.showTooltipHelper(this.context, holder.itemView, this.context.getString(R.string.tooltip_edit), Tooltip.Gravity.RIGHT);
        }


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /***
     * Add Items to Adapter
     * @param productList Product list
     */
    public void addItems(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    /**
     * Recycler View Holder
     */
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgLetter;
        final View itemView;
        private final TextView name;
        private final TextView expirationDate;
        private final TextView quantity;
        private final TextView value;
        private final TextView amount;
        int sectionPosition;
        final ImageView imgContextMenu;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.txtProduct);
            expirationDate = view.findViewById(R.id.txtExpirationDate);
            quantity = view.findViewById(R.id.lblQuantity);
            value = view.findViewById(R.id.lblVlr);
            amount = view.findViewById(R.id.lblAmount);
            imgLetter = view.findViewById(R.id.imgLetter);

            imgContextMenu = view.findViewById(R.id.imgContextMenu);
        }
    }
}