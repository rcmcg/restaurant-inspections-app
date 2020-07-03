package com.example.group20restaurantapp.Model;

import java.util.ArrayList;
import java.util.List;

public class Inspection {
    private String trackingNumber;
    private String inspectionDate;
    private String inspType; //Critical or non-critical
    private int numCritical;
    private int numNonCritical;
    private int hazardRating;

    class Violation{
        String violType; //Follow-up or routine
        String violationDetails;
    }

    List <Violation> violLump = new ArrayList<>();

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

    public int getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(int hazardRating) {
        this.hazardRating = hazardRating;
    }

    public List<Violation> getViolLump() {
        return violLump;
    }

    public void setViolLump(List<Violation> violLump) {
        this.violLump = violLump;
    }

}
