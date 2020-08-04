package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

import java.util.List;

/**
 * Activity to display information about a single restaurant. User can select an inspection to get
 * more information about it by launching InspectionActivity, or select the coordinates to launch
 * MapsActivity centered on the restaurant.
 */

public class RestaurantActivity extends AppCompatActivity {

    public static final String RESTAURANT_ACTIVITY_INSPECTION_TAG = "inspection";
    public static final String RESTAURANT_LATITUDE_INTENT_TAG = "Restaurant latitude";
    public static final String RESTAURANT_LONGITUDE_INTENT_TAG = "Restaurant longitude";
    public static final String RESTAURANT_NAME_INTENT_TAG = "Restaurant name";
    private RestaurantManager manager;
    List<Inspection> inspections;
    SharedPreferences preferences;
    private Menu restaurantMenu;
    int restaurantIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restaurantIndex = getIntent().getIntExtra(MainActivity.RESTAURANT_INDEX_INTENT_TAG,-1);

        // Get singleton
        manager = RestaurantManager.getInstance();

        Restaurant restaurant = null;
        if (restaurantIndex == -1) {
            Log.e("RestaurantActivity", "onCreate: Activity opened with no restaurant");
        } else {
            restaurant = manager.getIndexFilteredRestaurants(restaurantIndex);
        }

        assert restaurant != null;
        //inspection list of a single restaurant
        inspections = restaurant.getInspectionList();
        //sets a Textview with restaurant name
        setRestaurantText(restaurant);
        //sets a TextView with restaurant address
        setAddressText(restaurant);
        setCoordsTextAndClickCallback(restaurant);
        setRestaurantImg(restaurant);

        preferences = getSharedPreferences("favourites", 0);

        populateInspectionList(restaurant);
        registerClickCallback(restaurant);
        // setupDefaultIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        restaurantMenu = menu;
        setFavouritedImg();
        //list of favourite restaurants
        final MenuItem favouriteItem = restaurantMenu.findItem(R.id.favourite);
        final Restaurant restaurant = manager.getIndexFilteredRestaurants(restaurantIndex);
        //if the user clicks on a favourite restaurant
        favouriteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //it is removed from the favourite restaurant
                if (restaurant.isFavourite()){
                    manager.removeFavRestaurant(restaurant);
                    favouriteItem.setIcon(R.drawable.star_off);
                    restaurant.setFavourite(false);
                }
                //It is marked as favourite restaurant
                else{
                    favouriteItem.setIcon(R.drawable.star_on);
                    restaurant.setFavourite(true);
                    manager.addFavRestaurant(restaurant);
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(restaurant.getTrackingNumber(), restaurant.isFavourite());
                editor.apply();

                return false;
            }
        });

        return true;
    }
    // Source
    // https://stackoverflow.com/questions/36457564/display-back-button-of-action-bar-is-not-going-back-in-android/36457747
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //sets the restaurant image in image view
    private void setRestaurantImg(Restaurant restaurant) {
        ImageView imgViewRestaurant = findViewById(R.id.restaurant_img);
        imgViewRestaurant.setImageResource(restaurant.getIconImgId());
    }
    //sets the restaurant name
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
     //if a restaurant is favourite then it has a yellow star or else a grey star
    private void setFavouritedImg() {
        Restaurant restaurant = manager.getIndexFilteredRestaurants(restaurantIndex);
        MenuItem favouriteItem = restaurantMenu.findItem(R.id.favourite);

        //favourited = preferences.getBoolean(restaurant.getTrackingNumber(), false);
        if (restaurant.isFavourite()){
            favouriteItem.setIcon(R.drawable.star_on);
        }
        else{
            favouriteItem.setIcon(R.drawable.star_off);
        }
    }
     //If an user presses any restaurant's address then the latitude and longitude is passed to the maps activity
    //to launch the map from that location
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
    //populates each restaurant with it's own inspection list
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
            //Sets each violation with the appropriate image
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
                //If a user clicks on any inspection then launches the inspection activity
                //where a list of violation is displayed with details and an image
                Intent intent = InspectionActivity.makeIntent(RestaurantActivity.this);
                intent.putExtra(RESTAURANT_ACTIVITY_INSPECTION_TAG, selectedInspection);
                startActivity(intent);
            }
        });
    }

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, RestaurantActivity.class);
    }
}

