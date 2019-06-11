package com.example.search.address.repository.endpoint;

import com.example.search.address.repository.model.AddressAutoCompleteResponse;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AddressEndpoint {

    @POST("maps/api/place/autocomplete/json?&types=address")
    Single<AddressAutoCompleteResponse> searchAddress(@Query("input") String searchQuery);

}
