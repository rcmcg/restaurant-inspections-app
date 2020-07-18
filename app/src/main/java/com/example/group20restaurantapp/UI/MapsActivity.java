package com.example.group20restaurantapp.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.PegItem;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AskUserToUpdateDialogFragment.AskUserToUpdateDialogListener
{


    private GoogleMap mMap;

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 18f;
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO: Add if statement to check if it's been >= 20 hours since app last updated
        // TODO: Add function to check if the server has new data for us to download
        // Suppose there is new data
        newData = true;

        if (newData) {
            if (!manager.hasUserBeenAskedToUpdateThisSession()) {
                showAskUserToUpdateDialog();
                manager.setUserBeenAskedToUpdateThisSession(true);
            }
        }

        wireLaunchListButton();
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
        updateData = true;
    }

    public void onAskUserToUpdateDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Toast.makeText(MapsActivity.this,
                "MapsActivity: User pressed no, do not update", Toast.LENGTH_SHORT).show();
        updateData = false;
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

        mMap.getUiSettings().setZoomControlsEnabled(true);

        setUpClusterer();

        double[] chosenRestaurantLatLon = getChosenRestaurantLocation();

        Log.d(TAG, "onMapReady: chosenRestaurantLatLon = [" + chosenRestaurantLatLon[0]
                + "," + chosenRestaurantLatLon[1] + "]");

        if (chosenRestaurantLatLon[0] == -1 || chosenRestaurantLatLon[1] == -1) {
            Log.d(TAG, "onMapReady: Setting map to user's location");
            // Move the camera to surrey
            // TODO: The camera should pan to user's location on startup
            LatLng surrey = new LatLng(49.104431, -122.801094);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surrey, 10));
        } else {
            Log.d(TAG, "onMapReady: Setting map to chosen restaurant coords");
            LatLng chosenRestaurantCoords = new LatLng(
                    chosenRestaurantLatLon[0],
                    chosenRestaurantLatLon[1]
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenRestaurantCoords, 20));
        }

        //Set Custom InfoWindow Adapter
        CustomInfoAdapter adapter = new CustomInfoAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);
    }

    private void setUpClusterer() {
        // Initialize new clusterManager
        mClusterManager = new ClusterManager<PegItem>(this, mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        populateMapWithMarkers();
    }

    private void populateMapWithMarkers() {
        // Get Singleton RestaurantManager
        RestaurantManager manager = RestaurantManager.getInstance();

        for (Restaurant restaurant : manager) {
            PegItem pegItem = new PegItem(
                    restaurant.getLatitude(),
                    restaurant.getLongitude()
            );

            mClusterManager.addItem(pegItem);
        }
    }

    private double[] getChosenRestaurantLocation() {
        // Return as [lat,long]
        double restaurantLatitude = getIntent()
                .getDoubleExtra(RestaurantActivity.RESTAURANT_LATITUDE_INTENT_TAG,-1);
        double restaurantLongitude = getIntent()
                .getDoubleExtra(RestaurantActivity.RESTAURANT_LONGITUDE_INTENT_TAG,-1);
        return new double[]{restaurantLatitude, restaurantLongitude};
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    // TODO: Remove this function?
    public static Intent makeLaunchIntent(Context c, String message) {
        Intent i1 = new Intent(c, MapsActivity.class);
        i1.putExtra(EXTRA_MESSAGE, message);
        return i1;
    }

    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

        private Activity context;

        public CustomInfoAdapter(Activity context){
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

            if (restaurant.getInspectionSize() > 0){
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
                } else if (level.equals("Moderate")){
                    hazard.setImageResource(R.drawable.orange_diamond);
                } else {
                    hazard.setImageResource(R.drawable.red_octogon);
                }
            }
            else{
                //lastInspectionText.setText("");
                hazard.setImageResource(R.drawable.no_inspection_qmark);
            }
            return itemView;
        }
    }
}