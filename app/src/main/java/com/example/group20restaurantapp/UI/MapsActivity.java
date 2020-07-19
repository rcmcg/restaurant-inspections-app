package com.example.group20restaurantapp.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.PegItem;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, AskUserToUpdateDialogFragment.AskUserToUpdateDialogListener,
        PleaseWaitDialogFragment.PleaseWaitDialogListener
{


    private GoogleMap mMap;

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 18f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String RESTAURANT_ACTIVITY_RESTAURANT_TAG = "restaurant";
    private static final String EXTRA_MESSAGE = "Extra";
    private Boolean mLocationPermissionsGranted = false;
    private Marker mMarker;
    private RestaurantManager manager = RestaurantManager.getInstance();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterManager<PegItem> mClusterManager;
    // private ClusterRenderer<PegItem> mRenderer;

    // private List<Marker> mMarkerArray = new ArrayList<Marker>();

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
            Log.d(TAG, "onMapReady: chosen restaurant lat: " + chosenRestaurantLatLon[0] + " chosen restaurant lon: " + chosenRestaurantLatLon[1]);

            LatLng chosenRestaurantCoords = new LatLng(
                    chosenRestaurantLatLon[0],
                    chosenRestaurantLatLon[1]
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chosenRestaurantCoords, 20));

            // Open window of correct marker
            /*
            Marker chosenRestaurantMarker = null;
            for (Marker marker : mMarkerArray) {
                if (marker.getPosition().latitude == chosenRestaurantCoords.latitude && marker.getPosition().longitude == chosenRestaurantCoords.longitude) {
                    chosenRestaurantMarker = marker;
                    Log.d(TAG, "onMapReady: found chosenRestaurantMarker: " + chosenRestaurantMarker.getPosition().toString());
                    break;
                }
            }

             */

            // Add a new marker
            // mClusterManager.cluster();
            // Log.d(TAG, "onMapReady: mClusterManager.getMarkerCollection().getMarkers().size(): " + mClusterManager.getMarkerCollection().getMarkers().size());
            // mClusterManager.getMarkerCollection().getMarkers().size();

            /*
            java.util.Collection<Marker> markerCollection = mClusterManager.getMarkerCollection().;
            ArrayList<Marker> userList = new ArrayList<>(markerCollection);
            Log.d(TAG, "onMapReady: userList.size() " + userList.size());
            for (Marker marker : userList) {
                if (marker.getPosition().latitude == chosenRestaurantCoords.latitude && marker.getPosition().longitude == chosenRestaurantCoords.longitude) {
                    marker.showInfoWindow();
                }
            }

             */

            /*
            Log.d(TAG, "onMapReady: mClusterManager.getMarkerCollection().getMarkers().size(): " + mClusterManager.getMarkerCollection().getMarkers().size());
            for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
                Log.d(TAG, "onMapReady: marker.getPosition: " + marker.getPosition().toString());
                if (marker.getPosition().latitude == chosenRestaurantCoords.latitude && marker.getPosition().longitude == chosenRestaurantCoords.longitude) {
                    Log.d(TAG, "onMapReady: Correct marker found: marker.getPosition: " + marker.getPosition().toString());
                    break;
                }
            }
             */
            /*
            Marker currMarker;
            // Log.d(TAG, "onMapReady: mMarkerArray.size(): " + mMarkerArray.size());
            for (int i = 0; i < mMarkerArray.size(); i++) {
                currMarker = mMarkerArray.get(i);
                // Log.d(TAG, "onMapReady: marker[i]: currMarker [lat,lon]: [" + currMarker.getPosition().latitude + "," + currMarker.getPosition().longitude + "]");
                // Log.d(TAG, "onMapReady: marker[i]: chosenRestaurantCoords [lat,lon]: [" + chosenRestaurantCoords.latitude + "," + chosenRestaurantCoords.longitude + "]");
                if ((currMarker.getPosition().latitude == chosenRestaurantCoords.latitude) && (currMarker.getPosition().longitude == chosenRestaurantCoords.longitude)) {
                    Log.d(TAG, "onMapReady: Found restaurant: currMarker position: " + currMarker.getPosition().toString());

                    for( Marker m : mClusterManager.getMarkerCollection().getMarkers()) {
                        if (m.getPosition().latitude == currMarker.getPosition().latitude && m.getPosition().longitude == currMarker.getPosition().longitude) {
                            Log.d(TAG, "onMapReady: inside mClusterManager iteration, showing window");
                            m.showInfoWindow();
                        }
                    }
                    break;
                }
            }
             */
        }

        registerClickCallback();

        // Move the camera to surrey
        // TODO: The camera should pan to user's location on startup
        // LatLng surrey = new LatLng(49.104431, -122.801094);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(surrey));

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
                moveCamera(cluster.getPosition(), 20);
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

        // mRenderer = new DefaultClusterRenderer<>(this, mMap, mClusterManager);
        // mClusterManager.setRenderer(mRenderer);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        populateMapWithMarkers();
    }

    private void populateMapWithMarkers() {
        // Get Singleton RestaurantManager
        RestaurantManager manager = RestaurantManager.getInstance();

        for (Restaurant restaurant : manager) {
            // Add PegItem to ClusterManager
            PegItem pegItem = new PegItem(
                    restaurant.getLatitude(),
                    restaurant.getLongitude(),
                    restaurant.getName(),
                    getHazardIcon(restaurant)
            );
            mClusterManager.addItem(pegItem);

            // Add PegItem to array
            // mMarkerArray.add(pegItem);

            // Add Marker to mMarkerArray
            // LatLng location = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            // Marker marker = mMap.addMarker(new MarkerOptions().position(location));
            // mMarkerArray.add(marker);
        }

        // Set MapMarkers to invisible in mMarkerArray
        /*
        for (int i = 0; i < mMarkerArray.size(); i++) {
            mMarkerArray.get(i).setVisible(false);
        }
         */
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

    private BitmapDescriptor getHazardIcon(Restaurant restaurant) {
        Inspection RecentInspection = restaurant.getInspection(0);
        BitmapDescriptor hazardIcon = bitmapDescriptorFromVector(this,R.drawable.peg_green);
        if (RecentInspection != null) {
            String hazardLevel = RecentInspection.getHazardRating();

            if (hazardLevel.equals("Low")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_green);
            } else if (hazardLevel.equals("Moderate")) {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_yellow);
            } else {
                hazardIcon = bitmapDescriptorFromVector(this, R.drawable.peg_red);
            }
        }
        return hazardIcon;
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
}