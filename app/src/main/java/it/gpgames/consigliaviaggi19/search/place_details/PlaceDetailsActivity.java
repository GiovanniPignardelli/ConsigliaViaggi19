package it.gpgames.consigliaviaggi19.search.place_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.ReviewDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.WriteReviewActivity;
import it.gpgames.consigliaviaggi19.search.place_details.slider.PlaceSliderAdapter;
import it.gpgames.consigliaviaggi19.userpanel.UserPanelActivity;

/** Activity che si occupa di mostrare i dettagli di una struttura selezionata da ResultsActivity.*/
public class PlaceDetailsActivity extends AppCompatActivity implements ReviewsAdapter.RecyclerGetter, DatabaseCallback {

    /**Place da mostrare. Viene passato dall'activity chiamante.*/
    private Place toShow;

    //vari adapter che adattano i contenuti al layout
    private PlaceInformationAdapter placeInformationAdapter;
    private PlaceSliderAdapter sliderAdapter;
    private ReviewsAdapter reviewsAdapter;

    private TextView title, ratingInfo;
    private ImageView back, orderArrow;
    private SliderView slider;
    private RecyclerView information, reviews;
    private Button bWriteReview;
    private Switch switcher;
    private RatingBar ratingBar;

    //interi statici che indicano l'attuale ordine delle rewiews (per ordine si intende criterio di ordinamento e direzione)
    public final static int ORDER_ASC=1;
    public final static int ORDER_DESC=0;
    public final static int SORT_DATE=1;
    public final static int SORT_STAR=0;

    //intero che indica la direzione (asc o desc).
    private int actualOrder;
    //intero che indica il criterio (data o stelle)
    private int actualSort;

    private boolean alreadyWritten=false;

    public static final int REVIEW_BACK_CODE=1;

    private ReviewDAO reviewDao = DAOFactory.getDAOInstance().getReviewDAO();
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        actualOrder=ORDER_DESC;
        actualSort=SORT_STAR;

        slider=findViewById(R.id.PlaceImagesSlider);
        title=findViewById(R.id.title);
        information=findViewById(R.id.recyclerInfoView);
        back=findViewById(R.id.back);
        bWriteReview=findViewById(R.id.writeReviewButton);
        reviews=findViewById(R.id.recyclerReviews);
        ratingInfo=findViewById(R.id.infoRating);
        ratingBar=findViewById(R.id.ratingBar);
        switcher=findViewById(R.id.orderswitcher);
        orderArrow=findViewById(R.id.orderArrow);

