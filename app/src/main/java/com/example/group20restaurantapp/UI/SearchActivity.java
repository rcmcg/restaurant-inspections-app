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
    //Search filters
    private EditText searchField;
    private EditText violationCountField;
    private Button btnSearch;
    private Button clearBtn;
    private Spinner hazardSpinner;
    private Spinner ViolationSpinner;
    private Spinner FavoriteSpinner;

    private RestaurantManager manager = RestaurantManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setDefaultIntent();
        generateSearch();
    }

    private void generateSearch() {
        setupFields();
        setupButtons();
        setupSpinners();
    }

    private void setupFields() {
        searchField = (EditText) findViewById(R.id.search_message);
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

    //https://www.youtube.com/watch?v=GeO5F0nnzAw
    //set the hazardLevel and violationNum on spinners
    private void setupSpinners() {
        hazardSpinner = (Spinner) findViewById(R.id.Hazardlevel);
        ArrayAdapter<CharSequence> hazardAdapter = ArrayAdapter.createFromResource(this,
                R.array.HazardlevelStr, android.R.layout.simple_spinner_dropdown_item);
        hazardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardSpinner.setAdapter(hazardAdapter);
        hazardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setHazardLevelStr(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setHazardLevelStr(0);
            }
        });

        ViolationSpinner = (Spinner) findViewById(R.id.ViolationNum);
        ArrayAdapter<CharSequence> ViolationAdapter = ArrayAdapter.createFromResource(this,
                R.array.ViolationNumBound, android.R.layout.simple_spinner_dropdown_item);
        ViolationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ViolationSpinner.setAdapter(ViolationAdapter);
        ViolationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setViolationNum(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setViolationNum(0);
            }
        });


        FavoriteSpinner = (Spinner) findViewById(R.id.ChooseFavorite);
        ArrayAdapter<CharSequence> favoriteAdapter = ArrayAdapter.createFromResource(this,
                R.array.ChooseFavor, android.R.layout.simple_spinner_dropdown_item);
        favoriteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FavoriteSpinner.setAdapter(favoriteAdapter);
        FavoriteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean temp = false;
                if(position==0){
                    temp = true;
                }
                manager.setFavorite(temp);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setFavorite(false);
            }
        });
    }

    //submit the users's input
    private void submitSearch() {
        updateViolationCountRestriction();
        String itemSearch = searchField.getText().toString();
        manager.setItemSearch(itemSearch);
        this.finish();
    }

    private void updateViolationCountRestriction() {
        try {
            int bound = Integer.parseInt(violationCountField.getText().toString());
            manager.setViolationBound(bound);
        }
        catch (Exception e) {}
    }

    private void clearFilters() {
        manager.setItemSearch(null);
        manager.setHazardLevelStr(0);
        manager.setViolationNum(0);
    }

    private void setDefaultIntent() {
        Intent i = new Intent();
        setResult(Activity.RESULT_OK, i);
    }

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