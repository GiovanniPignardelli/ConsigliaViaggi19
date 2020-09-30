package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.content.Intent;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.GeolocationDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_map.MapExploreActivity;

public class GeolocationFirebaseDAO implements GeolocationDAO {

    private GeoFire geoFire = null;

    @Override
    public void getGeolocationByPlace(final Place p, final DatabaseCallback callback, final int callbackCode) {
        if(geoFire == null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
            geoFire = new GeoFire(ref);
        }
        geoFire.getLocation(p.getDbDocID(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                LatLng currentLatLng = new LatLng(location.latitude,location.longitude);
                MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title(p.getName()).snippet("Media recens.: "+p.getAvgReview().toString());
                switch(p.getCategory()){
                    case Place.CATEGORY_HOTEL: markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hotelmarker));
                        break;
                    case Place.CATEGORY_PLACE: markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.placemarker));
                        break;
                    case Place.CATEGORY_RESTAURANT: markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.foodmarker));
                        break;
                }
                callback.callback(p,markerOptions,callbackCode);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.manageError(databaseError.toException(),callbackCode);
            }
        });
    }
}
