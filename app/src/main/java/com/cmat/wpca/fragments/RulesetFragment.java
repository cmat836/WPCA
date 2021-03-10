package com.cmat.wpca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
            dataStore.load(getContext(), new JSONManager.Builder<RulesetEntry>("rulesets").setStorageType(JSONManager.StorageType.INTERNAL));
        }

        dataStore.makeBumFile(getContext());

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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
