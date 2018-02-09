package br.com.mwmobile.expirationcontrol.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.listener.OnProductListener;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.RecyclerViewType;
import br.com.mwmobile.expirationcontrol.ui.adapter.util.SectionModel;
import br.com.mwmobile.expirationcontrol.ui.adapter.viewHolder.SectionViewHolder;
import br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration;
import br.com.mwmobile.expirationcontrol.ui.sharedprefs.PreferencesManager;
import br.com.mwmobile.expirationcontrol.util.Constants;
import br.com.mwmobile.expirationcontrol.util.Utility;

import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_FIRST_DIVIDER;
import static br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration.SHOW_LAST_DIVIDER;

/**
 * Product Section Adapter
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 25/11/2017
 */
public class ProductSectionAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    private final OnProductListener listener;
    private final Context context;
    private final RecyclerViewType recyclerViewType;
    private final ArrayList<SectionModel> sectionModelArrayList;
    private boolean tooltipAdded;

    /**
     * Constructor
     *
     * @param context          Activity context
     * @param recyclerViewType Type of RecyclerView
     * @param sectionModels    Sections list
     * @param listener         Event listener
     */
    public ProductSectionAdapter(Context context, RecyclerViewType recyclerViewType, ArrayList<SectionModel> sectionModels, OnProductListener listener) {
        this.context = context;
        this.recyclerViewType = recyclerViewType;
        this.sectionModelArrayList = sectionModels;
        this.listener = listener;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_layout, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        final SectionModel sectionModel = sectionModelArrayList.get(position);

        holder.sectionLabel.setText(Utility.limitText(sectionModel.getSectionLabel(), Constants.SECTION_TEXT_LIMIT));
        holder.itemDecoration = new DividerItemDecoration(context, null, SHOW_FIRST_DIVIDER, SHOW_LAST_DIVIDER);

        //recycler view for items
        holder.itemDecoration = new DividerItemDecoration(context, null, SHOW_FIRST_DIVIDER, SHOW_LAST_DIVIDER);
        holder.itemRecyclerView.removeItemDecoration(holder.itemDecoration);
        holder.itemRecyclerView.addItemDecoration(holder.itemDecoration);

        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);

        /* set layout manager on basis of recyclerView enum type */
        switch (recyclerViewType) {
            case LINEAR_VERTICAL:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            case LINEAR_HORIZONTAL:
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
                break;
            case GRID:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                holder.itemRecyclerView.setLayoutManager(gridLayoutManager);
                break;
        }

        ListProductAdapter adapter = new ListProductAdapter(context, sectionModel.getProductList(), position, listener,
                true, Integer.parseInt(PreferencesManager.getExpirationDays(context)), tooltipAdded);
        tooltipAdded = true;
        holder.itemRecyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return sectionModelArrayList.size();
    }

}