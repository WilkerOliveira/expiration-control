package br.com.mwmobile.expirationcontrol.ui.adapter.viewModel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.mwmobile.expirationcontrol.R;
import br.com.mwmobile.expirationcontrol.ui.decorator.DividerItemDecoration;

/**
 * Section ViewHolder
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 14/12/2017
 */

public class SectionViewHolder extends RecyclerView.ViewHolder {

    public DividerItemDecoration itemDecoration;
    public final TextView sectionLabel;
    public final RecyclerView itemRecyclerView;

    public SectionViewHolder(View itemView) {
        super(itemView);

        sectionLabel = itemView.findViewById(R.id.section_label);
        itemRecyclerView = itemView.findViewById(R.id.item_recycler_view);

    }
}