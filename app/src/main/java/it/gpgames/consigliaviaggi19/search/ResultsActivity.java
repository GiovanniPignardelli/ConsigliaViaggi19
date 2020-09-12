package it.gpgames.consigliaviaggi19.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;
import it.gpgames.consigliaviaggi19.places.Hotel;


public class ResultsActivity extends AppCompatActivity {

    ImageView bBack;
    RecyclerView resultQueries;
    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    QueryResultsAdapter adapter;
    String querySearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        bBack = findViewById(R.id.back2);
        resultQueries = findViewById(R.id.resultQueries);
        initListeners();
        String querySearchString = getIntent().getStringExtra("searchString");
        QueryExecutor executor=new QueryExecutor(parseString(querySearchString, " "),this);
        executor.executeQuery();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
        adapter.stopListening();
    }

    public void setUpRecycleView(final Query topQuery, Query weakQuery) {
        Task<QuerySnapshot> result=topQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().isEmpty())Log.d("query", "la query non ha prodotto risultati");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("query", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("query", "Error getting documents: ", task.getException());
                }


                /*FirestoreRecyclerOptions<Place>options = new FirestoreRecyclerOptions.Builder<Place>().setQuery(topQuery,Place.class).build();
                adapter=new QueryResultsAdapter(options);
                resultQueries.setHasFixedSize(true);
                resultQueries.setLayoutManager(new LinearLayoutManager(ResultsActivity.this));
                resultQueries.setAdapter(adapter);

                 */
            }
        });


    }

    public static String[] parseString(String in, String pivot){
        return in.split(pivot);
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultsActivity.this, MainActivity.class));
                finish();
            }
        });
    }


}