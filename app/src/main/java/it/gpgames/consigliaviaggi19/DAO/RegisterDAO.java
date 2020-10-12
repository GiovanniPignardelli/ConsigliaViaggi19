package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.models.users.User;

/**Interfaccia che deve implementare ogni classe DAO che intenda operare sulla registrazione di nuovi utenti*/
public interface RegisterDAO {
    void register(User user, String password, DatabaseCallback callback, int callbackCode);
    void checkIfUsernameIsUsed(String username, DatabaseCallback callback, int callbackcode);
}
