package com.example.group20restaurantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeInspectionLists();
    }

    private void initializeInspectionLists() {


        for (Restaurant restaurant : RestaurantManager.getInstance())
        {

        }
    }
}