package com.example.group20restaurantapp.Model;

import com.example.group20restaurantapp.R;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Inspection implements Serializable {
    private String trackingNumber;
    private String inspectionDate;
    private String inspType;    // Follow-up or routine
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    List <Violation> violLump = new ArrayList<>();
    // List <String> violLump = new ArrayList<>();
    //new things I've added
    private int diffInDay;
    private String inspectionType;
    private String[] violations;
    public Inspection(){}
    private String[] parseViolations(String rawViolations) {
        return rawViolations.replace(",", ", ").split("\\|");
    }
    public int getDiffInDay() { return this.diffInDay; }

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
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            String rawInspectionDate = getInspectionDate();
            Date inspectionD = sdf.parse(rawInspectionDate);
            Date currentDate = new Date();

            long diffInMS = Math.abs(currentDate.getTime() - inspectionD.getTime());
            long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);
            this.diffInDay = (int) diffInDay;

            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionD);

            if (diffInDay <= 1) {

                this.inspectionDate = diffInDay + "Day";

            } else if (diffInDay <= 30) {

                this.inspectionDate = diffInDay + " Days";

            } else if (diffInDay <= 365) {

                this.inspectionDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);

            } else {

                this.inspectionDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.YEAR);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.inspectionDate = "N/A";
        }
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
