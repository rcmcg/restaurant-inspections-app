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
    // public int RESULT_SEARCH_UPDATED = 1;

    // Search filters
    private EditText searchField;
    private EditText violationCountField;
    private Button btnSearch;
    private Button clearBtn;
    private Spinner hazardSpinner;
    private Spinner violationSpinner;
    private Spinner favouriteSpinner;

    // Search terms
    // note, these need to be set by this activity, only update the
    // values in manager if the user presses search
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
        // setDefaultIntent();
        generateSearch();
    }

    private void generateSearch() {
        setupFields();
        setupButtons();
        setupSpinners();
    }

    private void setupFields() {
        //takes restaurant name as an input from user
        searchField = (EditText) findViewById(R.id.search_message);
        //takes violation count as an input from user
        violationCountField = (EditText) findViewById(R.id.count_text_search);
    }
    //This function sets up all the button
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

    //https://www.youtube.com/watch?v=GeO5F0nnzAw
    //set the hazardLevel and violationNum on spinners
    private void setupSpinners() {
        hazardSpinner = (Spinner) findViewById(R.id.Hazardlevel);
        ArrayAdapter<CharSequence> hazardAdapter = ArrayAdapter.createFromResource(this,
                R.array.HazardlevelStr, android.R.layout.simple_spinner_dropdown_item);
        //Dropdown is used for dropping down the choices as simple spinner dropdown is not appropriate
        hazardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Sets the spinner to hazard adapter, so that it gets all the choices
        hazardSpinner.setAdapter(hazardAdapter);
        //When an option is clicked, it can be processed
        hazardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //On clicked in one of the options, it gets the index position
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // manager.setSearchHazardLevelStr(position);
                searchHazardLevelStrIndex = position;
            }
            //If the user selects nothing
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // manager.setSearchHazardLevelStr(0);
                searchHazardLevelStrIndex = 0;
            }
        });

        violationSpinner = (Spinner) findViewById(R.id.ViolationNum);
        ArrayAdapter<CharSequence> ViolationAdapter = ArrayAdapter.createFromResource(this,
                R.array.ViolationNumBound, android.R.layout.simple_spinner_dropdown_item);
        ViolationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        violationSpinner.setAdapter(ViolationAdapter);
        violationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchViolationNumEquality = position;
                // manager.setSearchViolationNumEquality(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchViolationNumEquality = 0;
                // manager.setSearchViolationNumEquality(0);
            }
        });


        favouriteSpinner = (Spinner) findViewById(R.id.ChooseFavorite);
        ArrayAdapter<CharSequence> favoriteAdapter = ArrayAdapter.createFromResource(this,
                R.array.ChooseFavor, android.R.layout.simple_spinner_dropdown_item);
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

    // Submit the users's input
    private void submitSearch() {
        // Get the search term
        searchTerm = searchField.getText().toString();
        // Update search term in manager
        manager.setSearchTerm(searchTerm);
        // String itemSearch = searchField.getText().toString();

        // Update the hazard level of last inspection in manager
        manager.setSearchHazardLevelStr(searchHazardLevelStrIndex);

        // Set the searchViolationNumEquality in manager
        manager.setSearchViolationNumEquality(searchViolationNumEquality);

        // Set the violation bound in manager
        updateViolationCountRestriction();

        // Set searchFavouritesOnly in manager
        manager.setSearchFavouritesOnly(searchFavouritesOnly);

        // Update filtered restaurant list with new terms
        manager.updateFilteredRestaurants();

        setUpdateResult();

        this.finish();
    }

    private void setUpdateResult() {
        // Let the calling activity know they need to update their data
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }

    private void updateViolationCountRestriction() {
        try {
            int bound = Integer.parseInt(violationCountField.getText().toString());
            manager.setSearchViolationBound(bound);
        } catch (Exception e) {
            // Invalid entry or other error
            manager.setSearchViolationBound(-1);
        }
    }

    private void clearFilters() {
        // Reset search parametres in manager
        manager.setSearchTerm("");
        manager.setSearchHazardLevelStr(0);
        manager.setSearchViolationNumEquality(0);
        manager.setSearchViolationBound(-1);
        manager.setSearchFavouritesOnly(false);

        // Update filtered restaurant list with new terms
        manager.updateFilteredRestaurants();

        setUpdateResult();
    }

    /*
    private void setDefaultIntent() {
        Intent i = new Intent();
        setResult(Activity.RESULT_OK, i);
    }
     */

    //solve the action bar return to the previous activity
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