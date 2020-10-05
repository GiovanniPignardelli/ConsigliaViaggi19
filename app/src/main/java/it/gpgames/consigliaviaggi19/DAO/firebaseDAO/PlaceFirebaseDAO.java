package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.DatabaseUtilities;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;


public class PlaceFirebaseDAO implements PlaceDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    public void getPlaceByTags(final String searchString, String category, Integer minRating, String price, HashMap<Integer, ArrayList<String>> tags, Integer order, Integer direction, DatabaseCallback callback, int callbackCode)
    {
        if(searchString!=null)
            new FirebaseQueryExecutor(DatabaseUtilities.parseString(searchString, " "),category,minRating,price,tags,order,direction,callback,callbackCode).executeQuery();
        else
            new FirebaseQueryExecutor(null,category,minRating,price,tags,order,direction,callback,callbackCode).executeQuery();

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

    public static Place generatePlace(DocumentSnapshot result) {
        Place toObject=result.toObject(Place.class);
        HashMap<String, Boolean> map=(HashMap<String, Boolean>)result.get("generalTags");
        ArrayList<String> tags=generateTagList(map);
        toObject.setTags(tags);

        if(toObject.getCategory().equals(Place.CATEGORY_RESTAURANT))
        {
            return new Restaurant(toObject, generateTagList((HashMap<String,Boolean>)result.get("cuisineTags")),generateTagList((HashMap<String,Boolean>)result.get("serviceTags")), result.getId());
        }

        else if(toObject.getCategory().equals(Place.CATEGORY_HOTEL))
        {
            return new Hotel(toObject, String.valueOf(result.get("hClass")), generateTagList((HashMap<String,Boolean>)result.get("roomTags")), generateTagList((HashMap<String,Boolean>)result.get("roomTypeTags")), result.getId());
        }
        else
            return new Place(toObject, result.getId());
    }

    public static ArrayList<String> generateTagList(HashMap<String, Boolean> map)
    {
        ArrayList<String> tags=new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            String key = (String) pair.getKey();
            Boolean value = (Boolean) pair.getValue();
            if(value.equals(true))
                tags.add(key);
        }
        return tags;
    }

}
