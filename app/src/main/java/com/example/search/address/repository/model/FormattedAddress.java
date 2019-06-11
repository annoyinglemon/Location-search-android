package com.example.search.address.repository.model;

import com.google.gson.annotations.SerializedName;

public class FormattedAddress {

    @SerializedName("main_text")
    private String streetAddress;

    @SerializedName("secondary_text")
    private String cityAddress;


    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }

}
