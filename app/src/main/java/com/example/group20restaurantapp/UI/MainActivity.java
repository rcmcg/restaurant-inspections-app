package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.Model.TestCar;
import com.example.group20restaurantapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RestaurantManager manager;
    // private RestaurantManager restaurants = RestaurantManager.getInstance();
    private List<TestCar> myCars = new ArrayList<TestCar>();

    private String testName = "Lee Yuen Seafood Restaurant";
    private String testDate = "Last inspection: Jan 22";
    private int testNumInspections = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Following functions taken from Dr. Fraser's video linked below
        // https://www.youtube.com/watch?v=WRANgDgM2Zg
        // TODO: Update code so it works with the RestaurantManager after it has been filled
        populateRestaurantList();

        readRestaurantData();
        populateListView();
        registerClickCallback();
        InitInspectionLists();
    }

    private void readRestaurantData() {
        // manager is the instance of restaurant manager. Use this one to populate list
        manager= RestaurantManager.getInstance();

        // To read a resource, need an input stream
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);

        // To read from stream reader line by line, need a bufferreader
        // Need a build an input stream based on a character set
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try {
            //Escaping the header lines of the CSV file
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                //Splitting every line based on "," , Tokens are variables of Restaurant class
                String[] tokens=line.split(",");
                Restaurant r1=new Restaurant();
                r1.setTrackingNumber(tokens[0]);
                r1.setName(tokens[1]);
                r1.setAddress(tokens[2]);
                r1.setCity(tokens[3]);
                r1.setFacType(tokens[4]);
                r1.setLatitude(Double.parseDouble(tokens[5]));
                r1.setLongitude(Double.parseDouble(tokens[6]));
                r1.setIcon(0);
                //Adding the created Restaurants object to the manager instance
                manager.add(r1);
                //Log.d("Main activity","Just created" + r1);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    // TODO:I already have added the restaurants in manager instance so U can remove this function
    private void populateRestaurantList() {
        // This function will populate the RestaurantManager with Restaurants
        // For now uses the TestCar class
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
        // ArrayAdapter<RestaurantManager> adapter = new MyListAdapter();
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

            // Find the restaurant to work with
            // Restaurant currentRestaurant = RestaurantManager.getIndex(position);

            // Fill the restaurantIcon
            ImageView imgRestaurant = (ImageView) itemView.findViewById(R.id.restaurant_item_imgRestaurantIcon);
            // imgRestaurant.setImageDrawable(currentRestaurant.getIconID());

            // Fill the hazard icon
            ImageView imgHazardIcon = itemView.findViewById(R.id.restaurant_item_imgHazardIcon);

            // Change which icon is shown for demonstration
            if (position%4 == 1) {
                imgHazardIcon.setImageResource(R.drawable.yellow_triangle);
            } else if (position%4 == 2) {
                imgHazardIcon.setImageResource(R.drawable.orange_diamond);
            } else if (position%4 == 3) {
                imgHazardIcon.setImageResource(R.drawable.red_octogon);
            }

            // Set restaurant name text
            TextView restaurantName = itemView.findViewById(R.id.restaurant_item_txtRestaurantName);

            // Set last inspection date text
            TextView lastInspectionDate = itemView.findViewById(R.id.restaurant_item_txtLastInspectionDate);

            // Set number of violations text
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);

            // Set text to test data
            restaurantName.setText(testName);
            lastInspectionDate.setText(testDate);
            numViolationsLastInspection.setText("Violations: " + testNumInspections);

            return itemView;
        }
    }

    private void registerClickCallback() {
            ListView list = (ListView) findViewById(R.id.restaurantsListView);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                    TestCar clickedCar = myCars.get(position);
                    String message = "You clicked position " + position;
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void InitInspectionLists() {
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";

        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null){
                //Log.d("MyActivity", "Line: " + line);
                int i = 0;

                String[] lineSplit = line.split(",", 7);
                String trackNumCompare[] = {lineSplit[0], RestaurantManager.getInstance().getIndex(i).getTrackingNumber()};

                while (!trackNumCompare[0].equals(trackNumCompare[1])){
                    i++;
                    trackNumCompare[1] = RestaurantManager.getInstance().getIndex(i).getTrackingNumber();
                }

                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(lineSplit[0]);
                inspection.setInspectionDate(lineSplit[1]);
                inspection.setInspType(lineSplit[2]);
                inspection.setNumCritical(Integer.parseInt(lineSplit[3]));
                inspection.setNumNonCritical(Integer.parseInt(lineSplit[4]));
                inspection.setHazardRating(lineSplit[5]);
                String violations = lineSplit[6];

                String[] violationsSplit = violations.split("\\|");

                if (violationsSplit[0] != ""){
                    Log.d("MyActivity", "Violations for " + lineSplit[0] + ":" + Arrays.toString(violationsSplit));
                    for (String token : violationsSplit){
                        Log.d("MyActivity", "--Violations split: " + token);
                        inspection.getViolLump().add(token);
                    }
                }
                //Log.d("MyActivity", "Inspection: " + inspection);
                RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection);
            }

        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }

        /*int j = 0;
        while(j < 8){
            Log.d("MyActivity", "Num Inspections: " + RestaurantManager.getInstance().getIndex(j).getInspectionList().size());
            j++;
        }*/

    }
}
