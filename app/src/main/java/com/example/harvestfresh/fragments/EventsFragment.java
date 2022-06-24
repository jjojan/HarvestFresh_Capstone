package com.example.harvestfresh.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harvestfresh.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class EventsFragment extends Fragment implements OnMapReadyCallback {

    private final static String KEY_LOCATION = "location";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private long UPDATE_INTERVAL = 60000;
    private long FASTEST_INTERVAL = 5000;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;

    private static final int PERMISSION_CODE = 101;
    String[] permissions_all={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    LocationManager locationManager;
    boolean isGpsLocation;
    Location loc;
    boolean isNetworklocation;
    ProgressDialog progressDialog;

    Location mCurrentLocation;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching location...");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.5072, 0.1276))
                .title("London"));
        LatLng latLng = new LatLng(51.5072, 0.1276);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
    }

//    private void getLocation() {
//        progressDialog.show();
//        if(Build.VERSION.SDK_INT>=23){
//            if(checkPermission()){
//                getDeviceLocation();
//            }
//            else{
//                requestPermission();
//            }
//        }
//        else{
//            getDeviceLocation();
//        }
//    }
//
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(getActivity(),permissions_all,PERMISSION_CODE);
//    }
//
//    private boolean checkPermission() {
//        for(int i=0;i<permissions_all.length;i++){
//            int result= ContextCompat.checkSelfPermission(getContext(),permissions_all[i]);
//            if(result== PackageManager.PERMISSION_GRANTED){
//                continue;
//            }
//            else {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void getDeviceLocation() {
//        locationManager=(LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
//        isGpsLocation=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        isNetworklocation=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if(!isGpsLocation && !isNetworklocation){
//            showSettingForLocation();
//            getLastlocation();
//        }
//        else{
//            getFinalLocation();
//        }
//    }
//
//    private void getLastlocation() {
//        if(locationManager!=null) {
//            try {
//                Criteria criteria = new Criteria();
//                String provider = locationManager.getBestProvider(criteria,false);
//                Location location=locationManager.getLastKnownLocation(provider);
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case PERMISSION_CODE:
//                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                    getFinalLocation();
//                }
//                else{
//                    Toast.makeText(getContext(), "Permission Failed", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    private void getFinalLocation() {
//        //one thing i missed in permission let's complete it
//        try{
//            if(isGpsLocation){
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60*1,10, (LocationListener) getContext());
//                if(locationManager!=null){
//                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    if(loc!=null){
//                        updateUi(loc);
//                    }
//                }
//            }
//            else if(isNetworklocation){
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*1,10, (LocationListener) getContext());
//                if(locationManager!=null){
//                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    if(loc!=null){
//                        updateUi(loc);
//                    }
//                }
//            }
//            else{
//                Toast.makeText(getContext(), "Can't Get Location", Toast.LENGTH_SHORT).show();
//            }
//
//        }catch (SecurityException e){
//            Toast.makeText(getContext(), "Can't Get Location", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    private void updateUi(Location loc) {
//        if(loc.getLatitude()==0 && loc.getLongitude()==0){
//            getDeviceLocation();
//        }
//        else{
//            progressDialog.dismiss();
//            LatLng latLng=new LatLng(loc.getLatitude(),loc.getLongitude());
//            map.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
//            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
//
//        }
//
//    }
//
//    private void showSettingForLocation() {
//        AlertDialog.Builder al=new AlertDialog.Builder(getContext());
//        al.setTitle("Location Not Enabled!");
//        al.setMessage("Enable Location ?");
//        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        });
//        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        al.show();
//    }
//
//
//    public void onLocationChanged(Location location) {
//        updateUi(location);
//    }
//
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    public void onProviderDisabled(String provider) {
//
//    }

}