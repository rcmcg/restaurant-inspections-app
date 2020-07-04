package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.group20restaurantapp.R;

public class InspectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        // Populate the list view
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, InspectionActivity.class);
    }
}