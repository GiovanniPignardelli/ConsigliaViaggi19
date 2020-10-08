package it.gpgames.consigliaviaggi19.DAO.models.places;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**Model per gli Hotel.
 * Estende il model Place, al quale aggiunge informazioni quali roomTags,roomTypeTags e hClass.
 * @see Place*/
public class Hotel extends Place implements Serializable {

    private List<String> roomTags;
    private List<String> roomTypeTags;
    private String hClass;

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

    public String gethClass() {
        return hClass;
    }

    public void sethClass(String hClass) {
        this.hClass = hClass;
    }

    /**Gli hotel vengono generati partendo con la generazione di un Place. Il place generato viene passato al super_costruttore che inizializza tutti i suoi campi a quelli del place generato.*/
    public Hotel(Place toObject,String hClass, ArrayList<String> roomTags, ArrayList<String> roomTypeTags, String docID) {
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear()
                , toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID,toObject.getnReviews(), toObject.getAvgReview(), toObject.getSumReviews(), toObject.getPictures());
        this.hClass=hClass;
        this.roomTags=roomTags;
        this.roomTypeTags=roomTypeTags;
    }

}
