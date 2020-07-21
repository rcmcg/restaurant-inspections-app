package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.PegItem;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private RestaurantManager manager;

    private String restaurantDataURL;
    private String inspectionDataURL;

    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";

    private Date currentDate;

    private ClusterManager<PegItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        wireLaunchListButton();

        manager = RestaurantManager.getInstance();
        currentDate = new Date();

        // Update app on startup if necessary
        long appLastUpdated = getAppLastUpdated(this);
        if (appLastUpdated == -1) {
            // update the app
            // Check server for new data
            String[] restaurantDataURLDate = manager.getURL("https://data.surrey.ca/api/3/action/package_show?id=restaurants"); //Retrieve url used to request csv
            String[] inspectionDataURLDate = manager.getURL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");

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

            // Download new data
            initiateDownload();
            saveAppLastUpdated(currentDate.getTime());
        }

        // App is always updated if it's out of date, only read new data
        // Save new data
        fillRestaurantManager(true);

        Log.d(TAG, "onCreate: manager.getSize(): " + manager.getSize());
    }

    private void initiateDownload() {
        String newRestaurantData = "", newInspectionData = "";
        newRestaurantData = manager.getCSV(restaurantDataURL); //Request updated restaurant data csv
        newInspectionData = manager.getCSV(inspectionDataURL); //Request updated inspection data csv

        // Write new data to file
        manager.writeToFile(newRestaurantData, WEB_SERVER_RESTAURANTS_CSV, this);
        Log.d(TAG, "initiateDownload: finished writing newRestaurantData");
        manager.writeToFile(newInspectionData, WEB_SERVER_INSPECTIONS_CSV, this);
        Log.d(TAG, "initiateDownload: finished writing newInspectionData");
    }

    private Date getDateFromString(String rawString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.parse(rawString);
    }

    // TODO: Move to RestaurantManager.java?
    private void fillRestaurantManager(boolean hasAppBeenUpdated) {
        manager = RestaurantManager.getInstance();
        if (!hasAppBeenUpdated) {
            // manager.readRestaurantData(this);
            // manager.initInspectionLists(this);
        } else {
            manager.readNewRestaurantData(this);
            manager.initNewInspectionLists(this);
        }
        manager.sortInspListsByDate();
        manager.sortRestaurantsByName();
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

        // Add a marker in Surrey and move the camera
        // LatLng surrey = new LatLng(49.104431, -122.801094);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surrey, 10));

        // Move camera to Dragon Lounge
        LatLng dragonLounge = new LatLng(49.11171722,-122.72962952);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dragonLounge, 20));

        setUpClusterer();
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<PegItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setRenderer(new MyClusterRenderer(this, mMap, mClusterManager));

        addItems();
    }

    private void addItems() {
        double lat, lng;
        String title;
        String snippet = "this is the snippet";
        BitmapDescriptor icon;
        for (Restaurant restaurant : manager) {
            lat = restaurant.getLatitude();
            lng = restaurant.getLongitude();
            title = restaurant.getName();
            icon = getBitmapDescriptorForPegItem(restaurant);
            PegItem infoWindowItem = new PegItem(lat,lng, title, snippet, icon);
            mClusterManager.addItem(infoWindowItem);
        }
    }

    private BitmapDescriptor getBitmapDescriptorForPegItem(Restaurant restaurant) {
        BitmapDescriptor icon;
        Inspection lastInspection = null;
        if (restaurant.getInspectionSize() != 0) {
            lastInspection = restaurant.getInspectionList().get(0);
            if (lastInspection.getHazardRating().equals("Low")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.yellow_triangle_small);
            } else if (lastInspection.getHazardRating().equals("Moderate")) {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.orange_diamond_small);
            } else {
                icon = BitmapDescriptorFactory.fromResource(R.drawable.red_octagon_small);
            }
        } else {
            icon = BitmapDescriptorFactory.fromResource(R.drawable.no_inspection_qmark_small);
        }
        return icon;
    }

    private void saveAppLastUpdated(long currentDateInMs) {
        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("appLastUpdated", currentDateInMs);
        editor.apply();
    }

    static public long getAppLastUpdated(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getLong("appLastUpdated", -1);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    private class MyClusterRenderer extends DefaultClusterRenderer<PegItem> {

        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<PegItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull PegItem item, @NonNull MarkerOptions markerOptions) {
            markerOptions.icon(item.getIcon());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected boolean shouldRenderAsCluster(@NonNull Cluster<PegItem> cluster) {
            return (cluster.getSize() >= 8);
        }
    }
}