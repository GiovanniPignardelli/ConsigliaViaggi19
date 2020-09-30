package it.gpgames.consigliaviaggi19.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.search.place_map.MapExploreActivity;

/** Activity che si occupa di visualizzare i risultati di una query.
 * A questa activity viene anche passata la stringa di ricerca tramite intent*/
public class ResultsActivity extends AppCompatActivity implements DatabaseCallback {

    private static android.content.Context context;
    private TextView titleText;
    private ImageView bBack;
    private Button bMapExplore;
    private RecyclerView resultQueries;
    private QueryResultsAdapter adapter;
    private List<Place> lastQuery;
    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();


    /** Il metodo si occupa anche di generare il primo QueryExecutor.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        bMapExplore = findViewById(R.id.mapExplore);
        titleText= findViewById(R.id.titleResultText);
        resultQueries = findViewById(R.id.resultQueries);
        initListeners();
        context=this.getApplicationContext();

        List<Place> results = (List<Place>) getIntent().getSerializableExtra("query");
        setUpRecycleView(null, results); // weakList not implemented yet.
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
    public void setUpRecycleView(List<Place> weakList, List<Place> topList) {
        adapter = new QueryResultsAdapter(ResultsActivity.this, topList, this);
        lastQuery = topList;
        resultQueries.setAdapter(adapter);
        resultQueries.setLayoutManager(new LinearLayoutManager(ResultsActivity.this, RecyclerView.VERTICAL, false));
        bMapExplore.setEnabled(true);
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
    }

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

    }

    @Override
    public void callback(int callbackCode) {
        placeDao.getPlaceByTags(MainActivity.getLastSearchString(),this, 0);
    }

    @Override
    public void callback(Place place, int callbackCode) {

    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    @Override
    public void callback(User user, int callbackCode) {

    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {

    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {
        setUpRecycleView(null,topList);
    }

    @Override
    public void callback(String message, int callbackCode) {

    }

    @Override
    public void manageError(Exception e, int callbackCode) {

    }
}