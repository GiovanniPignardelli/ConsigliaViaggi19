package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.GeolocationFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.LoginFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.PlaceFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.RegisterFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.ReviewFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.UserFirebaseDAO;

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
