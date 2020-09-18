package it.gpgames.consigliaviaggi19.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.Context;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;

/** Activity che si occupa di visualizzare i risultati di una query.
 * A questa activity viene anche passata la stringa di ricerca tramite intent*/
public class ResultsActivity extends AppCompatActivity {

    private static android.content.Context context;
    private TextView titleText;
    private ImageView bBack;
    private RecyclerView resultQueries;
    private QueryResultsAdapter adapter;
    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    /** Il metodo si occupa anche di generare il primo QueryExecutor.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        titleText= findViewById(R.id.titleResultText);
        resultQueries = findViewById(R.id.resultQueries);
        initListeners();
        context=this.getApplicationContext();
        String querySearchString = getIntent().getStringExtra("searchString");
        titleText.setText("Ecco cosa abbiamo trovato per "+"\""+querySearchString+"\"");
        QueryExecutor executor=new QueryExecutor(parseString(querySearchString, " "),this);
        Log.d("query", "Executor creato. La query sta per partire.");
        executor.executeQuery();
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
        if(topList==null)
            Log.d("query","Topquery=null");
        else
            {
                for(Place place: topList)
                    Log.d("query",place.getName());
            }
        adapter = new QueryResultsAdapter(ResultsActivity.this, topList, this);
        resultQueries.setAdapter(adapter);
        resultQueries.setLayoutManager(new LinearLayoutManager(ResultsActivity.this, RecyclerView.VERTICAL, false));
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
    }

    public static void showDetails(Place toShow)
    {
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("toShow", toShow);
        context.startActivity(intent);
    }


}