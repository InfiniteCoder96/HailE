package com.androidapp.scalar.hailee;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidapp.scalar.hailee.service_pack.LocationManagerCheck;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GeoQuery geoQuery;
    DatabaseReference driverLocationRef;
    ValueEventListener driverLocationRefListner;


    private ImageView mUserLocationImageBtn;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button mHailBtn;
    private Marker mDriverMarker;

    private LatLng pickupLocation;

    private String destination;
    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    public static Boolean isLoggingOut = false;
    private String Customer_ID = "";
    private Boolean requestStatus = false;
    private Boolean onRideStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        LocationManagerCheck locationManagerCheck = new LocationManagerCheck(this);
        Location location = null;

        if (locationManagerCheck.isLocationServiceAvailable()) {

            LocationManager locationManager = null;

            if (locationManagerCheck.getProviderType() == 1) {

            }
            else if (locationManagerCheck.getProviderType() == 2){

            }

        }else{
            locationManagerCheck .createLocationServiceError(CustomerMapActivity.this);
        }

        NavigationView mNaviV = (NavigationView) findViewById(R.id.customerNV);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.customer_drawer);

        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(mNaviV);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                destination = place.getAddress().toString();
                Log.i("CustomerMapActivity", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("CustomerMapActivity", "An error occurred: " + status);
            }
        });

        mHailBtn = (Button) findViewById(R.id.hailBtn);
        mHailBtn.setVisibility(View.VISIBLE);
        mHailBtn.getText();
        mHailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(requestStatus){
                    requestStatus = false;
                    geoQuery.removeAllListeners();
                    driverLocationRef.removeEventListener(driverLocationRefListner);

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests");

                    GeoFire geoFire = new GeoFire(ref);

                    geoFire.removeLocation(user_id);
                }
                else{
                    requestStatus = true;
                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests");

                    GeoFire geoFire = new GeoFire(ref);

                    geoFire.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));


                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());



                    getClosestDriver();
               }


            }
        });


    }

//    public Boolean checkOnGoingRides(){
//
//        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("RideDetails");
//
//        FirebaseDatabase.getInstance().getReference().child("RideDetails").child("CustomerRideId").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                try {
//                    String user_ID = dataSnapshot.getValue(String.class);
//
//
//                    if (user_ID != null && user_ID.equals(user_id)) {
//                        onRideStatus = true;
//
//                    }
//
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(CustomerMapActivity.this, "Something went wrong..Please try again later",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//
//        });
//        return onRideStatus;
//    }


    public void getClosestDriver(){

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("DriversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude,pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!driverFound){
                    mHailBtn.setText("Getting Your Driver...");
                    driverFound = true;
                    driverFoundID = key;

                    String customer_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

//                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                    DatabaseReference rideDetailsRef = FirebaseDatabase.getInstance().getReference().child("RideDetails").child(driverFoundID);


                    HashMap map = new HashMap();
                    map.put("CustomerRideId", customer_id);
                    map.put("Destination", destination);


                    //driverRef.updateChildren(map);
                    rideDetailsRef.updateChildren(map);



                    getDriverLocation();


                    mHailBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound){
                    if(radius < 3) {
                        radius++;
                        getClosestDriver();
                    }
                    else{
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests");

                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.removeLocation(user_id);
                        Toast.makeText(CustomerMapActivity.this, "No drivers available",Toast.LENGTH_SHORT).show();
                        mHailBtn.setText("Hail a Taxi");
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void getDriverLocation(){

        DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriversWorking").child(driverFoundID).child("l");

        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;

                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLong = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng driverLatLang = new LatLng(locationLat,locationLong);

                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLang.latitude);
                    loc2.setLongitude(driverLatLang.longitude);

                    float distance_driver_customer = loc1.distanceTo(loc2);

                    if(distance_driver_customer < 100){
                        Intent intent = new Intent();
                        PendingIntent pendingIntent = PendingIntent.getActivity(CustomerMapActivity.this, 0, intent, 0);
                        Notification notification = new Notification.Builder(CustomerMapActivity.this)
                                .setTicker("HailE driver has arrived")
                                .setContentTitle("Your driver has arrived")
                                .setContentText("You have 10 mins to get on board")
                                .setSmallIcon(R.drawable.taxi)
                                .setContentIntent(pendingIntent).getNotification();

                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, notification);
                    }

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    DatabaseReference rideDetailsRef = FirebaseDatabase.getInstance().getReference().child("RideDetails").child(driverFoundID);
                    HashMap rideDetails = new HashMap();


                    rideDetails.put("Distance", distance_driver_customer);

                    //driverRef.updateChildren(map);
                    rideDetailsRef.updateChildren(rideDetails);

                    mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_pick_up_location)));
                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLang).title("Your Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_location_marker)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculateFare(Float distance) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return  true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItemDrawer(MenuItem menuItem){
        Fragment myFragment = null;
        Class fragmentClass;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(menuItem.getItemId()){
            case R.id.cu_home:
                Intent intent = new Intent(CustomerMapActivity.this, CustomerMapActivity.class);
                startActivity(intent);
                break;
            case R.id.cu_book_later:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
                break;
            case R.id.cu_mytrips:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
                break;
            case R.id.cu_payment:
                Intent intent2 = new Intent(CustomerMapActivity.this, Payment.class);
                startActivity(intent2);
                break;
            case R.id.cu_notifications:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
                break;
            case R.id.cu_settings:
                Intent intent3 = new Intent(CustomerMapActivity.this, CustomerProfileActivity.class);
                startActivity(intent3);
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();

        }

//        try {
//            myFragment = (Fragment) fragmentClass.newInstance();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//
//
//        fragmentManager.beginTransaction().replace(R.id.flcontent, myFragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);

                return true;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mUserLocationImageBtn = (ImageView) findViewById(R.id.userlocationpng);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyleretro));

            if (!success) {
                Log.e("CustomerMapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("CustomerMapActivity", "Can't find style. Error: ", e);
        }



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        mUserLocationImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                Location location = mMap.getMyLocation();

                if (location != null) {

                    LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition position = mMap.getCameraPosition();

                    CameraPosition.Builder builder = new CameraPosition.Builder();
                    builder.zoom(15);
                    builder.target(target);

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

                }
            }
        });
    }





    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));//1-21

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(user_id);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
