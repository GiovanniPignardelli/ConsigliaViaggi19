package it.gpgames.consigliaviaggi19.DAO;

import android.provider.ContactsContract;

public interface LoginDAO {
    public void isAuthenticated(DatabaseCallback callback, int callbackCode);
    public void authentication(String username, String password, DatabaseCallback callback, int callbackCode);
    public void signOut(DatabaseCallback callback, int callbackCode);
    public void resetPasswordRequest(DatabaseCallback callback ,int callbackCode);
    public void isTokenExpired(DatabaseCallback callback, int callbackCode);
}
