package com.example.search.address.repository.model;

import com.google.gson.annotations.SerializedName;

public class GeoCode {

    @SerializedName("formatted_address")
    private String description = "";

    @SerializedName("geometry")
    private GeoLocation geoLocation;


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public static boolean isValid(GeoCode geoCode) {
        return geoCode.geoLocation.getCoordinates() != null && geoCode.geoLocation.getCoordinates().getLatitude() != 0d
                && geoCode.geoLocation.getCoordinates().getLongitude() != 0d && geoCode.getDescription() != null && !geoCode.getDescription().isEmpty();
    }
}
