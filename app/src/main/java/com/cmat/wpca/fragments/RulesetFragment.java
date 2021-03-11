package com.cmat.wpca.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cmat.wpca.R;
import com.cmat.wpca.data.DataStore;
import com.cmat.wpca.data.RulesetEntry;
import com.cmat.wpca.data.json.JSONManager;

public class RulesetFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    DataStore<RulesetEntry> dataStore = new DataStore<>();

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


        if (!dataStore.isLoaded()) {
            dataStore.load(getContext(), new JSONManager.Builder<RulesetEntry>("rulesets", new RulesetEntry()).setStorageType(JSONManager.StorageType.INTERNAL));
        }

        Spinner rulesetSelector = (Spinner)view.findViewById(R.id.spinner_ruleset_select);
        rulesetSelector.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, dataStore.getDataAsArray());
        rulesetSelector.setAdapter(adapter);

        view.findViewById(R.id.backtostart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavHostFragment.findNavController(RuleSet.this).navigate(R.id.action_RuleSets_to_StartPage);
                NavHostFragment.findNavController(RulesetFragment.this).popBackStack(R.id.StartPage, false);
            }
        });

        view.findViewById(R.id.fab_addentry).setOnClickListener(this::onNewRulesetWindowButtonClick);
    }

    public void onNewRulesetWindowButtonClick(View v) {
        if (dataStore.isLoaded())
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
        if (dataStore.isLoaded())
            dataStore.refresh(getContext());
        EditText e = (EditText) (p.getContentView().findViewById(R.id.ruleset_name_edittext));
        RulesetEntry newset = new RulesetEntry.RulesetEntryBuilder(e.getText().toString()).build();
        dataStore.addEntry(newset);
        dataStore.write(getContext());
        refreshRulesetSpinner();
        p.dismiss();
    }

    public void refreshRulesetSpinner() {
        Spinner s = this.getView().findViewById(R.id.spinner_ruleset_select);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, dataStore.getDataAsArray());
        s.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
