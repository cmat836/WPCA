package com.cmat.wpca.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;

public class SelectableViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView infoText;
    private final FrameLayout layout;

    private Context context;

    private boolean selected;

    public SelectableViewHolder(@NonNull View itemView, Context c) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.selectable_item_text_name);
        infoText = (TextView) itemView.findViewById(R.id.selectable_item_text_info);
        layout = (FrameLayout) itemView.findViewById(R.id.selectable_layout);
        selected = false;
        context = c;
    }

    public void setSelected(boolean selected) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int backgroundcolor = typedValue.data;
        context.getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        int selectedcolor = typedValue.data;


        getLayout().setSelected(selected);
        this.selected = selected;
        getLayout().setBackground(selected ? new ColorDrawable((selectedcolor)) : new ColorDrawable((backgroundcolor)));

    }

    public boolean isSelected() {
        return selected;
    }

    public TextView getNameText() {
        return nameText;
    }

    public TextView getInfoText() {
        return infoText;
    }

    public FrameLayout getLayout() { return layout; }
}
