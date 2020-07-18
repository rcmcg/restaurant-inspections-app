package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Violation;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String RESTAURANT_INDEX_INTENT_TAG = "restaurantIndex";
    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";

    private RestaurantManager manager = RestaurantManager.getInstance();

    // Yellow, orange, red, with 20% transparency
    public static int[] itemViewBackgroundColours = {0x33FFFF00, 0x33FFA500, 0x33FF0000};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: These functions should be member functions of Restaurant?
        getUpdatedData(); //TODO: Call this after user allows update (after 20 hrs)
        readRestaurantData();
        InitInspectionLists();
        //Log.d("HELOOOO", "" + RestaurantManager.getInstance().getIndex(1300).getTrackingNumber() + "+");
        manager.sortRestaurantsByName();
        manager.sortInspListsByDate();


        // Following 2 functions take from Dr. Fraser's video linked below
        // https://www.youtube.com/watch?v=WRANgDgM2Zg
        populateListView();
        registerClickCallback();
        wireLaunchMapButton();
    }

    private void getUpdatedData() {
        String restaurantDataURL = "";
        String inspectionDataURL = "";

        restaurantDataURL = getURL("https://data.surrey.ca/api/3/action/package_show?id=restaurants"); //Retrieve url used to request csv
        inspectionDataURL = getURL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
        String newRestaurantData = requestData(restaurantDataURL); //Request updated restaurant data csv
        String newInspectionData = requestData(inspectionDataURL); //Request updated restaurant data csv

        //Write new csv from web server to internal storage
        writeToFile(newRestaurantData, WEB_SERVER_RESTAURANTS_CSV);
        writeToFile(newInspectionData, WEB_SERVER_INSPECTIONS_CSV);
    }

    private String getURL(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        String dataURL = "";
        try {
            Response response = client.newCall(request).execute();
            String getJSON = response.body().string();
            JSONObject jsonObj = new JSONObject(getJSON);
            JSONObject resultObj = jsonObj.getJSONObject("result");

            JSONArray resArr = resultObj.getJSONArray("resources");
            JSONObject data = resArr.getJSONObject(0);
            dataURL = data.getString("url");
            String lastModifiedDate = data.getString("last_modified");

        } catch (IOException | JSONException e) {
            Log.e("MYACTIVITY!", "ERROR!!!!");
            e.printStackTrace();
        }
        return dataURL;
    }

    private String requestData(String DataURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(DataURL)
                .method("GET", null)
                .build();
        String csv = "";
        try {
            Response response = client.newCall(request).execute();
            csv = response.body().string();
            //Log.d("MyActivity", getCSV); //Dump updated restaurants CSV into logcat

        } catch (IOException e) {
            Log.e("MYACTIVITY!", "ERROR!!!!");
            e.printStackTrace();
        }
        return csv;
    }

    private void writeToFile(String newData, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(newData.getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + WEB_SERVER_RESTAURANTS_CSV, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            //Execute even if catch
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    private void readRestaurantData() {
        // Get instance of RestaurantManager
        manager = RestaurantManager.getInstance();

        if (manager.getSize() == 0) {
            // To read a resource, need an input stream
            InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);

            // To read from stream reader line by line, need a bufferreader
            // Need to build an input stream based on a character set
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line = "";
            try {
                // Escaping the header lines of the CSV file
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    line = line.replace("\"", "");
                    // Splitting every line based on "," , Tokens are variables of Restaurant class
                    String[] tokens=line.split(",");

                    Restaurant newRestaurant = new Restaurant();
                    newRestaurant.setTrackingNumber(tokens[0]);
                    newRestaurant.setName(tokens[1]);
                    newRestaurant.setAddress(tokens[2]);
                    newRestaurant.setCity(tokens[3]);
                    newRestaurant.setFacType(tokens[4]);
                    newRestaurant.setLatitude(Double.parseDouble(tokens[5]));
                    newRestaurant.setLongitude(Double.parseDouble(tokens[6]));
                    newRestaurant.setImgId();

                    // Adding the created Restaurants object to the manager instance
                    manager.add(newRestaurant);
                    Log.d("Main activity","Just created" + newRestaurant);
                }
            } catch (IOException e) {
                Log.wtf("MyActivity", "Error reading data file on line" + line, e);
                e.printStackTrace();
            }
        }
        readNewRestaurantData();
    }

    private void readNewRestaurantData() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(WEB_SERVER_RESTAURANTS_CSV);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            br.readLine(); //Header line

            while ((line = br.readLine()) != null){
               line = line.replace("\"", "");
                String[] tokens = line.split(",");

                String [] restaurantData = new String[7];
                if (tokens.length > 7){ //Some restaurant names have ',' (commas) in them causing tokens[1] and tokens[2] to be a split version of the restaurant name
                    restaurantData[0] = tokens[0];
                    restaurantData[1] = tokens[1] + tokens[2];
                    System.arraycopy(tokens, 3, restaurantData, 2, 5);
                }
                else{
                    restaurantData = tokens.clone();
                }

                Restaurant newRestaurant = new Restaurant();
                newRestaurant.setTrackingNumber(restaurantData[0].replace(" ", ""));
                newRestaurant.setName(restaurantData[1]);
                newRestaurant.setAddress(restaurantData[2]);
                newRestaurant.setCity(restaurantData[3]);
                newRestaurant.setFacType(restaurantData[4]);
                newRestaurant.setLatitude(Double.parseDouble(restaurantData[5]));
                newRestaurant.setLongitude(Double.parseDouble(restaurantData[6]));
                newRestaurant.setImgId();

                manager.add(newRestaurant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void populateListView() {
        // Construct a new ArrayList from the manager Singleton to fill the listView
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
            imgRestaurant.setImageResource(currentRestaurant.getIconImgId());
            Log.d("MainActivity", "getView: currentRestaurant.getIconImgId: " + currentRestaurant.getIconImgId());

            // Fill the hazard icon and the background color of each item
            ImageView imgHazardIcon = itemView.findViewById(R.id.restaurant_item_imgHazardRating);
            if (currentRestaurant.getInspectionList().size() != 0) {
                // Inspection list in Restaurant is sorted on startup so the first index is the most recent
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);

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
            restaurantName.setText(getString(R.string.main_activity_restaurant_name,currentRestaurant.getName()));

            // Set last inspection date text
            TextView lastInspectionDate = itemView.findViewById(R.id.restaurant_item_txtLastInspectionDate);
            if (currentRestaurant.getInspectionList().size() != 0) {
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);
                lastInspectionDate.setText(
                        getString(R.string.main_activity_restaurant_item_last_inspection_date,
                                lastInspection.intelligentInspectDate())
                );
            } else {
                lastInspectionDate.setText(getString(R.string.main_activity_restaurant_item_last_inspection_date_no_inspection));
            }


            // Set number of violations text
            TextView numViolationsLastInspection = itemView.findViewById(R.id.restaurant_item_txtNumViolations);

            if (currentRestaurant.getInspectionList().size() != 0) {
                Inspection lastInspection = currentRestaurant.getInspectionList().get(0);
                String sumOfViolations = "" + (lastInspection.getNumCritical() + lastInspection.getNumNonCritical());
                numViolationsLastInspection.setText(
                        getString(R.string.main_activity_restaurant_item_violations, sumOfViolations)
                        );
            } else {
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
                Restaurant clickedRestaurant = manager.getIndex(position);

                // Launch restaurant activity
                Intent intent = RestaurantActivity.makeLaunchIntent(MainActivity.this);
                intent.putExtra(RESTAURANT_INDEX_INTENT_TAG, position);
                startActivity(intent);
            }
        });
    }

    private void InitInspectionLists() {
        // Create arrays for briefDescriptions of violations
        InputStream is2 = getResources().openRawResource(R.raw.violations_brief_descriptions);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is2, Charset.forName("UTF-8")));
        String line = "";

        List<Integer> violNumbers = new ArrayList<>();
        List<String> violBriefDescriptions = new ArrayList<>();

        try {
            bufferedReader.readLine();
            while ( (line = bufferedReader.readLine()) != null) {
                String[] lineSplit = line.split(",");
                violNumbers.add(Integer.parseInt(lineSplit[0]));
                violBriefDescriptions.add(lineSplit[1]);
                Log.d("MyActivity", "Added brief description " + lineSplit[1] + " to violBriefDescriptions");
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }

        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );  // Create reader

        line = "";

        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null){ //Iterate through lines (reports) in CSV
                //Log.d("MyActivity", "Line: " + line);
                int i = 0;
                line = line.replace("\"", "");

                String[] lineSplit = line.split(",", 7);
                //Find restaurant matching report tracking number being read
                while (!lineSplit[0].equals(RestaurantManager.getInstance().getIndex(i).getTrackingNumber())){
                    i++;
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

                if (!violationsArr[0].equals("")){ //Transfer each violation to class object's arraylist
                    // Log.d("MyActivity", "Violations for " + lineSplit[0] + ":" + Arrays.toString(violationsArr));
                    for (String violation : violationsArr){ // For each token, split it up farther into number, crit, details, repeat
                        // Log.d("MyActivity", "--Violations split: " + violation);
                        String[] violSplit = violation.split(",");

                        boolean crit = false;
                        if (violSplit[1].equals("Critical")){
                            crit = true;
                        }

                        boolean repeat = false;
                        if (violSplit[3].equals("Repeat")){
                            repeat = true;
                        }

                        int violNumber = Integer.parseInt(violSplit[0]);

                        int briefDescIndex = violNumbers.indexOf(violNumber);
                        String briefDesc = violBriefDescriptions.get(briefDescIndex);

                        Violation violObj = new Violation(violNumber,
                                crit,
                                violSplit[2],
                                briefDesc,
                                repeat);
                        inspection.getViolLump().add(violObj); // Append violation to violLump arraylist
                    }
                }
                RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
            }
        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
        initNewInspectionLists(violNumbers, violBriefDescriptions);
    }

    private void initNewInspectionLists(List<Integer> violNumbers, List<String> violBriefDescriptions) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(WEB_SERVER_INSPECTIONS_CSV);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            br.readLine(); //Header line
            String trackingNum = "";
            boolean unknownRestaurant = false;
            int i = 0;

            while ((line = br.readLine()) != null) { //Iterate through lines (reports) in CSV
                line = line.replace("\"", "");

                String[] lineSplit = line.split(",", 6);

                //Find restaurant matching report tracking number being read
                if (!trackingNum.equals(lineSplit[0])) {
                    i = 0;
                    while (!lineSplit[0].equals(RestaurantManager.getInstance().getIndex(i).getTrackingNumber())) {
                        i++;
                        if (i == RestaurantManager.getInstance().getSize()) {
                            unknownRestaurant = true;
                            break;
                        }
                    }
                    if (unknownRestaurant) {
                        continue;
                    }
                    trackingNum = lineSplit[0];
                    if (trackingNum.equals("SYOG-5M5942")){
                        String test = "";
                    }
                }
                //Initializing inspection object variables
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(lineSplit[0]);
                inspection.setInspectionDate(lineSplit[1]);
                inspection.setInspType(lineSplit[2]);
                inspection.setNumCritical(Integer.parseInt(lineSplit[3]));
                inspection.setNumNonCritical(Integer.parseInt(lineSplit[4]));
                String[] violationsArr = lineSplit[5].split("\\|"); //Split 'lump' of violations into array, each element containing a violation
                String[] violSplit;

                for (int violCount = 0; violCount < violationsArr.length; violCount++) { // For each token, split it up farther into number, crit, details, repeat
                    String[] test = violationsArr[violCount].split(",");

                    if (test.length == 2) {
                        inspection.setHazardRating(test[1]);
                        continue;
                    }

                    if (Integer.parseInt(test[0]) == 502 || Integer.parseInt(test[0]) == 305){
                        violSplit = new String[test.length - 1];

                        violSplit[0] = test[0];
                        violSplit[1] = test[1];
                        violSplit[2] = test[2] + test[3];
                        int length = 1;
                        if (test.length == 6){
                            length = 2;
                        }
                        System.arraycopy(test, 4, violSplit, 3, length);
                    }
                    else{
                        violSplit = test.clone();
                    }

                    if (violSplit.length == 5) {
                        inspection.setHazardRating(violSplit[4]);
                    }

                    boolean crit = false;
                    if (violSplit[1].equals("Critical")) {
                        crit = true;
                    }

                    boolean repeat = false;
                    if (violSplit[3].equals("Repeat")) {
                        repeat = true;
                    }

                    int violNumber = Integer.parseInt(violSplit[0]);

                    int briefDescIndex = violNumbers.indexOf(violNumber);
                    String briefDesc = violBriefDescriptions.get(briefDescIndex);

                    Violation violObj = new Violation(violNumber, crit, violSplit[2], briefDesc, repeat);
                    inspection.getViolLump().add(violObj); // Append violation to violLump arraylist
                }
                RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}