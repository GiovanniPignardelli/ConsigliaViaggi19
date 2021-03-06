package it.gpgames.consigliaviaggi19.DAO.models.places;

import java.io.Serializable;
import java.util.ArrayList;
/**Model per i ristoranti.
 * Estonde il model Place al quale aggiunge informazioni quali serviceTags e cuisineTags
 *  @see Place*/
public class Restaurant extends Place implements Serializable {

    private ArrayList<String> serviceTags;
    private ArrayList<String> cuisineTags;

    /**I Restaurant vengono generati partendo con la generazione di un Place. Il place generato viene passato al super_costruttore che inizializza tutti i suoi campi a quelli del place generato.*/
    public Restaurant(Place toObject, ArrayList<String> cuisineTags, ArrayList<String> serviceTags, String docID) {
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear(), toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID, toObject.getnReviews(), toObject.getAvgReview(), toObject.getSumReviews(), toObject.getPictures());

        this.cuisineTags=cuisineTags;
        this.serviceTags=serviceTags;
    }

    public Restaurant()
    {

    }

    public void setServiceTags(ArrayList<String> serviceTags) {
        this.serviceTags = serviceTags;
    }

    public void setCuisineTags(ArrayList<String> cuisineTags) {
        this.cuisineTags = cuisineTags;
    }

    public ArrayList<String> getServiceTags() {
        return serviceTags;
    }

    public ArrayList<String> getCuisineTags() {
        return cuisineTags;
    }


}
