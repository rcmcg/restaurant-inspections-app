package com.example.group20restaurantapp.Model;

import android.util.Log;

import com.example.group20restaurantapp.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
//This class is reviewed.
/**
 * Contains information and list of Inspections for a single restaurant
 */

public class Restaurant {

    private static final String TAG = "Restaurant.java";
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String trackingNumber;
    private String city;
    private String facType;
    private int iconImgId;
    private boolean isFavourite;
    private boolean isModified;
    //List of inspections, the restaurant had
    List<Inspection> inspectionList = new ArrayList<>();
    //Constructor
    public Restaurant(String name, String address, double latitude, double longitude, String trackingNumber, String city, String facType, int icon) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.trackingNumber = trackingNumber;
        this.city = city;
        this.facType = facType;
        setImgId();
        this.isFavourite = false;
    }

    public Restaurant() {}

    //Setter and Getter for each variable
    public String getName() {
        return name;
    }
    //sets the name of the restaurant
    public void setName(String name) {
        this.name = name;
    }
    //gets the address of the restaurant
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    //gets the latitude of the restaurant
    public double getLatitude() {
        return latitude;
    }
    //sets the latitude of the restaurant
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    //Gets the longitude of the restaurant
    public double getLongitude() {
        return longitude;
    }
    //Sets the longitude of the restaurant
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    //Gets the tracking number of the restaurant
    public String getTrackingNumber() {
        return trackingNumber;
    }
    //Sets the tracking number of the restaurant
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    //Gets the city to the restaurant
    public String getCity() {
        return city;
    }
    //Sets the city of the restaurant
    public void setCity(String city) {
        this.city = city;
    }

    public String getFacType() {
        return facType;
    }

    public void setFacType(String facType) {
        this.facType = facType;
    }
    //Used to set restaurant logo in the listView
    public int getIconImgId() {
        return iconImgId;
    }
    //Returns if a restaurant is favourite or not
    public boolean isFavourite() {
        return isFavourite;
    }
    //Sets if the restaurant is a favourite
    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
    //If any information about a restaurant is modified by last update
    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }
    //List of inspections for each restaurant
    public List<Inspection> getInspectionList() {
        if(this==null){
         return inspectionList = null;
        }
        return inspectionList;
    }
    //returns a list inspection for each restaureant if there is any
    public Inspection getInspection(int inspection) {
        if (inspectionList.size() <= inspection || inspection < 0){
            return null;
        }

        return inspectionList.get(inspection);
    }
    //A processed list of inspection is set to that particular restaurant
    public void setInspectionList(List<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }

    //Sets restaurant image, If there is a restaurant chain then, they have the same image.
    public void setImgId() {
        name = this.getName();
        String first = String.valueOf(name.charAt(0));
        String second = String.valueOf(name.charAt(1));

        if (name.matches("(.*)A&W(.*)")) {
            iconImgId = R.drawable.a_and_w;
        } else if (name.matches("A & W #0755")) {
            iconImgId = R.drawable.a_and_w;
        } else if (name.matches("Lee Yuen Seafood Restaurant")) {
            iconImgId = R.drawable.lee_yuen;
        } else if (name.matches("The Unfindable Bar")) {
            iconImgId = R.drawable.the_unfindable_bar;
        } else if (name.matches("Top in Town Pizza") || name.matches("Top In Town Pizza")) {
            iconImgId = R.drawable.restaurant_pizza;
        } else if (name.matches("104 Sushi & Co.")) {
            iconImgId = R.drawable.sushi_and_co;
        } else if (name.matches("Zugba Flame Grilled Chicken")) {
            iconImgId = R.drawable.zugba_flame_grilled_chicken;
        } else if (name.matches("7-Eleven(.*)")) {
            iconImgId = R.drawable.seven_eleven;
        } else if (name.matches("5 Star Catering")) {
            iconImgId = R.drawable.five_star_logo;
        } else if (name.matches("555 Pizza Ltd")) {
            iconImgId = R.drawable.triple_five;
        } else if (name.matches("777 Pizza & Donair")) {
            iconImgId = R.drawable.triple_pizza_donair;
        } else if (name.matches("Adana Grill")) {
            iconImgId = R.drawable.adana_grill;
        } else if (name.matches("Afghan Kitchen")) {
            iconImgId = R.drawable.afghan_kitchen;
        } else if (name.matches("Aggarwal Sweets")) {
            iconImgId = R.drawable.aggarwal;
        }else if(name.matches("Bengal Grill Restaurant")){
            iconImgId=R.drawable.bengal_grill;
        }else if(name.matches("Browns Socialhouse(.*)")) {
            iconImgId=R.drawable.brown;
        }else if(name.matches("Burger King(.*)")) {
            iconImgId=R.drawable.burger_king;
        }else if(name.matches("Church's Chicken(.*)")) {
            iconImgId=R.drawable.ccc;
        }else {
            // Generic image if restaurant not found
            iconImgId =  R.drawable.restaurant_icon_clipart;
        }
    }

    //returns the number of inspections, the restaurant had
    public int getInspectionSize() {
    return inspectionList.size();
}
    @Override
    public String toString() {
        return "Restaurants{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", city='" + city + '\'' +
                ", facType='" + facType + '\'' +
                ", icon=" + iconImgId +
                '}';
    }
   //Returns the hazard level of the last inspection
    public String getLastHazardLevel() {
        if (inspectionList.isEmpty()) return null;
        return inspectionList.get(0).getHazardRating();
    }
    //Returns the number of critical violations in last year.
    public int countCriticalViolationInLastYear() {
        int count = 0;
        for (Inspection inspection : inspectionList) {
            try { //if there is any critical violation
                if (inspection.getDiffInDay() <= 365) {
                    count = count + inspection.getNumCritical();
                }
            } catch (ParseException e){
                Log.e(TAG, "countCriticalViolation: ", e);
            }
        }
        return count;
    }
}


