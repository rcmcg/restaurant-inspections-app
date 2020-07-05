package com.example.groupassign1.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.groupassign1.Model.Inspection;
import com.example.groupassign1.Model.Restaurant;
import com.example.groupassign1.Model.RestaurantManager;
import com.example.groupassign1.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    SharedPreferences mSharedPreferences;

    private static final String EXTRA_MESSAGE = "Extra";
    private RestaurantManager manager;
    private int size = 0;
    private String[] restaurantStrings = new String[size];

    List<Restaurant> restaurants = new ArrayList<>();
    List<Restaurant> updatedRestaurants = new ArrayList<>();

    public static Intent makeLaunchIntent(Context c, String message) {
        Intent i1 = new Intent(c, MainActivity.class);
        i1.putExtra(EXTRA_MESSAGE, message);
        return i1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
    }
}