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

    public void setServiceTags(ArrayList<String> serviceTags) {
        this.serviceTags = serviceTags;
    }

    public void setCuisineTags(ArrayList<String> cuisineTags) {
        this.cuisineTags = cuisineTags;
    }

    private ArrayList<String> serviceTags;
    private ArrayList<String> cuisineTags;

    public Restaurant(Place toObject, ArrayList<String> cuisineTags, ArrayList<String> serviceTags, String docID) {
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear(), toObject.getLatitude(), toObject.getLongitude(), toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID, toObject.getnReviews(), toObject.getAvgReview());

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
