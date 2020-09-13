package it.gpgames.consigliaviaggi19.search;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.gpgames.consigliaviaggi19.places.Place;

public class QueryExecutor {
    int currentIndex;
    String[] parsedString;
    ResultsActivity waitingForResults;
    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    List<Place> weakList;
    List<Place> topList;
    public QueryExecutor(String[] parsed, ResultsActivity activity)
    {
        this.parsedString=parsed;
        this.currentIndex=0;
        this.waitingForResults=activity;
        this.weakList=null;
        this.topList=null;
    }

    private QueryExecutor(int currentIndex, String[] parsedString, ResultsActivity activity, List<Place> weakList, List<Place> topList)
    {
        this.currentIndex=currentIndex;
        this.parsedString=parsedString;
        this.waitingForResults=activity;
        this.weakList=weakList;
        this.topList=topList;
    }

    public void executeQuery()
    {
        dbRef.collection("hotels")
                .whereArrayContains("searchTags", parsedString[currentIndex])
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("query", "la query è terminata.");
                        if (task.isSuccessful())
                        {
                            Log.d("query", "Il task ha avuto successo");
                            List<Place> newList=new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                            {
                                Log.d("query", document.getId() + " => " + document.getData().toString());
                                newList.add(document.toObject(Place.class));
                            }

                            if(topList==null || topList.isEmpty())
                                topList=newList;
                            else if(topList.size()<newList.size())
                            {
                                weakList=topList;
                                topList=newList;
                            }
                            if(currentIndex+1 < parsedString.length-1)
                            {
                                QueryExecutor executor=new QueryExecutor(currentIndex+1,parsedString,waitingForResults,weakList,topList);
                                executor.executeQuery();
                            }
                            else
                            {
                                waitingForResults.setUpRecycleView(weakList,topList);
                            }


                        }
                        else
                        {
                            Log.d("query", "Error getting documents: ", task.getException());
                            Toast.makeText(waitingForResults,"E' stato riscontrato un errore.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
