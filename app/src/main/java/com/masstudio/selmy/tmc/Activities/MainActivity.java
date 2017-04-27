package com.masstudio.selmy.tmc.Activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Services.SurveyListenerService;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.masstudio.selmy.tmc.Utils.PointMap;
import com.masstudio.selmy.tmc.Utils.Signal;
import com.masstudio.selmy.tmc.Utils.TModels;
import com.masstudio.selmy.tmc.retrofit.ApiClient;
import com.masstudio.selmy.tmc.retrofit.ApiInterface;
import com.masstudio.selmy.tmc.retrofit.Element;
import com.masstudio.selmy.tmc.retrofit.Elements;
import com.masstudio.selmy.tmc.retrofit.MatrixResponse;
import com.masstudio.selmy.tmc.retrofit.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.masstudio.selmy.tmc.R.id.signal;
import static com.masstudio.selmy.tmc.Utils.Constants.findNearestPoint;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private static final int PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng origin = null, destinations = null;
    private MatrixResponse matrixResponse = null;
    private RelativeLayout relativeLayout;
    private LinearLayout signalsLayout;
    private View redV, yellowV, greenV;
    private TextView addressV, distanceV, timeV, signalCounterV;
    private CardView cardView;
    private List<PointMap> ptsAfricaMakram = new ArrayList<>();
    private List<PointMap> ptsMain;
    private DatabaseReference databaseReference;
    private List<Signal> signals;
    private Handler handler;
    private String nearestIntersection = "";
    private double distanceToNearest;
    private int nearestPoint;
    private Boolean reverse = false;
    private Boolean threadLocker = true;
    private Boolean selectDestination = false;
    private Boolean notifyDestination = false;

    private CircleButton button;
    private ImageView imageView;
    private int makramCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        initViews();
        askForProvider();
        //setPoints();
        ptsAfricaMakram = Constants.getAfricaMakram();
        //TODO : set ignals and checks
        ptsMain = Constants.setSignals();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place != null) {
                LatLng latLng = place.getLatLng();
                destinations = latLng;
                // TODO : Remove
                secondCounter();
                if (origin != null) {
                    getEstimations(latTOstring(origin), latTOstring(destinations), String.valueOf(System.currentTimeMillis()), TModels.Pessimistic, Constants.API_KEY_DIRECTION_MATRIX);
                    selectDestination = true;
                    //  40.6655101,-73.8918896999999
                    //  40.6905615,-73.997659
                    Log.d("Location* : ", latTOstring(origin) + latTOstring(destinations));

                }
            } else {
                Log.d("Picker", "");
                Log.d("Location*: ", "orgin null");
                //PLACE IS NULL
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // get Referenc to myMap + getCurrentLocation + setBoundsTo El-Nahas + onMapLoaded > mark all signals
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        getCurrentLocation();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (origin != null) {
                    moveToMyLocation();
                }
                // ToDO: set signals on Map Loaded
                markSignals();
                //getAfricaMakram();
            }

        });
    }

    // Location callbacks
    @Override
    public void onLocationChanged(Location location) {
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        moveToMyLocation();
        Log.d("Location***", origin.toString());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
        if (relativeLayout.getVisibility() == View.VISIBLE) {
            relativeLayout.setVisibility(View.GONE);
        }
        getCurrentLocation();
        Log.d("Location*", "Enabled");
        //Toast.makeText(this, "GPS ENABLED", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderDisabled(String s) {
        //askForProvider();
        //alert.show();
        if (relativeLayout.getVisibility() == View.GONE) {
            relativeLayout.setVisibility(View.VISIBLE);
        }
        if (origin != null) {
            Log.d("Location*", "not null orgin" + origin.latitude + origin.longitude);
        }
        Log.d("Location*", "Disabled");

    }

    // Most Imp
    private void moveToMyLocation() {

        Intent intent = new Intent(this, SurveyListenerService.class);
        Bundle bundle = new Bundle();
        // TODO :
        bundle.putParcelable("LOC",origin);
        intent.putExtras(bundle);
        startService(intent);

        notifyDestination();
        mMap.clear();
        /*
        PolylineOptions polygonOptions = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        polygonOptions.addAll(PolyUtil.decode("_{~vDmcr~DdB~Cl@Un@hCvHsC`KsDrHmCn@zC~@jCnApC|BxChBtCjBxBVb@pB`CjA|AnAlBp@tAb@hAt@pChEvQVKCOcCcKgAiE_@cB?[DURQvDuAxEcBZEd@N~AnBjFxI|Wb^lBfCtC~DvAlAj@z@\\[dI~KnHzLdNpRpEjGXf@^l@v@z@vAlAn@Zp@Vx@Tr@NbABbABn@CdAM`Cs@hBaAjBkAn@k@jGwFbAOrCi@TCX?VTXZr@xA`EbKNJ|AlEdTfi@`BnD~C`Hp@`BxEbKjKxTf@fArFzLzAxC`BpDpAdCdBpCjE|IhIfQbClF^`ApA`EtHzObDfHvAjDhF|K`ApBpFzKtCxFbB`DhHnOxFrMdEjJjAvCfB~CvAvB|BpCz@jAzIjMfB~ClEhKjDnMtJj_@dB~HjCvKfA`EdAzExDrOpA|Dl@zARVr@pAfBpBdGbHfAjAv@t@RXX^HTpGbGlGvGd@n@vBbC~ElDhBnAlEhDdHfFDTANORw@z@m@RiF|AYLuDjAsC`A`ArD~EfSD\\?HAZYJaIfCaC|@{Af@Pp@f@rB"));
        List<LatLng> list = PolyUtil.decode("_{~vDmcr~DdB~Cl@Un@hCvHsC`KsDrHmCn@zC~@jCnApC|BxChBtCjBxBVb@pB`CjA|AnAlBp@tAb@hAt@pChEvQVKCOcCcKgAiE_@cB?[DURQvDuAxEcBZEd@N~AnBjFxI|Wb^lBfCtC~DvAlAj@z@\\[dI~KnHzLdNpRpEjGXf@^l@v@z@vAlAn@Zp@Vx@Tr@NbABbABn@CdAM`Cs@hBaAjBkAn@k@jGwFbAOrCi@TCX?VTXZr@xA`EbKNJ|AlEdTfi@`BnD~C`Hp@`BxEbKjKxTf@fArFzLzAxC`BpDpAdCdBpCjE|IhIfQbClF^`ApA`EtHzObDfHvAjDhF|K`ApBpFzKtCxFbB`DhHnOxFrMdEjJjAvCfB~CvAvB|BpCz@jAzIjMfB~ClEhKjDnMtJj_@dB~HjCvKfA`EdAzExDrOpA|Dl@zARVr@pAfBpBdGbHfAjAv@t@RXX^HTpGbGlGvGd@n@vBbC~ElDhBnAlEhDdHfFDTANORw@z@m@RiF|AYLuDjAsC`A`ArD~EfSD\\?HAZYJaIfCaC|@{Af@Pp@f@rB");
        Log.d("S*S*", "" + list.size());
        mMap.addPolyline(polygonOptions);
        */
        markSignals();
        //LatLng pseudo = ptsAfricaMakram.get(makramCount).latLng;
        PointMap pointMap = new PointMap();
        pointMap.latLng = origin;
        nearestTo(pointMap);
        CameraPosition mLocation = CameraPosition.builder()
                .zoom(16)
                .bearing(0)
                .target(origin)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mLocation), 10, null);
        mMap.addMarker(new MarkerOptions().position(origin).title("MyLocation"));
        if (threadLocker) {
            threadLocker = false;
            MyThread myThread = new MyThread();
            myThread.start();
        }
    }

    private void notifyDestination() {
        if (!selectDestination)
            return;
        double x = Constants.distanceTo(origin, destinations);
        if ( x < 2 && !notifyDestination){
            notifyDestination = true;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Notification.Builder n  = new Notification.Builder(this)
                    .setContentTitle("TMC")
                    .setContentText("turn off gps please")
                    .setSmallIcon(R.drawable.ic_drive_eta_white_24dp)
                    .setContentIntent(PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setAutoCancel(true);
            notificationManager.notify(1, n.build());
        }
    }

    // checkPermissions of Fine|Coarse if they are available or not
    //  getLastKnownLocation"NETWORK" +  askForUpdates"Network + GPS"
    // SnackBar Error if location of lastKnown is Null
    private Boolean getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location*", "method0");
            return false;
        }
        Log.d("Location*", "method1");
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        if (location != null) {
            Log.d("Location*", "method2");
            origin = new LatLng(location.getLatitude(), location.getLongitude());
            if (origin != null) {
                moveToMyLocation();
            }
        } else {
            Log.d("Location*", "method4");
            /*
            Snackbar.make(findViewById(android.R.id.content), "Detection Error", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .show();
                    */
            return false;
        }
        return true;
    }
    private void nearestTo(PointMap P) {
        int nearest = Constants.findNearestPoint(P, ptsMain);
        float distance = Constants.distanceTo(P.latLng, ptsMain.get(nearest).latLng);

        if (!selectDestination) {
            button.setVisibility(View.GONE);
            Log.d("TestCard", "state 1  distance= " + distance + " signal = "+ ptsMain.get(nearest).name);
            return;
        }

        if (distance > 200 ) {
            signalsLayout.setVisibility(View.GONE);
            distanceToNearest = distance;
            //imageView.setVisibility(View.GONE);
            //signalCounterV.setVisibility(View.GONE);
            Log.d("TestCard", "state 2  distance= " + distance + " signal = "+ ptsMain.get(nearest).name);
            return;
        }
        if (!ptsMain.get(nearest).intersection.equals(nearestIntersection)) {
            //addressV.setTex`t("Address : " + ptsMain.get(nearest).name);
            //distanceV.setText("Distance : " + distance + " meter");
            distanceToNearest = distance;
            nearestPoint = nearest;
            nearestIntersection = ptsMain.get(nearest).intersection;

            signalsLayout.setVisibility(View.VISIBLE);
            signalCounterV.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);

            //imageView.setVisibility(View.VISIBLE);

            Log.d("TestCard", "state 3  distance= " + distance + " signal = "+ ptsMain.get(nearest).name);
            Log.d("CHECK_CARD1", "inter = " + nearestIntersection + " Distance = " + distanceToNearest);
            return;
        }
        if (distance < distanceToNearest) {
            //distanceV.setText("Distance : " + distance + " meter");
            Log.d("TestCard", "state 4  distance= " +distanceToNearest+" | "+distance + " signal = "+ ptsMain.get(nearest).name);
            distanceToNearest = distance;
            signalsLayout.setVisibility(View.VISIBLE);
            signalCounterV.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            //imageView.setVisibility(View.VISIBLE);

            Log.d("CHECK_CARD2", "inter = " + nearestIntersection + " Distance = " + distanceToNearest);
            Log.d("TestCard", "state 4  distance= " + distance + " signal = "+ ptsMain.get(nearest).name);
            return;
        }
        //cardView.setVisibility(View.VISIBLE);
        if (ptsMain.get(nearest).intersection.equals(nearestIntersection) && (distance > distanceToNearest)) {
            //cardView.setVisibility(View.GONE);

            signalsLayout.setVisibility(View.GONE);
            signalCounterV.setVisibility(View.GONE);
            Log.d("TestCard", "state 5  distance= " + distance + " signal = "+ ptsMain.get(nearest).name);
        }
        Log.d("CHECK_CARD4", "inter = " + nearestIntersection + " Distance = " + distanceToNearest + " distance Real = " + distance);
        /*
        CircleOptions circleOptions = new CircleOptions().center(P.latLng).radius(1);
        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);
        mMap.addCircle(circleOptions);
        */
        markSignals();
        /*
        if (AFMACount < ptsAfricaMakram.size())
            secondCounter(false,false);
            */
    }
    // Estimations
    private void getEstimations(String origin, String destination, String time, String Tmodel, String key) {

        Map<String, String> query = new HashMap<>();
        query.put("origins", origin);
        query.put("destinations", destination);
        query.put("departure_time", time);
        query.put("traffic_model", Tmodel);
        query.put("key", key);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MatrixResponse> call = apiService.getEstimations(query);
        call.enqueue(new Callback<MatrixResponse>() {
            @Override
            public void onResponse(Call<MatrixResponse> call, Response<MatrixResponse> response) {
                if (response.isSuccessful()) {
                    matrixResponse = response.body();
                    //placeView.setText(matrixResponse.getDestinationAddresses().get(0));
                    //Log.d("Response : ",matrixResponse.getRows().get(0).getElements().get(0).getDurationInTraffic().getText());
                    useEstimations();
                    Log.d("Retrofit*", "FAILED1");

                } else {
                    //  TODO : Snacksbar
                    Snackbar.make(findViewById(android.R.id.content), "Detection Error", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE)
                            .show();
                    Log.d("Retrofit*", "FAILED2");

                }
            }

            @Override
            public void onFailure(Call<MatrixResponse> call, Throwable t) {
                Log.d("Retrofit*", "FAILED");
            }
        });
    }
    private void useEstimations() {
        List<Elements> rows = matrixResponse.getRows();
        List<Element> row = rows.get(0).getElements();
        Element element = row.get(0);
        Stats distance = element.getDistance();
        Stats duration = element.getDuration();
        Stats traffic = element.getDurationInTraffic();
        //showAlert(msg);
        addressV.setText("Address : " + matrixResponse.getDestinationAddresses().get(0));
        distanceV.setText("Distance : " + distance.getText());
        timeV.setText("Time : " + traffic.getText());
        cardView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        Log.d("Retrofit*", matrixResponse.getDestinationAddresses().get(0));
    }
    //init
    private void initViews() {
        button = (CircleButton) findViewById(R.id.circle_Button);
        imageView = (ImageView) findViewById(R.id.signalImage);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        relativeLayout = (RelativeLayout) findViewById(R.id.layoutGone);
        signalsLayout = (LinearLayout) findViewById(signal);
        redV = findViewById(R.id.view_red);
        //yellowV = findViewById(R.id.view_yellow);
        //greenV = findViewById(R.id.view_green);
        addressV = (TextView) findViewById(R.id.address);
        distanceV = (TextView) findViewById(R.id.distance);
        signalCounterV = (TextView) findViewById(R.id.text_counter);
        timeV = (TextView) findViewById(R.id.time);
        cardView = (CardView) findViewById(R.id.card_view);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        button.setVisibility(View.GONE);
        signalsLayout.setVisibility(View.INVISIBLE);
    }
    public void pickLocation(View view) {
        /*
        Location location = new Location("P0");
        location.setLatitude(origin.latitude);
        location.setLongitude(origin.longitude);
        onLocationChanged(location);
        */
        notifyDestination = false;
        locationPlacesIntent();

    }
    // utils
    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    private void markSignals() {
        for (PointMap pointMap : ptsMain) {
            mMap.addMarker(new MarkerOptions().position(pointMap.latLng).title(pointMap.name));
        }
    }
    private String latTOstring(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }
    // init location manager & check if GPS & NETWORK Enabled
    private void askForProvider() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }
    // thread counter
    public class MyThread extends Thread {
        public Handler mHandler;

        @Override
        public void run() {
            /*
            Looper.prepare();
            mHandler = new Handler();
            Looper.loop();
            */
            while (true) {
                try {
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //signalsLayout.setVisibility(View.VISIBLE);
                            updateSignal();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // traffic signal  control
    private void turnOnGreen() {
        redV.setBackgroundColor(getResources().getColor(R.color.GREEN_ON));
        signalCounterV.setTextColor(getResources().getColor(R.color.GREEN_ON));
    }
    private void turnOnYellow() {
        redV.setBackgroundColor(getResources().getColor(R.color.YELLOW_ON));
        signalCounterV.setTextColor(getResources().getColor(R.color.YELLOW_ON));
    }
    private void turnOnRed() {
        redV.setBackgroundColor(getResources().getColor(R.color.RED_ON));
        signalCounterV.setTextColor(getResources().getColor(R.color.RED_ON));
    }
    public void expandList(View view) {
        ExpandableRelativeLayout expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);
        if (reverse) {
            CircleButton circleButton = (CircleButton) view;
            circleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
            reverse = false;
        } else {
            CircleButton circleButton = (CircleButton) view;
            circleButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up_black_24dp));
            reverse = true;
        }
        expandableLayout3.toggle();
    }
    private void updateSignal() {
        long hour, min, sec, millis;
        millis = System.currentTimeMillis();
        sec = (millis % 60000) / 1000;
        hour = (millis % 86400000) / 3600000 + 2;
        min = ((millis - sec) % 3600000) / 60000;
        long m = (hour * 60 + min) * 60 + sec;
        PointMap nearestP = ptsMain.get(nearestPoint);
        int rem = (int) (m % nearestP.total_period);
        String state;
        int count;
        if (rem >= 0 && rem < nearestP.periods[0]) {
            state = nearestP.status[0];
            count = nearestP.periods[0] - rem;
        } else if (rem >= nearestP.periods[0] && rem < nearestP.periods[1]) {
            state = nearestP.status[1];
            count = nearestP.periods[1] - rem;
        } else if (rem >= nearestP.periods[1] && rem < nearestP.periods[2]) {
            state = nearestP.status[2];
            count = nearestP.periods[2] - rem;
        } else {
            state = nearestP.status[3];
            count = nearestP.periods[3] + nearestP.periods[0] - rem;
        }
        switch (state) {
            case "Green":
                turnOnGreen();
                break;
            case "Yellow":
                turnOnYellow();
                break;
            case "Red":
                turnOnRed();
                break;
        }
        Log.d("time_TT*", "M = " + hour + " : " + min + " : " + sec + " state : " + state + " nearest = " + nearestPoint + "Count = " + count);
        signalCounterV.setText(String.valueOf(count));
    }
    // Testing
    private void getSignals() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        signals = new ArrayList<Signal>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Signal signal = child.getValue(Signal.class);
                    signals.add(signal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void secondCounter() {
        /*
        signalsLayout.setVisibility(View.GONE);
        signalCounterV.setVisibility(View.GONE);
        */

        int i = findNearestPoint(ptsAfricaMakram.get(0), ptsMain);
        nearestPoint = i;
        nearestIntersection = ptsMain.get(i).intersection;
        moveToMyLocation();

        Runnable task = new MyTask();
        Thread thread = new Thread(task);
        thread.start();
    }

    public class MyTask implements Runnable {

        public void run() {
            try {
                while (true){
                    Thread.sleep(5*1000);
                    //addressV.setText("TNTN* : " + Thread.currentThread().getName());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            makramCount += makramCount % ptsAfricaMakram.size();
                            if (makramCount < ptsAfricaMakram.size()){
                                moveToMyLocation();
                            }
                        }
                    });
                }

            } catch (InterruptedException e) {
            }
        }
    }

}
