package it.gpgames.consigliaviaggi19.map_search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;

public class MapExploreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView bBack;
    private MapView vMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_explore);
        bBack = findViewById(R.id.back3);
        vMapView = findViewById(R.id.mapView);
        initListeners();
        initMapView();
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain = new Intent(MapExploreActivity.this, MainActivity.class);
                startActivity(backToMain);
            }
        });
    }

    private void initMapView(){
        vMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}