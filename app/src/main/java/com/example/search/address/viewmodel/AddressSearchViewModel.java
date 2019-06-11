package com.example.search.address.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.search.address.repository.LocationModule;
import com.example.search.address.repository.endpoint.AddressEndpoint;
import com.example.search.address.repository.endpoint.GeocodeEndpoint;
import com.example.search.address.repository.model.Address;
import com.example.search.address.repository.model.AddressAutoCompleteResponse;
import com.example.search.address.repository.model.FormattedAddress;
import com.example.search.address.repository.model.GeoCode;
import com.example.search.address.repository.model.GeoCodingResponse;

import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddressSearchViewModel extends ViewModel {

    private AddressEndpoint addressEndpoint;
    private GeocodeEndpoint geocodeEndpoint;

    private Disposable searchDisposable;
    private Disposable locateDisposable;

    public MutableLiveData<List<Address>> addressSearchResults;
    public MutableLiveData<GeoCode> locatedGeoCode;
    public SingleLiveEvent<String> errorMessage;

    private AddressSearchViewModel(AddressEndpoint addressEndpoint, GeocodeEndpoint geocodeEndpoint) {
        this.addressEndpoint = addressEndpoint;
        this.geocodeEndpoint = geocodeEndpoint;
        addressSearchResults = new MutableLiveData<>();
        locatedGeoCode = new MutableLiveData<>();
        errorMessage = new SingleLiveEvent<>();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelAddressSearch();
        cancelCoordinatesLookup();
    }

    public void cancelAddressSearch() {
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }
    }

    public void clearSearchResults() {
        this.addressSearchResults.postValue(Collections.<Address>emptyList());
    }

    public void searchAddress(String query) {
        cancelAddressSearch();

        searchDisposable = addressEndpoint.searchAddress(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AddressAutoCompleteResponse>() {
                    @Override
                    public void accept(AddressAutoCompleteResponse response) {
                        if (!response.getResults().isEmpty()) {
                            addressSearchResults.postValue(response.getResults());
                        } else {
                            String error = "Search returned no results";
                            Log.d(this.getClass().getSimpleName(), error);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d(this.getClass().getSimpleName(), throwable.getMessage());
                    }
                });
    }

    public void locateCoordinates(Address address) {

        String addressQuery = address.getDescription();
        FormattedAddress formattedAddress = address.getFormattedAddress();

        if (!TextUtils.isEmpty(formattedAddress.getStreetAddress()) && !TextUtils.isEmpty(formattedAddress.getCityAddress())) {
            addressQuery = formattedAddress.getStreetAddress().concat(formattedAddress.getCityAddress());
        }

        locateCoordinates(addressQuery);
    }

    public void locateCoordinates(String addressQuery) {
        cancelCoordinatesLookup();

        locateDisposable = geocodeEndpoint.locateAddressCoordinates(addressQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GeoCodingResponse>() {
                    @Override
                    public void accept(GeoCodingResponse geoCodingResponse){
                        if (!geoCodingResponse.getResults().isEmpty() && GeoCode.isValid(geoCodingResponse.getResults().get(0))) {
                            locatedGeoCode.setValue(geoCodingResponse.getResults().get(0));

                        } else {
                            String error = "Couldn't locate coordinates";
                            errorMessage.setValue(error);
                            Log.d(this.getClass().getSimpleName(), error);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        errorMessage.setValue(throwable.getMessage());
                        Log.d(this.getClass().getSimpleName(), throwable.getMessage());
                    }
                });
    }

    private void cancelCoordinatesLookup() {
        if (locateDisposable != null && !locateDisposable.isDisposed()) {
            locateDisposable.dispose();
        }
    }

    public static class Factory implements ViewModelProvider.Factory {

        private LocationModule locationModule;

        public Factory(LocationModule locationModule) {
            this.locationModule = locationModule;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AddressSearchViewModel.class)) {
                return (T) new AddressSearchViewModel(locationModule.getAddressEndpoint(), locationModule.getGeocodeEndpoint());
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}
