package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;

/** Si occupa di eseguire le query. Ha due costruttori: uno pubblico, uno privato. Se la stringa di ricerca
 * ha più di una parola, vengono generati ricorsivamente tanti QueryExecutor quante sono le parole di ricerca.*/
public class FirebaseQueryExecutor {

    int callbackCode;

    /** La stringa di ricerca è stata precedentemente splittata in parole*/
    String[] parsedString;

    /** Posizione attuale nell'array delle stringhe di ricerca*/
    int currentIndex;

    /** Riferimento all'activity che attende i risultati della query*/
    DatabaseCallback waitingForResults;

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    /** Vengono mantenute sempre due liste di risultati. Quando una lista "migliore" (con meno elementi) viene
     * trovata, la vecchia topList diventa la weakList, e la topList diventa la nuova lista.*/
    List<Place> weakList;
    List<Place> topList;

    /** Costruttore pubblico che inizializza i parametri */
    public FirebaseQueryExecutor(String[] parsed, DatabaseCallback callback, int callbackCode)
    {
        this.parsedString=parsed;
        this.currentIndex=0;
        this.waitingForResults=callback;
        this.callbackCode = callbackCode;
        this.weakList=null;
        this.topList=null;
    }

    /** Costruttore privato. Esso viene utilizzato all'interno della classe stessa per generare nuovi QueryExecutor. A queste nuove istanze
     * verranno passati i dati ottenuti fino ad adesso, e l'indice della posizione dell'array (incrementato di 1)*/
    private FirebaseQueryExecutor(int currentIndex, String[] parsedString, DatabaseCallback callback, int callbackCode, List<Place> weakList, List<Place> topList)
    {
        this.callbackCode = callbackCode;
        this.currentIndex=currentIndex;
        this.parsedString=parsedString;
        this.waitingForResults=callback;
        this.weakList=weakList;
        this.topList=topList;
    }

    /** Metodo che esegue la query sulla parola contenuta in parsedString[currentIndex].
     *Viene generato un listener che attende la ricezione dei risultati.
     * Quando essi pervengono, vengono effettuati i controlli per gestire topList, weakList e l'interruzione dell'iterazione.*/
    public void executeQuery()
    {
        dbRef.collection("places")
                .whereArrayContains("searchTags", parsedString[currentIndex].toLowerCase())
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
                                if(document.toObject(Place.class).getCategory().equals(Place.CATEGORY_RESTAURANT))
                                {
                                    Log.d("gen", "sto generando ristorante");
                                    Restaurant rest=new Restaurant(document.toObject(Place.class), (ArrayList<String>) document.get("cuisineTags"),(ArrayList<String>)document.get("serviceTags"), document.getId());
                                    newList.add(rest);
                                }
                                else if(document.toObject(Place.class).getCategory().equals(Place.CATEGORY_HOTEL))
                                {
                                    Log.d("gen", "sto generando hotel");
                                    Hotel hotel=new Hotel(document.toObject(Place.class),  document.get("hClass").toString(), (ArrayList<String>) document.get("roomTags"), (ArrayList<String>) document.get("roomTypeTags"),document.getId());
                                    newList.add(hotel);
                                }
                                else if(document.toObject(Place.class).getCategory().equals(Place.CATEGORY_PLACE))
                                {
                                    Log.d("gen", "sto generando place");
                                    Place place=new Place(document.toObject(Place.class), document.getId());
                                    newList.add(place);
                                }

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
                                FirebaseQueryExecutor executor=new FirebaseQueryExecutor(currentIndex+1,parsedString,waitingForResults,callbackCode,weakList,topList);
                                executor.executeQuery();
                            }
                            else
                            {
                                waitingForResults.callback(weakList, topList, callbackCode);
                            }
                        }
                        else
                        {
                            Log.d("query", "Error getting documents: ", task.getException());
                            waitingForResults.manageError(task.getException(), callbackCode);
                        }
                    }
                });
    }
}