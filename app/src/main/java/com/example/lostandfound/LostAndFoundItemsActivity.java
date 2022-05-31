package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Item;

import java.util.ArrayList;
import java.util.List;

public class LostAndFoundItemsActivity extends AppCompatActivity {

    ListView itemsListView;
    List<Item> itemList;
    ArrayList<String> itemNameList;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and_found_items);

        itemsListView = findViewById(R.id.itemsListView);

        DatabaseHelper db = new DatabaseHelper(this);

        itemList = db.fetchAllItem();
        itemNameList = new ArrayList<>();

        for (Item item:itemList) {
            itemNameList.add(item.getName());
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNameList);
        itemsListView.setAdapter(adapter);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                intent.putExtra("name", itemList.get(i).getName());
                intent.putExtra("phone", itemList.get(i).getPhone());
                intent.putExtra("description", itemList.get(i).getDescription());
                intent.putExtra("date", itemList.get(i).getDate());
                intent.putExtra("locationName", itemList.get(i).getLocationName());
                intent.putExtra("locationLat", itemList.get(i).getLocationLat());
                intent.putExtra("locationLng", itemList.get(i).getLocationLng());
                intent.putExtra("id", Integer.toString(itemList.get(i).getItemId()));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==1) {

            itemsListView = findViewById(R.id.itemsListView);
            DatabaseHelper db = new DatabaseHelper(this);

            itemList = db.fetchAllItem();
            itemNameList = new ArrayList<>();

            for (Item item:itemList) {
                itemNameList.add(item.getName());
            }

            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNameList);
            itemsListView.setAdapter(adapter);
        }
    }
}