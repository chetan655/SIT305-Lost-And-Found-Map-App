package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Item;

public class ItemActivity extends AppCompatActivity {

    TextView itemNameTV;
    TextView itemDescriptionTV;
    TextView itemDateTV;
    TextView itemLocationTV;
    TextView itemPhoneTV;
    Button itemRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemNameTV = findViewById(R.id.itemNameTV);
        itemDescriptionTV = findViewById(R.id.itemDescriptionTV);
        itemDateTV = findViewById(R.id.itemDateTV);
        itemLocationTV = findViewById(R.id.itemLocationTV);
        itemPhoneTV = findViewById(R.id.itemPhoneTV);
        itemRemoveBtn = findViewById(R.id.itemRemoveBtn);

        Intent intentReceive = getIntent();
        String name = intentReceive.getStringExtra("name");
        String description = "Description \n" + intentReceive.getStringExtra("description");
        String date = "Date \n" + intentReceive.getStringExtra("date");
        String location = "Location \n" + intentReceive.getStringExtra("locationName");
        String locationLat = intentReceive.getStringExtra("locationLat");
        String locationLng = intentReceive.getStringExtra("locationLng");
        String phone = "Phone \n" + intentReceive.getStringExtra("phone");
        String id = intentReceive.getStringExtra("id");

        itemNameTV.setText(name);
        itemDescriptionTV.setText(description);
        itemDateTV.setText(date);
        itemLocationTV.setText(location);
        itemPhoneTV.setText(phone);

        itemRemoveBtn.setOnClickListener(view -> {
            DatabaseHelper db = new DatabaseHelper(this);
            Item item = new Item(name, phone, description, date, location, locationLat, locationLng);
            item.setItemId(Integer.parseInt(id));
            db.deleteItem(item);
            Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}