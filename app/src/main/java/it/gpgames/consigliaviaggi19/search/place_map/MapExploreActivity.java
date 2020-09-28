package it.gpgames.consigliaviaggi19.search.place_map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.userpanel.UserData;

public class MapExploreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private class ResultMarkerAdapter
    {
        private List<Place> results = resultsToShow;

        public void startAdapter(){
            List<Marker> resultMarkers = new ArrayList<>();
            for(Place p : results){
                resultMarkers.add(createResultMarker(p));
            }
        }

        public Marker createResultMarker(Place p){
            LatLng currentLatLng = new LatLng(Double.parseDouble(p.getLatitude()),Double.parseDouble(p.getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title(p.getName()).snippet("Media recens.: "+p.getAvgReview().toString());
            switch(p.getCategory()){
                case "hotel": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hotelmarker));
                    break;
                case "place": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.placemarker));
                    break;
                case "restaurant": markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.foodmarker));
                    break;
            }
            Marker placeMarker = mMap.addMarker(markerOptions);
            placeMarker.setTag(p);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(MapExploreActivity.this, PlaceDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("toShow", (Place) marker.getTag());
                    MapExploreActivity.this.startActivity(intent);
                }
            });
                    placeMarker.showInfoWindow();
            return placeMarker;
        }
    }

    private ActivityCompat callingActivity;
    private FusedLocationProviderClient client;
    private ImageView bBack;
    private SupportMapFragment fMap;
    private GoogleMap mMap;
    private List<Place> resultsToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_explore);
        bBack = findViewById(R.id.back3);

        resultsToShow = (List<Place>) getIntent().getBundleExtra("query").getSerializable("query");

        FragmentManager fm = getSupportFragmentManager();
        fMap = (SupportMapFragment) fm.findFragmentById(R.id.map);

        initListeners();
        checkLocationPermissions();
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /** Controlla la permission ACCESS_FINE_LOCATION.
     * - PERMISSION_GRANTED: la mappa è inizializzata nel SupportMapFragment e punta alla currentLocation dell'user.
     * - PERMISSION_DENIED: requestPermissions() è chiamato, riducendoci al caso precedente.
     */
    private void checkLocationPermissions(){
        if(ActivityCompat.checkSelfPermission(MapExploreActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            client = LocationServices.getFusedLocationProviderClient(this);
            fMap.getMapAsync(this);
            Task<Location> task = client.getLastLocation();
            initMapView(task);
        }
        else{
           ActivityCompat.requestPermissions(MapExploreActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

    }

    /** Questo metodo viene chiamato dopo il checkLocationPermissions() per inizializzare la mappa e il relativo currentLocation.*/
    private void initMapView(Task task){

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng).title("Sei qui!").icon(BitmapDescriptorFactory.fromResource(R.drawable.currentpos)).snippet(UserData.getLocalInstance().getDisplayName());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,10));
                    Marker userMarker = mMap.addMarker(markerOptions);

                }
                setupResultMarkerAdapter();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                checkLocationPermissions();
            }
            setupResultMarkerAdapter();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setupResultMarkerAdapter(){
        ResultMarkerAdapter ra = new ResultMarkerAdapter();
        ra.startAdapter();
    }
}