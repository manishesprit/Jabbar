package com.jabbar.Ui;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.jabbar.API.GetFavoriteAPI;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.FavoriteListBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import static com.jabbar.Ui.InputDataActivity.PERMISSION_CODE;
import static com.jabbar.Utils.Config.TAG_GET_FAVORITE;


/**
 * Created by hardikjani on 6/13/17.
 */

public class FavoriteFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GetLocation.MyLocationListener {


    private View mView;
    MapView mMapView;
    private GoogleMap googleMap;
    private GetLocation getLocation;
    public ArrayList<ContactsBean> contactFavoriteBeanArrayList;
    private LatLng currentLatLong;
    private ProgressDialog progressDialog;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");


    }

    public void UpdateFavorite(boolean onlyDbUpdate) {
        if (onlyDbUpdate) {
            AddMarker();
        } else {
            Toast.makeText(getContext(), "OnUpdate MyMapFragment", Toast.LENGTH_SHORT).show();
            setMarker();
        }
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

//        setMarker();
        AddMarker();
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
        googleMap.clear();
        contactFavoriteBeanArrayList = new UserBll(getContext()).geBuddiestList(true);
        if (contactFavoriteBeanArrayList != null && contactFavoriteBeanArrayList.size() > 0) {
            for (int i = 0; i < contactFavoriteBeanArrayList.size(); i++) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(contactFavoriteBeanArrayList.get(i).location.split(",")[0]), Double.parseDouble(contactFavoriteBeanArrayList.get(i).location.split(",")[1])))
                        .title(contactFavoriteBeanArrayList.get(i).name)
                        .snippet(contactFavoriteBeanArrayList.get(i).status)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                marker.setTag(contactFavoriteBeanArrayList.get(i));

            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(Utils.getLocationFromString(contactFavoriteBeanArrayList.get(contactFavoriteBeanArrayList.size() - 1).location)).zoom(10).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (googleMap != null) {
            ContactsBean contactsBean = (ContactsBean) marker.getTag();
            if (contactsBean != null) {
                Toast.makeText(getContext(), "Hi! " + contactsBean.name, Toast.LENGTH_SHORT).show();
            }

        }
        return false;
    }


    @Override
    public void getLoc(boolean b) {


//        currentLatLong = new LatLng(Double.parseDouble(Pref.getValue(getContext(), Config.PREF_LOCATION, "").split(",")[0]), Double.parseDouble(Pref.getValue(getContext(), Config.PREF_LOCATION, "").split(",")[1]));
//        Marker MyLoc = googleMap.addMarker(new MarkerOptions().position(currentLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));
//
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(MyLoc.getPosition()).zoom(10).build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (Utils.isOnline(getContext())) {
            progressDialog.show();
            new GetFavoriteAPI(getContext(), new ResponseListener() {
                @Override
                public void onResponce(String tag, int result, Object obj) {
                    progressDialog.dismiss();
                    if (tag.equalsIgnoreCase(TAG_GET_FAVORITE) && result == 0) {
                        FavoriteListBean favoriteListBean = (FavoriteListBean) obj;
                        if (favoriteListBean != null && favoriteListBean.favorite_list != null && favoriteListBean.favorite_list.size() > 0) {
                            new UserBll(getContext()).UpdateFavoriteContact(favoriteListBean.favorite_list);
                            AddMarker();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
