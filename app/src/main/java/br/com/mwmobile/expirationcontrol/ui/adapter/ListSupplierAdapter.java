package br.com.mwmobile.expirationcontrol.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.listener.OnSupplierListener;
import br.com.mwmobile.expirationcontrol.repository.local.model.Supplier;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.util.Constants;
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
    private List<Supplier> supplierListToRemove = new ArrayList<>();
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

        holder.txtName.setText(Utility.limitText(supplier.getName(), Constants.TEXT_LIMIT));

        holder.itemView.setTag(supplier.getId());

        ImageUtil.setLetter(holder.imgLetter, supplier.getName());

        holder.imgLetter.setOnLongClickListener(view -> {

            if (!this.supplierListToRemove.contains(supplier)) {
                this.supplierListToRemove.add(supplier);
            }

            holder.imgLetter.setImageResource(R.drawable.ic_check_circle_outline);
            listener.onLongClick(supplier,false);
            return true;
        });

        holder.imgLetter.setOnClickListener(view -> {

            if (this.supplierListToRemove.contains(supplier)) {
                this.supplierListToRemove.remove(supplier);
                ImageUtil.setLetter(holder.imgLetter, supplier.getName().toUpperCase());
                listener.onRemoveItemClick(supplier);
            } else if (this.supplierListToRemove.size() > 0) {
                this.supplierListToRemove.add(supplier);
                holder.imgLetter.setImageResource(R.drawable.ic_check_circle_outline);
                listener.onLongClick(supplier, false);
            } else {
                ImageUtil.setLetter(holder.imgLetter, supplier.getName().toUpperCase());
                listener.onRemoveItemClick(supplier);
            }
        });

        holder.itemView.setOnClickListener(view -> listener.onClick(position));

        if (!tooltip && PreferencesManager.getTipStatus(context)) {
            Utility.showTooltipHelper(this.context, holder.imgLetter, this.context.getString(R.string.tooltip_remove_helper), Tooltip.Gravity.BOTTOM);
            this.tooltip = true;

            Utility.showTooltipHelper(this.context, holder.itemView, this.context.getString(R.string.tooltip_edit), Tooltip.Gravity.RIGHT);
        }

        holder.imgContextMenu.setOnClickListener(view -> {

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.imgContextMenu);
            //inflating menu from xml resource
            popup.inflate(R.menu.edit_delete);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit_menu:
                        listener.onClick(position);
                        break;
                    case R.id.delete_menu:
                        listener.onLongClick(supplier, true);
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();

        });
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
        final ImageView imgContextMenu;
        private final TextView txtName;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            txtName = view.findViewById(R.id.txtName);
            imgLetter = view.findViewById(R.id.imgLetter);
            imgContextMenu = view.findViewById(R.id.imgContextMenu);
        }


    }
}