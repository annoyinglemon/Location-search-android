package com.example.search.address.ui;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.search.address.R;
import com.example.search.address.databinding.ActivityAddressSearchBinding;
import com.example.search.address.repository.LocationModule;
import com.example.search.address.repository.model.Address;
import com.example.search.address.repository.model.GeoCode;
import com.example.search.address.viewmodel.AddressSearchViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


public class AddressSearchActivity extends FragmentActivity implements OnMapReadyCallback, SuggestionsFragment.SuggestionSelectedListener {

    private static final String BACK_STACK_NAME = "suggestions_back_stack";
    private static final String DEFAULT_ADDRESS = "455 Dovercourt Rd Suite 101, Toronto, ON M6H 2W3";

    private static final int USER_INPUT_DEBOUNCE_TIMEOUT = 500;
    private static final int SEARCH_TEXT_LENGTH = 3;

    private static final double DEFAULT_LATITUDE = 43.6543498;
    private static final double DEFAULT_LONGITUDE = -79.426316;

    private static final float DEFAULT_ZOOM_LEVEL = 19f;

    private ActivityAddressSearchBinding activityAddressSearchBinding;
    private AddressSearchViewModel addressSearchViewModel;
    private Disposable searchQueryDisposable;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private Polygon currentStar;

    private PublishSubject<String> searchQuerySubject =  PublishSubject.create();

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable editable) {}

        @Override
        public void onTextChanged(CharSequence searchQuery, int start, int before, int count) {
            String query = searchQuery.toString();
            if (query.length() >= SEARCH_TEXT_LENGTH) {
                searchQuerySubject.onNext(query);

            } else {
                addressSearchViewModel.cancelAddressSearch();
                addressSearchViewModel.clearSearchResults();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAddressSearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_address_search);
        activityAddressSearchBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initializeGoogleMapFragment();
        initializeViewModel();

        addressSearchViewModel.locatedGeoCode.observe(this, new Observer<GeoCode>() {
            @Override
            public void onChanged(GeoCode geoCode) {
                if (googleMap != null) {
                    focusMapToGeoCode(geoCode);
                }
            }
        });

        addressSearchViewModel.errorMessage.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(AddressSearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    addressSearchViewModel.cancelAddressSearch();

                } else if (activityAddressSearchBinding.editTextAddressQuery.
                        getText().toString().length() >= SEARCH_TEXT_LENGTH) {
                    searchQuerySubject.onNext(activityAddressSearchBinding.editTextAddressQuery.getText().toString());
                }
            }
        });

        activityAddressSearchBinding.editTextAddressQuery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in_fragment, 0, 0, R.anim.fade_out_fragment)
                            .add(R.id.frameLayout_mainContainer, SuggestionsFragment.newInstance())
                            .addToBackStack(BACK_STACK_NAME).commit();
                }
            }
        });

        searchQueryDisposable = searchQuerySubject
                .debounce(USER_INPUT_DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String searchQuery) {
                        addressSearchViewModel.searchAddress(searchQuery);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (!TextUtils.isEmpty(throwable.getMessage())) {
                            Log.d(this.getClass().getSimpleName(), throwable.getMessage());
                        }
                    }
                });

        activityAddressSearchBinding.editTextAddressQuery.addTextChangedListener(searchTextWatcher);

        activityAddressSearchBinding.editTextAddressQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && textView.getText().length() >= SEARCH_TEXT_LENGTH) {
                    hideOnScreenKeyboard();
                    addressSearchViewModel.locateCoordinates(textView.getText().toString());
                    popSuggestionsFragment();
                    return true;

                } else {
                    Toast.makeText(AddressSearchActivity.this, "Enter at least " + SEARCH_TEXT_LENGTH + " characters", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapStyleOptions noLabelMapStyle = new MapStyleOptions(getResources().getString(R.string.map_style_no_label));
        this.googleMap.setMapStyle(noLabelMapStyle);

        if (addressSearchViewModel != null && addressSearchViewModel.locatedGeoCode.getValue() != null) {
            GeoCode geoCode = addressSearchViewModel.locatedGeoCode.getValue();
            focusMapToGeoCode(geoCode);
        } else {
            focusMapToCoordinates(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_ADDRESS);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popSuggestionsFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (searchQueryDisposable != null && !searchQueryDisposable.isDisposed()) {
            searchQueryDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onSuggestionSelected(Address address) {
        hideOnScreenKeyboard();

        activityAddressSearchBinding.editTextAddressQuery.removeTextChangedListener(searchTextWatcher);
        activityAddressSearchBinding.editTextAddressQuery.setText(address.getDescription());
        activityAddressSearchBinding.editTextAddressQuery.addTextChangedListener(searchTextWatcher);

        addressSearchViewModel.locateCoordinates(address);
        popSuggestionsFragment();
    }

    private void initializeGoogleMapFragment() {
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_mainContainer, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }

    private void initializeViewModel() {
        LocationModule locationModule = new LocationModule();
        AddressSearchViewModel.Factory vmFactory = new AddressSearchViewModel.Factory(locationModule);
        addressSearchViewModel = ViewModelProviders.of(this, vmFactory).get(AddressSearchViewModel.class);
    }

    private void popSuggestionsFragment() {
        getSupportFragmentManager().popBackStack(BACK_STACK_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activityAddressSearchBinding.editTextAddressQuery.clearFocus();
    }

    private void focusMapToGeoCode(GeoCode geoCode) {
        double latitude = geoCode.getGeoLocation().getCoordinates().getLatitude();
        double longitude = geoCode.getGeoLocation().getCoordinates().getLongitude();

        focusMapToCoordinates(latitude, longitude, geoCode.getDescription());
    }

    private void focusMapToCoordinates(double latitude, double longitude, String description) {
        LatLng coordinates = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(MarkerUtils.createDescriptorFromVector(this, R.drawable.custom_marker))
                .position(coordinates)
                .title(description);


        if (currentMarker != null && currentMarker.isVisible()) {
            currentMarker.remove();
        }
        currentMarker = googleMap.addMarker(markerOptions);

        if (currentStar != null && currentStar.isVisible()) {
            currentStar.remove();
        }
        currentStar = googleMap.addPolygon(MarkerUtils.createStarForCoordinates(this, coordinates));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM_LEVEL));
    }

    private void hideOnScreenKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(activityAddressSearchBinding.editTextAddressQuery.getWindowToken(), 0);
        }
    }

}
