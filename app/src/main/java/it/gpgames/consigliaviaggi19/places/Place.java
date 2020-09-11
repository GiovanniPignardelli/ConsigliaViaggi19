package it.gpgames.consigliaviaggi19.places;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Place {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriceTag() {
        return priceTag;
    }

    public void setPriceTag(String priceTag) {
        this.priceTag = priceTag;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAddYear() {
        return addYear;
    }

    public void setAddYear(String addYear) {
        this.addYear = addYear;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String name;
    private String address;
    private String city;
    private String postal_code;
    private String state;
    private String priceTag;
    private List<String> tags;

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Place(String name, String address, String city, String postal_code, String state, String priceTag, List<String> tags, String addYear, String latitude, String longitude, String email, String telephone, String website) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postal_code = postal_code;
        this.state = state;
        this.priceTag = priceTag;
        this.tags = tags;
        this.addYear = addYear;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.telephone = telephone;
        this.website = website;
    }

    private List<String> pictures;
    private String addYear;
    private String latitude;
    private String longitude;
    private String email;
    private String telephone;
    private String website;

    public static void PlaceGenerator(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference places = mFirestore.collection("places");
        final Place toAdd = new Place("Hotel Bobby", "Via Martino 3", "Afragola", "80021", "Italia", "€",new ArrayList<String>(Arrays.asList("Vista mare","Free Wifi")),"2020","40.936752", "14.319622","info@bobby.com","0818526746","www.bobby.com");
        places.add(toAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("RestaurantGenerator", "DocumentSnapshot written with ID: " + documentReference.getId());

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RestaurantGenerator", "Error adding document", e);
                    }
                });
    }
}
