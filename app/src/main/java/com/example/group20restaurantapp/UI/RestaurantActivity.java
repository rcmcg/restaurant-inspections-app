package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class RestaurantActivity extends AppCompatActivity {

    private RestaurantManager manager;
    // private Restaurant restaurant;
    // private int size = 0;

    // private String[] inspectionStrings = new String[size];

    private static final String EXTRA_MESSAGE = "Extra";

    // The name of the restaurant is stored in the Restaurant object
    // private String restaurantString;    // Name of calling restaurant object
    // The Restaurant object contains a list of inspections
    // private ArrayList<Inspection> inspectionList;
    List<Inspection> inspections;

    public static Intent makeLaunchIntent(Context c, String message) {
        Intent intent = new Intent(c, RestaurantActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int restaurantIndex = getIntent().getIntExtra("restaurantIndex",-1);
        Log.d("RestaurantActivity", "onCreate: restaurantIndex = " + restaurantIndex);

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

        populateInspectionList(restaurant);
        registerClickCallback(restaurant);
        setupDefaultIntent();
    }

    private void setupDefaultIntent() {
        Intent i = new Intent();
        i.putExtra("result", 0);
        setResult(Activity.RESULT_OK, i);
    }

    private void populateInspectionList(Restaurant restaurant) {

        manager = RestaurantManager.getInstance();
        processInspections(restaurant);

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
            String temp = currentInspection.getHazardRating();
            if (temp.equals("Low")) {
                ImageView imageview = (ImageView) itemView.findViewById(R.id.imgViewViolationIcon);
                imageview.setImageResource(R.drawable.yellow_triangle);
            } else if (temp.equals("Moderate")){
                ImageView imageview = (ImageView) itemView.findViewById(R.id.imgViewViolationIcon);
                imageview.setImageResource(R.drawable.orange_diamond);
            } else if (temp.equals("High")){
                ImageView imageview = (ImageView) itemView.findViewById(R.id.imgViewViolationIcon);
                imageview.setImageResource(R.drawable.red_octogon);
            }

            // Set inspection date
            TextView inspectionDateTxt = itemView.findViewById(R.id.inspection_item_txtViewInspectionDate);
            inspectionDateTxt.setText("Inspection date: " + currentInspection.intelligentInspectDate());

            // Set critical violations text
            TextView critViolationsTxt = itemView.findViewById(R.id.inspection_item_txtViewCritViolations);
            critViolationsTxt.setText("Critical violaions: " + currentInspection.getNumCritical());

            // Set non-critical violations text
            TextView nonCritViolationsTxt = itemView.findViewById(R.id.inspection_item_txtViewNonCritViolations);
            nonCritViolationsTxt.setText("Non-critical violaions: " + currentInspection.getNumNonCritical());

            return itemView;
        }

    }


    private void processInspections(Restaurant restaurant) {
        // This code now sets the strings outside the listview, has nothing to do with inspections
        // Consider changing its name/functionality

        // Receive message from MainActivity
        // Message contains details of selected Restaurant
        // Intent intent2 = getIntent();
        // restaurantString = intent2.getStringExtra(EXTRA_MESSAGE);

        // Find the Restaurant and assign it to our local Restaurant object
        /*
        for (Restaurant temp : manager) {
            if (temp.toString().equals(restaurantString)) {
                restaurant = temp;
            }
        }
         */
        // Populate the list of inspections for the selected restaurant

        // inspectionList = (ArrayList<Inspection>) restaurant.getInspectionList();
        TextView txtViewRestaurantName = findViewById(R.id.name_resActivity);
        txtViewRestaurantName.setText(restaurant.getName());

        TextView txtViewAddress = findViewById(R.id.address_resActivity);
        txtViewAddress.setText("Address: " + restaurant.getAddress());

        TextView txtViewCoords = findViewById(R.id.coords_resActivity);
        txtViewCoords.setText("Coordinates: " + (float) restaurant.getLatitude() + ", " + (float) restaurant.getLongitude());
    }

    private void registerClickCallback(final Restaurant restaurant) {
        ListView list = findViewById(R.id.restaurant_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inspection selectedInspection = restaurant.getInspectionList().get(position);
                Intent intent = InspectionActivity.makeLaunchIntent(RestaurantActivity.this, "InspectionActivity");
                intent.putExtra("inspection", selectedInspection);
                startActivity(intent);
            }
        });

    }

}
