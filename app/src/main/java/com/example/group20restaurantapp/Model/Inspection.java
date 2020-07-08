package com.example.group20restaurantapp.Model;

import android.util.Log;

import com.example.group20restaurantapp.R;

import java.io.IOException;
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
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    List <Violation> violLump = new ArrayList<>();

    public Inspection(){}

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
            Date inspectionD = sdf.parse(rawInspectionDate);

            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            assert inspectionD != null;
            inspectionCalendar.setTime(inspectionD);

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

    public String intelligentInspectDate() {
        String intelligentDate = "";
        try {
            // Get the current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date currentDate = new Date();

            // Get the difference in days between inspectionDate and currentDate
            String rawInspectionDate = getInspectionDate();
            Date inspectionD = sdf.parse(rawInspectionDate);
            long diffInMS = Math.abs(currentDate.getTime() - inspectionD.getTime());
            long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);

            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionD);

            if (diffInDay <= 1) {
                intelligentDate = diffInDay + " day";
            } else if (diffInDay <= 30) {
                intelligentDate = diffInDay + " days";
            } else if (diffInDay <= 365) {
                intelligentDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);
            } else {
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
