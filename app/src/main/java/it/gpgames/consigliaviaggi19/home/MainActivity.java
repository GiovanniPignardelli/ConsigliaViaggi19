package it.gpgames.consigliaviaggi19.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.PlaceFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.LoginActivity;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderAdapter;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItemsGetter;
import it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.search.place_map.MapExploreActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.userpanel.UserPanelActivity;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItem;

public class MainActivity extends AppCompatActivity implements DatabaseCallback {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    private SliderView sliderView;
    static List<HomeSliderItem> SliderItemToShow = new ArrayList<>();
    private ImageView bMapExplore;
    private ImageView bUserPanel;
    private SearchView svSearchPlaces;
    private static String lastSearchString;
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();
    private LoginDAO loginDao = DAOFactory.getDAOInstance().getLoginDAO();

    public static String getLastSearchString() {
        return lastSearchString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sliderView=findViewById(R.id.PlaceImagesSlider);
        bUserPanel=findViewById(R.id.user);
        svSearchPlaces = findViewById(R.id.searchView);
        bMapExplore = findViewById(R.id.mapSearch);
        loginDao.isTokenExpired(this,CALLBACK_DEFAULT_CODE);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    /**Inizializza tutte le componenti della activity_main.xml*/
    private void init()
    {
        initSlider();
        initListeners();
    }

    /**Inizializza i listener della activity_main.xml:
     * - OnClickListener(bUserPanel): button per aprire l'UserPanelActivity;*/
    private void initListeners()
    {
        //Inizializzazione listener dello user_button, per accede al pannello di controllo dell'utente
        bUserPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDao.getUserByID(User.getLocalInstance().getUserID(),MainActivity.this, 0);
            }
        });


        svSearchPlaces.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                lastSearchString=query;
                placeDao.getPlaceByTags(lastSearchString,null,null,null,null, OrderSelectorActivity.FLAG_BEST_MATCH,OrderSelectorActivity.FLAG_DESC,MainActivity.this, CALLBACK_DEFAULT_CODE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        bMapExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toExploreMap = new Intent(MainActivity.this, MapExploreActivity.class);
                startActivity(toExploreMap);
            }
        });

    }

    /** Inizializza lo slider di immagini nell'activity_main.xml. Nota: vedere HomeSliderItemsGetter e HomeSliderAdapter. */
    private void initSlider(){
        final DatabaseReference sliderImgsRef = dbRef.child("home").child("slider");
        sliderImgsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HandlerThread ht = new HandlerThread("SliderItemsGetterThread");
                ht.start();
                Handler sliderInitializator = new Handler(ht.getLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();
                        if(bundle.containsKey("SliderItemList")) {
                            SliderItemToShow =  bundle.getParcelableArrayList("SliderItemList");
                            Handler uiThread = new Handler(Looper.getMainLooper());
                            uiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    sliderView.setSliderAdapter(new HomeSliderAdapter(MainActivity.this,SliderItemToShow));
                                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                                    sliderView.setScrollTimeInSec(4);
                                    sliderView.startAutoCycle();
                                }
                            });
                        }
                    }
                };

                Runnable sliderRunnable= new HomeSliderItemsGetter(sliderInitializator,dataSnapshot);
                sliderInitializator.post(sliderRunnable);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    public static NetworkChangeReceiver getNetworkChangeReceiver() {
        return networkChangeReceiver;
    }


    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {

    }

    @Override
    public void callback(int callbackCode) {
        Intent accessNeeded = new Intent(MainActivity.this, LoginActivity.class);
        accessNeeded.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(accessNeeded);
    }

    @Override
    public void callback(Place place, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    private static final int CALLBACK_USER_PANEL = 0;
    private static final int CALLBACK_SET_LOCAL_USER = 1;

    @Override
    public void callback(User user, int callbackCode) {
        switch(callbackCode)
        {
            case CALLBACK_USER_PANEL:
                Intent showUser = new Intent(MainActivity.this,UserPanelActivity.class);
                showUser.putExtra("userToShow",(Parcelable) user);
                showUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(showUser);
                break;
            case CALLBACK_SET_LOCAL_USER:
                User.setLocalInstance(user);
                break;
        }

    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(String message, int callbackCode) {
        userDao.getUserByID(message,this,1);
    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {
        Intent iShowResults = new Intent(MainActivity.this, ResultsActivity.class);
        iShowResults.putExtra("query", (Serializable) topList);
        startActivity(iShowResults);
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
