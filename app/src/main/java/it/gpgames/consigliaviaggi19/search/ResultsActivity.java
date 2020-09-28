package it.gpgames.consigliaviaggi19.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_map.MapExploreActivity;

/** Activity che si occupa di visualizzare i risultati di una query.
 * A questa activity viene anche passata la stringa di ricerca tramite intent*/
public class ResultsActivity extends AppCompatActivity {

    private static android.content.Context context;
    private TextView titleText;
    private ImageView bBack;
    private Button bMapExplore;
    private RecyclerView resultQueries;
    private QueryResultsAdapter adapter;
    private List<Place> lastQuery;
    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();


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

        String querySearchString = MainActivity.getLastSearchString();
        titleText.setText("Ecco cosa abbiamo trovato per "+"\""+querySearchString+"\"");
        QueryExecutor executor=new QueryExecutor(parseString(querySearchString, " "),this);
        Log.d("query", "Executor creato. La query sta per partire.");
        executor.executeQuery();
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

/** Splitta una stringa con un dato pivot. Restituisce un array delle dimensioni del numero di parole della stringa.*/
    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
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


}