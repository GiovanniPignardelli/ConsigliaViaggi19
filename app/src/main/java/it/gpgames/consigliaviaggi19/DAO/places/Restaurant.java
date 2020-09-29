package it.gpgames.consigliaviaggi19.DAO.places;

import java.io.Serializable;
import java.util.ArrayList;

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
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear(), toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID, toObject.getnReviews(), toObject.getAvgReview());

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
