package br.com.mwmobile.expirationcontrol.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;
import br.com.mwmobile.expirationcontrol.util.DateUtil;
import br.com.mwmobile.expirationcontrol.util.ExpirationStatus;
import br.com.mwmobile.expirationcontrol.util.ImageUtil;
import br.com.mwmobile.expirationcontrol.util.NumberUtil;

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
    private List<Product> productList;
    private OnProductListener listener;
    private boolean fullList;

    /**
     * Constructor
     *
     * @param productList     List of Products
     * @param sectionPosition Section Position
     * @param listener        Event listener
     * @param fullList        Show or not the full designer
     * @param expirationDays  Expiration Day
     */
    public ListProductAdapter(List<Product> productList, int sectionPosition, OnProductListener listener, boolean fullList, int expirationDays) {
        this.productList = productList;
        this.listener = listener;
        this.fullList = fullList;
        this.sectionPosition = sectionPosition;
        this.expirationDays = expirationDays;

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

        holder.name.setText(product.getName());

        holder.expirationDate.setText(DateUtil.parseToString(product.getExpiration()));

        //check if the product has status
        if (product.getStatus() != null) {
            if (product.getStatus() == ExpirationStatus.EXPIRATED) {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.expirated));
            } else if (product.getStatus() == ExpirationStatus.WARNING) {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.warning_expiration));
            } else {
                holder.expirationDate.setTextColor(((Context) this.listener).getResources().getColor(R.color.normal_expiration));
            }
        }

        if (holder.quantity != null)
            holder.quantity.setText(NumberUtil.currencyToString(product.getQuantity()));

        holder.itemView.setTag(product.getId());

        holder.sectionPosition = this.sectionPosition;

        if (holder.imgLetter != null) {

            ImageUtil.setLetter(holder.imgLetter, product.getName());

            holder.imgLetter.setOnLongClickListener(view -> {
                holder.imgLetter.setImageResource(R.drawable.ic_check);
                listener.onLongClick(product);
                return true;
            });

            holder.imgLetter.setOnClickListener(view -> {
                ImageUtil.setLetter(holder.imgLetter, product.getName());
                listener.onRemoveItemClick(product);
            });

            if (!fullList) holder.imgLetter.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(product));
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

        int sectionPosition;
        ImageView imgLetter;
        View itemView;
        private TextView name;
        private TextView expirationDate;
        private TextView quantity;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.txtSupplier);
            expirationDate = view.findViewById(R.id.txtExpirationDate);
            quantity = view.findViewById(R.id.lblQuantity);
            imgLetter = view.findViewById(R.id.imgLetter);
        }
    }
}