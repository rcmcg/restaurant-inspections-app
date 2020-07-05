package com.example.groupassign1.Model;
import com.example.groupassign1.R;

import java.util.ArrayList;

public class Restaurant {

        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private String trackingNumber;
        private String city;
        private String facType;
        private int icon;
        private int criticalViolationCount;
        private boolean isFavourite;
        public ArrayList<Inspection> inspections;

        //Constructor
        public Restaurant(String name, String address, double latitude, double longitude,
                          String trackingNumber, String city, String facType) {
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.trackingNumber = trackingNumber;
            this.city = city;
            this.facType = facType;
            this.icon = matchLogo();
            this.inspections = new ArrayList<>();
            this.criticalViolationCount = countCriticalViolation();
        }

        public Restaurant() {

        }

        //Setter and Getter for each variable

        public ArrayList<Inspection> getInspections() {
        return inspections;
    }

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

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public boolean getFavourite(){
        return isFavourite;
    }

        private int countCriticalViolation() {
            int count = 0;
            for (Inspection inspection : inspections) {
                if (inspection.getDiffInDay() <= 365) {
                    count = count + inspection.getNumCritical();
                }
            }
            return count;
        }

        public String getLastHazardLevel() {
            return inspections.get(0).getHazardRating();
        }

        private int matchLogo(){
            name = this.getName();
            if (name.matches("^(A&W).*")){
                return R.drawable.a_and_w;
            }
            else if (name.matches("Lee Yuen Seafood Restaurant")){
                return R.drawable.lee_yuen;
            }
            else if (name.matches("The Unfindable Bar")){
                return R.drawable.the_unfindable_bar;
            }
            else if (name.matches("Top in Town Pizza")){
                return R.drawable.top_in_town_pizza;
            }
            else if (name.matches("104 Sushi & Co")){
                return R.drawable.sushi_and_co;
            }
            return R.drawable.zugba_flame_grilled_chicken;
        }

        public Inspection getInspection(int inspection) {
            if (inspections.size() <= inspection || inspection < 0){
                return null;
            }

            return inspections.get(inspection);
        }

        public int getInspectionSize() {
        return inspections.size();
    }

        @Override
        public String toString() {
            boolean empty = false;
            Inspection first = new Inspection("", "", "", 0, 0, "", "");

            if (inspections.isEmpty()) {

                empty = true;

            } else {

                first = inspections.get(0);

            }
            return "Restaurants{" +
                    "name='" + name + '\'' +
                    ", address='" + address + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", trackingNumber='" + trackingNumber + '\'' +
                    ", city='" + city + '\'' +
                    ", facType='" + facType + '\'' +
                    ", icon=" + icon +
                    '}';
        }
    }


