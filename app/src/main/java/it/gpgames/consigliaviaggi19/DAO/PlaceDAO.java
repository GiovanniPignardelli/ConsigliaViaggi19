package it.gpgames.consigliaviaggi19.DAO;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public interface PlaceDAO {
    public void getPlaceByTags(final String searchString,String category, Integer minRating, String price, HashMap<Integer, ArrayList<String>> tags,Integer order, Integer direction, DatabaseCallback callback, int callbackCode);

    public void getPlaceByID(final String id, DatabaseCallback callback, int callbackCode);

    public void getPlaceByID(final String dataID, final DatabaseCallback callback, final ReviewsAdapter.ReviewViewHolder holder, int callbackCode);

    public void getPlaceByLocation(String me, String me1, DatabaseCallback callback, int callbackCode);

    public void getPlaceByLocation(LatLng loc, float radius, DatabaseCallback callback, int callbackCode);
}
