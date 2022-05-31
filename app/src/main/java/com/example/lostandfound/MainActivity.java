package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button newAdvertBtn, showAllItemsBtn, showMapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newAdvertBtn = findViewById(R.id.newAdvertBtn);
        showAllItemsBtn = findViewById(R.id.showAllItemsBtn);
        showMapBtn = findViewById(R.id.showMapBtn);

        newAdvertBtn.setOnClickListener(view -> {
            Intent newAdvertIntent = new Intent(getApplicationContext(), NewAdvertActivity.class);
            startActivity(newAdvertIntent);
        });

        showAllItemsBtn.setOnClickListener(view -> {
            Intent showAllItemsIntent = new Intent(getApplicationContext(), LostAndFoundItemsActivity.class);
            startActivity(showAllItemsIntent);
        });

        showMapBtn.setOnClickListener(view -> {
            Intent showMapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(showMapIntent);
        });
    }
}