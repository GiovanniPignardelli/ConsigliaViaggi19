package it.gpgames.consigliaviaggi19.search;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class QueryExecutor {
    int currentIndex;
    String[] parsedString;
    ResultsActivity waitingForResults;
    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
    com.google.firebase.firestore.Query query=null;
    com.google.firebase.firestore.Query topQuery=null;
    com.google.firebase.firestore.Query weakQuery=null;

    public QueryExecutor(String[] parsed, ResultsActivity activity)
    {
        this.parsedString=parsed;
        this.currentIndex=0;
        this.waitingForResults=activity;
    }

    private QueryExecutor(int actualIndex, String[] parsedString, ResultsActivity activity, com.google.firebase.firestore.Query weakQuery, com.google.firebase.firestore.Query topQuery )
    {
        this.currentIndex=actualIndex;
        this.parsedString=parsedString;
        this.waitingForResults=activity;
        this.weakQuery=weakQuery;
        this.topQuery=topQuery;
        this.query=topQuery;
    }

    public void executeQuery()
    {
        if(query==null)
        {
            query=dbRef.collection("hotels").whereArrayContains("searchTags",parsedString[currentIndex]).limit(50);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    Log.d("query",new Integer(currentIndex).toString());
                    if(query!=null)
                    {
                        Log.d("query","La query non è null");
                        weakQuery=topQuery;
                        topQuery=query;
                        Log.d("query","Lunghezza: "+parsedString.length+"; indice: "+currentIndex);
                        if(currentIndex<parsedString.length-1)
                        {
                            Log.d("query","Lunghezza: "+parsedString.length+"; indice: "+currentIndex);
                            new QueryExecutor(currentIndex+1,parsedString,waitingForResults,weakQuery,topQuery).executeQuery();
                        }
                        else
                        {
                            waitingForResults.setUpRecycleView(topQuery,weakQuery);
                        }
                    }
                    else
                    {
                        Log.d("query","La query è null");
                        if(currentIndex<parsedString.length-1)
                        {
                            new QueryExecutor(currentIndex+1,parsedString,waitingForResults,weakQuery,topQuery).executeQuery();
                        }
                        else
                        {
                            waitingForResults.setUpRecycleView(topQuery,weakQuery);
                        }
                    }

                }
            });
        }

    }

}
