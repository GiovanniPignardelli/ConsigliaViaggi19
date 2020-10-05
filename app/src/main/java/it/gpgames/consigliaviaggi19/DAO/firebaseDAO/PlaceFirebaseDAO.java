package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderAdapter;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItem;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;


public class PlaceFirebaseDAO implements PlaceDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    GeoFire geoFire = null;

    public void getPlaceByTags(final String searchString, String category, Integer minRating, String price, HashMap<Integer, ArrayList<String>> tags, Integer order, Integer direction, DatabaseCallback callback, int callbackCode)
    {
        if(searchString!=null)
            new FirebaseQueryExecutor(DatabaseUtilities.parseString(searchString, " ",true),category,minRating,price,tags,order,direction,callback,callbackCode).executeQuery();
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

    @Override
    public void getPlaceByLocation(String locType, String locName, final DatabaseCallback callback, final int callbackCode) {
        Query query=dbRef.collection("places");

        if(locType.equals(HomeSliderItem.STRING_CITY))
            query=query.whereEqualTo("city",locName);
        else if(locType.equals(HomeSliderItem.STRING_STATE))
            query=query.whereEqualTo("state",locName);

        query=query.orderBy("avgReview",Query.Direction.DESCENDING).orderBy("name",Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    ArrayList<Place> placeList=new ArrayList<>();

                    for(DocumentSnapshot doc:task.getResult())
                    {
                        placeList.add(generatePlace(doc));
                    }

                    callback.callback(null,placeList,callbackCode);
                }
                else
                    callback.manageError(task.getException(),callbackCode);
            }
        });
    }


    @Override
    public void getPlaceByLocation(LatLng loc, float radius, final DatabaseCallback callback, final int callbackCode) {
        if(geoFire == null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
            geoFire = new GeoFire(ref);
        }
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(loc.latitude, loc.longitude), radius);
        final List<String> inRadiusPlaces = new ArrayList<>();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                inRadiusPlaces.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                final Iterator i = inRadiusPlaces.iterator();
                while(i.hasNext()){
                    dbRef.collection("places").document((String) i.next()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                Place newPlace = generatePlace(task.getResult());
                                if(newPlace != null) callback.callback(newPlace,callbackCode);
                                return;
                            }
                        }
                    });
                }
                callback.manageError(new Exception("Nessuna struttura trovata nei dintorni."), callbackCode);

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
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
