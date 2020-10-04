package it.gpgames.consigliaviaggi19.DAO;

public class DatabaseUtilities {

    /** Splitta una stringa con un dato pivot e setta tutti i caratteri a lowercase. Restituisce un array delle dimensioni del numero di parole della stringa.*/
    public static String[] parseString(String in, String pivot){
        String[] parsed=in.split(pivot);
        for(int i=0;i<parsed.length;i++)
        {
            parsed[i]=parsed[i].toLowerCase();
        }

        return parsed;
    }

    public static class DataNotFoundException extends Exception{
        public DataNotFoundException(){
            super("Unsuccessful data retrieving from Firestore with specified id.");
        }
    }

}
