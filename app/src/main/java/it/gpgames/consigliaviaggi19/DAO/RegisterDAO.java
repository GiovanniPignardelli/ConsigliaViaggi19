package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.models.users.User;

public interface RegisterDAO {
    void register(User user, String password, DatabaseCallback callback, int callbackCode);
}
