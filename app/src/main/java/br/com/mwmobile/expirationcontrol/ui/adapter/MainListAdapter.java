package br.com.mwmobile.expirationcontrol.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;

/**
 * Main List Adapter
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.RecyclerViewHolder> {

    private final int sectionPosition;
    private final Context context;
    private final List<Product> productList;
    private final OnProductListener listener;

    /**
     * Constructor
     *
     * @param productList     Product List
     * @param sectionPosition Section position
     * @param listener        Event Listener
     * @param context         Activity Context
     */
    MainListAdapter(List<Product> productList, int sectionPosition, OnProductListener listener, Context context) {
        this.productList = productList;
        this.listener = listener;
        this.sectionPosition = sectionPosition;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_main_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        Product product = productList.get(position);

        holder.name.setText(product.getName());

        holder.expirationDate.setText(DateUtil.parseToString(product.getExpiration()));

        if (holder.quantity != null)
            holder.quantity.setText(NumberUtil.currencyToString(product.getQuantity()));

        if (holder.value != null)
            holder.value.setText(NumberUtil.currencyToString(product.getValue()));

        if (holder.amount != null)
            holder.amount.setText(NumberUtil.currencyToString(product.getValue().multiply(product.getQuantity())));

        holder.itemView.setTag(product.getId());

        holder.sectionPosition = this.sectionPosition;

        if (product.getStatus() != null) {
            if (product.getStatus() == ExpirationStatus.EXPIRED) {
                holder.viewExpirationStatus.setBackgroundColor(context.getResources().getColor(R.color.expired));
            } else if (product.getStatus() == ExpirationStatus.WARNING) {
                holder.viewExpirationStatus.setBackgroundColor(context.getResources().getColor(R.color.warning_expiration));
            } else {
                holder.viewExpirationStatus.setBackgroundColor(context.getResources().getColor(R.color.normal_expiration));
            }
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * Recycler View Holder
     */
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        int sectionPosition;
        final View viewExpirationStatus;
        final View itemView;
        private final TextView name;
        private final TextView expirationDate;
        private final TextView quantity;
        private final TextView value;
        private final TextView amount;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.lblProduct);
            expirationDate = view.findViewById(R.id.txtExpirationDate);
            quantity = view.findViewById(R.id.lblQuantity);
            value = view.findViewById(R.id.lblVlr);
            amount = view.findViewById(R.id.lblAmount);
            viewExpirationStatus = view.findViewById(R.id.viewExpirationStatus);
        }
    }
}