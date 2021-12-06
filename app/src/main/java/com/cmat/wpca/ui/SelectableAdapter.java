package com.cmat.wpca.ui;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.entry.IEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class SelectableAdapter<T> extends RecyclerView.Adapter<SelectableViewHolder> {
    ArrayList<T> selectedData = new ArrayList<>();
    ArrayList<T> data = new ArrayList<>();

    public SelectableAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public SelectableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selectable, parent, false);
        TypedValue typedValue = new TypedValue();
        parent.getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int backgroundColor = typedValue.data;
        parent.getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        int selectedColor = typedValue.data;
        return new SelectableViewHolder(v, backgroundColor, selectedColor);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableViewHolder holder, int position) {
        T object = data.get(position);
        holder.getNameText().setText(this.getNameText(object));
        holder.getInfoText().setText(this.getInfoText(object));
        holder.setSelected(selectedData.contains(object));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.isSelected() && isSelectionAllowed()) {
                    holder.setSelected(true);
                    selectedData.remove(object);
                    selectedData.add(object);
                    onSelectViewHolder(object, true);

                } else {
                    holder.setSelected(false);
                    selectedData.remove(object);
                    onSelectViewHolder(object, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void modifyData(ArrayList<T> newData) {
        this.data = newData;
        for (T e : selectedData) {
            if (!this.data.contains(e)) {
                selectedData.remove(e);
            }
        }
        this.notifyDataSetChanged();
    }

    public ArrayList<T> getSelectedData() {
        return selectedData;
    }

    public abstract String getNameText(T object);

    public abstract String getInfoText(T object);

    public abstract boolean isSelectionAllowed();

    public abstract void onSelectViewHolder(T object, boolean selected);
}
