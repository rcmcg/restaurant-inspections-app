package com.example.group20restaurantapp.Model;

import com.example.group20restaurantapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains information about a particular violation
 */

public class Violation implements Serializable {
    private int violNumber;
    private Boolean critical;
    private String violDetails;
    private String briefDetails;
    private Boolean repeat;
    private int violImgId;

    public Violation(int violNumber, Boolean critical, String violDetails, String briefDetails, Boolean repeat) {
        this.violNumber = violNumber;
        this.critical = critical;
        this.violDetails = violDetails;
        this.briefDetails = briefDetails;
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

    public int getViolNumber() {
        return violNumber;
    }

    public Boolean getCritical() {
        return critical;
    }

    public String getViolDetails() {
        return violDetails;
    }

    public String getBriefDetails() { return briefDetails; }

    public Boolean getRepeat() {
        return repeat;
    }
}
