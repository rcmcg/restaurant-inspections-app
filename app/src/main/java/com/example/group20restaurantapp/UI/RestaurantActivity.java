package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    public static final String RESTAURANT_ACTIVITY_INSPECTION_TAG = "inspection";
    public static final String RESTAURANT_LATITUDE_INTENT_TAG = "Restaurant latitude";
    public static final String RESTAURANT_LONGITUDE_INTENT_TAG = "Restaurant longitude";
    public static final String RESTAURANT_NAME_INTENT_TAG = "Restaurant name";
    private RestaurantManager manager;
    List<Inspection> inspections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int restaurantIndex = getIntent().getIntExtra(MainActivity.RESTAURANT_INDEX_INTENT_TAG,-1);

        // Get singleton
        manager = RestaurantManager.getInstance();

        Restaurant restaurant = null;
        if (restaurantIndex == -1) {
            Log.e("RestaurantActivity", "onCreate: Activity opened with no restaurant");
        } else {
            restaurant = manager.getIndex(restaurantIndex);
        }

        assert restaurant != null;
        inspections = restaurant.getInspectionList();

        setRestaurantText(restaurant);
        setAddressText(restaurant);
        setCoordsTextAndClickCallback(restaurant);
        setRestaurantImg(restaurant);

        populateInspectionList(restaurant);
        registerClickCallback(restaurant);
        // setupDefaultIntent();
    }

    // Source
    // https://stackoverflow.com/questions/36457564/display-back-button-of-action-bar-is-not-going-back-in-android/36457747
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRestaurantImg(Restaurant restaurant) {
        ImageView imgViewRestaurant = findViewById(R.id.restaurant_img);
        imgViewRestaurant.setImageResource(restaurant.getIconImgId());
    }

    private void setRestaurantText(Restaurant restaurant) {
        TextView txtViewRestaurantName = findViewById(R.id.name_resActivity);
        txtViewRestaurantName.setText(
                getString(R.string.restaurant_activity_restaurant_name,restaurant.getName())
        );
    }

    private void setAddressText(Restaurant restaurant) {
        TextView txtViewAddress = findViewById(R.id.address_resActivity);
        txtViewAddress.setText(
                getString(R.string.restaurant_activity_restaurant_address,restaurant.getAddress())
        );
    }

    private void setCoordsTextAndClickCallback(final Restaurant restaurant) {
        TextView txtViewCoords = findViewById(R.id.coords_resActivity);
        String coordsString = "" + restaurant.getLatitude() + "," + restaurant.getLongitude();
        txtViewCoords.setText(
                getString(R.string.restaurant_activity_restaurant_coords,coordsString)
        );

        txtViewCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get intent from MapsActivity
                Intent intent = MapsActivity.makeIntent(RestaurantActivity.this);

                // Launch maps activity to open restaurant marker with location data and name
                intent.putExtra(RESTAURANT_LATITUDE_INTENT_TAG, restaurant.getLatitude());
                intent.putExtra(RESTAURANT_LONGITUDE_INTENT_TAG, restaurant.getLongitude());
                intent.putExtra(RESTAURANT_NAME_INTENT_TAG, restaurant.getName());
                startActivity(intent);
            }
        });
    }

    private void populateInspectionList(Restaurant restaurant) {
        manager = RestaurantManager.getInstance();

        ArrayAdapter<Inspection> adapter = new CustomAdapter();
        ListView list = (ListView) findViewById(R.id.restaurant_view);
        list.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<Inspection> {
        public CustomAdapter() {
            super(RestaurantActivity.this, R.layout.inspection_item_view, inspections);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.inspection_item_view, parent, false);
            }
            Inspection currentInspection = inspections.get(position);

            // Set icon and background color for each itemView
            String temp = currentInspection.getHazardRating();
            ImageView imageview = (ImageView) itemView.findViewById(R.id.imgViewViolationIcon);
            if (temp.equals("Low")) {
                imageview.setImageResource(R.drawable.yellow_triangle);
                itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[0]);
            } else if (temp.equals("Moderate")){
                imageview.setImageResource(R.drawable.orange_diamond);
                itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[1]);
            } else if (temp.equals("High")){
                imageview.setImageResource(R.drawable.red_octogon);
                itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[2]);
            }

            // Set inspection date
            TextView inspectionDateTxt = itemView.findViewById(R.id.inspection_item_txtViewInspectionDate);
            inspectionDateTxt.setText(
                    getString(
                            R.string.restaurant_activity_inspection_item_date,
                            currentInspection.intelligentInspectDate())
            );

            // Set critical violations text
            TextView critViolationsTxt = itemView.findViewById(R.id.inspection_item_txtViewCritViolations);
            critViolationsTxt.setText(
                    getString(
                            R.string.restaurant_activity_inspection_item_crit_viols,
                            "" + currentInspection.getNumCritical())
            );

            // Set non-critical violations text
            TextView nonCritViolationsTxt = itemView.findViewById(R.id.inspection_item_txtViewNonCritViolations);
            nonCritViolationsTxt.setText(
                    getString(
                            R.string.restaurant_activity_inspection_item_non_crit_viols,
                            "" + currentInspection.getNumNonCritical())
            );

            return itemView;
        }
    }

    private void registerClickCallback(final Restaurant restaurant) {
        // Set list of inspections as clickable to launch InspectionActivity
        ListView list = findViewById(R.id.restaurant_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inspection selectedInspection = restaurant.getInspectionList().get(position);
                Intent intent = InspectionActivity.makeIntent(RestaurantActivity.this);
                intent.putExtra(RESTAURANT_ACTIVITY_INSPECTION_TAG, selectedInspection);
                startActivity(intent);
            }
        });
    }

    /*
    private void setupDefaultIntent() {
        Intent i = new Intent();
        i.putExtra("result", 0);
        setResult(Activity.RESULT_OK, i);
    }
     */

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, RestaurantActivity.class);
    }
}

