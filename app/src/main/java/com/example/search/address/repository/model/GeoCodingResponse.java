package com.example.search.address.repository.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class GeoCodingResponse {

    @SerializedName("results")
    private List<GeoCode> results = Collections.emptyList();

    public void setResults(List<GeoCode> results) {
        this.results = results;
    }

    public List<GeoCode> getResults() {
        return results;
    }
}
