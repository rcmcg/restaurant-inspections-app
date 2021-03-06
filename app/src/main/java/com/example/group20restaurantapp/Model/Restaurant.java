package com.example.group20restaurantapp.Model;

import android.util.Log;

import com.example.group20restaurantapp.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Contains information and list of Inspections for a single restaurant
 */

public class Restaurant {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String trackingNumber;
    private String city;
    private String facType;
    private int iconImgId;
    private boolean isFavourite;
    private boolean isModified;         // If a restaurant has new information after an update
    private List<Inspection> inspectionList = new ArrayList<>();    // Contains all inspections for this restaurant

    private static final String TAG = "Restaurant.java";

    // Constructor
    public Restaurant(String name, String address, double latitude, double longitude, String trackingNumber, String city, String facType, int icon) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.trackingNumber = trackingNumber;
        this.city = city;
        this.facType = facType;
        setIconImgId();
        this.isFavourite = false;
    }

    public Restaurant() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFacType() {
        return facType;
    }

    public void setFacType(String facType) {
        this.facType = facType;
    }

    public int getIconImgId() {
        return iconImgId;
    }

    // Sets restaurant image. If there is a restaurant chain then they have the same image.
    public void setIconImgId() {
        name = this.getName();

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

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public List<Inspection> getInspectionList() {
        return inspectionList;
    }

    public Inspection getInspection(int inspection) {
        if (inspectionList.size() <= inspection || inspection < 0){
            return null;
        }
        return inspectionList.get(inspection);
    }

    public void setInspectionList(List<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }

    public int getInspectionSize() {
        return inspectionList.size();
    }

    public void addInspection(Inspection inspection) {
        inspectionList.add(inspection);
    }

    public String getLastHazardLevel() {
        if (inspectionList.isEmpty()) return null;
        return inspectionList.get(0).getHazardRating();
    }

    public void sortInspListByDate() {
        Comparator<Inspection> compareByDate = new Comparator<Inspection>() { //Compares inspection dates
            @Override
            public int compare(Inspection i1, Inspection i2) {
                return i1.getInspectionDate().compareTo(i2.getInspectionDate());
            }
        };

        Collections.sort(inspectionList, compareByDate.reversed()); // Sort ArrayList in reverse order
    }

    // Returns the number of critical violations in last year.
    public int countCriticalViolationInLastYear() {
        int count = 0;
        for (Inspection inspection : inspectionList) {
            try { //if there is any critical violation
                if (inspection.getDiffInDay() <= 365) {
                    count = count + inspection.getNumCriticalViolations();
                }
            } catch (ParseException e){
                Log.e(TAG, "countCriticalViolation: ", e);
            }
        }
        return count;
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
}


