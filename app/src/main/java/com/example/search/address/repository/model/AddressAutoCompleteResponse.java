package com.example.search.address.repository.model;


import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class AddressAutoCompleteResponse {

    @SerializedName("predictions")
    private List<Address> results = Collections.emptyList();

    public void setResults(List<Address> results) {
        this.results = results;
    }

    public List<Address> getResults() {
        return results;
    }

}
