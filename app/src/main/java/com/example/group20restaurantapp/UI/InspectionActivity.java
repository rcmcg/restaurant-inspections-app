package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group20restaurantapp.Model.Inspection;
import com.example.group20restaurantapp.R;

public class InspectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        Inspection inspection = (Inspection) getIntent().getSerializableExtra("inspection");

        setInspectionDateText(inspection);
        setInspectionTypeText(inspection);
        setInspectionCritViolText(inspection);
        setInspectionNonCritViolText(inspection);
        setHazardRatingIcon(inspection);
        setHazardRatingText(inspection);

        // Populate the list view
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
        inspectionHazardRating.setText("Type: " + inspection.getHazardRating());
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, InspectionActivity.class);
    }
}