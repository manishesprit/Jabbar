package com.jabbar.Ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jabbar.Bean.ContactBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;

import static com.jabbar.Ui.InputDataActivity.PERMISSION_CODE;


/**
 * Created by hardikjani on 6/13/17.
 */

public class FavoriteFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GetLocation.MyLocationListener {


    private View mView;
    MapView mMapView;
    private GoogleMap googleMap;
    private GetLocation getLocation;
    public ArrayList<ContactBean> contactFavoriteBeanArrayList;
    private LatLng currentLatLong;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void UpdateFavorite() {
        Toast.makeText(getContext(), "OnUpdate MyMapFragment", Toast.LENGTH_SHORT).show();
        setMarker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mymap, null);

        mMapView = (MapView) mView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        setMarker();
    }

    public void setMarker() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, PERMISSION_CODE);
        } else {
            googleMap.setOnMarkerClickListener(this);
            // update current location
            getLocation = new GetLocation(getContext(), this);
            getLocation.UpdateLocation();

        }
    }

    public void AddMarker() {

        contactFavoriteBeanArrayList = new UserBll(getContext()).geBuddiestList(true);
        for (ContactBean contactBean : contactFavoriteBeanArrayList) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(contactBean.location.split("_")[0]), Double.parseDouble(contactBean.location.split("_")[1])))
                    .title(contactBean.name)
                    .snippet(contactBean.status)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            marker.setTag(String.valueOf(contactBean.id));

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (googleMap != null) {
            Toast.makeText(getContext(), "User id " + marker.getTag(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void getLoc(boolean b) {
        currentLatLong = new LatLng(Double.parseDouble(Pref.getValue(getContext(), Config.PREF_LOCATION, "").split(",")[0]), Double.parseDouble(Pref.getValue(getContext(), Config.PREF_LOCATION, "").split(",")[0]));
        Marker MyLoc = googleMap.addMarker(new MarkerOptions().position(currentLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(MyLoc.getPosition()).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        AddMarker();
    }
}
