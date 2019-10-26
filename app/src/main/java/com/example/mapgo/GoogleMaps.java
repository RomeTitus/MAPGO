package com.example.mapgo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private int SelectedRout = 999999;
    private GoogleMap mMap;
    private ImageView imageCar, imageCycle, imageWalk;
    private Switch switchImperial;
    private User user;
    private FirebaseAuth auth;
    GeoApiContext geoApiContext = null;
    String TAG = "GoogleMapsDebug";
    private Button BtnHistory, BtnCancel, BtnStart, BtnEndTrip, BtnLogout, BtnDelete;
    private ImageButton imageButtonNavigation;
    private boolean NavigationPresent = false;
    private LinearLayout linearLayoutMenu, linearLayoutSearch, linearLayoutTapToSearch, linearLayoutShowSearchResult, LinearlayoutInfo,LinearLayoutStartTripDetails, LinearLayoutActiveTrip;
    private TextView txtGetName, TxtPlaceName, TxtRoute, TxtDuration, TxtDistance;
    Location userLocation = null;
    private String prefferedMode, StartTime;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private double childCountPrimaryKey = -999;
    private GoogleSignInClient mGoogleSignInClient;

    private  ArrayList<PolylineData> polylineDataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = auth.getCurrentUser();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_google_maps);
        //txtSearch = findViewById(R.id.TxtSearch);
        BtnLogout = findViewById(R.id.BtnLogout);
        BtnDelete = findViewById(R.id.BtnDelete);
        BtnEndTrip = findViewById(R.id.BtnEndTrip);
        BtnStart = findViewById(R.id.BtnStart);
        BtnCancel = findViewById(R.id.BtnCancel);
        BtnHistory = findViewById(R.id.BtnHistory);
        TxtPlaceName = findViewById(R.id.TxtPlaceName);
        TxtRoute = findViewById(R.id.TxtRoute);
        TxtDuration = findViewById(R.id.TxtStarTime);
        TxtDistance = findViewById(R.id.TxtDistance);
        txtGetName = findViewById(R.id.TxtGetName);

        LinearLayoutActiveTrip = findViewById(R.id.LinearLayoutActiveTrip);
        LinearLayoutStartTripDetails = findViewById(R.id.LinearLayoutStartTripDetails);
        imageButtonNavigation = findViewById(R.id.NavSetting);
        LinearlayoutInfo = findViewById(R.id.LinearlayoutInfo);
        linearLayoutTapToSearch = findViewById(R.id.LinearLayoutTapToSearch);
        linearLayoutMenu = findViewById(R.id.LinearLayoutMenu);
        linearLayoutSearch = findViewById(R.id.LinearLayoutSearch);
        linearLayoutShowSearchResult = findViewById(R.id.LinearLayoutShowSearchResult);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        LinearLayoutActiveTrip.setVisibility(View.GONE);
        LinearLayoutStartTripDetails.setVisibility(View.VISIBLE);
        LinearlayoutInfo.setVisibility(View.GONE);
        linearLayoutMenu.setVisibility(View.GONE);
        linearLayoutShowSearchResult.setVisibility(View.GONE);


        //gets google LogIn Info
        // Configure Google Sign In  https://firebase.google.com/docs/auth/android/google-signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imageButtonNavigation.setBackgroundResource(R.drawable.ic_popmenuon_black_24dp);

        imageButtonNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavigationPresent == true) {
                    imageButtonNavigation.setBackgroundResource(R.drawable.ic_popmenuon_black_24dp);
                    NavigationPresent = false;
                    linearLayoutMenu.setVisibility(View.GONE);

                }else{
                    imageButtonNavigation.setBackgroundResource(R.drawable.ic_popmenuoff_black_24dp);
                    NavigationPresent = true;
                    linearLayoutMenu.setVisibility(View.VISIBLE);
                }

            }
        });

