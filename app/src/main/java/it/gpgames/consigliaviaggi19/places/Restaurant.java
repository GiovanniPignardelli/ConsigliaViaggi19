package it.gpgames.consigliaviaggi19.places;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Restaurant extends Place {

    public Restaurant(String name, String address, String city, String postal_code, String state, String priceTag, List<String> tags, String addYear, String latitude, String longitude, String email, String telephone, String website, List<String> serviceTags, List<String> cuisineTags) {
        super(name, address, city, postal_code, state, priceTag, tags, addYear, latitude, longitude, email, telephone, website);
        this.serviceTags = serviceTags;
        this.cuisineTags = cuisineTags;
    }

    public static void RestaurantGenerator(){
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            final CollectionReference restaurants = mFirestore.collection("restaurants");
            final Restaurant toAdd = new Restaurant("Hotel Bobby", "Via Martino 3", "Afragola", "80021", "Italia", "€",new ArrayList<String>(Arrays.asList("Vista mare","Free Wifi")),"2020","40.936752", "14.319622","info@bobby.com","0818526746","www.bobby.com",new ArrayList<String>(Arrays.asList("Asporto","Al tavolo")),new ArrayList<String>(Arrays.asList("Italiana","Bar")));
            restaurants.add(toAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.update("serviceTags", toAdd.serviceTags);
                    documentReference.update("cuisineTags", toAdd.cuisineTags);
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

    private List<String> serviceTags;
    private List<String> cuisineTags;

}
