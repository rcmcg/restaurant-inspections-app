package com.example.group20restaurantapp.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton class which contains all instances of Restaurant
 */

public class RestaurantManager implements Iterable<Restaurant>{

    // Iterable and a singleton class of restaurants object
    private List<Restaurant> restaurantList = new ArrayList<>();
    private static RestaurantManager  instance;
    private String searchTerm = "";
    private String hazardLevelFilter = "All";
    private String comparator = "All";
    private boolean favouriteOnly = false;
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

        if (restaurantName.toLowerCase().contains(searchTerm.toLowerCase()) &&
                ((hazardLevelFilter.equalsIgnoreCase("All")) ||
                        (hazardLevel.equalsIgnoreCase(hazardLevelFilter))) ) {

            return true;

        } else {

            return false;
        }

    }
}
