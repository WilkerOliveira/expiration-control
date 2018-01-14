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
import br.com.mwmobile.expirationcontrol.listener.OnSupplierListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.util.ImageUtil;
import br.com.mwmobile.expirationcontrol.util.Utility;
import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Supplier Adapter
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class ListSupplierAdapter extends RecyclerView.Adapter<ListSupplierAdapter.RecyclerViewHolder> {

    private final OnSupplierListener listener;
    private final Context context;
    private List<Supplier> supplierList;
    private boolean tooltip;

    /**
     * Constructor
     *
     * @param supplierList Supplier list
     * @param listener     Event listener
     * @param context      Context
     */
    public ListSupplierAdapter(List<Supplier> supplierList, OnSupplierListener listener, Context context) {
        this.supplierList = supplierList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_supplier_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        Supplier supplier = supplierList.get(position);
        holder.txtName.setText(supplier.getName());

        holder.itemView.setTag(supplier.getId());

        ImageUtil.setLetter(holder.imgLetter, supplier.getName());

        holder.imgLetter.setOnLongClickListener(view -> {
            holder.imgLetter.setImageResource(R.drawable.ic_check_circle_outline);
            listener.onLongClick(supplier);
            return true;
        });

        holder.imgLetter.setOnClickListener(view -> {
            ImageUtil.setLetter(holder.imgLetter, supplier.getName());
            listener.onRemoveItemClick(supplier);
        });

        holder.itemView.setOnClickListener(view -> listener.onClick(position));
        if (!tooltip) {
            Utility.showTooltipHelper(this.context, holder.imgLetter, this.context.getString(R.string.tooltip_remove_helper), Tooltip.Gravity.BOTTOM);
            this.tooltip = true;

            Utility.showTooltipHelper(this.context, holder.itemView, this.context.getString(R.string.tooltip_edit), Tooltip.Gravity.RIGHT);
        }
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    /***
     * Add Items to Adapter
     * @param supplierList Supplier list
     */
    public void addItems(List<Supplier> supplierList) {
        this.supplierList = supplierList;
        notifyDataSetChanged();
    }

    /**
     * Recycler View Holder
     */
    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgLetter;
        final View itemView;
        private final TextView txtName;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            txtName = view.findViewById(R.id.txtName);
            imgLetter = view.findViewById(R.id.imgLetter);
        }
    }
}