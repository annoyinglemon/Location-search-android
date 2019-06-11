package com.example.search.address.repository.endpoint;

import com.example.search.address.repository.model.GeoCodingResponse;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeocodeEndpoint {

    @POST("maps/api/geocode/json?")
    Single<GeoCodingResponse> locateAddressCoordinates(@Query("address") String address);

}
