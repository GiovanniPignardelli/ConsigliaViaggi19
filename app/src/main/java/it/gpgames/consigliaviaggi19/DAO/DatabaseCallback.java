package it.gpgames.consigliaviaggi19.DAO;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public interface DatabaseCallback
{
    void callback(int callbackCode);
    void callback(Place place, int callbackCode);
    void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
    void callback(User user, int callbackCode);
    void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
    void callback(List<Review> reviews, int callbackCode);
    void callback(List<Place> weakList, List<Place> topList, int callbackCode);
    void callback(String message, int callbackCode);
    void manageError(Exception e, int callbackCode);
}