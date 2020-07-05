package com.example.group20restaurantapp.Model;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

        private String name;
        private String address;
        private double latitude;
        private double longitude;
        private String trackingNumber;
        private String city;
        private String facType;
        private int icon;
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
            this.icon = icon;
        }

        public Restaurant() {

        }

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

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public List<Inspection> getInspectionList() {
            return inspectionList;
        }

        public void setInspectionList(List<Inspection> inspectionList) {
            this.inspectionList = inspectionList;
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
                    ", icon=" + icon +
                    '}';
        }
    }


