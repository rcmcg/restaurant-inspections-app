package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Restaurant;
import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.Model.Violation;
import com.example.group20restaurantapp.R;

import java.io.ObjectInputStream;
import java.util.List;

/**
 * Activity for an individual violation. Displays a list of violations for that inspection report.
 * User can press a violation to get more information about it.
 */

public class InspectionActivity extends AppCompatActivity {

    private static List<Violation> violationList;
    private static final String ACTION_BAR_TITLE = "Inspection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ACTION_BAR_TITLE);

        // Get inspection passed from RestaurantActivity as a serializableExtra
        Inspection inspection = (Inspection) getIntent().getSerializableExtra(RestaurantActivity.RESTAURANT_ACTIVITY_INSPECTION_TAG);

        assert inspection != null;
        violationList = inspection.getViolLump();

        setInspectionDateText(inspection);
        setInspectionTypeText(inspection);
        setInspectionCritViolText(inspection);
        setInspectionNonCritViolText(inspection);
        setHazardRatingIcon(inspection);
        setHazardRatingText(inspection);

        // Populate the list view
        populateListView(inspection);
        registerClickCallback();
    }

    // Source
    // https://stackoverflow.com/questions/36457564/display-back-button-of-action-bar-is-not-going-back-in-android/36457747
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Populates each inspection with the list of associated violations
    private void populateListView(Inspection inspection) {
        // Construct a new ArrayList from the list of Violations in inspection
        List<Violation> violationList = inspection.getViolLump();

        // Setup the listView
        ArrayAdapter<Violation> adapter = new MyListAdapter(violationList);
        ListView list = findViewById(R.id.lstViewViolations);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Violation> {

        public MyListAdapter(List<Violation> violationList) {
            super(InspectionActivity.this, R.layout.violation_item_view, violationList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_item_view, parent, false);
            }

            // Grab the current violation
            Violation currentViolation = InspectionActivity.getViolationList().get(position);

            // Set violation icon
            ImageView violationIcon = itemView.findViewById(R.id.violation_item_imgViolationIcon);
            violationIcon.setImageResource(currentViolation.getViolImgId());

            // Set violation brief description
            TextView violationDescription = itemView.findViewById(R.id.violation_item_txtBriefDescription);
            violationDescription.setText(
                    getString(R.string.inspection_activity_violation_item_brief_description,
                            currentViolation.getBriefDetails())
                    );

            // Set the severity icon, if the violation is critical
            ImageView violationSeverityIcon = itemView.findViewById(R.id.violation_item_imgSeverityIcon);
            if (currentViolation.getCritical()) {
                violationSeverityIcon.setImageResource(R.drawable.violation_icon_crit_skull);
                violationSeverityIcon.setBackgroundColor(Color.RED);
            } else {
                violationSeverityIcon.setImageResource(R.drawable.violation_icon_noncrit_exclamation);
                violationSeverityIcon.setBackgroundColor(Color.YELLOW);
            }

            return itemView;
        }
    }
    // IF any violation is clicked in the violation list then, details of the violation
    //is shown in a toast message
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.lstViewViolations);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get correct violation
                Violation clickedViolation = InspectionActivity.getViolationList().get(position);

                // Display full details of violation in a toast
                String message = getString(R.string.inspection_activity_violation_item_full_description,
                        "" + clickedViolation.getViolNumber(),
                        "" + clickedViolation.getCritical(),
                        "" + clickedViolation.getViolDetails(),
                        "" + clickedViolation.getRepeat()
                );
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    //Returns a list of violations for any particular inspection
    private static List<Violation> getViolationList() {
        return violationList;
    }
    //Sets the formatted inspection date in the textview
    private void setInspectionDateText(Inspection inspection) {
        TextView inspectionDate = findViewById(R.id.txtInspectionDate);
        inspectionDate.setText(getString(R.string.inspection_activity_date,
                inspection.fullFormattedDate()));
    }
    //If the inspection was routine or a follow-up
    private void setInspectionTypeText(Inspection inspection) {
        TextView inspectionType = findViewById(R.id.txtInspectionType);
        inspectionType.setText(getString(R.string.inspection_activity_type,
                inspection.getInspType()));
    }
    //Number of critical violation is set to a text view
    private void setInspectionCritViolText(Inspection inspection) {
        TextView inspectionNumCrit = findViewById(R.id.txtNumberCritViolations);
        inspectionNumCrit.setText(getString(R.string.inspection_activity_critical_violations,
                "" + inspection.getNumCritical()));
    }
    //Number of non-critical violation is set to a text view
    private void setInspectionNonCritViolText(Inspection inspection) {
        TextView inspectionNumNonCrit = findViewById(R.id.txtNumberNonCritViolations);
        inspectionNumNonCrit.setText(getString(R.string.inspection_activity_non_critical_violations,
                "" + inspection.getNumNonCritical()));
    }
    //Sets the appropriate icon for inspection, based on last inspection
    private void setHazardRatingIcon(Inspection inspection) {
        ImageView hazardIcon = findViewById(R.id.imgHazardIcon);
        if (inspection.getHazardRating().equals("Low")) {
            hazardIcon.setImageResource(R.drawable.yellow_triangle);
        } else if (inspection.getHazardRating().equals("Moderate")) {
            hazardIcon.setImageResource(R.drawable.orange_diamond);
        } else if (inspection.getHazardRating().equals("High")) {
            hazardIcon.setImageResource(R.drawable.red_octogon);
        }
    }

    private void setHazardRatingText(Inspection inspection) {
        TextView inspectionHazardRating = findViewById(R.id.txtHazardRating);
        inspectionHazardRating.setBackgroundColor(Color.BLACK);
        inspectionHazardRating.setText(getString(R.string.inspection_activity_hazard_rating,
                inspection.getHazardRating()));
        if (inspection.getHazardRating().equals("Low")) {
            inspectionHazardRating.setTextColor(Color.YELLOW);
        } else if (inspection.getHazardRating().equals("Moderate")) {
            // Set text color to bright orange
            inspectionHazardRating.setTextColor(Color.rgb(255, 165, 0));
        } else if (inspection.getHazardRating().equals("High")) {
            inspectionHazardRating.setTextColor(Color.RED);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, InspectionActivity.class);
    }

}