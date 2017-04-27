package com.masstudio.selmy.tmc.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class TableMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_map);
        path = getIntent().getExtras().getString("poly");
        Log.d("PATHMAPPATH",path);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("PATHMAPPATH","R");
        List<LatLng> list = Constants.decodeSegment(path);
        PolylineOptions polylineOptions = new PolylineOptions().addAll(list).color(Color.RED).width(10);
        mMap.addPolyline(polylineOptions);
        CameraPosition mLocation = CameraPosition.builder()
                .target(list.get(0))
                .zoom(17)
                .bearing(0)
                .build();
        Log.d("PATHMAPPATH",list.get(0).toString());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mLocation), 10, null);
    }
}
