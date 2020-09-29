package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.PlaceFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.ReviewFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.UserFirebaseDAO;

public class DAOFactory {
    private static DAOFactory dao;
    String db = "firebase";

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
}
