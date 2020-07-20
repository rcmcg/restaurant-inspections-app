package com.example.group20restaurantapp.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.group20restaurantapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Singleton class which contains all instances of Restaurant
 */

public class RestaurantManager implements Iterable<Restaurant>{

    // Iterable and a singleton class of restaurants object
    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";
    private List<Restaurant> restaurantList = new ArrayList<>();
    private static RestaurantManager  instance;
    private String searchTerm = "";
    private String hazardLevelFilter = "All";
    private String comparator = "All";
    private boolean favouriteOnly = false;
    private List<Integer> violNumbers = new ArrayList<>();
    private List<String> violBriefDescriptions = new ArrayList<>();
    private String restaurantsLastModified = "";
    private String inspectionsLastModified = "";

    private boolean userBeenAskedToUpdateThisSession = false;
    private boolean isDownloadCancelled = false;

    OkHttpClient client = new OkHttpClient().newBuilder().build();

    public boolean isDownloadCancelled() {
        return isDownloadCancelled;
    }

    public void setDownloadCancelled(boolean downloadCancelled) {
        isDownloadCancelled = downloadCancelled;
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

    // Return a restaurant object by taking an input of index in restaurant List
    public Restaurant getIndex(int n){
        return restaurantList.get(n);
    }
    // Singleton class and adding restaurants from CSV

    public int findIndex(Restaurant restaurant){
        for(int i = 0 ; i < restaurantList.size() ; i++){
            if(restaurant == restaurantList.get(i)){
                return i;
            }
        }
        return -1;
    }

    public RestaurantManager(){
        // Prevent from instantiating
    }

    //Returns a single instance of the restaurant objects
    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }

    public int getSize() {
        return restaurantList.size();
    }

    public boolean hasUserBeenAskedToUpdateThisSession() {
        return userBeenAskedToUpdateThisSession;
    }

    public void setUserBeenAskedToUpdateThisSession(boolean userBeenAskedToUpdateThisSession) {
        this.userBeenAskedToUpdateThisSession = userBeenAskedToUpdateThisSession;
    }

    public void sortRestaurantsByName() {
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

    public Restaurant findRestaurantByLatLng(double latitude, double longitude) {
        for (Restaurant restaurant: restaurantList) {
            if (restaurant.getLatitude() == latitude && restaurant.getLongitude() == longitude) {
                return restaurant;
            }
        }
        return null;
    }

    public List<Restaurant> getRestaurants() {
        searchTerm = searchTerm.trim();
        if (searchTerm.isEmpty() &&
                hazardLevelFilter.equalsIgnoreCase("All") &&
                comparator.equalsIgnoreCase("All") &&
                !favouriteOnly) {

            return restaurantList; // O(1) when search term is empty.
        }

        List<Restaurant> filteredRestaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            if (qualifies(restaurant)) {
                filteredRestaurants.add(restaurant);
            }
        }
        return filteredRestaurants;
    }

    private boolean qualifies(Restaurant restaurant) {
        String restaurantName = restaurant.getName();
        restaurantName = restaurantName.toLowerCase();
        String hazardLevel = restaurant.getLastHazardLevel();

        return restaurantName.toLowerCase().contains(searchTerm.toLowerCase()) &&
                ((hazardLevelFilter.equalsIgnoreCase("All")) ||
                        (hazardLevel.equalsIgnoreCase(hazardLevelFilter)));
    }

    public String[] getURL(String requestURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // OkHttpClient client = new OkHttpClient().newBuilder()
           //      .build();
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
            }
            else{
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

        // OkHttpClient client = new OkHttpClient().newBuilder()
                // .build();
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

    public void writeToFile(String newData, String fileName, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(newData.getBytes());
            //Toast.makeText(this, "Saved to " + context.getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();

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

    public void readRestaurantData(Context context) {
        // Get instance of RestaurantManager
        if (getSize() == 0) {
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
    }

    public void initNewInspectionLists(Context context) {
        FileInputStream fis;
        try {
            fis = context.openFileInput(WEB_SERVER_INSPECTIONS_CSV);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
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
                    while (!lineSplit[0].equals(RestaurantManager.getInstance().getIndex(i).getTrackingNumber())) {
                        i++;
                        if (i == RestaurantManager.getInstance().getSize()-1) {
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
                inspection.setInspectionDate(lineSplit[1]);
                inspection.setInspType(lineSplit[2]);
                inspection.setNumCritical(Integer.parseInt(lineSplit[3]));
                inspection.setNumNonCritical(Integer.parseInt(lineSplit[4]));

                if (lineSplit[5].equals(",Low") || lineSplit[5].equals(",")){
                    inspection.setHazardRating("Low");
                    RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
                    continue;
                }
                if (lineSplit[5].equals(",Moderate")){
                    inspection.setHazardRating("Moderate");
                    RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
                    continue;
                }
                if (lineSplit[5].equals(",High")){
                    inspection.setHazardRating("High");
                    RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
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
                    }
                    else{
                        violSplit[2] = violSplit[2].replace("Not Critical", "");
                    }

                    boolean repeat = true;
                    if (violSplit[2].contains("Not Repeat")){
                        violSplit[2] = violSplit[2].replace("Not Repeat", "");
                        repeat = false;
                    }
                    else{
                        violSplit[2] = violSplit[2].replace("Repeat", "");
                    }

                    if (violSplit[2].contains("Moderate")){
                        violSplit[2] = violSplit[2].replace("Moderate", "");
                        inspection.setHazardRating("Moderate");
                    }
                    else if (violSplit[2].contains("High")){
                        violSplit[2] = violSplit[2].replace("High", "");
                        inspection.setHazardRating("High");
                    }
                    else{
                        violSplit[2] = violSplit[2].replace("Low", "");
                        inspection.setHazardRating("Low");
                    }

                    String briefDesc;
                    if (violNumbers.indexOf(violNumber) == -1){
                        briefDesc = "Construction plans ignoring Regulations";
                    }
                    else{
                        int briefDescIndex = violNumbers.indexOf(violNumber);
                        briefDesc = violBriefDescriptions.get(briefDescIndex);
                    }

                    Violation violObj = new Violation(violNumber, crit, violSplit[2], briefDesc, repeat);
                    inspection.getViolLump().add(violObj); // Append violation to violLump arraylist
                }
                RestaurantManager.getInstance().getIndex(i).getInspectionList().add(inspection); //Add inspection to Restaurant's inspection list
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
