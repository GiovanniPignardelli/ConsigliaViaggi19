package it.gpgames.consigliaviaggi19.DAO.models.places;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public Hotel(Place toObject,String hClass, ArrayList<String> roomTags, ArrayList<String> roomTypeTags, String docID) {
        super(toObject.getName(),toObject.getAddress(),toObject.getCity(),toObject.getPostal_code(),toObject.getState(),toObject.getPriceTag(),toObject.getTags(),toObject.getAddYear()
                , toObject.getEmail(), toObject.getTelephone(), toObject.getWebsite(), toObject.getCategory(), docID,toObject.getnReviews(), toObject.getAvgReview(), toObject.getSumReviews());
        this.hClass=hClass;
        this.roomTags=roomTags;
        this.roomTypeTags=roomTypeTags;
    }

}
