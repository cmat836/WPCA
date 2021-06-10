package com.cmat.wpca.fragments;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;

public class InfoViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView infoText;

    public InfoViewHolder(@NonNull View itemView) {
        super(itemView);
        nameText = (TextView) itemView.findViewById(R.id.textViewName);
        infoText = (TextView) itemView.findViewById(R.id.textViewInfo);
    }

    public TextView getNameText() {
        return nameText;
    }

    public TextView getInfoText() {
        return infoText;
    }
}
