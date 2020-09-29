package it.gpgames.consigliaviaggi19.DAO;

public class DatabaseUtilities {

    /** Splitta una stringa con un dato pivot. Restituisce un array delle dimensioni del numero di parole della stringa.*/
    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
    }

    public static class DataNotFoundException extends Exception{
        public DataNotFoundException(){
            super("Unsuccessful data retrieving from Firestore with specified id.");
        }
    }

}
