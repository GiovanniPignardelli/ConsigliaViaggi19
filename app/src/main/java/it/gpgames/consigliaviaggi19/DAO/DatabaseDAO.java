package it.gpgames.consigliaviaggi19.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.places.Place;
import it.gpgames.consigliaviaggi19.DAO.places.Restaurant;
import it.gpgames.consigliaviaggi19.DAO.users.UserData;

public class DatabaseDAO {
    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    public void showPlaceFromID(final String dataID, final DatabaseCallback callback)
    {
        dbRef.collection("places").document(dataID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Place toShow=generatePlace(task.getResult());
                    callback.show(toShow);
                }
                else
                {
                    String error="Operazione su db non riuscita";
                    Log.d("query",error);
                    callback.manageError(error);
                }

            }
        });
    }

    public void showUserFromID(final String userID, final DatabaseCallback callback)
    {
        dbRef.collection("userPool").whereEqualTo("UserId",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult().size()>0)
                {
                    if(task.getResult().size()>1)   Log.d("query", "E' stato trovato più di un utente con id: "+userID+". Verrà restituito il primo.");
                    UserData toShow=task.getResult().toObjects(UserData.class).get(0);
                    callback.show(toShow);
                }
                else
                {
                    String error;
                    if(task.isSuccessful()) error="Nessun dato trovato";
                    else error="Operazione su db non riuscita";
                    Log.d("query",error);
                    callback.manageError(new Exception(""));
                }
            }
        });
    }

    private Place generatePlace(DocumentSnapshot result) {
        Place toObject=result.toObject(Place.class);
        if(toObject.getCategory().equals(Place.CATEGORY_RESTAURANT))
            return new Restaurant(toObject, (ArrayList<String>) result.get("cuisineTags"),(ArrayList<String>)result.get("serviceTags"), result.getId());

        else if(toObject.getCategory().equals(Place.CATEGORY_HOTEL))
            return new Hotel(toObject,  result.get("hClass").toString(), (ArrayList<String>) result.get("roomTags"), (ArrayList<String>) result.get("roomTypeTags"),result.getId());

        else
            return new Place(toObject, result.getId());
    }

    public void showPlaceListFromSearchString(final String searchString, final DatabaseCallback callback)
    {
        QueryExecutor queryExecutor=new QueryExecutor(parseString(searchString," "),callback);
    }

    public interface DatabaseCallback
    {
        void show(Place place);
        void show(UserData user);
        void showResults(List<Place> weakList, List<Place> topList);
        void manageError(Exception e);
    }

    /** Splitta una stringa con un dato pivot. Restituisce un array delle dimensioni del numero di parole della stringa.*/
    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
    }

}