//https://developers.google.com/places/android-sdk/autocomplete
// Initialize the AutocompleteSupportFragment.
       final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.clear();
                LatLng latLng = place.getLatLng();
                LinearLayoutStartTripDetails.setVisibility(View.VISIBLE);
                LinearLayoutActiveTrip.setVisibility(View.GONE);
                //googleMap.addMarker(new MarkerOptions().position(sydney)
                //        .title("Marker in Sydney"));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(place.getName()));
                calculateDirections(marker);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });



        switchImperial = findViewById(R.id.switchImperial2);
        imageCar = findViewById(R.id.imageCarTransport2);
        imageCycle = findViewById(R.id.imageCycleTransport2);
        imageWalk = findViewById(R.id.imageWalkTransport2);

        imageCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                prefferedMode = "CAR";
                imageCar.setBackgroundResource(R.drawable.circle);
                imageCycle.setBackgroundResource(R.color.colorWhite);
                imageWalk.setBackgroundResource(R.color.colorWhite);
                user.setUser_StartTransport("CAR");
                myRef.child("Users").child(firebaseUser.getUid()).setValue(user);
            }
        });

        imageCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                prefferedMode = "CYCLE";
                imageCar.setBackgroundResource(R.color.colorWhite);
                imageCycle.setBackgroundResource(R.drawable.circle);
                imageWalk.setBackgroundResource(R.color.colorWhite);
                user.setUser_StartTransport("CYCLE");
                myRef.child("Users").child(firebaseUser.getUid()).setValue(user);
            }
        });

        imageWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                prefferedMode = "WALK";
                imageCar.setBackgroundResource(R.color.colorWhite);
                imageCycle.setBackgroundResource(R.color.colorWhite);
                imageWalk.setBackgroundResource(R.drawable.circle);
                user.setUser_StartTransport("WALK");
                myRef.child("Users").child(firebaseUser.getUid()).setValue(user);
            }
        });
        try{
        switchImperial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                user.setUser_isImperial(Boolean.toString(switchImperial.isChecked()));
                myRef.child("Users").child(firebaseUser.getUid()).setValue(user);



                if(polylineDataList.size()>=SelectedRout){

                    TxtPlaceName.setText(polylineDataList.get(SelectedRout-1).getLeg().endAddress);
                    TxtRoute.setText("Route: " + SelectedRout);
                    TxtDuration.setText(polylineDataList.get(SelectedRout-1).getLeg().duration.toString());

                    if(user.getUser_isImperial().equals("false")){
                        TxtDistance.setText(polylineDataList.get(SelectedRout-1).getLeg().distance.toString());
                    }else {
                        double distanceInMiles= (polylineDataList.get(SelectedRout-1).getLeg().distance.inMeters/1609.34)*10;
                        int distanceInMilesFormat = (int) distanceInMiles;
                        double distanceInMilesWithOneDecimal = distanceInMilesFormat;
                        distanceInMilesWithOneDecimal = distanceInMilesWithOneDecimal /10;
                        TxtDistance.setText(distanceInMilesWithOneDecimal + " mi");
                    }

                }

            }
        });
        }catch (Exception e){
            Log.e(TAG, "onCancelled", e);
        }
        BtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GoogleMaps.this, historyView.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });


        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mMap.clear();
            polylineDataList.clear();
            SelectedRout = 999999;
            centerMapOnMyLocation();
            LinearLayoutStartTripDetails.setVisibility(View.VISIBLE);
                LinearLayoutActiveTrip.setVisibility(View.GONE);
            LinearlayoutInfo.setVisibility(View.GONE);
            }
        });

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapOnMyLocation();
                for (int i = 0; i < polylineDataList.size(); i++) {
                    if( (i+1) == SelectedRout){

                    }else{
                        polylineDataList.get(i).getPolyline().remove();
                    }
                }
                LinearLayoutActiveTrip.setVisibility(View.VISIBLE);
                https://www.javatpoint.com/java-get-current-date
                LinearLayoutStartTripDetails.setVisibility(View.GONE);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                StartTime = dtf.format(now);
            }
        });

        BtnEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                PolylineData polylineData = polylineDataList.get(SelectedRout-1);

               // myRef.child("Trip").child(firebaseUser.getUid()).child("TEST").setValue(polylineData);

                mMap.clear();
                polylineDataList.clear();
                SelectedRout = 999999;
                centerMapOnMyLocation();
                LinearLayoutStartTripDetails.setVisibility(View.VISIBLE);
                LinearLayoutActiveTrip.setVisibility(View.GONE);
                LinearlayoutInfo.setVisibility(View.GONE);

