package br.com.mwmobile.expirationcontrol.ui.adapter;

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

/**
 * Supplier Adapter
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */

public class ListSupplierAdapter extends RecyclerView.Adapter<ListSupplierAdapter.RecyclerViewHolder> {

    private List<Supplier> supplierList;
    private OnSupplierListener listener;

    /**
     * Construtor
     *
     * @param supplierList Supplier list
     * @param listener     Event listener
     */
    public ListSupplierAdapter(List<Supplier> supplierList, OnSupplierListener listener) {
        this.supplierList = supplierList;
        this.listener = listener;
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
            holder.imgLetter.setImageResource(R.drawable.ic_check);
            listener.onLongClick(supplier);
            return true;
        });

        holder.imgLetter.setOnClickListener(view -> {
            ImageUtil.setLetter(holder.imgLetter, supplier.getName());
            listener.onRemoveItemClick(supplier);
        });

        holder.itemView.setOnClickListener(view -> listener.onClick(position));
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

        ImageView imgLetter;
        View itemView;
        private TextView txtName;

        RecyclerViewHolder(View view) {
            super(view);
            itemView = view;
            txtName = view.findViewById(R.id.txtName);
            imgLetter = view.findViewById(R.id.imgLetter);
        }
    }
}