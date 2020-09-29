package it.gpgames.consigliaviaggi19.DAO.places;


public class Review {

    String placeId;
    String userId;
    String text;
    String year;
    String month;
    String day;
    Integer rating;


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Review(String placeId, String userId, String text, String year, String month, String day, Integer rating) {
        this.placeId = placeId;
        this.userId = userId;
        this.text = text;
        this.year = year;
        this.month = month;
        this.day = day;
        this.rating = rating;
    }


    public Review()
    {

    }

    public String getDate()
    {
        return day+"/"+month+"/"+year;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

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
