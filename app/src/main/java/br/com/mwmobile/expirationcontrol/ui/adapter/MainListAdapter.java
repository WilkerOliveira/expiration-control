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
    private List<Product> productList;
    private OnProductListener listener;

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

        holder.itemView.setTag(product.getId());

        holder.sectionPosition = this.sectionPosition;

        if (product.getStatus() != null) {
            if (product.getStatus() == ExpirationStatus.EXPIRATED) {
                holder.viewExpirationStatus.setBackgroundColor(context.getResources().getColor(R.color.expirated));
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
        View viewExpirationStatus;
        View itemView;
        private TextView name;
        private TextView expirationDate;
        private TextView quantity;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.lblProduct);
            expirationDate = view.findViewById(R.id.lblExpiration);
            quantity = view.findViewById(R.id.lblQuantity);
            viewExpirationStatus = view.findViewById(R.id.viewExpirationStatus);
        }
    }
}