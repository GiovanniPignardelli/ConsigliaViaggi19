package it.gpgames.consigliaviaggi19.map_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;

public class MapExploreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView bBack;
    SupportMapFragment fMap;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_explore);
        bBack = findViewById(R.id.back3);
        FragmentManager fm = getSupportFragmentManager();
        fMap = (SupportMapFragment) fm.findFragmentById(R.id.map);
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
        fMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}