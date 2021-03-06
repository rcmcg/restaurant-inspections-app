package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Activity that displays restaurants in internal storage in list form. User can switch to the
 * MapActivity or select a restaurant in the list to launch RestaurantActivity for that restaurant
 */

public class MainActivity extends AppCompatActivity {
    public static final String RESTAURANT_INDEX_INTENT_TAG = "restaurantIndex";
    public static final int LAUNCH_SEARCH_ACTIVITY = 458;
    private RestaurantManager manager = RestaurantManager.getInstance();

    // Yellow, orange, red, with 20% transparency
    public static int[] itemViewBackgroundColours = {0x33FFFF00, 0x33FFA500, 0x33FF0000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateListView();
        registerClickCallback();
        wireLaunchMapButton();
        wireLaunchSearchButton();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    // Wires button to launch SearchActivity
    private void wireLaunchSearchButton() {
        Button btnSearch = findViewById(R.id.GoToSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, LAUNCH_SEARCH_ACTIVITY);
            }
        });
    }

    // Wires button to launch MapsActivity
    private void wireLaunchMapButton() {
        Button btn = findViewById(R.id.btnLaunchMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntent(MainActivity.this);
                finish();
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        // Construct a new ArrayList from the manager Singleton to fill the listView
        List<Restaurant> restaurantList = restaurantList();

        // Setup the listView
        ArrayAdapter<Restaurant> adapter = new MyListAdapter(restaurantList);
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setAdapter(adapter);
    }

    // Creates a new list for the listView from the manager Singleton
    public ArrayList<Restaurant> restaurantList() {
        ArrayList<Restaurant> newRestaurantList = new ArrayList<>();
        for (Restaurant restaurant : manager) {
            newRestaurantList.add(restaurant);
        }
        return newRestaurantList;
    };

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(List<Restaurant> restaurantList) {
            super(MainActivity.this, R.layout.restaurant_item_view, restaurantList);
        }

        @SuppressLint({"StringFormatMatches", "StringFormatInvalid"})
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_item_view, parent, false);
            }

            // Find the restaurant to work with
            final Restaurant currentRestaurant = manager.getIndexFilteredRestaurants(position);

            final ImageView imgFavourite = itemView.findViewById(R.id.img_favourite);

            // Set imgFavourite accordingly
            if (currentRestaurant.isFavourite()){
                imgFavourite.setImageResource(R.drawable.star_on);
                imgFavourite.setTag("favourited");
            } else {
                imgFavourite.setImageResource(R.drawable.star_off);
                imgFavourite.setTag("unfavourited");
            }

            // Fill the restaurantIcon
            ImageView imgRestaurant = (ImageView) itemView.findViewById(R.id.restaurant_item_imgRestaurantIcon);
            imgRestaurant.setImageResource(currentRestaurant.getIconImgId());
            Log.d("MainActivity", "getView: currentRestaurant.getIconImgId: " + currentRestaurant.getIconImgId());

            // Fill the hazard icon and the background color of each item
            ImageView imgHazardIcon = itemView.findViewById(R.id.restaurant_item_imgHazardRating);
            if (currentRestaurant.getInspectionSize() != 0) {
                // Inspection list in Restaurant is sorted on startup so the first index is the most recent
                Inspection lastInspection = currentRestaurant.getInspection(0);
                if (lastInspection.getHazardRating().equals("Low")) {
                    imgHazardIcon.setImageResource(R.drawable.yellow_triangle);
                    itemView.setBackgroundColor(itemViewBackgroundColours[0]);
                } else if (lastInspection.getHazardRating().equals("Moderate")) {
                    imgHazardIcon.setImageResource(R.drawable.orange_diamond);
                    itemView.setBackgroundColor(itemViewBackgroundColours[1]);
                } else if (lastInspection.getHazardRating().equals("High")) {
                    imgHazardIcon.setImageResource(R.drawable.red_octogon);
                    itemView.setBackgroundColor(itemViewBackgroundColours[2]);
                }
            } else {
                // No inspection for this restaurant
                imgHazardIcon.setImageResource(R.drawable.no_inspection_qmark);
            }

            // Set restaurant name text
            TextView restaurantName = itemView.findViewById(R.id.restaurant_item_txtRestaurantName);
            String temp=String.format(getResources().getString(R.string.main_activity_restaurant_name,currentRestaurant.getName()));
            restaurantName.setText(temp);

            // Set last inspection date text
            TextView lastInspectionDate = itemView.findViewById(R.id.restaurant_item_txtLastInspectionDate);
            if (currentRestaurant.getInspectionSize() != 0) {
                //Inspections list is sorted, so the first inspection is the latest
                Inspection lastInspection = currentRestaurant.getInspection(0);
                String temp2=String.format(getResources().getString(R.string.main_activity_restaurant_item_last_inspection_date,
                        lastInspection.intelligentInspectDate()));
                lastInspectionDate.setText(temp2);
            } else {
                lastInspectionDate.setText(getString(R.string.main_activity_restaurant_item_last_inspection_date_no_inspection));
            }

            // Set number of violations text
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);

            if (currentRestaurant.getInspectionSize() != 0) {
                Inspection lastInspection = currentRestaurant.getInspection(0);
                // Sums up number of critical and non-critical violations
                String sumOfViolations = "" + (lastInspection.getNumCriticalViolations() + lastInspection.getNumNonCriticalViolations());
                String temp3= String.format(getResources().getString(R.string.main_activity_restaurant_item_violations, sumOfViolations));
                numViolationsLastInspection.setText(temp3);
            } else {
                // If the restaurant had no violation in an inspection
                numViolationsLastInspection.setText(getString(R.string.main_activity_restaurant_item_violations_no_inspection));
            }

            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Restaurant clickedRestaurant = manager.getIndexFilteredRestaurants(position);

                // Launch restaurant activity
                Intent intent = RestaurantActivity.makeLaunchIntent(MainActivity.this);
                intent.putExtra(RESTAURANT_INDEX_INTENT_TAG, position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SearchActivity.RESULT_OK) {
            populateListView();
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}