package it.gpgames.consigliaviaggi19.places;


import java.time.Year;
import java.util.Date;
import java.util.List;

public class Review {

    String placeId;
    String userId;
    String text;
    Date date;
    Integer rating;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


}