        toShow=(Place)getIntent().getSerializableExtra("toShow");
        if(toShow!=null)
            init();
        else
        {
            Toast.makeText(getApplicationContext(),"Errore nella visualizzazione della struttura. Riprovare.", Toast.LENGTH_LONG);
            finish();
        }

    }

    private void initListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!alreadyWritten)
                {
                    Intent i=new Intent(PlaceDetailsActivity.this, WriteReviewActivity.class);
                    i.putExtra("toShow", toShow);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),"Hai già scritto una recensione per questa struttura.",Toast.LENGTH_LONG).show();
            }
        });

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    actualSort=SORT_DATE;
                }
                else
                {
                    actualSort=SORT_STAR;
                }
                refreshReviews();
            }
        });

        orderArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualOrder==ORDER_ASC)
                {
                    actualOrder=ORDER_DESC;
                    v.setRotation(180);
                }
                else if(actualOrder==ORDER_DESC)
                {
                    actualOrder=ORDER_ASC;
                    v.setRotation(0);
                }
                refreshReviews();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    private void init()
    {
        initListeners();
        initPlaceSlider();
        refreshReviews();
        checkIfReviewExists();


        title.setText(toShow.getName());
        List<Pair<Integer,String>> info=new ArrayList<>();
        info.add(new Pair<Integer, String>(PlaceInformationAdapter.POINTER_ID, toShow.getAddress()+", "+toShow.getCity()+", "+toShow.getPostal_code()+", "+toShow.getState()));
        info.add(new Pair<Integer, String>(PlaceInformationAdapter.TAGS_ID, makeString(toShow.getTags())));
        if(toShow.getTelephone()!=null && !(toShow.getTelephone().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.PHONE_ID, toShow.getTelephone()));
        if(toShow.getEmail()!=null && !(toShow.getEmail().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.EMAIL_ID, toShow.getEmail()));
        if(toShow.getWebsite()!=null && !(toShow.getWebsite().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.WEB_ID,toShow.getWebsite()));


        if(toShow.getCategory().equals(Place.CATEGORY_RESTAURANT))
        {
            Restaurant toShowR=(Restaurant)toShow;
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.FOOD_ID, makeString(toShowR.getCuisineTags())));
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.SERVICE_ID, makeString(toShowR.getServiceTags())));
        }
        else if(toShow.getCategory().equals(Place.CATEGORY_HOTEL))
        {
            Hotel toShowH=(Hotel)toShow;
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.ROOM_ID, makeString(toShowH.getRoomTags())));
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.ROOMTYPE_ID, makeString(toShowH.getRoomTypeTags())));
        }

        if(toShow.getPriceTag()!=null && !(toShow.getPriceTag().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.EURO_ID, toShow.getPriceTag()));
        if(toShow.getAddYear()!=null && !(toShow.getAddYear().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.CLOCK_ID, toShow.getAddYear()));

        placeInformationAdapter = new PlaceInformationAdapter(PlaceDetailsActivity.this, info);
        information.setAdapter(placeInformationAdapter);
        information.setLayoutManager(new LinearLayoutManager(PlaceDetailsActivity.this, RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                RecyclerView.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);

        ratingInfo.setText(String.format("%.2f", toShow.getAvgReview())+"\n("+toShow.getnReviews()+")");

        ratingBar.setRating(toShow.getAvgReview());
    }

    /** Il metodo controlla che l'utente corrente non abbia già scritto una recensione per la struttura visualizzata.*/
    private void checkIfReviewExists() {
        reviewDao.getReviewsByPlaceIDAndUserID(toShow.getDbDocID(),User.getLocalInstance().getUserID(),this, CALLBACK_IF_ABLE_TO_REVIEW);
    }

    /**Metodo che aggiorna le review relative alla struttura. Esegue un controllo sui valori attuali degli attributi actualOrder e actualSort per effettuare la query*/
    private void refreshReviews() {
        reviewDao.getReviewsByPlaceID(toShow.getDbDocID(),PlaceDetailsActivity.this, actualSort, actualOrder, CALLBACK_REFRESH_REVIEWS);
    }

    /**Inizializza lo slider delle immagini della struttura recuperandole dal database*/
    private void initPlaceSlider() {
        final List<String> urls=new ArrayList<String>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("Places/Pictures/" + toShow.getDbDocID()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> list=listResult.getItems();
                final Integer itemsToGet = list.size();
                for(StorageReference ref: list)
                {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urls.add(uri.toString());
                            if(urls.size() == itemsToGet){
                                startSliderAdapter(urls);
                            }
                        }
                    });
                }
            }
        });
    }

    /** Avvia lo slider per le immagini della struttura visualizzata.*/
    private void startSliderAdapter(List<String> urls) {
        sliderAdapter=new PlaceSliderAdapter(getApplicationContext(),urls, toShow.getDbDocID(), PlaceDetailsActivity.this);
        slider.setSliderAdapter(sliderAdapter);
        slider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        slider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        slider.setIndicatorSelectedColor(Color.WHITE);
        slider.setIndicatorUnselectedColor(Color.GRAY);
        slider.setScrollTimeInSec(4);
        slider.startAutoCycle();
    }

    /**Riceve in input i tags e li restituisce in un'unica stringa divisa da ", ".*/
    private String makeString(List<String> tags) {
        String result=new String();
        for(String s: tags)
        {
            result=result.concat(s+", ");
        }

        return result.substring(0,result.length()-2);
    }

    public RecyclerView getReviewsRecyclerView()
    {
        return reviews;
    }

    @Override
    public void show(String id, int flag) {
        switch (flag)
        {
            case ReviewsAdapter.FLAG_USER:
                userDao.getUserByID(id,this,null,0);
                break;
        }
    }

    @Override
    public void callback(int callbackCode) {

    }

    @Override
    public void callback(Place place, int callbackCode) {

    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    @Override
    public void callback(User user, int callbackCode) {
        Intent i=new Intent(this, UserPanelActivity.class);
        i.putExtra("userToShow",user);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    public static final int CALLBACK_REFRESH_REVIEWS = 0;
    public static final int CALLBACK_IF_ABLE_TO_REVIEW = 1;

    @Override
    public void callback(List<Review> reviewsList, int callbackCode) {
        switch(callbackCode){
            case CALLBACK_REFRESH_REVIEWS:
                reviewsAdapter=new ReviewsAdapter(PlaceDetailsActivity.this,reviewsList, PlaceDetailsActivity.this, ReviewsAdapter.FLAG_USER);
                reviews.setAdapter(reviewsAdapter);
                reviews.setLayoutManager(new LinearLayoutManager(PlaceDetailsActivity.this, RecyclerView.VERTICAL, false));
                break;
            case CALLBACK_IF_ABLE_TO_REVIEW:
                if(!reviewsList.isEmpty())
                    alreadyWritten=true;
                else{
                    alreadyWritten=false;
                    bWriteReview.setEnabled(true);
                }
                break;
        }

    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {

    }

    @Override
    public void callback(String message, int callbackCode) {

    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
