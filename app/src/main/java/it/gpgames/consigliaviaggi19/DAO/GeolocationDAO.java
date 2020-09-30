package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.models.places.Place;

public interface GeolocationDAO {
    public void getGeolocationByPlace(Place p, DatabaseCallback callback, int callbackCode);
}
