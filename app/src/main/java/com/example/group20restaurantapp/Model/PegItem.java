package com.example.group20restaurantapp.Model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Custom ClusterItem class for use with the ClusterManager for the Google Maps functionality
 */
//To peg an restaurant position(latitude and longitude), name and hazard icon is needed
public class PegItem implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private BitmapDescriptor mHazard;
    //constructor
    public PegItem(double lat, double lng, String mTitle, BitmapDescriptor mHazard) {
        this.mPosition = new LatLng(lat, lng);
        this.mTitle = mTitle;
        this.mHazard = mHazard;
    }
    //Peg the item based on it's latitude and longitude
    public PegItem(double lat, double lng, String hazardIcon) {
        mPosition = new LatLng(lat, lng);
    }
    //returns a LatLng object which has the restaurant's latitude and loongitude
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    //returns the title of the restaurant
    @Override
    public String getTitle() {
        return mTitle;
    }
    //Returns hazard icon for the Maps Activity
    public BitmapDescriptor getHazard() {
        return mHazard;
    }

    @Override
    public String getSnippet() {
        return "";
    }
}
