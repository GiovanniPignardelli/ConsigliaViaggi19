package it.gpgames.consigliaviaggi19.places;


import java.time.Year;
import java.util.Date;
import java.util.List;

public class Review {

    String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Review(String placeId, String userId, String text, String date, Integer rating) {
        this.placeId = placeId;
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.rating = rating;
    }

    String userId;
    String text;
    String date;
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
