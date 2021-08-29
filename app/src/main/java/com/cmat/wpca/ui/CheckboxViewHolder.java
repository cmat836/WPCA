package com.cmat.wpca.ui;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;

public class CheckboxViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final CheckBox checkbox;

    public CheckboxViewHolder(@NonNull View itemView) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.checkbox_item_text_name);
        checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_item_checkbox);
    }

    public TextView getNameText() {
        return nameText;
    }

    public CheckBox getInfoText() {
        return checkbox;
    }
}
