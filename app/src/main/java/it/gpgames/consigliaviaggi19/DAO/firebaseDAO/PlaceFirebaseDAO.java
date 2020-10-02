package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.DatabaseUtilities;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;


public class PlaceFirebaseDAO implements PlaceDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    public void getPlaceByTags(final String searchString, String category, Integer minRating, String price, HashMap<Integer, ArrayList<String>> tags, DatabaseCallback callback, int callbackCode)
    {
        new FirebaseQueryExecutor(DatabaseUtilities.parseString(searchString, " "),category,minRating,price,tags,callback,callbackCode).executeQuery();
    }

    public void getPlaceByID(final String dataID, final DatabaseCallback callback, int callbackCode){
        getPlaceByID(dataID,callback,null, callbackCode);
    }

    public void getPlaceByID(final String dataID, final DatabaseCallback callback, final ReviewsAdapter.ReviewViewHolder holder, final int callbackCode)
    {
        dbRef.collection("places").document(dataID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Place toShow=generatePlace(task.getResult());
                    if(holder == null) callback.callback(toShow, callbackCode);
                    else callback.callback(toShow, holder, callbackCode);
                }
                else callback.manageError(new DatabaseUtilities.DataNotFoundException(), callbackCode);
            }
        });
    }

    static Place generatePlace(DocumentSnapshot result) {
        Place toObject=result.toObject(Place.class);
        if(toObject.getCategory().equals(Place.CATEGORY_RESTAURANT))
            return new Restaurant(toObject, (ArrayList<String>) result.get("cuisineTags"),(ArrayList<String>)result.get("serviceTags"), result.getId());

        else if(toObject.getCategory().equals(Place.CATEGORY_HOTEL))
            return new Hotel(toObject,  result.get("hClass").toString(), (ArrayList<String>) result.get("roomTags"), (ArrayList<String>) result.get("roomTypeTags"),result.getId());

        else
            return new Place(toObject, result.getId());
    }

}