//https://www.javatpoint.com/java-get-current-date
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                PolylineInfo polylineInfo = new PolylineInfo();

                polylineInfo.setUserStartTime(StartTime);
                polylineInfo.setUserEndTime(dtf.format(now));

                //polylineData.polyline.getPoints();


                polylineInfo.setPolylineDataPointGoogleMap(polylineData.polyline.getPoints());
                int PrimeryKey = ((int)childCountPrimaryKey);
                myRef.child("Trip").child(firebaseUser.getUid()).child(""+PrimeryKey).setValue(polylineInfo);

            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView Alart;
                Button BtnCommit, BtnCancel;
                final Dialog dialog = new Dialog(GoogleMaps.this);
                dialog.setContentView(R.layout.dialog_fragment);//popup view is the layout you created
                Alart = dialog.findViewById(R.id.textView8);
                BtnCommit = dialog.findViewById(R.id.BtnCommit);
                BtnCancel = dialog.findViewById(R.id.BtnCancel);
                Alart.setText(firebaseUser.getDisplayName() + " , Do you want to sign out?");
                BtnCommit.setText("Sign Me Out");
                BtnCancel.setText("Cancel");

                BtnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        signOut();
                        Intent intent = new Intent(GoogleMaps.this, MainActivity.class);
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        finish();




                    }
                });
                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        BtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView Alart;
                Button BtnCommit, BtnCancel;
                final Dialog dialog = new Dialog(GoogleMaps.this);
                dialog.setContentView(R.layout.dialog_fragment);//popup view is the layout you created
                Alart = dialog.findViewById(R.id.textView8);
                BtnCommit = dialog.findViewById(R.id.BtnCommit);
                BtnCancel = dialog.findViewById(R.id.BtnCancel);
                Alart.setText(firebaseUser.getDisplayName() + " , Do you want to Delete all your data?");
                BtnCommit.setText("YES, Delete My Data");
                BtnCancel.setText("Cancel");
                BtnCommit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        signOut();
                        Intent intent = new Intent(GoogleMaps.this, MainActivity.class);
                        //intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                        dialog.dismiss();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query DeleteQuery = ref.child("Trip");

                        DeleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                    String Key = Snapshot.getKey();
                                    if(Key.equals(firebaseUser.getUid())){
                                        Snapshot.getRef().removeValue();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });

                        DeleteQuery = ref.child("Users");

                        DeleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                    String Key = Snapshot.getKey();
                                    if(Key.equals(firebaseUser.getUid())){
                                        Snapshot.getRef().removeValue();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled", databaseError.toException());
                            }
                        });



                        finish();
                        FirebaseAuth.getInstance().signOut();
                    }
                });
                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });




