package it.gpgames.consigliaviaggi19.DAO.models.places;

import java.io.Serializable;
import java.util.List;

public class Place implements Serializable {

    public static final String CATEGORY_RESTAURANT="restaurant";
    public static final String CATEGORY_PLACE="place";
    public static final String CATEGORY_HOTEL="hotel";

    private String name;
    private String address;
    private String city;
    private String postal_code;
    private String state;
    private String priceTag;
    private String region;
    private String province;
    private List<String> tags;
    private String dbDocID;
    private List<String> pictures;
    private String addYear;

    private String email;
    private String telephone;
    private String website;
    private Integer nReviews;
    private Float avgReview;
    private int sumReviews;
    public String category;

    public int getSumReviews() {
        return sumReviews;
    }

    public void setSumReviews(int sumReviews) {
        this.sumReviews = sumReviews;
    }

    public Integer getnReviews() {
        return nReviews;
    }

    public void setnReviews(Integer nReviews) {
        this.nReviews = nReviews;
    }

    public Float getAvgReview() {
        return avgReview;
    }

    public void setAvgReview(Float avgReview) {
        this.avgReview = avgReview;
    }

    public String getCategory() { return category; }

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

    public String getDbDocID() {
        return dbDocID;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Place(String name, String address, String city, String postal_code, String state, String priceTag, List<String> tags, String addYear, String email, String telephone, String website, String category, String docID, Integer nReviews, Float avgReview, int sumReviews, List<String> pictures) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postal_code = postal_code;
        this.state = state;
        this.priceTag = priceTag;
        this.tags = tags;
        this.addYear = addYear;
        this.email = email;
        this.telephone = telephone;
        this.website = website;
        this.category = category;
        this.dbDocID=docID;
        this.nReviews=nReviews;
        this.avgReview=avgReview;
        this.sumReviews=sumReviews;
        this.pictures = pictures;
    }

    public Place(Place toObject, String docID)
    {
        this.dbDocID=docID;
        this.category=toObject.category;
        this.address=toObject.address;
        this.addYear=toObject.addYear;
        this.city=toObject.city;
        this.email=toObject.email;
        this.name=toObject.name;
        this.pictures=toObject.pictures;
        this.postal_code=toObject.postal_code;
        this.priceTag=toObject.priceTag;
        this.province=toObject.province;
        this.region=toObject.region;
        this.state=toObject.state;
        this.tags=toObject.tags;
        this.telephone=toObject.telephone;
        this.website=toObject.website;
        this.nReviews=toObject.nReviews;
        this.avgReview=toObject.avgReview;
    }

    public Place()
    {

    }


    /*
    public static void PlaceGenerator(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference places = mFirestore.collection("places");
        final Place toAdd = new Place("Hotel Bobby", "Via Martino 3", "Afragola", "80021", "Italia", "€",new ArrayList<String>(Arrays.asList("Vista mare","Free Wifi")),"2020","40.936752", "14.319622","info@bobby.com","0818526746","www.bobby.com","restaurant");
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
    */
}
