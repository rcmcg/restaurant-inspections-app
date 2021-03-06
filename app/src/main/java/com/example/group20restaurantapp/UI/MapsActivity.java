package com.example.group20restaurantapp.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.PegItem;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity that utilizes Google Maps to show user restaurants in internal storage, restaurants
 * in Surrey in this case. User can interact with the map and markers or switch to the list
 * of restaurants by launching MainActivity.
 */

public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        AskUserToUpdateDialogFragment.AskUserToUpdateDialogListener,
        PleaseWaitDialogFragment.PleaseWaitDialogListener,
        GoogleMap.OnCameraMoveStartedListener
{
    // Map variables
    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private ClusterManager<PegItem> mClusterManager;
    private Marker singleRestaurantMarker;          // Temporary marker for launching MapsActivity from RestaurantActivity
    private Boolean followUser = false;
    private static int updateLocationIter = 0;      // Used to update the users location as they move

    // Model variables
    private RestaurantManager manager = RestaurantManager.getInstance();
    private String restaurantDataURL;
    private String inspectionDataURL;
    private Date currentDate;
    private DialogFragment pleaseWaitDialog;

    // Constants
    private static final String TAG = "MapActivity";
    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";
    private static final String WEB_SERVER_RESTAURANTS_URL = "https://data.surrey.ca/api/3/action/package_show?id=restaurants";
    private static final String WEB_SERVER_INSPECTIONS_URL = "https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int ZOOM_STREETS = 15;
    public static final int ZOOM_CITY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("MapsActivity", "Working onCreate");

        getLocationPermissionFromUser();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Initializes the map
        mapFragment.getMapAsync(this);

        long appLastUpdated = getAppLastUpdated(this);

        // read data on startup
        if (manager.getSizeAllRestaurants() == 0) {
            if (appLastUpdated == -1) {
                Log.d(TAG, "onCreate: Filling with default restaurants");
                manager.fillRestaurantManager(false, this);
            } else {
                Log.d(TAG, "onCreate: Filling with updated restaurants");
                manager.fillRestaurantManager(true, this);
            }
        }

        // Decide whether or not we ask user to update
        currentDate = new Date();
        Log.d(TAG, "onCreate: Time since last update in hours: " +  timeSinceLastAppUpdateInHours(currentDate));
        if (appLastUpdated == -1 || timeSinceLastAppUpdateInHours(currentDate) >= 20) {
            // App has never been updated or it's been over 20 hours since the last update
            Log.d(TAG, "onCreate: App has never been updated or it's been over 20 hours since the last update");

            // Check server for new data, get URL as well as date data last updated
            String[] restaurantDataURLDate = manager
                    .getURLDateLastModified(WEB_SERVER_RESTAURANTS_URL); //Retrieve url used to request csv
            String[] inspectionDataURLDate = manager
                    .getURLDateLastModified(WEB_SERVER_INSPECTIONS_URL);

            // Update global URL variables
            restaurantDataURL = restaurantDataURLDate[0];
            inspectionDataURL = inspectionDataURLDate[0];

            // Get restaurant and inspection data last updated as Date
            Date restaurantLastUpdatedDate = null;
            Date inspectionLastUpdatedDate = null;
            try {
                restaurantLastUpdatedDate = getDateFromString(restaurantDataURLDate[1]);
                inspectionLastUpdatedDate = getDateFromString(inspectionDataURLDate[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Check which data is out of date
            boolean restaurantDataOutOfDate = false, inspectionDataOutOfDate = false;
            assert restaurantLastUpdatedDate != null;
            if (restaurantLastUpdatedDate.getTime() > appLastUpdated) {
                restaurantDataOutOfDate = true;
            }

            assert inspectionLastUpdatedDate != null;
            if (inspectionLastUpdatedDate.getTime() > appLastUpdated) {
                inspectionDataOutOfDate = true;
            }

            if (appLastUpdated == -1 || restaurantDataOutOfDate || inspectionDataOutOfDate) {
                // App has never been updated or appData is out of date
                if (!manager.hasUserBeenAskedToUpdateThisSession()) {
                    Log.d(TAG, "onCreate: Asking user if they want to update");
                    showAskUserToUpdateDialog();
                    manager.setUserBeenAskedToUpdateThisSession(true);
                }
            }
        }

        wireLaunchListButton();
        wireSearchButton();
    }

    private void wireLaunchListButton() {
        Button btn = findViewById(R.id.btnLaunchList);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MainActivity.makeIntent(MapsActivity.this);
                finish();
                startActivity(intent);
            }
        });
    }

    private void wireSearchButton() {
        Button searchButton = findViewById(R.id.MaptoSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i3 = new Intent(MapsActivity.this, SearchActivity.class);
                startActivityForResult(i3, MainActivity.LAUNCH_SEARCH_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SearchActivity.RESULT_OK) {
            // User entered new search terms, update the map by clearing and repopulating
            mClusterManager.clearItems();
            mClusterManager.cluster();
            setUpClusterer();
            mClusterManager.cluster();
        }
    }

    private void getLocationPermissionFromUser() {
        Log.d("MapsActivity", "Working till get location permission");
        //String array that sends the permission location
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        // Access_Fine_Location and Access_Coarse_Location is made global
        // Package manager actually checks if the permission was granted for Fine_Location
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Package manager checks if the Coarse_Location has been granted
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // If they both have been granted, then permission is actually granted
                mLocationPermissionsGranted = true;
            } else {
                // Else going to receive a result
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private Date getDateFromString(String rawString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.parse(rawString);
    }

    private long timeSinceLastAppUpdateInHours(Date currentDate) {
        long appLastUpdatedInMs = getAppLastUpdated(this);
        long diffInMs = currentDate.getTime() - appLastUpdatedInMs;
        return TimeUnit.HOURS.convert(diffInMs, TimeUnit.MILLISECONDS);
    }

    // Asks the user if they want to update, they may accept or decline
    public void showAskUserToUpdateDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AskUserToUpdateDialogFragment();
        dialog.show(getSupportFragmentManager(), "AskUserToUpdateFragment");
    }

    public void onAskUserToUpdateDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        // Launch please-wait dialog and start the download
        showPleaseWaitDialog();

        // Wait a few seconds to give the user a chance to cancel
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initiateDownload();
            }
        }, 2000);   // 2 seconds
    }

    public void onAskUserToUpdateDialogNegativeClick(DialogFragment dialog) {
        // User declined update, the dialog is dismissed
    }

    // Tell the user to wait for download and offer a chance to cancel
    public void showPleaseWaitDialog() {
        // Create an instance of the dialog fragment and show it
        pleaseWaitDialog = new PleaseWaitDialogFragment();
        pleaseWaitDialog.show(getSupportFragmentManager(), "PleaseWaitFragment");
    }

    @Override
    public void onPleaseWaitDialogNegativeClick(DialogFragment dialog) {
        // User pressed dialog's negative button, ie, wants to cancel the download
        manager.setDownloadCancelled(true);
        manager.cancelDownloads();
    }

    private void initiateDownload() {
        String newRestaurantData = "", newInspectionData = "";
        // Initiates download, checks if user cancelled before starting
        if(!manager.isDownloadCancelled()) {
            newRestaurantData = manager.getCSV(restaurantDataURL); //Request updated restaurant data csv
        }

        if (!manager.isDownloadCancelled()) {
            newInspectionData = manager.getCSV(inspectionDataURL); //Request updated inspection data csv
        }

        // Log.d(TAG, "onAskUserToUpdateDialogPositiveClick: newRestaurantData: " + newRestaurantData);
        // Log.d(TAG, "onAskUserToUpdateDialogPositiveClick: newInspectionData: " + newInspectionData);
        Log.d(TAG, "onAskUserToUpdateDialogPositiveClick: finished getting new data or user cancelled");

        // Finish please-wait dialog if it isn't already finished
        finishPleaseWaitDialog();

        if (!manager.isDownloadCancelled()) {
            // Safe to write new data
            manager.writeToFile(newRestaurantData, WEB_SERVER_RESTAURANTS_CSV, this);
            Log.d(TAG, "initiateDownload: finished writing newRestaurantData");
            manager.writeToFile(newInspectionData, WEB_SERVER_INSPECTIONS_CSV, this);
            Log.d(TAG, "initiateDownload: finished writing newInspectionData");

            // Save list of favourite restaurants pre update and clear current favRestaurantsList
            manager.setPreUpdateFavList();
            manager.clearFavRestaurantsList();
            // Manager singleton instance is filled with new data
            manager.refillRestaurantManagerNewData(this);
            saveAppLastUpdated(currentDate.getTime());

            // Update filtered restaurant list
            manager.updateFilteredRestaurants();

            // Update the map
            mClusterManager.clearItems();
            mClusterManager.cluster();
            setUpClusterer();
            mClusterManager.cluster();

            boolean atLeastOneRestaurantModified = setRestaurantModifiedFlagsPostUpdate();
            if (atLeastOneRestaurantModified) {
                // Launch an activity displaying which restaurants have been modified
                Intent intent = ModifiedFavRestaurantsActivity.makeIntent(MapsActivity.this);
                startActivity(intent);
            }
        }
    }

    /*
        Set the isModified attributes of the favourite restaurants after the update. Return boolean
        indicating at least one restaurant was modified to see if we should launch activity
        to inform the user of their modified favourite restaurants
     */
    private boolean setRestaurantModifiedFlagsPostUpdate() {
        boolean atLeastOneRestaurantModified = false;
        for (Iterator<Restaurant> it = manager.favRestaurantIterator(); it.hasNext(); ) {
            Restaurant restaurant = it.next();
            if (restaurant.getInspectionSize() > 0){
                // Compare size of inspection list preupdate to size post update
                // => set a flag to indicate new inspections were added
                for (Iterator<Restaurant> iter = manager.preUpdateFavRestaurantIterator(); iter.hasNext(); ) {
                    Restaurant r = iter.next();
                    if (r.getTrackingNumber().equals(restaurant.getTrackingNumber())){
                        if (restaurant.getInspectionSize() > r.getInspectionSize()){
                            restaurant.setModified(true);
                            atLeastOneRestaurantModified = true;
                            break;
                        }
                    }
                }
            }
        }
       return atLeastOneRestaurantModified;
    }

    private void finishPleaseWaitDialog() {
        if (pleaseWaitDialog != null) {
            pleaseWaitDialog.dismiss();
        }
    }

    private void getDeviceLocation() {
        // mFusedLocationProvider interacting with location using fused location provider
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                // Returns the last location
                // If the task.isSuccessful is true, that means we have found location
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Gets the current location
                            Location currentLocation = (Location) task.getResult();
                            followUser = true;
                            // Moves the camera to that result
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), ZOOM_STREETS);
                        } else {
                            // Location can not be found
                            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    // Checks for permission result for getLocationPermissionFromUser() function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Assume the permission is false to begin with
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If grant results.length > 0 that means some kind of permission was granted
                // If grant results does not equal, permission granted, then permission has been denied
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    //If the loop goes through all the grant permissions, then the permission has been granted
                    mLocationPermissionsGranted = true;

                    // Restart activity with new location permission
                    finish();
                    Intent refreshIntent = makeIntent(this);
                    overridePendingTransition(0, 0);
                    startActivity(refreshIntent);
                    overridePendingTransition(0, 0);
                    manager.setUserBeenAskedToUpdateThisSession(false);
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Package manager checks if the permission is granted to access fine and coarse location
        if (mLocationPermissionsGranted) {
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }
            // If the permission is granted then, a blue dot is set to the user's location
            mMap.setMyLocationEnabled(true);
            // Camera follows user as they move, stops following if the user interacts with the map in any way
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    // Don't constantly print to log
                    if (updateLocationIter%5 == 0) {
                        Log.d(TAG, "onMyLocationChange: followUser: " + followUser + ", updateIter: " + updateLocationIter);
                    }

                    updateLocationIter++;
                    if (followUser && (updateLocationIter %3 == 0)) {   // Only update the location every 3 ticks
                        if (updateLocationIter > 8) {                   // Let the camera settle on user's location first
                            Log.d(TAG, "onMyLocationChange: moveCamera()");
                            moveCamera(new LatLng(location.getLatitude(),location.getLongitude()));
                        }
                    }
                }
            });
        }

        // Configure map UI
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //Set Custom InfoWindow Adapter
        CustomInfoAdapter adapter = new CustomInfoAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        mMap.setOnCameraMoveStartedListener(this);

        setUpClusterer();
        mClusterManager.cluster();
        registerClickCallback();

        // Move camera to Surrey first
        LatLng surrey = new LatLng(49.104431, -122.801094);
        moveCamera(surrey, ZOOM_CITY);

        // Get chosen restaurant from intent (if it exists)
        double[] chosenRestaurantLatLon = getChosenRestaurantLocation();
        String chosenRestaurantName = getIntent().getStringExtra(RestaurantActivity.RESTAURANT_NAME_INTENT_TAG);

        Log.d(TAG, "onMapReady: chosenRestaurantName: " + chosenRestaurantName);
        Log.d(TAG, "onMapReady: chosenRestaurantLatLon = ["
                + chosenRestaurantLatLon[0]
                + "," + chosenRestaurantLatLon[1] + "]");

        // If the user didn't open the map by clicking restaurant coordinates then switching to the map starts at user's location
        LatLng chosenRestaurantCoords = null;
        if (chosenRestaurantLatLon[0] == -1 || chosenRestaurantLatLon[1] == -1) {
            Log.d(TAG, "onMapReady: Setting map to user's location");
            getDeviceLocation();
        } else {
            // If the user have chosen any restaurant coordinates then switching to map, starts at restaurant's coordinates
            Log.d(TAG, "onMapReady: Setting map to chosen restaurant coords");
            Log.d(TAG, "onMapReady: chosen restaurant lat: "
                    + chosenRestaurantLatLon[0] + " chosen restaurant lon: "
                    + chosenRestaurantLatLon[1]);

            chosenRestaurantCoords = new LatLng(
                    chosenRestaurantLatLon[0],
                    chosenRestaurantLatLon[1]
            );

            // Move camera to chosen restaurant
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenRestaurantCoords, ZOOM_STREETS));

            Restaurant restaurant = manager.findRestaurantByLatLng(chosenRestaurantLatLon[0],
                    chosenRestaurantLatLon[1], chosenRestaurantName);

            // Confirm we have the correct position
            if (restaurant.getLongitude() ==  chosenRestaurantLatLon[1] &&
                    restaurant.getLatitude() ==  chosenRestaurantLatLon[0])
            {
                // Add a marker and display it's window, delete when the user pans
                singleRestaurantMarker = mMap.addMarker(new MarkerOptions()
                        .position(chosenRestaurantCoords)
                        .icon(getHazardIcon(restaurant))
                        .title(chosenRestaurantName));
                singleRestaurantMarker.showInfoWindow();
            }
        }
    }

    private void setUpClusterer() {
        // Initialize new clusterManager
        mClusterManager = new ClusterManager<PegItem>(this, mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new MarkerClusterRenderer(getApplicationContext(), mMap, mClusterManager));
        populateMapWithMarkers();
    }

    private void populateMapWithMarkers() {
        // Get Singleton RestaurantManager
        RestaurantManager manager = RestaurantManager.getInstance();
        Log.d(TAG, "populateMapWithMarkers: Populating map with markers");
        // Sets the up the map pegs
        for (Restaurant restaurant : manager) {
            PegItem pegItem = new PegItem(
                    restaurant.getLatitude(),
                    restaurant.getLongitude(),
                    restaurant.getName(),
                    getHazardIcon(restaurant)
            );
            mClusterManager.addItem(pegItem);
        }
    }

    // Setting hazard icon according to the most inspection
    private BitmapDescriptor getHazardIcon(Restaurant restaurant) {
        Inspection RecentInspection = restaurant.getInspection(0);
        BitmapDescriptor hazardIcon;
        if (RecentInspection != null) {
            String hazardLevel = RecentInspection.getHazardRating();
            if (hazardLevel.equals("Low")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.ic_yellow_triangle);
            } else if (hazardLevel.equals("Moderate")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.ic_orange_diamond);
            } else {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.ic_red_octagon);
            }
        } else {
            hazardIcon = bitmapDescriptorFromVector(this, R.drawable.ic_question_mark);
        }
        return hazardIcon;
    }

    private double[] getChosenRestaurantLocation() {
        // Return as [lat,long]
        double restaurantLatitude = getIntent()
                .getDoubleExtra(RestaurantActivity.RESTAURANT_LATITUDE_INTENT_TAG,-1);
        double restaurantLongitude = getIntent()
                .getDoubleExtra(RestaurantActivity.RESTAURANT_LONGITUDE_INTENT_TAG,-1);
        return new double[]{restaurantLatitude, restaurantLongitude};
    }

    // If the user presses on any restaurant marker launch the RestaurantActivity for that restaurant
    private void registerClickCallback() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Find the restaurant we need to open for RestaurantActivity
                LatLng latLngF = marker.getPosition();
                double lat = latLngF.latitude;
                double lng = latLngF.longitude;
                String restaurantName = marker.getTitle();
                Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng, restaurantName);

                //Restaurant's index is needed to launch the restaurant activity with that particular restaurant
                int restaurantIndex = manager.findIndexFromFilteredRestaurants(restaurant);

                // Launch RestaurantActivity with the correct index
                Intent intent = RestaurantActivity.makeLaunchIntent(MapsActivity.this);
                intent.putExtra(MainActivity.RESTAURANT_INDEX_INTENT_TAG, restaurantIndex);
                startActivity(intent);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                moveCamera(marker.getPosition());
                marker.showInfoWindow();
                Log.d(TAG, "onMarkerClick: Marker clicked, setting followUser to false");
                followUser = false;
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                followUser = false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                followUser = true;
                return false;
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PegItem>() {
            @Override
            public boolean onClusterClick(Cluster<PegItem> cluster) {
                moveCamera(cluster.getPosition(), ZOOM_STREETS);
                followUser = false;
                return true;
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera (zoom): moving: " + latLng + ", zoom: " + zoom);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(location);
    }

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera : moving: " + latLng);
        CameraUpdate location = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(location);
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        switch (reason) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                followUser = false;
                if (singleRestaurantMarker != null) {
                    singleRestaurantMarker.remove();
                    singleRestaurantMarker = null;
                }
        }
    }

    // Mode private means this value will be used only in this application
    private void saveAppLastUpdated(long currentDateInMs) {
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("appLastUpdated", currentDateInMs);
        editor.apply();
    }

    static private long getAppLastUpdated(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getLong("appLastUpdated", -1);
    }

    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
        private Activity context;

        public CustomInfoAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View itemView = context.getLayoutInflater().inflate(R.layout.map_infowindow_layout, null);

            // Find the restaurant to work with by searching and matching latitude, longitude and name
            LatLng latLngF = marker.getPosition();
            double lat = latLngF.latitude;
            double lng = latLngF.longitude;
            Restaurant restaurant = null;
            restaurant = manager.findRestaurantByLatLng(lat, lng, marker.getTitle());
            if (restaurant == null){
                Log.d(TAG, "Cluster Click!");
                return null;
            }

            // Setting the restaurant's image
            ImageView logo = itemView.findViewById(R.id.info_item_restaurantLogo);
            logo.setImageResource(restaurant.getIconImgId());

            // Setting the restaurant's name
            TextView restaurantNameText = itemView.findViewById(R.id.info_item_restaurantName);
            restaurantNameText.setText(restaurant.getName());

            // Setting the restaurant's address
            TextView addressText = itemView.findViewById(R.id.info_item_address);
            addressText.setText(restaurant.getAddress());

            // Info about the last inspection
            TextView lastInspectionText = itemView.findViewById(R.id.info_item_lastInspection);
            ImageView hazard = itemView.findViewById(R.id.info_item_hazardImage);

            // Set last inspection text and hazard icon, if this restaurant has had an inspection
            Inspection recentInspection = null;
            if (restaurant.getInspectionSize() > 0) {
                recentInspection = restaurant.getInspection(0);
                lastInspectionText.setText(
                        getString(
                                R.string.restaurant_activity_inspection_item_date,
                                recentInspection.intelligentInspectDate())
                );

                // Hazard rating to determine right hazard icon
                String level = recentInspection.getHazardRating();
                if (level.equals("Low")) {
                    hazard.setImageResource(R.drawable.yellow_triangle);
                } else if (level.equals("Moderate")) {
                    hazard.setImageResource(R.drawable.orange_diamond);
                } else {
                    hazard.setImageResource(R.drawable.red_octogon);
                }
            } else {
                // Leave text as default
                hazard.setImageResource(R.drawable.no_inspection_qmark);
            }
            return itemView;
        }
    }

    // For peg icon
    // Source:https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        // Sets bound to the height and width of the position to that the image matches
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private class MarkerClusterRenderer extends DefaultClusterRenderer<PegItem> {
        // Pegs the restaurants in the Map Activity
        public MarkerClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<PegItem> clusterManager) {
            super(context, map, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PegItem item, MarkerOptions markerOptions) {
            // Use this to make your change to the marker option for the marker before its rendered on map
            markerOptions.icon(item.getHazard());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<PegItem> cluster) {
            return (cluster.getSize() >= 8);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }
}