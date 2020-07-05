package com.example.group20restaurantapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.Model.Violation;
import com.example.group20restaurantapp.R;

import java.io.ObjectInputStream;
import java.util.List;

public class InspectionActivity extends AppCompatActivity {

    private static List<Violation> violationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        Inspection inspection = (Inspection) getIntent().getSerializableExtra("inspection");

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

            // Grab the correct violation
            Violation currentViolation = InspectionActivity.getViolationList().get(position);

            // Set violation icon
            ImageView violationIcon = itemView.findViewById(R.id.violation_item_imgViolationIcon);
            violationIcon.setImageResource(currentViolation.getViolImgId());
            // violationIcon.setImageResource(R.drawable.violation_rat);

            // Set violation brief description
            // TODO: Add and integrate briefDescriptions.txt
            TextView violationDescription = itemView.findViewById(R.id.violation_item_txtBriefDescription);
            violationDescription.setText(currentViolation.getBriefDetails());

            // Set the severity icon
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

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.lstViewViolations);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get correct violation
                Violation clickedViolation = InspectionActivity.getViolationList().get(position);
                // Display full details of violation in a toast
                // TODO: move to strings.xml with placeholder
                String message = "Violation number: " + clickedViolation.getViolNumber()
                        + ". Critical: " + clickedViolation.getCritical()
                        + ". Details: " + clickedViolation.getViolDetails()
                        + ". Repeat: " + clickedViolation.getRepeat();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static List<Violation> getViolationList() {
        return violationList;
    }

    private void setInspectionDateText(Inspection inspection) {
        TextView inspectionDate = findViewById(R.id.txtInspectionDate);
        inspectionDate.setText("Date: " + inspection.getInspectionDate());
    }

    private void setInspectionTypeText(Inspection inspection) {
        TextView inspectionType = findViewById(R.id.txtInspectionType);
        inspectionType.setText("Type: " + inspection.getInspType());
    }

    private void setInspectionCritViolText(Inspection inspection) {
        TextView inspectionNumCrit = findViewById(R.id.txtNumberCritViolations);
        inspectionNumCrit.setText("Critical violations: " + inspection.getNumCritical());
    }

    private void setInspectionNonCritViolText(Inspection inspection) {
        TextView inspectionNumNonCrit = findViewById(R.id.txtNumberNonCritViolations);
        inspectionNumNonCrit.setText("Non-critical violations: " + inspection.getNumNonCritical());
    }

    private void setHazardRatingIcon(Inspection inspection) {
        ImageView hazardIcon = findViewById(R.id.imgHazardIcon);
        if (inspection.getHazardRating() == "Low") {
            hazardIcon.setImageResource(R.drawable.yellow_triangle);
        } else if (inspection.getHazardRating() == "Moderate") {
            hazardIcon.setImageResource(R.drawable.orange_diamond);
        } else if (inspection.getHazardRating() == "High") {
            hazardIcon.setImageResource(R.drawable.red_octogon);
        }
    }

    private void setHazardRatingText(Inspection inspection) {
        TextView inspectionHazardRating = findViewById(R.id.txtHazardRating);
        inspectionHazardRating.setText("Hazard rating: " + inspection.getHazardRating());
        if (inspection.getHazardRating() == "Low") {
            inspectionHazardRating.setBackgroundColor(Color.YELLOW);
        } else if (inspection.getHazardRating() == "Moderate") {
            // Set to orange
            inspectionHazardRating.setBackgroundColor(Color.rgb(255,165,0));
        } else if (inspection.getHazardRating() == "High") {
            inspectionHazardRating.setBackgroundColor(Color.RED);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, InspectionActivity.class);
    }
}