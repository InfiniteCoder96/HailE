package com.androidapp.scalar.hailee;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
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
import android.widget.ImageView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private ImageView mUserLocationImageBtn;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private String Customer_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);


        NavigationView mNaviV = (NavigationView) findViewById(R.id.driverNV);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.driver_drawer);

        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(mNaviV);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getAssignedCustomer();
    }


    private void getAssignedCustomer() {
        String driver_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("RideDetails").child(driver_ID);
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("CustomerRideId") != null){
                        Customer_ID = map.get("CustomerRideId").toString();
                        getAssignedCustomerPickupLocation();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getAssignedCustomerPickupLocation() {
        DatabaseReference assignedCustomerPickupLocation = FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(Customer_ID).child("l");
        assignedCustomerPickupLocation.addValueEventListener(new ValueEventListener() {
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

                    mMap.addMarker(new MarkerOptions().position(driverLatLang).title("Pick up Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.customer_location_marker)));

                    DatabaseReference customerRequestref = FirebaseDatabase.getInstance().getReference("CustomerRequests");

                    GeoFire geoFire = new GeoFire(customerRequestref);
                    geoFire.removeLocation(Customer_ID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            case R.id.dr_home:
                Intent intent = new Intent(DriverMapActivity.this, DriverMapActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.dr_mytrips:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
                break;
            case R.id.dr_notifications:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
                break;
            case R.id.dr_settings:
                fragmentManager.beginTransaction().replace(R.id.flcontent, new DriverProfileFragment()).commit();
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
                Log.e("DriverMapActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("DriverMapActivity", "Can't find style. Error: ", e);
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
                    builder.zoom(10);
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

        if(getApplicationContext() != null) {


            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));//1-21

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DriversAvailable");
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DriversWorking");

                GeoFire geoFireAvailable = new GeoFire(refAvailable);
                GeoFire geoFireWorking = new GeoFire(refWorking);

                switch(Customer_ID){
                    case "":
                        geoFireWorking.removeLocation(user_id);
                        geoFireAvailable.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                        break;
                    default:
                        geoFireAvailable.removeLocation(user_id);
                        geoFireWorking.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }


            }
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
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriversAvailable");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(user_id);
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
