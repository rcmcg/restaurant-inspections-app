package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Violation;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
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
    private RestaurantManager manager = RestaurantManager.getInstance();

    private String testDate = "Last inspection: Jan 22";
    private int testNumInspections = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readRestaurantData();

        // Following functions taken from Dr. Fraser's video linked below
        // https://www.youtube.com/watch?v=WRANgDgM2Zg
        populateListView();
        registerClickCallback();
        InitInspectionLists();

        setupTestButton();
    }

    private void setupTestButton() {
        Button btn = findViewById(R.id.btnTest);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grab restaurant with multiple inspections for testing
                RestaurantManager manager = RestaurantManager.getInstance();
                Restaurant testRestaurant = manager.getIndex(5);
                Inspection testInspection = testRestaurant.getInspectionList().get(0);

                // Launch inspection activity with an Inspection object for testing
                Intent intent = InspectionActivity.makeIntent(MainActivity.this);
                intent.putExtra("inspection", testInspection);
                startActivity(intent);
            }
        });
    }

    private void readRestaurantData() {
        // Get instance of RestaurantManager
        manager = RestaurantManager.getInstance();

        // TODO: Remove double quotes around strings where necessary
        // Can use substring.() to return the string without the first and last chars, ie, the double quotes

        // To read a resource, need an input stream
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);

        // To read from stream reader line by line, need a bufferreader
        // Need to build an input stream based on a character set
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
                Log.d("Main activity","Just created" + r1);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    private void populateListView() {
        // Construct a new ArrayList from the manager Singleton
        List<Restaurant> restaurantList = restaurantList();

        // Setup the listView
        ArrayAdapter<Restaurant> adapter = new MyListAdapter(restaurantList);
        ListView list = (ListView) findViewById(R.id.restaurantsListView);
        list.setAdapter(adapter);
    }

    public ArrayList<Restaurant> restaurantList() {
        ArrayList<Restaurant> newRestaurantList = new ArrayList<>();
        for (int i = 0; i < manager.getSize(); i++) {
            newRestaurantList.add(manager.getIndex(i));
        }
        return newRestaurantList;
    };

    private class MyListAdapter extends ArrayAdapter<Restaurant> {

        public MyListAdapter(List<Restaurant> restaurantList) {
            super(MainActivity.this, R.layout.restaurant_item_view, restaurantList);
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
            Restaurant currentRestaurant = manager.getIndex(position);

            // Fill the restaurantIcon
            ImageView imgRestaurant = (ImageView) itemView.findViewById(R.id.restaurant_item_imgRestaurantIcon);
            // TODO: Give each restaurant a custom icon?
            // imgRestaurant.setImageDrawable(currentRestaurant.getIconID());

            // Fill the hazard icon
            // TODO: Set the appropriate icon based on the restaurant's last inspections hazard level and remove if structure
            ImageView imgHazardIcon = itemView.findViewById(R.id.restaurant_item_imgHazardRating);

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
            restaurantName.setText(currentRestaurant.getName());

            // Set last inspection date text
            // TODO: Grab the most recent inspection and display its date
            TextView lastInspectionDate = itemView.findViewById(R.id.restaurant_item_txtLastInspectionDate);
            // lastInspectionDate.setText();

            // Set number of violations text
            // TODO: Display most recent inspections number of violations
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);

            // Set text to test data
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
                Restaurant clickedRestaurant = manager.getIndex(position);
                String message = "You clicked position " + position
                        + " which is restaurant " + clickedRestaurant.getName();
                        // + " with inspections " + clickedRestaurant.getInspectionList();
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                // Launch restaurant activity
            }
        });
    }

    private void InitInspectionLists() {
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        ); //Create reader

        String line = "";

        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null){ //Iterate through lines (reports) in CSV
                //Log.d("MyActivity", "Line: " + line);
                int i = 0;

                String[] lineSplit = line.split(",", 7);
                //Find corresponding restaurant to the report currently being read
                String trackNumCompare[] = {lineSplit[0], RestaurantManager.getInstance().getIndex(i).getTrackingNumber()};
                while (!trackNumCompare[0].equals(trackNumCompare[1])){
                    i++;
                    trackNumCompare[1] = RestaurantManager.getInstance().getIndex(i).getTrackingNumber();
                }
                //Initializing inspection object variables
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(lineSplit[0]);
                inspection.setInspectionDate(lineSplit[1]);
                inspection.setInspType(lineSplit[2]);
                inspection.setNumCritical(Integer.parseInt(lineSplit[3]));
                inspection.setNumNonCritical(Integer.parseInt(lineSplit[4]));
                inspection.setHazardRating(lineSplit[5]);

                String[] violationsArr = lineSplit[6].split("\\|"); //Split 'lump' of violations into array, each element containing a violation

                if (violationsArr[0] != ""){ //Transfer each violation to class object's arraylist
                    Log.d("MyActivity", "Violations for " + lineSplit[0] + ":" + Arrays.toString(violationsArr));
                    for (String violation : violationsArr){ // For each token, split it up farther into number, crit, details, repeat
                        Log.d("MyActivity", "--Violations split: " + violation);
                        String[] violSplit = violation.split(",");

                        boolean crit = false;
                        if (violSplit[1].replace("\"","").equals("Critical")){
                            crit = true;
                        }

                        boolean repeat = false;
                        if (violSplit[3].replace("\"","").equals("\"Repeat\"")){
                            repeat = true;
                        }

                        Violation violObj = new Violation(Integer.parseInt(violSplit[0].replace("\"", "")), crit, violSplit[2], repeat); //Create violation object
                        Log.d("MyActivity", "----violation.violNum: " + violObj.getViolNumber());
                        Log.d("MyActivity", "----violation.crit: " + violObj.getCritical());
                        Log.d("MyActivity", "----violation.violDetails: " + violObj.getViolDetails());
                        Log.d("MyActivity", "----violation.repeat: " + violObj.getRepeat());
                        inspection.getViolLump().add(violObj); //Append violation to violLump arraylist
                    }
                }
                RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
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