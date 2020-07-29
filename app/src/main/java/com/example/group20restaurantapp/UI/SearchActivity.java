package com.example.group20restaurantapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.group20restaurantapp.R;

public class SearchActivity extends AppCompatActivity {
    private Button searchBtn;
    private Button clearBtn;
    private EditText searchField;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBtn = findViewById(R.id.btn_search);
        clearBtn = findViewById(R.id.btn_clean);
        searchField = findViewById(R.id.search_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}