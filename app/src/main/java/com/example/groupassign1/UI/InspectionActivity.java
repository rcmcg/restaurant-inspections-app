package com.example.groupassign1.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.groupassign1.Model.Inspection;
import com.example.groupassign1.Model.Restaurant;
import com.example.groupassign1.Model.RestaurantManager;
import com.example.groupassign1.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InspectionActivity extends AppCompatActivity {

    private static final String EXTRA_MESSAGE = "Extra";
    private static final String RESTAURANT_MESSAGE = "Restaurant";

    public static Intent makeLaunchIntent(Context c, String message) {
        Intent intent = new Intent(c, InspectionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

    private Inspection mInspection;
    private Restaurant restaurant;
    private ArrayList<String> violations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getInspection();
        displayDetails();
        registerClickCallback();

    }

    private void displayDetails(){
        violationListView();

        TextView trackingNumberText= findViewById(R.id.trackingNumber);
        TextView inspectionDateText= findViewById(R.id.inspectionDate);
        TextView inspectionTypeText= findViewById(R.id.inspectionType);
        TextView numCriticalText= findViewById(R.id.numCritical);
        TextView numNonCriticalText= findViewById(R.id.numNonCritical);
        TextView hazardRatingText= findViewById(R.id.hazardRating);
        ImageView hazardImage = findViewById(R.id.hazardImage);


        trackingNumberText.setText(mInspection.getTrackingNumber());
        inspectionDateText.setText(getFormatDate());
        inspectionTypeText.setText(mInspection.getInspectionType());
        numCriticalText.setText(Integer.toString(mInspection.getNumCritical()));
        numNonCriticalText.setText(Integer.toString(mInspection.getNumNonCritical()));
        hazardRatingText.setText(mInspection.getHazardRating());

        if (mInspection.getHazardRating().equals("Low")) {

            hazardRatingText.setTextColor(Color.GREEN);

        } else if (mInspection.getHazardRating().equals("Moderate")) {

            hazardRatingText.setTextColor(Color.rgb(204, 204, 0));

        } else if (mInspection.getHazardRating().equals("High")) {

            hazardRatingText.setTextColor(Color.RED);

        }

        hazardImage.setImageResource(mInspection.getHazardIcon());

    }

    // Return a date formatted in "May 12, 2019"
    private String getFormatDate() {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
            Date inspectionDate = sdf.parse(mInspection.getInspectionDate());
            Calendar inspectionCalendar = Calendar.getInstance();

            inspectionCalendar.setTime(inspectionDate);
            String[] indexToMonth = new DateFormatSymbols().getMonths();

            return indexToMonth[inspectionCalendar.get(Calendar.MONTH)]
                    + " " + inspectionCalendar.get(Calendar.DAY_OF_MONTH)
                    + ", " + inspectionCalendar.get(Calendar.YEAR);

        }

        catch (Exception e) {

            // Handle it.
        }

        return "N/A";
    }

    private void getInspection() {

        RestaurantManager manager = RestaurantManager.getInstance();
        Intent intent = getIntent();
        String messageRestaurant = intent.getStringExtra(RESTAURANT_MESSAGE);
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        for (Restaurant temp : manager) {
            if (messageRestaurant.equals(temp.getTrackingNumber())) {
                restaurant = temp;
            }
        }

        for (Inspection temp : restaurant.inspections) {
            if (temp.toString().equals(message)) {
                mInspection = temp;
            }
        }

        for (int i = 0; i < mInspection.getViolations().length; i++) {
            violations.add(mInspection.getShortViolation(i));
        }
    }

    private void violationListView() {

        ArrayAdapter<String> adapter = new CustomAdapter();
        ListView violationsList = findViewById(R.id.violationsList);
        violationsList.setAdapter(adapter);

    }

    private class CustomAdapter extends ArrayAdapter<String> {

        public CustomAdapter() {
            super(InspectionActivity.this, R.layout.layout_violation, violations);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.layout_violation, parent, false);
            }

            String currentViolation = violations.get(position);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.violationimage);
            ImageView severityImage = (ImageView) itemView.findViewById(R.id.violationSeverity);

            //Setup severity icon
            if (violations.get(position).contains("Not Critical")) {
                severityImage.setImageResource(R.drawable.green);
            }
            else if (violations.get(position).contains("Critical")) {
                severityImage.setImageResource(R.drawable.red);
            }
            else {
                imageView.setImageResource(R.drawable.blank);
            }

            //Setup violation type icon
            if (violations.get(position).contains("pests") || violations.get(position).contains("Pests")) {

                imageView.setImageResource(R.drawable.pest);

            } else if (violations.get(position).contains("Equipment") || violations.get(position).contains("equipment")) {

                imageView.setImageResource(R.drawable.equipment);

            } else if (violations.get(position).contains("food") || violations.get(position).contains("Food") || violations.get(position).contains("Cold")) {

                imageView.setImageResource(R.drawable.food);

            } else if (violations.get(position).contains("Sanitized") || violations.get(position).contains("sanitized")) {

                imageView.setImageResource(R.drawable.sanitizer);

            } else if (violations.get(position).contains("handwashing")) {

                imageView.setImageResource(R.drawable.handwash);

            } else {

                imageView.setImageResource(R.drawable.blank);

            }

            TextView textView = (TextView) itemView.findViewById(R.id.violationtext);
            textView.setText(currentViolation);

            return itemView;
        }

    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.violationsList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View itemView = view;
                TextView textView = (TextView) itemView.findViewById(R.id.violationtext);
                String message = textView.getText().toString();

                for (String temp : mInspection.getViolations()) {
                    if (temp.length() > 10) {
                        if (temp.length() < 40) {
                            if (temp.equals(message)) {
                                message = temp;
                            }
                        } else {

                            if ((temp.substring(0, 40) + "...").equals(message)) {
                                message = temp;
                            }

                        }
                    }
                }
                if(message.length()>10) {
                    showToast(message);
                }
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(InspectionActivity.this, text, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        finish();
        return true;
    }
}
