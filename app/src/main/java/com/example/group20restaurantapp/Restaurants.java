package com.example.group20restaurantapp;

public class Restaurants {


        private String name;
        private String adress;
        private double lattitude;
        private double longitude;
        private String trackinngnumber;
        private String City;
        private String factype;
        private int icon;

        //Constructor
        public Restaurants(String name, String adress, double lattitude, double longitude, String trackinngnumber, String city, String factype, int icon) {
            this.name = name;
            this.adress = adress;
            this.lattitude = lattitude;
            this.longitude = longitude;
            this.trackinngnumber = trackinngnumber;
            this.City = city;
            this.factype = factype;
            this.icon = icon;
        }

        public Restaurants() {

        }

        //Setter and Getter for each variable

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAdress() {
            return adress;
        }

        public void setAdress(String adress) {
            this.adress = adress;
        }

        public double getLattitude() {
            return lattitude;
        }

        public void setLattitude(double lattitude) {
            this.lattitude = lattitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getTrackinngnumber() {
            return trackinngnumber;
        }

        public void setTrackinngnumber(String trackinngnumber) {
            this.trackinngnumber = trackinngnumber;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String city) {
            this.City = city;
        }

        public String getFactype() {
            return factype;
        }

        public void setFactype(String factype) {
            this.factype = factype;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }


        @Override
        public String toString() {
            return "Restaurants{" +
                    "name='" + name + '\'' +
                    ", adress='" + adress + '\'' +
                    ", lattitude=" + lattitude +
                    ", longitude=" + longitude +
                    ", trackinngnumber='" + trackinngnumber + '\'' +
                    ", City='" + City + '\'' +
                    ", factype='" + factype + '\'' +
                    ", icon=" + icon +
                    '}';
        }
    }


