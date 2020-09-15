package it.gpgames.consigliaviaggi19.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.places.Place;

public class ResultsActivity extends AppCompatActivity {

    private ImageView bBack;
    private RecyclerView resultQueries;
    private QueryResultsAdapter adapter;
    private static NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        resultQueries = findViewById(R.id.resultQueries);
        initListeners();
        String querySearchString = getIntent().getStringExtra("searchString");
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

    public void setUpRecycleView(List<Place> weakList, List<Place> topList) {
        if(topList==null)
            Log.d("query","Topquery=null");
        else
            {
                for(Place place: topList)
                    Log.d("query",place.getName());
            }
        adapter = new QueryResultsAdapter(ResultsActivity.this, topList);
        resultQueries.setAdapter(adapter);
        resultQueries.setLayoutManager(new LinearLayoutManager(ResultsActivity.this, RecyclerView.VERTICAL, false));
    }


    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ResultsActivity.this, MainActivity.class));
                finish();
            }
        });
    }


}