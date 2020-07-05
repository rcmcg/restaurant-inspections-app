package com.example.group20restaurantapp.Model;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestaurantManager implements Iterable<Restaurant>{
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
    private RestaurantManager(){
        //Prevent from instantiating
    }

    //Returns a single instance of the restaurant objects
    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
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
