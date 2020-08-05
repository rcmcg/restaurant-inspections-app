package com.example.group20restaurantapp.Model;

import android.util.Log;

import com.example.group20restaurantapp.R;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Contains information about an individual inspection and a list of violations
 */

public class Inspection implements Serializable {
    private String trackingNumber;
    private String inspectionDate;
    private String inspType;    // Follow-up or routine
    private int numCriticalViolations;
    private int numNonCriticalViolations;
    private String hazardRating;
    List <Violation> violLump = new ArrayList<>();

    public Inspection(){}

    // Returns the tracking number of the associated restaurant
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public String fullFormattedDate() {
        // Return full date in format Month Day, Year
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String rawInspectionDate = getInspectionDate();

        String fullFormattedDate = "";
        try {
            // Create date object from inspectionDate
            Date inspectionD = sdf.parse(rawInspectionDate);

            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            assert inspectionD != null;
            inspectionCalendar.setTime(inspectionD);
            // Date format is changed to Month Day, Year from year,month,day
            fullFormattedDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)] + " "
                        + inspectionCalendar.get(Calendar.DAY_OF_MONTH)
                        + ", "
                        + inspectionCalendar.get(Calendar.YEAR);

        } catch (Exception e) {
            Log.e("Inspection.java", "fullFormattedDate: error creating date");
            e.printStackTrace();
        }
        return fullFormattedDate;
    }

    //https://www.baeldung.com/java-date-difference
    public String intelligentInspectDate() {
        String intelligentDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            // Get the current date
            Date currentDate = new Date();

            // Get the difference in days between inspectionDate and currentDate
            String rawInspectionDate = getInspectionDate();
            Date inspectionD = sdf.parse(rawInspectionDate);
            long diffInMS = Math.abs(currentDate.getTime() - inspectionD.getTime());
            long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);

            //https://stackoverflow.com/questions/36370895/getyear-getmonth-getday-are-deprecated-in-calendar-what-to-use-then
            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionD);

            if (diffInDay <= 1) {
                intelligentDate = diffInDay + " day";
            } else if (diffInDay <= 30) {   // Show days since last inspection
                intelligentDate = diffInDay + " days";
            } else if (diffInDay <= 365) {  // Show month and day of inspection
                intelligentDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);
            } else {                        // Show month and year of inspection
                intelligentDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.YEAR);
            }
        } catch (Exception e) {
            Log.d("Inspection.java", "intelligentInspectDate: Failed to produce date");
            e.printStackTrace();
        }
        return intelligentDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    // Returns the inspection type, if the device language is french, then french word is returned
    // Returns spanish if the language is spanish
    public String getInspType() {
        if (Locale.getDefault().getLanguage()=="fr") {
            if (this.inspType.matches("(.*)Fo(.*)")) {
            return"suivre";}
        } else if(Locale.getDefault().getLanguage()=="es") {
            if (this.inspType.matches("(.*)Fo(.*)")) {
                return"Seguimiento";
            }else if(this.inspType.matches("(.*)Ro(.*)")){
                return "Routina";
            }
        } else {
            return inspType;
        }
        return inspType;
    }

    public void setInspType(String inspType) {
        this.inspType = inspType;
    }

    public int getNumCriticalViolations() {
        return numCriticalViolations;
    }

    public void setNumCriticalViolations(int numCriticalViolations) {
        this.numCriticalViolations = numCriticalViolations;
    }

    public int getNumNonCriticalViolations() {
        return numNonCriticalViolations;
    }

    public void setNumNonCriticalViolations(int numNonCriticalViolations) {
        this.numNonCriticalViolations = numNonCriticalViolations;
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

    public Violation getViolation(int index) {
        return violLump.get(index);
    }

    public int getDiffInDay() throws ParseException {
        // Get the current date
        Date currentDate = new Date();

        // Get the difference in days between inspectionDate and currentDate
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String rawInspectionDate = getInspectionDate();
        Date inspectionD = sdf.parse(rawInspectionDate);
        long diffInMS = Math.abs(currentDate.getTime() - inspectionD.getTime());
        return (int) TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate='" + inspectionDate + '\'' +
                ", inspType='" + inspType + '\'' +
                ", numCritical=" + numCriticalViolations +
                ", numNonCritical=" + numNonCriticalViolations +
                ", hazardRating=" + hazardRating +
                ", violLump= " + Arrays.toString(violLump.toArray()) +
                '}';
    }
}
