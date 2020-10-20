package it.gpgames.consigliaviaggi19.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firestore.v1.StructuredQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.search.filters.FiltersSelectorActivity;
import it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.search.place_map.MapExploreActivity;

/** Activity che si occupa di visualizzare i risultati di una query.
 * A seconda del tipo di ricerca, i risultati possono essergli passati tramite Intent, oppure è lei a doverli richiedere al DOA.
 * Implementa per questo motivo l'interfaccia DatabaseCallback.
 * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback
 * Implementa le interfacce FilterCallback e OrderCallback perché instanza le rispettive activity e ne attende riscontri.
 * @see it.gpgames.consigliaviaggi19.search.filters.FiltersSelectorActivity.FilterCallback
 * @see it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity.OrderCallback*/
public class ResultsActivity extends AppCompatActivity implements DatabaseCallback, FiltersSelectorActivity.FilterCallback, OrderSelectorActivity.OrderCallback {

    private static android.content.Context context;
    private TextView titleText;
    private ImageView bBack, ImgNoRes;
    private Button bMapExplore, bFilter, bOrder;
    private RecyclerView resultQueries;
    private QueryResultsAdapter adapter;
    private List<Place> lastQuery = new ArrayList<>();
    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();

    private static ResultsActivity lastInstance;


    //filters
    private HashMap<Integer,ArrayList<String>> tags;
    private String category;
    private Integer minRating;
    private String priceTag;

    //order
    private Integer order=OrderSelectorActivity.FLAG_BEST_MATCH;
    private Integer direction=OrderSelectorActivity.FLAG_DESC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        bMapExplore = findViewById(R.id.mapExplore);
        titleText= findViewById(R.id.titleResultText);
        resultQueries = findViewById(R.id.resultQueries);
        bFilter = findViewById(R.id.filterBy);
        bOrder = findViewById(R.id.orderBy);
        ImgNoRes = findViewById(R.id.no_res_view);
        initListeners();
        context=getApplicationContext();
        lastInstance=this;

        titleText.setText(getIntent().getStringExtra("title"));

        if(getIntent().getBooleanExtra("removeButtons",true))
        {
            bFilter.setEnabled(false);
            bOrder.setEnabled(false);
        }

        switch(getIntent().getIntExtra("type",1)){
            case 0: placeDao.getPlaceByLocation(new LatLng(getIntent().getDoubleExtra("lat",0),getIntent().getDoubleExtra("long",0)),getIntent().getFloatExtra("distance",0),this,0);
                    setUpRecyclerView(null,null);
            break;
            case 1: List<Place> results = (List<Place>) getIntent().getSerializableExtra("query");
                setUpRecyclerView(null, results); // weakList not implemented yet.
            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    /** Quando si ottengono i risultati di una query, si passano le liste ottenute a questo metodo che si occupa
     * di organizzare i risultati nella scrollView dell'activity. Per farlo si serve dell'adapter QueryResultAdapter.*/
    public void setUpRecyclerView(List<Place> weakList, List<Place> topList) {
        if(topList!=null && topList.size()>0)
        {
            lastQuery = topList;
            adapter = new QueryResultsAdapter(ResultsActivity.this, lastQuery, this);
            resultQueries.setAdapter(adapter);
            resultQueries.setLayoutManager(new LinearLayoutManager(ResultsActivity.this, RecyclerView.VERTICAL, false));
            bMapExplore.setEnabled(true);
            ImgNoRes.setVisibility(View.INVISIBLE);
        }
        else
        {
            ImgNoRes.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Non ho trovato risultati.", Toast.LENGTH_LONG).show();
        }

    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bMapExplore.setEnabled(false);
        bMapExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMapExplore = new Intent(ResultsActivity.this, MapExploreActivity.class);
                Bundle queryBundle = new Bundle();
                queryBundle.putSerializable("query", (Serializable) lastQuery);
                toMapExplore.putExtra("query",queryBundle);
                startActivity(toMapExplore);
            }
        });

        bFilter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                tags=null;
                category=null;
                minRating=null;
                priceTag=null;
                Intent i=new Intent(ResultsActivity.this,FiltersSelectorActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order=OrderSelectorActivity.FLAG_BEST_MATCH;
                direction= OrderSelectorActivity.FLAG_DESC;
                Intent i=new Intent(ResultsActivity.this,OrderSelectorActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    public static FiltersSelectorActivity.FilterCallback getLastFilterInstance()
    {
        return lastInstance;
    }

    public static OrderSelectorActivity.OrderCallback getLastOrderInstance()
    {
        return lastInstance;
    }

    /**Metodo richiamato per avviare l'activity PlaceDetailsActivity
     * @param toShow Luogo del quale si devono visualizzare i dettagli.*/
    public static void showDetails(Place toShow)
    {
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.putExtra("toShow", toShow);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public RecyclerView getRecyclerView()
    {
        return resultQueries;
    }

    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override

    public void callback(int callbackCode) {
        placeDao.getPlaceByTags(MainActivity.getLastSearchString(),category,minRating,priceTag,tags,order,direction,this,0);
    }

    @Override
    public void callback(HandshakeResponse hreq, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, int callbackCode) {
        lastQuery.add(place);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {
        setUpRecyclerView(null,topList);
    }

    @Override
    public void places_callback(List<Place> places, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(String message, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this,"Non ho trovato risultati.",Toast.LENGTH_LONG).show();
        Log.d("query",e.getMessage());
    }

    @Override
    public void setCategory(int categoryFlag) {
        switch(categoryFlag)
        {
            case FiltersSelectorActivity.FLAG_ANY:
                this.category=null;
                break;
                case FiltersSelectorActivity.FLAG_HOTEL:
                    this.category=Place.CATEGORY_HOTEL;
                    break;
                    case FiltersSelectorActivity.FLAG_PLACE:
                        this.category=Place.CATEGORY_PLACE;
                        break;
            case FiltersSelectorActivity.FLAG_RESTAURANT:
                this.category=Place.CATEGORY_RESTAURANT;
                break;
        }
    }

    @Override
    public void setMinRating(int rating) {
        this.minRating=rating;
    }

    @Override
    public void setPriceString(String price) {
        this.priceTag=price;
    }

    @Override
    public void setTags(HashMap<Integer, ArrayList<String>> tags) {
        this.tags=tags;
    }

    @Override
    public void refreshFilter() {
        placeDao.getPlaceByTags(MainActivity.getLastSearchString(),category,minRating,priceTag,tags,order,direction,this,0);
    }

    @Override
    public void setOrder(int orderFlag) {
        this.order=orderFlag;
    }

    @Override
    public void setDirection(int directionFlag) {
        this.direction=directionFlag;
    }

    @Override
    public void refreshOrder() {
        placeDao.getPlaceByTags(MainActivity.getLastSearchString(),category,minRating,priceTag,tags,order,direction,this,0);
    }
}