package com.example.search.address.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.search.address.R;
import com.example.search.address.databinding.FragmentSuggestionsBinding;
import com.example.search.address.repository.model.Address;
import com.example.search.address.viewmodel.AddressSearchViewModel;

import java.util.List;


public class SuggestionsFragment extends Fragment implements SuggestionsAdapter.OnAddressSelectedListener {

    public SuggestionsFragment() {
    }

    private SuggestionsAdapter suggestionsAdapter;
    private SuggestionSelectedListener suggestionSelectedListener;

    static SuggestionsFragment newInstance() {
        return new SuggestionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentSuggestionsBinding fragmentSuggestionsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestions, container, false);

        suggestionsAdapter = new SuggestionsAdapter(this);

        fragmentSuggestionsBinding.recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(getActivity()));
        fragmentSuggestionsBinding.recyclerViewSuggestions.setAdapter(suggestionsAdapter);
        fragmentSuggestionsBinding.recyclerViewSuggestions.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        AddressSearchViewModel addressSearchViewModel = ViewModelProviders.of(getActivity()).get(AddressSearchViewModel.class);
        addressSearchViewModel.addressSearchResults.observe(getViewLifecycleOwner(), new Observer<List<Address>>() {
            @Override
            public void onChanged(List<Address> addresses) {
                if (!addresses.isEmpty()) {
                    suggestionsAdapter.setResultAddresses(addresses);
                    fragmentSuggestionsBinding.textViewHint.setVisibility(View.GONE);
                    fragmentSuggestionsBinding.recyclerViewSuggestions.setVisibility(View.VISIBLE);
                } else {
                    fragmentSuggestionsBinding.textViewHint.setVisibility(View.VISIBLE);
                    fragmentSuggestionsBinding.recyclerViewSuggestions.setVisibility(View.GONE);
                }
            }
        });
        return fragmentSuggestionsBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof SuggestionSelectedListener) {
            suggestionSelectedListener = (SuggestionSelectedListener) context;
        } else {
            throw new IllegalStateException("Parent class must implement SuggestionSelectedListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onAddressSelected(Address address) {
        suggestionSelectedListener.onSuggestionSelected(address);
    }

    interface SuggestionSelectedListener {
        void onSuggestionSelected(Address address);
    }
}
