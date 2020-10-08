package it.gpgames.consigliaviaggi19.DAO;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.LoginFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

/**Interfaccia che deve implementare ogni classe che attende risultati da richieste effettuate ad un DAO.
 * I vari overloading del metodo callback definiscono i vari casi.
 * Infatti sono molteplici i tipi di richiesta che una classe può effettuare ad un DAO.*/
public interface DatabaseCallback
{
    int CALLBACK_DEFAULT_CODE = 0;

    void callback(Place place, MarkerOptions mOpt, int callbackCode);
    void callback(int callbackCode);
    void callback(HandshakeResponse hreq, int callbackCode);
    void callback(Place place, int callbackCode);
    void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
    void callback(User user, int callbackCode);
    void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
    void callback(List<Review> reviews, int callbackCode);
    void callback(List<Place> weakList, List<Place> topList, int callbackCode);
    void places_callback(List<Place> places, int callbackCode);
    void callback(String message, int callbackCode);
    void manageError(Exception e, int callbackCode);
}