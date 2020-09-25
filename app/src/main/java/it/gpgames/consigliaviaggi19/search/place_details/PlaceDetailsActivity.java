package it.gpgames.consigliaviaggi19.search.place_details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderAdapter;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.places.Hotel;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;
import it.gpgames.consigliaviaggi19.places.Review;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.WriteReviewActivity;
import it.gpgames.consigliaviaggi19.search.place_details.slider.PlaceSliderAdapter;

/** Activity che si occupa di mostrare i dettagli di una struttura selezionata da ResultsActivity.*/
public class PlaceDetailsActivity extends AppCompatActivity {

    /** Holder del Place da mostrare. Viene passato come extra all'activity.*/
    private Place toShow;

    //vari adapter che adattano i contenuti al layout
    private PlaceInformationAdapter placeInformationAdapter;
    private PlaceSliderAdapter sliderAdapter;
    private ReviewsAdapter reviewsAdapter;

    private TextView title, avgReview;
    private ImageView back, orderArrow;
    private SliderView slider;
    private RecyclerView information, reviews;
    private Button bWriteReview;
    private Switch switcher;
    private RatingBar ratingBar;

    //interi statici che indicano l'attuale ordine delle rewiews (per ordine si intende criterio di ordinamento e direzione)
    private final static int ORDER_ASC=1;
    private final static int ORDER_DESC=0;
    private final static int SORT_DATE=1;
    private final static int SORT_STAR=0;

    //intero che indica la direzione (asc o desc).
    private int actualOrder;
    //intero che indica il criterio (data o stelle)
    private int actualSort;

    private boolean alreadyWritten=false;

    NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        actualOrder=ORDER_ASC;
        actualSort=SORT_DATE;

        slider=findViewById(R.id.PlaceImagesSlider);
        toShow=(Place)getIntent().getSerializableExtra("toShow");
        title=findViewById(R.id.title);
        information=findViewById(R.id.recyclerInfoView);
        back=findViewById(R.id.back);
        bWriteReview=findViewById(R.id.writeReviewButton);
        reviews=findViewById(R.id.recyclerReviews);
        avgReview=findViewById(R.id.nReview);
        ratingBar=findViewById(R.id.ratingBar);
        switcher=findViewById(R.id.orderswitcher);
        orderArrow=findViewById(R.id.orderArrow);

        init();
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
                    i.putExtra("name", toShow.getName());
                    Log.d("id",toShow.getDbDocID());
                    i.putExtra("id", toShow.getDbDocID());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
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
        refreshReviews();
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

        avgReview.setText(toShow.getAvgReview().toString());
        ratingBar.setRating(toShow.getAvgReview());
    }

    private void checkIfReviewExists() {
        FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("placeId",toShow.getDbDocID()).whereEqualTo("userId", FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().isEmpty())
                        alreadyWritten=false;
                    else
                        alreadyWritten=true;

                    bWriteReview.setEnabled(true);
                }
                else
                    Log.d("query", "Errore nella ricerca della corrispondenza utente-recensione-struttura");
            }
        });
    }

    /**Metodo che aggiorna le review relative alla struttura. Esegue un controllo sui valori attuali degli attributi actualOrder e actualSort per effettuare la query*/
    private void refreshReviews() {
        Query query=FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("placeId", toShow.getDbDocID());
        if(actualSort==SORT_DATE && actualOrder==ORDER_ASC)
            query=query.orderBy("date", Query.Direction.ASCENDING);
        else if(actualSort==SORT_DATE && actualOrder==ORDER_DESC)
            query=query.orderBy("date", Query.Direction.DESCENDING);
        else if(actualSort==SORT_STAR && actualOrder==ORDER_ASC)
            query=query.orderBy("rating", Query.Direction.ASCENDING);
        else
            query=query.orderBy("rating", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    reviewsAdapter=new ReviewsAdapter(PlaceDetailsActivity.this,task.getResult().toObjects(Review.class));
                    reviews.setAdapter(reviewsAdapter);
                    reviews.setLayoutManager(new LinearLayoutManager(PlaceDetailsActivity.this, RecyclerView.VERTICAL, false));
                }
                else
                {
                    Log.d("query", "Errore nel caricamento delle review: "+task.getException().getMessage());
                }
            }
        });
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

    /**Riceve in input un array di stringhe, e genera una stringa composta dalle stesse parole dell'array, ma splittate con ", "*/
    private String makeString(List<String> tags) {
        String result=new String();
        for(String s: tags)
        {
            result=result.concat(s+", ");
        }

        return result.substring(0,result.length()-2);
    }
}
