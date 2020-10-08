package it.gpgames.consigliaviaggi19.DAO;

/**Classe che contiene funzionalità per il Database*/
public class DatabaseUtilities {

    /** Splitta una stringa con un dato pivot e setta tutti i caratteri a lowercase. Restituisce un array delle dimensioni del numero di parole della stringa.*/
    public static String[] parseString(String in, String pivot,boolean toLowecase){
        String[] parsed=in.split(pivot);

        if(toLowecase)
        {
            for(int i=0;i<parsed.length;i++)
            {
                parsed[i]=parsed[i].toLowerCase();
            }
        }


        return parsed;
    }

    public static class DataNotFoundException extends Exception{
        public DataNotFoundException(){
            super("Unsuccessful data retrieving from Firestore with specified id.");
        }
    }

}
