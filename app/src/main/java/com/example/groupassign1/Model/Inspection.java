package com.example.groupassign1.Model;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.example.groupassign1.R;

public class Inspection {
    private String formattedDate;
    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private String hazardRating;
    private int diffInDay;

    private int numCritical;
    private int numNonCritical;

    private String[] violations;

    public Inspection(String trackingNumber, String inspectionDate, String inspectionType,
            int numCritical, int numNonCritical, String hazardRating, String violations) {
            this.trackingNumber = trackingNumber;
            this.inspectionDate = inspectionDate;
            this.inspectionType = inspectionType;
            this.numCritical = numCritical;
            this.numNonCritical = numNonCritical;
            this.hazardRating = hazardRating;
            this.violations = parseViolations(violations);
            initDate();
    }

    public void initDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            String rawInspectionDate = getInspectionDate();
            Date inspectionDate = sdf.parse(rawInspectionDate);
            Date currentDate = new Date();

            long diffInMS = Math.abs(currentDate.getTime() - inspectionDate.getTime());
            long diffInDay = TimeUnit.DAYS.convert(diffInMS, TimeUnit.MILLISECONDS);
            this.diffInDay = (int) diffInDay;

            String[] indexToMonth = new DateFormatSymbols().getMonths();
            Calendar inspectionCalendar = Calendar.getInstance();
            inspectionCalendar.setTime(inspectionDate);

            if (diffInDay <= 1) {

                this.formattedDate = diffInDay + "Day";

            } else if (diffInDay <= 30) {

                this.formattedDate = diffInDay + " Days";

            } else if (diffInDay <= 365) {

                this.formattedDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH);

            } else {

                this.formattedDate = indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                        + " " + inspectionCalendar.get(Calendar.YEAR);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.formattedDate = "N/A";
        }
    }

    public String dateFormatter() { return this.formattedDate; }
    public int getDiffInDay() { return this.diffInDay; }

    private String[] parseViolations(String rawViolations) {
        return rawViolations.replace(",", ", ").split("\\|");
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getInspectionDate() {
        return this.inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }


    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String[] getViolations() {
        return this.violations;
    }


    public int getHazardIcon() {

        if (hazardRating.equals("Low")) {

            return R.drawable.green;

        } else if (hazardRating.equals("Moderate")) {

            return R.drawable.yellow;

        } else {

            return R.drawable.red;

        }

    }

    @Override
    public String toString() {

        return  numCritical + ", " +
                numNonCritical + ", " +
                this.dateFormatter() + ", " +
                inspectionType + ", " +
                hazardRating;

    }

    public String getShortViolation(int position) {

        if (violations.length == 0) {

            return "";

        }

        String[] shortViolations = new String[violations.length];

        for (int i = 0; i < violations.length; i++) {

            if (violations[i].length() > 10) {

                if (violations[i].length() < 40) {

                    shortViolations[i] = violations[i];

                } else {

                    shortViolations[i] = violations[i].substring(0, 40) + "...";

                }
            }

            else {

                shortViolations[i] = violations[i];

            }
        }

        return shortViolations[position];
    }

}
