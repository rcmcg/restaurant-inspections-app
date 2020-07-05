package com.example.groupassign1.Model;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestaurantManager implements Iterable<Restaurant> {

    //Implement iterable and a singleton class of restaurants object
    private List<Restaurant> restaurantList = new ArrayList<>();

    public void add(Restaurant restaurant){
        restaurantList.add(restaurant);
    }
    public void delete(Restaurant restaurant){
        restaurantList.remove(restaurant);
    }

    //Return a restaurant object by taking an input of index in restaurant List
    public Restaurant getIndex(int n){
        return restaurantList.get(n);
    }

    //Singleton class and adding restaurants from CSV
    private static RestaurantManager instance;

    private String hazardLevelFilter = "All";

    private String comparator = "All";

    private boolean favouriteOnly = false;

    private int violationLimit;

    private RestaurantManager(){
        //Prevent from instantiating
    }
    private String searchTerm = "";

    //Returns a single instance of the restaurant objects
    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    public Restaurant find(String tracking){
        for (Restaurant restaurant: restaurantList) {
            if (restaurant.getTrackingNumber().equals(tracking)) {
                return restaurant;
            }
        }
        return null;
    }

    // TESTING
    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurantList.iterator();
    }

    public int getSize() {
        return restaurantList.size();
    }
}
