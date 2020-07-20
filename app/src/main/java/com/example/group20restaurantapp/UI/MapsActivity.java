package com.example.group20restaurantapp.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AskUserToUpdateDialogFragment.AskUserToUpdateDialogListener,
        PleaseWaitDialogFragment.PleaseWaitDialogListener
{

    private GoogleMap mMap;

    private static final String TAG = "MapActivity";
    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";
    private static final float DEFAULT_ZOOM = 10f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String EXTRA_MESSAGE = "Extra";
    private Boolean mLocationPermissionsGranted = false;
    private Marker mMarker;
    private RestaurantManager manager = RestaurantManager.getInstance();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterManager<PegItem> mClusterManager;
    private Boolean updateData = false;
    private Boolean newData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermissionFromUser();
        Log.d("MapsActivity", "Working on oncreate");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String restaurantDataURL = manager.getURL("https://data.surrey.ca/api/3/action/package_show?id=restaurants"); //Retrieve url used to request csv
        String inspectionDataURL = manager.getURL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
        String newRestaurantData = manager.getCSV(restaurantDataURL); //Request updated restaurant data csv
        String newInspectionData = manager.getCSV(inspectionDataURL); //Request updated restaurant data csv

        //Write new csv from web server to internal storage
        manager.writeToFile(newRestaurantData, WEB_SERVER_RESTAURANTS_CSV, this);
        manager.writeToFile(newInspectionData, WEB_SERVER_INSPECTIONS_CSV, this);

        manager.readRestaurantData(this);
        manager.readNewRestaurantData(this);

        manager.initInspectionLists(this);
        manager.initNewInspectionLists(this);

        manager.sortInspListsByDate();
        manager.sortRestaurantsByName();

        // TODO: Check if there is new data on the server
        // newData = manager.checkForNewData();
        newData = true;

        // TODO: Add one more outer if statement checking if it's been >= 20 hours since
        // in-app data has been updated

        if (newData) {
            if (!manager.hasUserBeenAskedToUpdateThisSession()) {
                showAskUserToUpdateDialog();
                manager.setUserBeenAskedToUpdateThisSession(true);
            }
        }

        // Read installed data

        wireLaunchListButton();
    }

    public void showPleaseWaitDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new PleaseWaitDialogFragment();
        dialog.show(getSupportFragmentManager(), "PleaseWaitFragment");
    }

    @Override
    public void onPleaseWaitDialogNegativeClick(DialogFragment dialog) {
        // User pressed dialog's negative button, ie, wants to cancel the download
        Toast.makeText(MapsActivity.this,
                "User pressed cancel. Cancel the download", Toast.LENGTH_SHORT).show();

        // Update global variable for onAskUserToUpdateDialogPositiveClick
        // isDownloadCancelled = true;
    }

    public void showAskUserToUpdateDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AskUserToUpdateDialogFragment();
        dialog.show(getSupportFragmentManager(), "AskUserToUpdateFragment");
    }

    public void onAskUserToUpdateDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Toast.makeText(MapsActivity.this,
                "MapsActivity: User pressed yes to update", Toast.LENGTH_SHORT).show();
        updateData = true; // this won't work

        // Launch please-wait dialog and start the download
        showPleaseWaitDialog();

        // Start download

        // if (!isDownloadCancelled)
            // finish the please wait dialog
            // Update relevant data
        // else
            // do not update any data
    }

    public void onAskUserToUpdateDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Toast.makeText(MapsActivity.this,
                "MapsActivity: User pressed no, do not update", Toast.LENGTH_SHORT).show();
        // do nothing
        // updateData = false;
    }

    private void getDeviceLocation() {
        Log.d("MapsActivity", "Code has executed till getdevicelocation function");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                 Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d("MapsActivity", "Found Location");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d("MapsActivity", "Current location cannot be found");
                            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void getLocationPermissionFromUser() {
        Log.d("MapsActivity", "Working till get location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MapsActivity", "Working onrequestPermissionResult");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    refreshMap();
                }
            }
        }
    }

    private void refreshMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                // TODO: Remove this return statement? What is it for?
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        setUpClusterer();

        registerClickCallback();

        // Move the camera to surrey
        // TODO: The camera should pan to user's location on startup
        LatLng surrey = new LatLng(49.104431, -122.801094);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(surrey));

        //Set Custom InfoWindow Adapter
        CustomInfoAdapter adapter = new CustomInfoAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        // Receive intent from Restaurant Activity
        Intent i_receive = getIntent();
        String resID = i_receive.getStringExtra(EXTRA_MESSAGE);
    }

    private void registerClickCallback() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Find the restaurant to work with.
                LatLng latLngF = marker.getPosition();
                double lat = latLngF.latitude;
                double lng = latLngF.longitude;
                Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng);
                int tempIndex = manager.findIndex(restaurant);
                Intent intent = RestaurantActivity.makeLaunchIntent(MapsActivity.this);
                intent.putExtra(MainActivity.RESTAURANT_INDEX_INTENT_TAG, tempIndex);

                // what is 451 for?
                MapsActivity.this.startActivityForResult(intent, 451);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                moveCamera(marker.getPosition(), DEFAULT_ZOOM);
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // I don't think anything should happen unless you press a marker
                // No need to re-initialize every map marker as far as I can tell

                // Clear everything
                // mClusterManager.clearItems();

                // Clear the currently open marker
                // mMap.clear();

                // Reinitialize clusterManager
                // setUpClusterer();

                // Focus map on the position that was clicked on map
                // moveCamera(latLng, 15f);
            }
        });

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<PegItem>() {
            @Override
            public boolean onClusterClick(Cluster<PegItem> cluster) {
                moveCamera(cluster.getPosition(), 15);
                return true;
            }
        });
    }

    /**
     * Move the camera according to Latitude and longitude
     * DEFAULT_ZOOM = 15
     */
    private void moveCamera(LatLng latLng, float zoom) {
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(location);
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
        for (Restaurant restaurant : manager.getRestaurants()) {
            String temp = restaurant.getName();
            PegItem pegItem = new PegItem(
                    restaurant.getLatitude(),
                    restaurant.getLongitude(),
                    temp,
                    getHazardIcon(restaurant)
            );
            mClusterManager.addItem(pegItem);
        }
    }

    private BitmapDescriptor getHazardIcon(Restaurant restaurant) {
        Inspection RecentInspection = restaurant.getInspection(0);
        BitmapDescriptor hazardIcon;
        if (RecentInspection != null) {
            String hazardLevel = RecentInspection.getHazardRating();
            if (hazardLevel.equals("Low")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_yellow);
            } else if (hazardLevel.equals("Moderate")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_orangle);
            } else {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_red);
            }
        }
        else{
            hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_green);
        }
        return hazardIcon;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
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

            // Find the restaurant to work with.
            LatLng latLngF = marker.getPosition();
            double lat = latLngF.latitude;
            double lng = latLngF.longitude;
            Restaurant restaurant = manager.findRestaurantByLatLng(lat, lng);

            // Fill the view
            ImageView logo = itemView.findViewById(R.id.info_item_restaurantLogo);

            logo.setImageResource(restaurant.getIconImgId());

            TextView restaurantNameText = itemView.findViewById(R.id.info_item_restaurantName);

            String temp = restaurant.getName();

            if (temp.length() > 25) {
                temp = temp.substring(0, 25) + "...";
            }
            restaurantNameText.setText(temp);

            TextView addressText = itemView.findViewById(R.id.info_item_address);
            addressText.setText(restaurant.getAddress());

            Inspection RecentInspection = null;
            TextView lastInspectionText = itemView.findViewById(R.id.info_item_lastInspection);
            ImageView hazard = itemView.findViewById(R.id.info_item_hazardImage);

            if (restaurant.getInspectionSize() > 0) {
                RecentInspection = restaurant.getInspectionList().get(0);
                //lastInspectionText.setText(RecentInspection.getInspectionDate());
                lastInspectionText.setText(
                        getString(
                                R.string.restaurant_activity_inspection_item_date,
                                RecentInspection.intelligentInspectDate())
                );

                String level = RecentInspection.getHazardRating();
                if (level.equals("Low")) {
                    hazard.setImageResource(R.drawable.yellow_triangle);
                } else if (level.equals("Moderate")) {
                    hazard.setImageResource(R.drawable.orange_diamond);
                } else {
                    hazard.setImageResource(R.drawable.red_octogon);
                }
            } else {
                lastInspectionText.setText("");
                hazard.setImageResource(R.drawable.no_inspection_qmark);
            }
            return itemView;
        }
    }

    // For peg icon
    // Learned from:https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private class MarkerClusterRenderer extends DefaultClusterRenderer<PegItem> {

        public MarkerClusterRenderer(Context context, GoogleMap map,
                                     ClusterManager<PegItem> clusterManager) {
            super(context, map, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PegItem item, MarkerOptions markerOptions) {
            // use this to make your change to the marker option
            // for the marker before it gets render on the map
            markerOptions.icon(item.getHazard());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}



