package com.example.group20restaurantapp.Model;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private boolean userBeenAskedToUpdateThisSession = false;
    private OkHttpClient client = new OkHttpClient().newBuilder().build();

    private String restaurantsLastModified = "";
    private String inspectionsLastModified = "";

    private static final String WEB_SERVER_RESTAURANTS_CSV = "updatedRestaurants.csv";
    private static final String WEB_SERVER_INSPECTIONS_CSV = "updatedInspections.csv";

    private List<Integer> violNumbers = new ArrayList<>();
    private List<String> violBriefDescriptions = new ArrayList<>();

    public boolean hasUserBeenAskedToUpdateThisSession() {
        return userBeenAskedToUpdateThisSession;
    }

    public void setUserBeenAskedToUpdateThisSession(boolean userBeenAskedToUpdateThisSession) {
        this.userBeenAskedToUpdateThisSession = userBeenAskedToUpdateThisSession;
    }

    // Iterable and a singleton class of restaurants object
    private List<Restaurant> restaurantList = new ArrayList<>();

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
    private static RestaurantManager  instance;
    private RestaurantManager(){
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
}