try{
    FetchUserDetails(firebaseUser.getUid());
}catch (Exception e){

}


    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Zoom in Current location
        centerMapOnMyLocation();

        LatLng sydney = new LatLng(-33.852, 151.211);

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyCJjzK-BpLse88AKfN1HiChhILrpEecFik")
                    .build();
        }
        mMap.setOnPolylineClickListener(this);

    }


    private void centerMapOnMyLocation() {
        //https://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(GoogleMaps.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(GoogleMaps.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        userLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (userLocation != null)
        {
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    //Checks if the SearchText is in focus or not
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //https://stackoverflow.com/questions/5582702/disable-edittext-blinking-cursor
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                EditText edit = ((EditText) v);
                Rect outR = new Rect();
                edit.getGlobalVisibleRect(outR);
                Boolean isKeyboardOpen = !outR.contains((int)ev.getRawX(), (int)ev.getRawY());

                if (isKeyboardOpen) {

                    edit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                }
                edit.setCursorVisible(isKeyboardOpen);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void calculateDirections(Marker marker){
       com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        userLocation.getLatitude(),
                        userLocation.getLongitude()
                )
        );
        //Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(polylineDataList.size()>0) {
                    for (int i = 0; i < polylineDataList.size(); i++) {

                        polylineDataList.get(i).getPolyline().remove();

                    }

                    polylineDataList.clear();
                    polylineDataList = new ArrayList<>();

                }
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(GoogleMaps.this, R.color.colorPrimaryDark));
                    polyline.setClickable(true);
                    polylineDataList.add(new PolylineData(polyline, route.legs[0]));

                }

                double QuickestTime = 999999999;
                for (int i = 0; i < polylineDataList.size(); i++) {
                    double currentDuration = polylineDataList.get(i).getLeg().duration.inSeconds;
                        if(currentDuration<QuickestTime){
                            QuickestTime = currentDuration;
                            onPolylineClick(polylineDataList.get(i).getPolyline());
                            //Zooms in on the current route
                            zoomRoute(polylineDataList.get(i).getPolyline().getPoints());

                        }
                }
                LinearlayoutInfo.setVisibility(View.VISIBLE);
            }
        });
    }
    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        for (int i = 0; i < polylineDataList.size(); i++) {

            Log.d(TAG, "onPolylineClick: toString: " + polylineDataList.toString());

            if(polyline.getId().equals( polylineDataList.get(i).getPolyline().getId())){
                polylineDataList.get(i).getPolyline().setColor(ContextCompat.getColor(GoogleMaps.this, R.color.colorCyan));
                polylineDataList.get(i).getPolyline().setZIndex(1);

                LatLng endLocatin = new LatLng(
                        polylineDataList.get(i).getLeg().endLocation.lat,
                        polylineDataList.get(i).getLeg().endLocation.lng
                );

                TxtPlaceName.setText(polylineDataList.get(i).getLeg().endAddress);
                TxtRoute.setText("Route: " + (i + 1));
                SelectedRout = i+1;
                TxtDuration.setText(polylineDataList.get(i).getLeg().duration.toString());

                if(user.getUser_isImperial().equals("false")){
                    TxtDistance.setText(polylineDataList.get(i).getLeg().distance.toString());
                }else {
                    double distanceInMiles= (polylineDataList.get(i).getLeg().distance.inMeters/1609.34)*10;
                    int distanceInMilesFormat = (int) distanceInMiles;
                    double distanceInMilesWithOneDecimal = distanceInMilesFormat;
                    distanceInMilesWithOneDecimal = distanceInMilesWithOneDecimal /10;
                    TxtDistance.setText(distanceInMilesWithOneDecimal + " mi");
                }




                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocatin)
                        .title("Trip: " + (i+1))
                        .snippet("Length: " + polylineDataList.get(i).getLeg().duration)
                );
                marker.showInfoWindow();


            }else{
                polylineDataList.get(i).getPolyline().setColor(ContextCompat.getColor(GoogleMaps.this, R.color.colorPrimaryDark));
                polylineDataList.get(i).getPolyline().setZIndex(0);
            }
        }

    }

    public void FetchUserDetails(final String userID){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.child("Users").child(userID).orderByChild("userId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //final ArrayList<User> UserList = new ArrayList<User>();
                //deal with data object
                try{
                    user = dataSnapshot.getValue(User.class);
                    txtGetName.setText("Welcome: " + user.getUser_Name());

                    if(user.getUser_StartTransport().equals("CAR")){
                        imageCar.setBackgroundResource(R.drawable.circle);
                        imageCycle.setBackgroundResource(R.color.colorWhite);
                        imageWalk.setBackgroundResource(R.color.colorWhite);
                    }else if(user.getUser_StartTransport().equals("CYCLE")){
                        imageCar.setBackgroundResource(R.color.colorWhite);
                        imageCycle.setBackgroundResource(R.drawable.circle);
                        imageWalk.setBackgroundResource(R.color.colorWhite);
                    }else{
                        imageCar.setBackgroundResource(R.color.colorWhite);
                        imageCycle.setBackgroundResource(R.color.colorWhite);
                        imageWalk.setBackgroundResource(R.drawable.circle);
                    }

                    if(user.getUser_isImperial().equals("false")){
                        switchImperial.setChecked(false);
                    }else {
                        switchImperial.setChecked(true);
                    }
                }catch (Exception e){

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //toastMsg(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });


        myRef.child("Trip").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    childCountPrimaryKey = dataSnapshot.getChildrenCount()+1;
                }catch (Exception e){

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //toastMsg(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }



    private void signOut() {
        try {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        }catch (Exception e){

        }
    }

}
