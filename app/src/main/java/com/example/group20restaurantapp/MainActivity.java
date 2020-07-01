package com.example.group20restaurantapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // private RestaurantManager restaurants = RestaurantManager.getInstance();
    private List<TestCar> myCars = new ArrayList<TestCar>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String testName = "Lee Yuen Seafood Restaurant";
        String testDate = "Jan 22";
        int testNumInspections = 5;

        populateRestaurantList();
        populateListView();
        registerClickCallback();
    }

    private void populateRestaurantList() {
        // This function will populate the RestaurantManager with Restaurants
        myCars.add(new TestCar("Ford", 1940, 0, "Needing work"));
        myCars.add(new TestCar("Toyota", 1994, 0, "Lovable"));
        myCars.add(new TestCar("Honda", 1999, 0, "Wet"));
        myCars.add(new TestCar("Porche", 2005, 0, "Fast!"));
        myCars.add(new TestCar("Jeep", 200, 0, "Awesome"));
        myCars.add(new TestCar("Rust-Bucket", 2010, 0, "Not *very* good"));
        myCars.add(new TestCar("Moon Lander", 1971, 0, "Out of this world"));
    }

    private void populateListView() {
        ArrayAdapter<TestCar> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<TestCar> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.restaurant_item_view, myCars);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurant_item_view, parent, false);
            }

            // Find the car to work with
            TestCar currentCar = myCars.get(position);

            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.restaurant_item_imgRestaurantIcon);
            // imageView.setImageDrawable(currentCar.getIconID());

            // Set Restaurant Name
            TextView restaurantName = itemView.findViewById(R.id.restaurant_item_txtRestaurantName);
            restaurantName.setText(currentCar.getMake());

            // Set last inspection date
            TextView lastInspectionDate = itemView.findViewById(R.id.restaurant_item_txtLastInspectionDate);
            lastInspectionDate.setText("" + currentCar.getYear());

            // Set number of violations
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);
            numViolationsLastInspection.setText("" + currentCar.getYear()%5);

            return itemView;
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TestCar clickedCar = myCars.get(position);
                String message = "You clicked position " + position
                                + " Which is car make " + clickedCar.getMake();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}