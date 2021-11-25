package com.cmat.wpca.ui;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;

import java.util.ArrayList;

public abstract class DisplayAdapter<T> extends RecyclerView.Adapter<DisplayViewHolder> {
    ArrayList<T> data = new ArrayList<>();

    public DisplayAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display, parent, false);
        return new DisplayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayViewHolder holder, int position) {
        T object = data.get(position);
        holder.getNameText().setText(this.getNameText(object));
        holder.getInfoText().setText(this.getInfoText(object));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public abstract String getNameText(T object);

    public abstract String getInfoText(T object);

}

