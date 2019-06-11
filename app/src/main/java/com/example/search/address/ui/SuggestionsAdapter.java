package com.example.search.address.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.search.address.R;
import com.example.search.address.databinding.ItemSuggestionBinding;
import com.example.search.address.databinding.ItemSuggestionsEmptyHeaderBinding;
import com.example.search.address.repository.model.Address;

import java.util.List;


public class SuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnAddressSelectedListener onAddressSelectedListener;
    private List<Address> resultAddresses;

    public SuggestionsAdapter(OnAddressSelectedListener onAddressSelectedListener) {
        this.onAddressSelectedListener = onAddressSelectedListener;
    }

    public void setResultAddresses(List<Address> resultAddresses) {
        this.resultAddresses = resultAddresses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.ADDRESS.ordinal();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemType itemType = ItemType.values()[viewType];

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (itemType == ItemType.HEADER) {
            ItemSuggestionsEmptyHeaderBinding emptyHeaderBinding = DataBindingUtil.inflate(inflater, R.layout.item_suggestions_empty_header, parent, false);
            return new HeaderViewHolder(emptyHeaderBinding);

        } else {
            ItemSuggestionBinding suggestionBinding = DataBindingUtil.inflate(inflater, R.layout.item_suggestion, parent, false);
            return new AddressViewHolder(suggestionBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddressViewHolder) {
            final Address address = resultAddresses.get(position - 1);

            if (address.getFormattedAddress() != null && !TextUtils.isEmpty(address.getFormattedAddress().getStreetAddress())) {
                ((AddressViewHolder) holder).suggestionBinding.setHeader(address.getFormattedAddress().getStreetAddress());
                ((AddressViewHolder) holder).suggestionBinding.setSubHeader(address.getFormattedAddress().getCityAddress());

            } else {
                ((AddressViewHolder) holder).suggestionBinding.setHeader(address.getDescription());
                ((AddressViewHolder) holder).suggestionBinding.setSubHeader("");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAddressSelectedListener.onAddressSelected(address);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 1;
        if (resultAddresses != null) {
            count = count + resultAddresses.size();
        }
        return count;
    }

    private enum ItemType {
        HEADER,
        ADDRESS
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(ItemSuggestionsEmptyHeaderBinding emptyHeaderBinding) {
            super(emptyHeaderBinding.getRoot());
        }
    }

    private class AddressViewHolder extends RecyclerView.ViewHolder {
        ItemSuggestionBinding suggestionBinding;
        AddressViewHolder(ItemSuggestionBinding suggestionBinding) {
            super(suggestionBinding.getRoot());
            this.suggestionBinding = suggestionBinding;
        }
    }

    interface OnAddressSelectedListener {
        void onAddressSelected(Address address);
    }

}
