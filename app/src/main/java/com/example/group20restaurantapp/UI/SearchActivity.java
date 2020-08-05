package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.group20restaurantapp.Model.RestaurantManager;
import com.example.group20restaurantapp.R;

public class SearchActivity extends AppCompatActivity {
    // Search filters
    private EditText searchField;
    private EditText violationCountField;
    private Button btnSearch;
    private Button clearBtn;
    private Spinner hazardSpinner;
    private Spinner violationSpinner;
    private Spinner favouriteSpinner;

    // Search terms: these need to be set by this activity, only update the values in manager if the
    // user presses search
    private String searchTerm;
    private int searchHazardLevelStrIndex;
    private int searchViolationNumEquality;
    private int searchViolationBound;
    private boolean searchFavouritesOnly;

    private RestaurantManager manager = RestaurantManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        generateSearch();
    }

    private void generateSearch() {
        setupFields();
        setupButtons();
        setupSpinners();
    }

    private void setupFields() {
        searchField = (EditText) findViewById(R.id.search_message);
        // Takes violation count as an input from user
        violationCountField = (EditText) findViewById(R.id.count_text_search);
    }

    private void setupButtons() {
        btnSearch= (Button) findViewById(R.id.btn_search);
        clearBtn = (Button) findViewById(R.id.btn_clean);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSearch();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
                finish();
            }
        });
    }

    // https://www.youtube.com/watch?v=GeO5F0nnzAw
    // Set the hazardLevel and violationNum on spinners
    private void setupSpinners() {
        // Get the hazard spinner
        hazardSpinner = (Spinner) findViewById(R.id.Hazardlevel);
        // Set an adapter for the hazardSpinner
        ArrayAdapter<CharSequence> hazardAdapter = ArrayAdapter.createFromResource(this,
                R.array.HazardlevelStr, android.R.layout.simple_spinner_dropdown_item);
        // Set spinner layout
        hazardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardSpinner.setAdapter(hazardAdapter);

        hazardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchHazardLevelStrIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchHazardLevelStrIndex = 0;
            }
        });

        // Get the violation spinner
        violationSpinner = (Spinner) findViewById(R.id.ViolationNum);
        // Set an adapter for the violation spinner
        ArrayAdapter<CharSequence> ViolationAdapter = ArrayAdapter.createFromResource(this,
                R.array.ViolationNumBound, android.R.layout.simple_spinner_dropdown_item);
        // Set spinner layout
        ViolationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        violationSpinner.setAdapter(ViolationAdapter);

        violationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchViolationNumEquality = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchViolationNumEquality = 0;
            }
        });

        // Get the favourite spinner
        favouriteSpinner = (Spinner) findViewById(R.id.ChooseFavorite);
        // Set an adapter for the favourite spinner
        ArrayAdapter<CharSequence> favoriteAdapter = ArrayAdapter.createFromResource(this,
                R.array.ChooseFavor, android.R.layout.simple_spinner_dropdown_item);
        // Set spinner layout
        favoriteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favouriteSpinner.setAdapter(favoriteAdapter);

        favouriteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchFavouritesOnly = position != 0;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchFavouritesOnly = false;
            }
        });
    }

    // Submit the users's input to the manager Singleton
    private void submitSearch() {
        // Get the search term and update searchTerm in manager
        searchTerm = searchField.getText().toString();
        manager.setSearchTerm(searchTerm);

        // Get global variable and update the hazard level of last inspection in manager
        manager.setSearchHazardLevelStr(searchHazardLevelStrIndex);

        // Get global variable and set the searchViolationNumEquality in manager
        manager.setSearchViolationNumEquality(searchViolationNumEquality);

        // Set the violation bound in manager
        updateViolationCountRestriction();

        // Get global variable and set searchFavouritesOnly in manager
        manager.setSearchFavouritesOnly(searchFavouritesOnly);

        // Update filtered restaurant list with new terms
        manager.updateFilteredRestaurants();

        setUpdateResult();
        finish();
    }

    private void setUpdateResult() {
        // Let the calling activity know they need to update their data
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }

    private void updateViolationCountRestriction() {
        try {
            searchViolationBound = Integer.parseInt(violationCountField.getText().toString());
            manager.setSearchViolationBound(searchViolationBound);
        } catch (Exception e) {
            // Invalid entry or other error
            manager.setSearchViolationBound(-1);
        }
    }

    private void clearFilters() {
        // Reset search parameters in manager
        manager.setSearchTerm("");
        manager.setSearchHazardLevelStr(0);
        manager.setSearchViolationNumEquality(0);
        manager.setSearchViolationBound(-1);
        manager.setSearchFavouritesOnly(false);

        // Update filtered restaurant list with new terms
        manager.updateFilteredRestaurants();

        setUpdateResult();
    }

    // Solve the action bar return to the previous activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}