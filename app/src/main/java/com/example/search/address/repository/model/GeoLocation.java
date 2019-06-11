package com.example.search.address.repository.model;

import com.google.gson.annotations.SerializedName;

public class GeoLocation {

    @SerializedName("location")
    private Coordinates coordinates;

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public class Coordinates {

        @SerializedName("lat")
        private double latitude = 0d;

        @SerializedName("lng")
        private double longitude = 0d;


        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

}
