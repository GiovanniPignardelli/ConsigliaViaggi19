package it.gpgames.consigliaviaggi19.DAO;

public interface LoginDAO {
    public void isAuthenticated(DatabaseCallback callback, int callbackCode);
    public void authentication(String username, String password, DatabaseCallback callback, int callbackCode);
}
