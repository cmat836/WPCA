package com.cmat.wpca.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.RulesetEntry;

public class RulesetFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DataStore<RulesetEntry> dataStore = new DataStore<>("rulesets", RulesetEntry.class);

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ruleset_page, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataStore.load(getContext());

        RecyclerView rulesetDisplay = (RecyclerView)view.findViewById(R.id.rulesetdisplay_recyclerview);
        rulesetDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
        RulesetAdapter radapter = new RulesetAdapter(this);
        rulesetDisplay.setAdapter(radapter);

        Spinner rulesetSelector = (Spinner)view.findViewById(R.id.spinner_ruleset_select);
        rulesetSelector.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, dataStore.getArrayOfEntryNames());
        rulesetSelector.setAdapter(adapter);

        view.findViewById(R.id.backtostart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavHostFragment.findNavController(RuleSet.this).navigate(R.id.action_RuleSets_to_StartPage);
                NavHostFragment.findNavController(RulesetFragment.this).popBackStack(R.id.StartPage, false);
            }
        });

        view.findViewById(R.id.fab_addentry).setOnClickListener(this::onNewRulesetWindowButtonClick);
        view.findViewById(R.id.fab_removeentry).setOnClickListener(this::onRemoveRulesetButtonClick);
    }

    private void onRemoveRulesetButtonClick(View view) {

    }

    public void onNewRulesetWindowButtonClick(View v) {
        dataStore.refresh(getContext());

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newRulesetView = inflater.inflate(R.layout.popupwindow_ruleset_create, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(newRulesetView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        newRulesetView.findViewById(R.id.createruleset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateRulesetButtonClick(v, popupWindow);
            }
        });
    }

    public void onCreateRulesetButtonClick(View v, PopupWindow p) {
        EditText e = (EditText) (p.getContentView().findViewById(R.id.ruleset_name_edittext));
        RulesetEntry newset = new RulesetEntry.RulesetEntryBuilder(e.getText().toString()).build();
        dataStore.setEntry(newset);
        refreshRulesetSpinner();
        dataStore.refresh(getContext());
        p.dismiss();
    }

    public void onModifyRuleButtonClick(View v, PopupWindow p, int rule) {
        EditText e = (EditText) (p.getContentView().findViewById(R.id.rule_new_edittext));
        Object r = dataStore.getSelected().getRuleByPosition(rule);
        if (r instanceof RulesetEntry.Rule) {
            ((RulesetEntry.Rule)r).setInfo(e.getText().toString());
        }
        dataStore.markModified(dataStore.getSelected().getName());
        dataStore.refresh(getContext());
        refreshRulesetDisplaySet();
        p.dismiss();
    }

    public void refreshRulesetSpinner() {
        Spinner s = this.getView().findViewById(R.id.spinner_ruleset_select);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, dataStore.getArrayOfEntryNames());
        s.setAdapter(adapter);
    }

    public void refreshRulesetDisplaySet() {
        RecyclerView rulesetDisplay = (RecyclerView)this.getView().findViewById(R.id.rulesetdisplay_recyclerview);
        RulesetAdapter radapter = new RulesetAdapter(this);
        rulesetDisplay.setAdapter(radapter);
    }

    private boolean onRuleLongPress(View view, int rule) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ruleModify = inflater.inflate(R.layout.popupwindow_rule_modify, null);
        Object r = dataStore.getSelected().getRuleByPosition(rule);
        if (r instanceof RulesetEntry.Rule) {
            ((TextView)ruleModify.findViewById(R.id.rulemodify_name_textview)).setText(((RulesetEntry.Rule)r).getName());
        }
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(ruleModify, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        ruleModify.findViewById(R.id.modifyrule_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onModifyRuleButtonClick(v, popupWindow, rule);
            }
        });
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = (String)parent.getItemAtPosition(position);
        dataStore.setSelected(name);
        refreshRulesetDisplaySet();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class RulesetAdapter extends RecyclerView.Adapter<ViewHolder> {
        RulesetFragment context;

        public RulesetAdapter(RulesetFragment context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ruleset_display_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (context.dataStore.getSelected().getName() == "blank" || context.dataStore.getSelected().getName() == "") {
                holder.getNameText().setText("");
                holder.getInfoText().setText("");
            } else {
                Object r = context.dataStore.getSelected().getRuleByPosition(position);
                if (r instanceof RulesetEntry.Rule) {
                    holder.getNameText().setText(((RulesetEntry.Rule)r).getName());
                    holder.getInfoText().setText(((RulesetEntry.Rule)r).getInfo());
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return context.onRuleLongPress(v, position);
                        }
                    });
                } else {
                    holder.getNameText().setText("Name");
                    holder.getInfoText().setText(r.toString());
                }
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView infoText;

        public ViewHolder(@NonNull View itemView) {
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
}
