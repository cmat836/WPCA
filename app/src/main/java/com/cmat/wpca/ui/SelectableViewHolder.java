package com.cmat.wpca.ui;

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

    ColorDrawable backgroundColor;
    ColorDrawable selectedColor;

    private boolean selected;

    public SelectableViewHolder(@NonNull View itemView, int backgroundColor, int selectedColor) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.selectable_item_text_name);
        infoText = (TextView) itemView.findViewById(R.id.selectable_item_text_info);
        layout = (FrameLayout) itemView.findViewById(R.id.selectable_layout);
        selected = false;
        this.backgroundColor = new ColorDrawable(backgroundColor);
        this.selectedColor = new ColorDrawable(selectedColor);
    }

    public void setSelected(boolean selected) {
        getLayout().setSelected(selected);
        this.selected = selected;
        getLayout().setBackground(selected ? selectedColor : backgroundColor);

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
