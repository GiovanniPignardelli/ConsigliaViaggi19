package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.GeolocationFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.LoginFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.PlaceFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.RegisterFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.ReviewFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.UserFirebaseDAO;

/**Classe singoletto che serve come filtro tra l'app e il database. Infatti tramite questa classe, e le varie interfacce ...DAO, il grado
 * di modularità dell'app è alto. Se si vuole cambiare database di appoggio, è sufficiente cambiare la stringa db e implementare
 * i vari metodi definiti dalle interfacce, senza che il resto dell'app abbia bisongo di ristrutturazioni.
 * La classe di occupa di istanziare [Model][DB_UTILIZZATO]DAO in base alla stringa che rappresenta il db e il tipo di DAO richiesto.*/
public class DAOFactory {
    private static DAOFactory dao;
    private static String db = "firebase";

    private DAOFactory()
    {
        //private constructor for singleton class
    }

    public static synchronized DAOFactory getDAOInstance() {
        if (dao == null)
            dao = new DAOFactory();
        return dao;
    }

    public ReviewDAO getReviewDAO() {
        if (db.equals("firebase"))
            return new ReviewFirebaseDAO();
        return null;
    }

    public UserDAO getUserDAO(){
        if(db.equals("firebase"))
            return new UserFirebaseDAO();
        return null;
    }

    public PlaceDAO getPlaceDAO(){
        if(db.equals("firebase"))
            return new PlaceFirebaseDAO();
        return null;
    }

    public LoginDAO getLoginDAO(){
        if(db.equals("firebase"))
            return new LoginFirebaseDAO();
        return null;
    }

    public RegisterDAO getRegisterDAO(){
        if(db.equals("firebase"))
            return new RegisterFirebaseDAO();
        return null;
    }

    public GeolocationDAO getGeolocationDAO() {
        if (db.equals("firebase"))
            return new GeolocationFirebaseDAO();
        return null;
    }
}
