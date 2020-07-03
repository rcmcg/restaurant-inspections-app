package com.example.group20restaurantapp;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Restaurantmanager implements Iterable<Restaurants>{

    //Implement iterable and a singleton class of restaurants object
    private List<Restaurants> restaurantslist=new ArrayList<>();

    public void add(Restaurants restaurant){
        restaurantslist.add(restaurant);
    }
    public void delete(Restaurants restaurant){
        restaurantslist.remove(restaurant);
    }

    //Return a restaurant object by taking an input of index in restaurant List
    public Restaurants getibjectbyindex(int n){
        System.out.println("This is the specific Restaurant" + restaurantslist.get(n));
        return restaurantslist.get(n);

    }

    //Singleton class and adding restaurants from CSV
    private static Restaurantmanager instance;
    private Restaurantmanager(){
        //Prevent from instantiating
    }
    //Returns a single instance of the restaurant objects
    public static Restaurantmanager getInstance() {
        if (instance == null) {
            instance=new Restaurantmanager();


        }
        return instance;
    }

    @Override
    public Iterator<Restaurants> iterator() {
        return restaurantslist.iterator();
    }
    //returns how many restaurant objects in the instance
    public int getmanagersize(){
        int element=0;
        for(Restaurants r1: restaurantslist){
            element=element+1;

        }
        return element;
    }




}
