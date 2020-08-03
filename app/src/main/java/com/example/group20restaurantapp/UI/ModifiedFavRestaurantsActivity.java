package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;

import java.util.List;

public class ModifiedFavRestaurantsActivity extends AppCompatActivity {

    private RestaurantManager manager = RestaurantManager.getInstance();
    private List<Restaurant> modifiedRestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modified_fav_restaurants);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateListView();
        wireOkButton();
    }


    private void populateListView() {
        // Fill the list of favourite restaurants that have been modified since last update
        modifiedRestaurantList = manager.getListOfModifiedRestaurants();

        // Setup the ListView
        ArrayAdapter<Restaurant> adapter = new MyListAdapter(modifiedRestaurantList);
        ListView list = (ListView) findViewById(R.id.modifiedFavRestaurantsListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        public MyListAdapter(List<Restaurant> restaurantList) {
            super(ModifiedFavRestaurantsActivity.this, R.layout.restaurant_item_view, restaurantList);
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
            final Restaurant currentRestaurant = modifiedRestaurantList.get(position);

            final ImageView imgFavourite = itemView.findViewById(R.id.img_favourite);
            // Set imgFavourite accordingly
            if (currentRestaurant.isFavourite()){
                imgFavourite.setImageResource(R.drawable.star_on);
                imgFavourite.setTag("favourited");
            }
            else{
                imgFavourite.setImageResource(R.drawable.star_off);
                imgFavourite.setTag("unfavourited");
            }

            // Fill the restaurantIcon
            ImageView imgRestaurant = (ImageView) itemView.findViewById(R.id.restaurant_item_imgRestaurantIcon);
            imgRestaurant.setImageResource(currentRestaurant.getIconImgId());
            Log.d("MainActivity", "getView: currentRestaurant.getIconImgId: " + currentRestaurant.getIconImgId());

            // Fill the hazard icon and the background color of each item
            ImageView imgHazardIcon = itemView.findViewById(R.id.restaurant_item_imgHazardRating);
            if (currentRestaurant.getInspectionList().size() != 0) {
                // Inspection list in Restaurant is sorted on startup so the first index is the most recent
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);
                if (lastInspection.getHazardRating().equals("Low")) {
                    imgHazardIcon.setImageResource(R.drawable.yellow_triangle);
                    itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[0]);
                } else if (lastInspection.getHazardRating().equals("Moderate")) {
                    imgHazardIcon.setImageResource(R.drawable.orange_diamond);
                    itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[1]);
                } else if (lastInspection.getHazardRating().equals("High")) {
                    imgHazardIcon.setImageResource(R.drawable.red_octogon);
                    itemView.setBackgroundColor(MainActivity.itemViewBackgroundColours[2]);
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
            if (currentRestaurant.getInspectionList().size() != 0) {
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);
                String temp2=String.format(getResources().getString(R.string.main_activity_restaurant_item_last_inspection_date,
                        lastInspection.intelligentInspectDate()));
                lastInspectionDate.setText(temp2);
            } else {
                lastInspectionDate.setText(getString(R.string.main_activity_restaurant_item_last_inspection_date_no_inspection));
            }

            // Set number of violations text
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);
            if (currentRestaurant.getInspectionList().size() != 0) {
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);
                String sumOfViolations = "" + (lastInspection.getNumCritical() + lastInspection.getNumNonCritical());
                String temp3= String.format(getResources().getString(R.string.main_activity_restaurant_item_violations, sumOfViolations));
                numViolationsLastInspection.setText(temp3);
            } else {
                numViolationsLastInspection.setText(getString(R.string.main_activity_restaurant_item_violations_no_inspection));
            }

            return itemView;
        }
    }

    private void wireOkButton() {
        Button btn = findViewById(R.id.btnOkModifiedFav);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    public static Intent makeIntent(Context context) {
        return new Intent(context, ModifiedFavRestaurantsActivity.class);
    }
}
