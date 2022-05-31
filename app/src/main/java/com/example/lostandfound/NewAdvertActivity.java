package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Item;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;


public class NewAdvertActivity extends AppCompatActivity {

    private static final String TAG = "New Advert Activity";
    RadioGroup radioBtnsGroup;
    RadioButton selectedRadioButton;
    EditText itemNameETV;
    EditText phoneETV;
    EditText itemDescriptionETV;
    EditText itemDateETV;
    Button getItemCurrentLocationBtn;
    Button itemSaveBtn;
    String placeName, placeLat, placeLng;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);

        radioBtnsGroup = findViewById(R.id.radioBtnsGroup);
        itemNameETV = findViewById(R.id.itemNameETV);
        phoneETV = findViewById(R.id.phoneETV);
        itemDescriptionETV = findViewById(R.id.itemDescriptionETV);
        itemDateETV = findViewById(R.id.itemDateETV);
        getItemCurrentLocationBtn = findViewById(R.id.getItemCurrentLocationBtn);
        itemSaveBtn = findViewById(R.id.itemSaveBtn);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewAdvertActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        String API_KEY = BuildConfig.MAPS_API_KEY;

        Places.initialize(getApplicationContext(), API_KEY);

        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapAutoCompleteETV);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                placeName = place.getName();
                placeLat = Double.toString(place.getLatLng().latitude);
                placeLng = Double.toString(place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

                Log.i(TAG, "An error occurred: " + status);
            }
        });

        getItemCurrentLocationBtn.setOnClickListener(view -> {

            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

                placeResponse.addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        FindCurrentPlaceResponse response = task.getResult();

                        placeName = response.getPlaceLikelihoods().get(0).getPlace().getName();
                        placeLat = Double.toString(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().latitude);
                        placeLng = Double.toString(response.getPlaceLikelihoods().get(0).getPlace().getLatLng().longitude);

                        autocompleteFragment.setText(placeName);

                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                        }

                    } else {
                        Exception exception = task.getException();

                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewAdvertActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }
            }
        });


        DatabaseHelper db = new DatabaseHelper(this);

        itemSaveBtn.setOnClickListener(view -> {
            int selectedId = radioBtnsGroup.getCheckedRadioButtonId();
            String name;
            name = itemNameETV.getText().toString();
            String phone = phoneETV.getText().toString();
            String description = itemDescriptionETV.getText().toString();
            String date = itemDateETV.getText().toString();

            if (name.equals("") || phone.equals("") || description.equals("") || date.equals("") || placeName == null || placeLat == null || placeLng == null) { // check for empty fields
                Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            } else if (selectedId == -1) {
                Toast.makeText(this, "Please select post type", Toast.LENGTH_SHORT).show();
            } else {
                selectedRadioButton = findViewById(selectedId);
                name = selectedRadioButton.getText().toString() + " " + name;

                Item item = new Item(name, phone, description, date, placeName, placeLat, placeLng);
                db.insertItem(item);
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NewAdvertActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}