package com.example.search.address.repository.model;

import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("description")
    private String description;

    @SerializedName("structured_formatting")
    private FormattedAddress formattedAddress;


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setFormattedAddress(FormattedAddress formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public FormattedAddress getFormattedAddress() {
        return formattedAddress;
    }

}
