package com.example.group20restaurantapp.Model;

import com.example.group20restaurantapp.R;

import java.util.ArrayList;
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
        }

        public Restaurant() {}

        //Setter and Getter for each variable
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

        public List<Inspection> getInspectionList() {

            if(this==null){
             return inspectionList = new ArrayList<>();
            }
            return inspectionList;
        }

        public void setInspectionList(List<Inspection> inspectionList) {
            this.inspectionList = inspectionList;
        }


        public void setImgId(){
            name = this.getName();
            if (name.matches("(.*)A&W(.*)")){
                iconImgId = R.drawable.a_and_w;
            }
            else if (name.matches("Lee Yuen Seafood Restaurant")){
                iconImgId =  R.drawable.lee_yuen;
            }
            else if (name.matches("The Unfindable Bar")){
                iconImgId =  R.drawable.the_unfindable_bar;
            }
            else if (name.matches("Top in Town Pizza") || name.matches("Top In Town Pizza")){
                iconImgId =  R.drawable.restaurant_pizza;
            }
            else if (name.matches("104 Sushi & Co.")){
                iconImgId =  R.drawable.sushi_and_co;
            } else if (name.matches("Zugba Flame Grilled Chicken")){
                iconImgId =  R.drawable.zugba_flame_grilled_chicken;
            } else {
                // Generic image if restaurant not found
                iconImgId =  R.drawable.restaurant_icon_clipart;
            }
        }


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
        public String getLastHazardLevel() {
            if (inspectionList.isEmpty()) return "None";
            return inspectionList.get(0).getHazardRating();
        }
        private int countCriticalViolation() {
            int count = 0;
            for (Inspection inspection : inspectionList) {
                if (inspection.getDiffInDay() <= 365) {
                    count = count + inspection.getNumCritical();
                }
            }
            return count;
        }
}


