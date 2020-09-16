package it.gpgames.consigliaviaggi19.places;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hotel extends Place implements Serializable {

    public Hotel(String name, String address, String city, String postal_code, String state, String priceTag, List<String> tags, String addYear, String latitude, String longitude, String email, String telephone, String website, List<String> roomTags, List<String> roomTypeTags, Integer hClass, String category) {
        super(name, address, city, postal_code, state, priceTag, tags, addYear, latitude, longitude, email, telephone, website, category);
        this.roomTags = roomTags;
        this.roomTypeTags = roomTypeTags;
        this.hClass = hClass;
    }

    private List<String> roomTags;
    private List<String> roomTypeTags;
    private Integer hClass;

    public List<String> getRoomTags() {
        return roomTags;
    }

    public void setRoomTags(List<String> roomTags) {
        this.roomTags = roomTags;
    }

    public List<String> getRoomTypeTags() {
        return roomTypeTags;
    }

    public void setRoomTypeTags(List<String> roomTypeTags) {
        this.roomTypeTags = roomTypeTags;
    }

    public Integer gethClass() {
        return hClass;
    }

    public void sethClass(Integer hClass) {
        this.hClass = hClass;
    }

    /*
    public static void HotelGenerator(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference hotels = mFirestore.collection("places");
        final Hotel toAdd = new Hotel("Hotel Pasqua", "Via Tullio 23", "Sorrento", "83242341", "Italia", "€€€",new ArrayList<String>(Arrays.asList("Vista mare","Free Wifi","Parcheggio","Piscina","Spa")),"2020","40.936752", "14.319622","info@bobby.com","0818326746","www.bobby.com",new ArrayList<String>(Arrays.asList("Smart TV","Aria condizionata","Caffé","Frigorifero","Cassaforte")),new ArrayList<String>(Arrays.asList("Suite","Vista piscina","Vista mare","Vista montagne","Singola","Matrimoniale","Deluxe")),5, Place.CATEGORY_HOTEL);
        hotels.add(toAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.update("roomTags", toAdd.roomTags);
                documentReference.update("roomTypeTags", toAdd.roomTypeTags);
                documentReference.update("hClass",toAdd.hClass);
                documentReference.update("category", "hotel");
                Log.d("HotelGenerator", "DocumentSnapshot written with ID: " + documentReference.getId());

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HotelGenerator", "Error adding document", e);
                    }
                });
    }
    */


}
