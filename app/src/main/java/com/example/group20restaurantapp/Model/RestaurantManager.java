package com.example.group20restaurantapp.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import com.example.group20restaurantapp.R;
import com.example.group20restaurantapp.UI.MapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Singleton class which contains all instances of Restaurant
 * Contains functions for reading and writing to files, downloading new data, among others
 */

public class RestaurantManager implements Iterable<Restaurant>{
    // Constants
    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";

    // Variables
    private List<Restaurant> restaurantList = new ArrayList<>();            // Master list of restaurants
    private List<Restaurant> filteredRestaurantList = new ArrayList<>();    // Filtered list for use by app
    private List<Restaurant> favRestaurantsList = new ArrayList<>();

    public List<Restaurant> getPreupdateFavList() {
        return preupdateFavList;
    }

    private List<Restaurant> preupdateFavList;
    private List<Integer> violNumbers;
    private List<String> violBriefDescriptions;
    private static RestaurantManager manager;
    private String restaurantsLastModified = "";
    private String inspectionsLastModified = "";
    private boolean userBeenAskedToUpdateThisSession = false;
    private boolean isDownloadCancelled = false;
    OkHttpClient client = new OkHttpClient().newBuilder().build();

    // Search parameters, initialized with default values
    private String searchTerm = "";
    private String searchHazardLevelStr = "";
    private int searchViolationNumEquality = 0;   // 0: N/A, 1: <=, 2: >=
    private int searchViolationBound = -1;
    private boolean searchFavouritesOnly = false;

    // Iterable and a singleton class of restaurants object
    private RestaurantManager(){
        // Prevent from instantiating
    }

    public void setPreupdateFavList() {
        preupdateFavList = new ArrayList<Restaurant>(favRestaurantsList);
    }

    //set the favorite of search
    public void setSearchFavouritesOnly(boolean searchFavouritesOnly){
        this.searchFavouritesOnly = searchFavouritesOnly;
    }

    //set the limit of the violationNum from search
    public void setSearchViolationBound(int searchViolationBound) {
        this.searchViolationBound = searchViolationBound;
    }

    // Returns a single instance of the RestaurantManager
    public static RestaurantManager getInstance() {
        if (manager == null) {
            manager = new RestaurantManager();
        }
        return manager;
    }

    public void setSearchHazardLevelStr(int index) {
        if (index == 0) this.searchHazardLevelStr = "";
        else if (index == 1) this.searchHazardLevelStr = "Low";
        else if (index == 2) this.searchHazardLevelStr = "Moderate";
        else if (index == 3) this.searchHazardLevelStr = "High";
    }

    public void setSearchViolationNumEquality(int index) {
        this.searchViolationNumEquality = index;
    }

    public int getSizeAllRestaurants() {
        return restaurantList.size();
    }

    public int getSizeFilteredRestaurants() {
        return filteredRestaurantList.size();
    }

    public List<Restaurant> getFavRestaurantsList() {
        return favRestaurantsList;
    }

    public boolean isDownloadCancelled() {
        return isDownloadCancelled;
    }

    public void setDownloadCancelled(boolean downloadCancelled) {
        isDownloadCancelled = downloadCancelled;
    }

    public boolean hasUserBeenAskedToUpdateThisSession() {
        return userBeenAskedToUpdateThisSession;
    }

    public void setUserBeenAskedToUpdateThisSession(boolean userBeenAskedToUpdateThisSession) {
        this.userBeenAskedToUpdateThisSession = userBeenAskedToUpdateThisSession;
    }

    private List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    // Return a restaurant object by taking an input of index in restaurant List
    public Restaurant getIndexAllRestaurants(int n){
        return restaurantList.get(n);
    }

    public Restaurant getIndexFilteredRestaurants(int n) {
        return filteredRestaurantList.get(n);
    }

    public void cancelDownloads() {
        client.dispatcher().cancelAll();
    }

    public void add(Restaurant restaurant){
        restaurantList.add(restaurant);
    }

    public void delete(Restaurant restaurant){
        restaurantList.remove(restaurant);
    }

