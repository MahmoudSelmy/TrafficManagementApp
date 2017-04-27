package com.masstudio.selmy.tmc.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.masstudio.selmy.tmc.Activities.SegmentDetailsActivity;
import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TableMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<PolylineOptions> mPolylines = new ArrayList<>();

    public TableMapFragment() {
        // Required empty public constructor
    }
    public static TableMapFragment getInstance(){
        TableMapFragment myFragment=new TableMapFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_map, container, false);
        initLines();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapT);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setView();
        drawSegments();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickCoords) {
                int i = 0;
                for (PolylineOptions polyline : mPolylines) {
                    Boolean onPath = PolyUtil.isLocationOnPath(clickCoords,polyline.getPoints(),false,5);
                    if (onPath) {
                        // TODO : intent to page
                        Log.d("TableFragmentNew","i = " + i);
                        Intent intent = new Intent(getActivity(), SegmentDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("SID",i);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    }
                    i++;
                }
            }
        });
    }
    private void initLines() {
        List<List<LatLng>> linesPts = Constants.decodeSegments();
        int i =0;
        for (List<LatLng> pts : linesPts){
            PolylineOptions polyline = new PolylineOptions().width(10).geodesic(true);
            polyline.addAll(pts);
            polyline.color(Color.RED);
            if (i % 2 == 1)
                polyline.color(Color.BLUE);
            mPolylines.add(polyline);
            i++;
        }
    }
    private void drawSegments() {
        for (PolylineOptions poly : mPolylines){
            mMap.addPolyline(poly);
        }
    }

    private void setView() {
        LatLng pt = mPolylines.get(0).getPoints().get(0);
        CameraPosition mLocation = CameraPosition.builder()
                .target(pt)
                .zoom(16)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mLocation), 10, null);
    }


}
