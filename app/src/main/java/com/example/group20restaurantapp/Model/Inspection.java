package com.example.group20restaurantapp.Model;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.group20restaurantapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Inspection {
    private String trackingNumber;
    private String inspectionDate;
    private String inspType;    // Follow-up or routine
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;

    static class Violation{
        int violNumber;
        Boolean critical;
        String violationDetails;
        Boolean repeat;
        int violImgId;

        public Violation(int violNumber, Boolean critical, String violDetails, Boolean repeat) {
            this.violNumber = violNumber;
            this.critical = critical;
            this.violationDetails = violDetails;
            this.repeat = repeat;
            setViolImgId(violNumber);
        }

        public int getViolImgId() {
            return violImgId;
        }

        public void setViolImgId(int violNumber) {
            // Create collection of permit related violations
            List<Integer> permitViolNums = Arrays.asList(103, 104, 212, 314, 501, 502);
            ArrayList<Integer> permitViolArrayList = new ArrayList<>();
            permitViolArrayList.addAll(permitViolNums);

            // Create collection of bad food related violations
            List<Integer> badFoodViolNums = Arrays.asList(201,202,203,204,205,206,208,209,210,211);
            ArrayList<Integer> badFoodArrayList = new ArrayList<>();
            badFoodArrayList.addAll(badFoodViolNums);

            // Create collection of utensil/equipment related violations
            List<Integer> utensilsViolNums = Arrays.asList(301,302,303,307,308,310);
            ArrayList<Integer> utensilsArrayList = new ArrayList<>();
            utensilsArrayList.addAll(utensilsViolNums);

            if (violNumber == 101) {
                this.violImgId = R.drawable.violation_construction;
            } else if (violNumber == 102) {
                this.violImgId = R.drawable.violation_restaurant;
            } else if (permitViolArrayList.contains(violNumber)) {
                this.violImgId = R.drawable.violation_permit;
            } else if (badFoodArrayList.contains(violNumber)) {
                this.violImgId = R.drawable.violation_bad_food;
            } else if (utensilsArrayList.contains(violNumber)) {
                this.violImgId = R.drawable.violation_utensils;
            } else if (violNumber == 304 || violNumber == 305) {
                this.violImgId = R.drawable.violation_rat;
            } else if (violNumber == 306 || violNumber == 311) {
                this.violImgId = R.drawable.violation_dirty_kitchen;
            } else if (violNumber == 309) {
                this.violImgId = R.drawable.violation_cleaners;
            } else if (violNumber == 312) {
                this.violImgId = R.drawable.violation_storage;
            } else if (violNumber == 313) {
                this.violImgId = R.drawable.violation_dog;
            } else if (violNumber == 315) {
                this.violImgId = R.drawable.violation_thermometer;
            } else if (violNumber == 401 || violNumber == 402 || violNumber == 403) {
                this.violImgId = R.drawable.violation_washing_hands;
            } else if (violNumber == 404) {
                this.violImgId = R.drawable.violation_smoking;
            } else {
                this.violImgId = R.drawable.violation_generic;
            }

        }
    }

    List <Violation> violLump = new ArrayList<>();
    // List <String> violLump = new ArrayList<>();

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspType() {
        return inspType;
    }

    public void setInspType(String inspType) {
        this.inspType = inspType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public List<Violation> getViolLump() {
        return violLump;
    }

    public void setViolLump(List<Violation> violLump) {
        this.violLump = violLump;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate='" + inspectionDate + '\'' +
                ", inspType='" + inspType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating=" + hazardRating +
                ", violLump= " + Arrays.toString(getViolLump().toArray()) +
                '}';
    }
}
