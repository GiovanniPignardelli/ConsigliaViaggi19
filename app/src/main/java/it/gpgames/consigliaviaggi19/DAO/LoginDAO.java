package it.gpgames.consigliaviaggi19.DAO;

import android.provider.ContactsContract;

import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.LoginFirebaseDAO;
/**Interfaccia che deve implementare ogni classe DAO che intenda operare sul Login*/
public interface LoginDAO {
    public void isAuthenticated(DatabaseCallback callback, int callbackCode);
    public void authentication(String username, String password, DatabaseCallback callback, int callbackCode);
    public void signOut(DatabaseCallback callback, int callbackCode);
    public void resetPasswordRequest(DatabaseCallback callback ,int callbackCode);
    public void isTokenExpired(DatabaseCallback callback, int callbackCode);
    public void checkHandshakeRequests(String userID, DatabaseCallback callback, int callbackCode);
    public void sendHandshakeResponse(HandshakeResponse hreq, DatabaseCallback callback, int callbackCode);
}
