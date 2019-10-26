package com.example.mapgo;


import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.media.CamcorderProfile.get;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGoogleMapsPreview extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;
    int index;
    protected MapView mMapView;
    TextView TxtStarTime,TxtEndTime;
private PolylineInfo polylineInfo;
private List<String> PolylineData;
    public FragmentGoogleMapsPreview() {
        // Required empty public constructor
    }


    public FragmentGoogleMapsPreview(PolylineInfo polylineInfo, List<String> PolylineData, int index) {
        this.polylineInfo = polylineInfo;
        this.PolylineData = PolylineData;
        this.index = index;
    }

    //fragment_fragment_google_maps_preview

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_google_maps_preview, parent, false);
        polylineInfo = polylineInfo;
        TxtStarTime = view.findViewById(R.id.TxtStarTime);
        TxtEndTime = view.findViewById(R.id.TxtEndTime);
        mMapView = (MapView) view.findViewById(R.id.fragment_embedded_map_view_mapview);
        mMapView.setId(index);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        TxtStarTime.setText("Departure Time: " +  polylineInfo.getUserStartTime());
        TxtEndTime.setText("Arrival Time: " + polylineInfo.getUserEndTime());

        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
//        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_embedded_map_view_mapview)).getMapAsync(this);
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                //Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        addPolylinesToMap();
    }


    private void addPolylinesToMap(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {




                PolylineOptions options = new PolylineOptions();
                options.color(Color.parseColor("#00b4dd"));
                for (int z = 0; z < PolylineData.size();) {
                    String latitude  = PolylineData.get(z);
                    z++;
                    String longitude  = PolylineData.get(z);
                    //LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                    options.add(new com.google.android.gms.maps.model.LatLng(Double.valueOf(latitude), Double.valueOf(longitude)));
                    z++;
                }


                if(gMap == null){

                }else{
                    Polyline line = gMap.addPolyline(options);
                    zoomRoute(line.getPoints());




                //line = myMap.addPolyline(options);


               // polyline.setColor(ContextCompat.getColor(GoogleMaps.this, R.color.colorPrimaryDark));

                }

            }
            });

    }


    public void zoomRoute(List<com.google.android.gms.maps.model.LatLng > lstLatLngRoute) {

        if (gMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        gMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }







}