    // Singleton class and adding restaurants from CSV
    public int findIndexFromFilteredRestaurants(Restaurant restaurant){
        for(int i = 0 ; i < filteredRestaurantList.size() ; i++){
            if(restaurant == filteredRestaurantList.get(i)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        if (filteredRestaurantList.size() == 0) {
            // Fill filtered restaurant with all restaurants
            updateFilteredRestaurants();
        }
        return filteredRestaurantList.iterator();
    }

    private void resetRestaurantList() {
        restaurantList.clear();
    }

    public void sortRestaurantsByName() {
        // Sorts master list of restaurants
        Comparator<Restaurant> compareByName = new Comparator<Restaurant>() { //Compares restaurant names
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getName().compareTo(r2.getName());
            }
        };
        Collections.sort(restaurantList, compareByName);
    }

    public void sortInspListsByDate() {
        Comparator<Inspection> compareByDate = new Comparator<Inspection>() { //Compares inspection dates
            @Override
            public int compare(Inspection i1, Inspection i2) {
                return i1.getInspectionDate().compareTo(i2.getInspectionDate());
            }
        };

        for (Restaurant restaurant : restaurantList){
            Collections.sort(restaurant.inspectionList, compareByDate.reversed()); //Sort arraylist in reverse order
        }
    }

    public Restaurant findRestaurantByLatLng(double latitude, double longtitude, String name) {
        for (Restaurant restaurant : restaurantList) {
            if (
                    restaurant.getLatitude() == latitude
                    && restaurant.getLongitude() == longtitude
                    && restaurant.getName().equals(name)
                )
            {
                return restaurant;
            }
        }
        return null;
    }

    private void resetFilteredRestaurants() {
        filteredRestaurantList.clear();
    }

    public void updateFilteredRestaurants() {
        // searchTerm: Restaurant name must contain searchTerm as a substring
        // hazardLevelLastInspection: Restaurants last inspection have this hazard level
        // Clear the filtered restaurant list before updating
        filteredRestaurantList.clear();

        // Refill filtered list with the correct restaurants
        for (Restaurant restaurant : manager.getRestaurantList()) {
            if (restaurant.getName().toLowerCase().contains(searchTerm.toLowerCase())   // Check the name contains the search term
                && (searchHazardLevelStr.equals("") // If user doesn't care about hazard level the condition evaluates to true
                    || (restaurant.getInspectionList().size() != 0 && restaurant.getInspection(0).getHazardRating().equals(searchHazardLevelStr)))   // Verify most recent inspection is correct
                && (searchViolationNumEquality == 0  // User doesn't care about violations, evaluates to true
                    || (searchViolationNumEquality == 1 && restaurant.countCriticalViolationInLastYear() >= searchViolationBound)    // User wants to check if number >= N
                    || (searchViolationNumEquality == 2 && restaurant.countCriticalViolationInLastYear() <= searchViolationBound))   // User wants to check if number <= N
                && (!searchFavouritesOnly || restaurant.isFavourite())    // If user doesn't care about favourites, evaluate to true, otherwise verify restaurant is a favourite or not
            ) {
                filteredRestaurantList.add(restaurant);
            }
        }
    }

    private List<Restaurant> getFilteredRestaurantList() {
        return filteredRestaurantList;
    }

    public void refillRestaurantManagerNewData(Context context) {
        resetRestaurantList();
        fillRestaurantManager(true, context);
    }

    public void fillRestaurantManager(boolean hasAppBeenUpdated, Context context) {
        if (!hasAppBeenUpdated) {
            readRestaurantData(context);
            initInspectionLists(context);
        } else {
            readNewRestaurantData(context);
            initNewInspectionLists(context);
        }
        sortInspListsByDate();
        sortRestaurantsByName();
    }

    public String[] getURLDateLastModified(String requestURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Request request = new Request.Builder()
                .url(requestURL)
                .method("GET", null)
                .build();
        String dataURL = "";
        String lastModifiedDate = "";
        try {
            Response response = client.newCall(request).execute();
            String getJSON = response.body().string();
            JSONObject jsonObj = new JSONObject(getJSON);
            JSONObject resultObj = jsonObj.getJSONObject("result");

            JSONArray resArr = resultObj.getJSONArray("resources");
            JSONObject data = resArr.getJSONObject(0);
            dataURL = data.getString("url");
            if (restaurantsLastModified.equals("")){
                restaurantsLastModified = data.getString("last_modified");
            } else{
                inspectionsLastModified = data.getString("last_modified");
            }
            lastModifiedDate = data.getString("last_modified");

        } catch (IOException | JSONException e) {
            Log.e("MYACTIVITY!", "ERROR!!!!");
            e.printStackTrace();
        }
        return new String[]{dataURL, lastModifiedDate};
    }

    public String getCSV(String DataURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Request request = new Request.Builder()
                .url(DataURL)
                .method("GET", null)
                .build();
        String csv = "";
        try {
            Response response = client.newCall(request).execute();
            csv = response.body().string();
            //Log.d("MyActivity", getCSV); // Dump updated restaurants CSV into logcat

        } catch (IOException e) {
            Log.e("MYACTIVITY!", "ERROR!!!!");
            e.printStackTrace();
        }
        return csv;
    }

    public void writeToFile(String newData, String fileName, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(newData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            // Execute even if catch
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readRestaurantData(Context context) {
        // Get instance of RestaurantManager
        if (getSizeAllRestaurants() == 0) {
            // To read a resource, need an input stream
            InputStream is = context.getResources().openRawResource(R.raw.restaurants_itr1);

            // To read from stream reader line by line, need a buffered reader
            // Need to build an input stream based on a character set
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
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

                    SharedPreferences preferences = context.getSharedPreferences("favourites", 0);
                    // Check 'favourited' status of restaurant
                    boolean favStatus = preferences.getBoolean(newRestaurant.getTrackingNumber(), false);
                    newRestaurant.setFavourite(favStatus);
                    if (favStatus){
                        Restaurant restaurantRef = newRestaurant;
                        favRestaurantsList.add(restaurantRef);
                    }
                    // Adding the created Restaurants object to the manager instance
                    add(newRestaurant);
                    Log.d("Main activity","Just created" + newRestaurant);
                }
            } catch (IOException e) {
                Log.wtf("MyActivity", "Error reading data file on line" + line, e);
                e.printStackTrace();
            }
        }
    }

    public void readNewRestaurantData(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(WEB_SERVER_RESTAURANTS_CSV);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            br.readLine(); // Header line

            while ((line = br.readLine()) != null) {
                line = line.replace("\"", "");
                String[] tokens = line.split(",");

                String [] restaurantData = new String[7];
                // Some restaurant names have ',' (commas) in them causing tokens[1] and tokens[2] to be a split version of the restaurant name
                if (tokens.length > 7){
                    restaurantData[0] = tokens[0];
                    restaurantData[1] = tokens[1] + tokens[2];
                    System.arraycopy(tokens, 3, restaurantData, 2, 5);
                } else {
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

                SharedPreferences preferences = context.getSharedPreferences("favourites", 0);
                // Check 'favourited' status of restaurant
                boolean favStatus = preferences.getBoolean(newRestaurant.getTrackingNumber(), false);
                newRestaurant.setFavourite(favStatus);
                if (favStatus){
                    favRestaurantsList.add(newRestaurant);
                }
                add(newRestaurant);
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

    public void initInspectionLists(Context context) {
        // Create arrays for briefDescriptions of violations
        InputStream is2 = context.getResources().openRawResource(R.raw.violations_brief_descriptions);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8));
        String line = "";

        violNumbers = new ArrayList<>();
        violBriefDescriptions = new ArrayList<>();

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

        InputStream is = context.getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
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
                while (!lineSplit[0].equals(getIndexAllRestaurants(i).getTrackingNumber())){
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
                getIndexAllRestaurants(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
            }
        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }
    }

    public void initNewInspectionLists(Context context) {
        // Create arrays for briefDescriptions of violations
        InputStream is2 = context.getResources().openRawResource(R.raw.violations_brief_descriptions);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is2, StandardCharsets.UTF_8));
        String line = "";
        violNumbers = new ArrayList<>();
        violBriefDescriptions = new ArrayList<>();

        try {
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

        FileInputStream fis;
        try {
            fis = context.openFileInput(WEB_SERVER_INSPECTIONS_CSV);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            line = "";
            br.readLine(); //Header line
            String prevTrackingNum = "";
            boolean unknownRestaurant = false;
            int i = 0;

            while ((line = br.readLine()) != null) { //Iterate through lines (reports) in CSV
                line = line.replace("\"", "");

                String[] lineSplit = line.split(",", 6);
                lineSplit[0] = lineSplit[0].replace(" ", "");

                //Find restaurant with matching report tracking number being read
                if (!prevTrackingNum.equals(lineSplit[0])){
                    i = 0;
                    while (!lineSplit[0].equals(getIndexAllRestaurants(i).getTrackingNumber())) {
                        i++;
                        if (i == getSizeAllRestaurants()-1) {
                            unknownRestaurant = true;
                            break;
                        }
                    }
                    prevTrackingNum = lineSplit[0];
                    if (unknownRestaurant) {
                        unknownRestaurant = false;
                        continue;
                    }
                }

                    //Initializing inspection object variables
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(lineSplit[0].replace(" ", ""));

                if (inspection.getTrackingNumber().equals("SWOD-APSP3X")){
                    String name;
                    name = "Adam";
                }

                inspection.setInspectionDate(lineSplit[1]);
                inspection.setInspType(lineSplit[2]);
                inspection.setNumCritical(Integer.parseInt(lineSplit[3]));
                inspection.setNumNonCritical(Integer.parseInt(lineSplit[4]));

                if (lineSplit[5].equals(",Low") || lineSplit[5].equals(",")) {
                    inspection.setHazardRating("Low");
                    getIndexAllRestaurants(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
                    continue;
                }
                if (lineSplit[5].equals(",Moderate")) {
                    inspection.setHazardRating("Moderate");
                    getIndexAllRestaurants(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
                    continue;
                }
                if (lineSplit[5].equals(",High")) {
                    inspection.setHazardRating("High");
                    getIndexAllRestaurants(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
                    continue;
                }
                String[] violationsArr = lineSplit[5].split("\\|"); //Split 'lump' of violations into array, each element containing a violation

                for (int violCount = 0; violCount < violationsArr.length; violCount++) { // For each token, split it up farther into number, crit, details, repeat
                    String[] violSplit = violationsArr[violCount].split(",", 3);

                    int violNumber = Integer.parseInt(violSplit[0]);

                    boolean crit = false;
                    if (violSplit[1].equals("Critical")) {
                        violSplit[2] = violSplit[2].replace("Critical", "");
                        crit = true;
                    } else {
                        violSplit[2] = violSplit[2].replace("Not Critical", "");
                    }

                    boolean repeat = true;
                    if (violSplit[2].contains("Not Repeat")) {
                        violSplit[2] = violSplit[2].replace("Not Repeat", "");
                        repeat = false;
                    } else {
                        violSplit[2] = violSplit[2].replace("Repeat", "");
                    }

                    if (violSplit[2].contains("Moderate")) {
                        violSplit[2] = violSplit[2].replace("Moderate", "");
                        inspection.setHazardRating("Moderate");

                    } else if (violSplit[2].contains("High")) {
                        violSplit[2] = violSplit[2].replace("High", "");
                        inspection.setHazardRating("High");

                    } else {
                        violSplit[2] = violSplit[2].replace("Low", "");
                        inspection.setHazardRating("Low");
                    }

                    String briefDesc;
                    int briefDescIndex = violNumbers.indexOf(violNumber);
                    briefDesc = violBriefDescriptions.get(briefDescIndex);

                    Violation violObj = new Violation(violNumber, crit, violSplit[2], briefDesc, repeat);
                    inspection.getViolLump().add(violObj); // Append violation to violLump arraylist
                }
                getIndexAllRestaurants(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
}
