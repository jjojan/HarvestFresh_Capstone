package com.example.harvestfresh.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harvestfresh.R;
import com.example.harvestfresh.StoreFront;
import com.example.harvestfresh.StoreFrontAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EventsFragment extends Fragment {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "EventsFragment";
    private static final String CURRENT_LOCATION = "Current Location";
    private static final String ERROR_MESSAGE = "An error occured";
    private static final String MILES_AWAY = " Miles Away";
    private static final int ZOOM_SIZE = 12;
    private static final int QUERY_SIZE = 20;

    private GoogleMap mMap;
    private GoogleMap markerMap;
    private StoreFrontAdapter fragmentAdapter;
    private List<StoreFront> allStores;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            markerMap = googleMap;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allStores = new ArrayList<>();
        fragmentAdapter = new StoreFrontAdapter(getContext(), allStores);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();

        SupportMapFragment mapFragment = new SupportMapFragment();
        getMapFragment(mapFragment);

        Places.initialize(getContext(), getString(R.string.google_maps_api_key));

        AutocompleteSupportFragment autocompleteFragment = new AutocompleteSupportFragment();
        autocompleteFragment = setCompleteFragment(autocompleteFragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                String placeName = place.getName();
                LatLng searchLatLng = place.getLatLng();
                goPlace(searchLatLng, placeName);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, ERROR_MESSAGE + status);
            }
        });
    }

    private AutocompleteSupportFragment setCompleteFragment(AutocompleteSupportFragment autocompleteFragment) {
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager()
                        .findFragmentById(R.id.fragmentAutoComplete);
        autocompleteFragment.setPlaceFields
                (Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        return autocompleteFragment;
    }

    private SupportMapFragment getMapFragment(SupportMapFragment mapFragment) {
         mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        return mapFragment;
    }

    private void goPlace(LatLng searchLatLng, String placeName) {
        MarkerOptions markerOptions = new MarkerOptions().position(searchLatLng).title(placeName);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(searchLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 12));
        mMap.addMarker(markerOptions);
    }

    private void placeMarkers(ParseGeoPoint userLocation) {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(QUERY_SIZE);

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    return;
                }
                for (StoreFront store : stores) {
                    LatLng newLocation = new LatLng(store.getLocation().getLatitude(),
                            store.getLocation().getLongitude());
                    ParseGeoPoint storeMarker = new ParseGeoPoint(newLocation.latitude,
                            newLocation.longitude);
                    double milesDistance = Math.round(userLocation.distanceInMilesTo(storeMarker));

                    MarkerOptions newMarker = new MarkerOptions()
                            .position(newLocation)
                            .title(store.getName())
                            .snippet(Double.toString(milesDistance) + MILES_AWAY)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapsicon));

                    markerMap.addMarker(newMarker);
                }
                allStores.addAll(stores);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                updateCurrentLocation();
                Toast.makeText(getContext(), currentLocation.getLatitude() + ", " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(callback);
            }
        });
    }

    private void updateCurrentLocation() {
        LatLng latLng = new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude());
                
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(CURRENT_LOCATION);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_SIZE));
        mMap.addMarker(markerOptions);
        ParseGeoPoint userLocation = new ParseGeoPoint(latLng.latitude, latLng.longitude);

        placeMarkers(userLocation);
    }
}

