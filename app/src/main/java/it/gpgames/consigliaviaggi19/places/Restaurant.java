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

public class Restaurant extends Place implements Serializable {


    public Restaurant()
    {

    }

    /*
    public static void RestaurantGenerator(){
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            final CollectionReference restaurants = mFirestore.collection("places");
            //final Restaurant toAdd = new Restaurant("Hotel Bobby", "Via Martino 3", "Afragola", "80021", "Italia", "€",new ArrayList<String>(Arrays.asList("Vista mare","Free Wifi")),"2020","40.936752", "14.319622","info@bobby.com","0818526746","www.bobby.com",new ArrayList<String>(Arrays.asList("Asporto","Al tavolo")),new ArrayList<String>(Arrays.asList("Italiana","Bar")));
            restaurants.add(toAdd).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentReference.update("serviceTags", toAdd.serviceTags);
                    documentReference.update("cuisineTags", toAdd.cuisineTags);
                    documentReference.update("category","restaurant");
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
        */


    public void setServiceTags(ArrayList<String> serviceTags) {
        this.serviceTags = serviceTags;
    }

    public void setCuisineTags(ArrayList<String> cuisineTags) {
        this.cuisineTags = cuisineTags;
    }

    private ArrayList<String> serviceTags;
    private ArrayList<String> cuisineTags;

    public Restaurant(Place toObject, ArrayList<String> cuisineTags, ArrayList<String> serviceTags, String docID) {
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear(), toObject.getLatitude(), toObject.getLongitude(), toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID,0, (float) 0);

        this.cuisineTags=cuisineTags;
        this.serviceTags=serviceTags;
    }

    public ArrayList<String> getServiceTags() {
        return serviceTags;
    }

    public ArrayList<String> getCuisineTags() {
        return cuisineTags;
    }
}
