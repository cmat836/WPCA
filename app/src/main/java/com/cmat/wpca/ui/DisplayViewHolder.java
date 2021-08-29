package com.cmat.wpca.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;

public class DisplayViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView infoText;

    public DisplayViewHolder(@NonNull View itemView) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.display_item_text_name);
        infoText = (TextView) itemView.findViewById(R.id.display_item_text_info);
    }

    public TextView getNameText() {
        return nameText;
    }

    public TextView getInfoText() {
        return infoText;
    }
}
