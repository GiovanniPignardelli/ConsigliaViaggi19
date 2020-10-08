package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.DatabaseUtilities;
import it.gpgames.consigliaviaggi19.DAO.models.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.filters.FiltersSelectorActivity;
import it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity;

/** Classe incaricata di esegure le query sui Place su Firestore.*/
public class FirebaseQueryExecutor {

    /**Codice di ritorno al callback; per discriminare i ritorni al DatabaseCallback
     * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback*/
    private int callbackCode;

    /** Riferimento all'activity che attende i risultati della query. Essa deve implementare l'interfaccia DatabaseCallback
     * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback*/
    private DatabaseCallback waitingForResults;

    private final FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    /** La stringa di ricerca è stata precedentemente splittata in parole*/
    private String[] parsedString;

    /**Categoria dei place da cercare: una tra "place", "hotel", "restaurant".*/
    private String category;

    /**Punteggio minimo dei place da cercare.*/
    private Integer minRating;

    /**Fascia economica dei place da cercare. E' una stringa tra: "€", "€€", "€€€"*/
    private String price;

    /**Mappa che tiene corrispondenze Interi - Liste di Stringhe. La chiave intera rappresenta un flag che indica la categoria del Tag, mentre il valore (la lista di Stringhe), indica i tag.
     * Per dettagli sui flag:
     * @see it.gpgames.consigliaviaggi19.search.filters.FiltersSelectorActivity*/
    private HashMap<Integer,ArrayList<String>> tags;

    /**Criterio di ordinamento. E' un flag che prende valori tra quelli nella classe OrderSelectorActivity.
     * @see it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity*/
    private Integer order;

    /**Direzione dell'ordinamento. Prende valore da uno degli appositi flag in OrderSelectorActivity.
     * @see it.gpgames.consigliaviaggi19.search.filters.order.OrderSelectorActivity*/
    private Integer direction;

    /**Firestore non supporta l'ordinamento per migliori corrispondenze. Questo tipo di ordinamento
     * viene eseguito sul client, e questo attributo discrimina quando questo deve avvenire.*/
    private boolean isToReorderByBestMatch=false;

    /** Costruttore pubblico che inizializza i parametri */
    public FirebaseQueryExecutor(final String[] parsed, String category, Integer minRating, String price, HashMap<Integer, ArrayList<String>> tags,Integer order,Integer direction, DatabaseCallback callback, int callbackCode)
    {
        this.parsedString=parsed;
        this.waitingForResults=callback;
        this.callbackCode = callbackCode;
        this.category=category;
        this.price=price;
        this.minRating=minRating;
        this.tags=tags;
        this.direction=direction;
        this.order=order;
    }

    /**Metodo che esegue la query richiama il waitingForResult col metodo callback().*/
    public void executeQuery()
    {
        //Query di base con la ricerca sui tag di ricerca.
        Query query=dbRef.collection("places");

        //se ho inserito una stringa di ricerca.
        if(parsedString!=null)
            query=query.whereArrayContainsAny("searchTags", Arrays.asList(parsedString));

        //applico i filtri
        query=applyFilter(query);

        //applico l'ordine
        query=applyOrder(query);

        query.limit(100).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    ArrayList<Place> placeList=new ArrayList<>();
                    for(QueryDocumentSnapshot doc:task.getResult())
                    {
                        Place generatedPlace=PlaceFirebaseDAO.generatePlace(doc);
                        placeList.add(generatedPlace);
                    }
                    if(isToReorderByBestMatch)
                        reorderByBestMatch(placeList);
                    waitingForResults.callback(null, placeList, callbackCode);
                }
                else
                    waitingForResults.manageError(task.getException(),callbackCode);
            }
        });
    }

    /**Metodo che opera su una lista di place e li ordina per migliori corrispondenze.
     * Se ho due Place A e B, A è una miglior corrispondenza rispetto a B se A contiene nel suo nome più parole prese dalla stringa di ricerca rispetto a B
     * Viene instanziato un Comparator che compara i Place proprio in base a questa relazione d'ordine.
     * Se due Place hanno le stesse corrispondenze, vengono ordinati in ordine alfabetico.
     * @param placeList Lista dei place da riordinare.
     * @throws IllegalStateException*/
    private void reorderByBestMatch(ArrayList<Place> placeList) {

        if(parsedString==null)
        {
            waitingForResults.manageError(new IllegalStateException("Non posso verificare le migliori corrispondenze se non hai inserito una stringa di ricerca."), callbackCode);
            return;
        }

        //le parole della stringa di ricerca vengono messe in un set, così da non avere duplicati e poter verificare facilmente se una stringa appartiene all'insieme.
        final HashSet<String> searchStrings=new HashSet<>();
        searchStrings.addAll(Arrays.asList(parsedString));

        Comparator<Place> comparator=new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                int n1=0,n2=0;
                for(String s: DatabaseUtilities.parseString(o1.getName(), " ",true))
                {
                    if(searchStrings.contains(s))
                        n1++;
                }

                for(String s: DatabaseUtilities.parseString(o2.getName(), " ",true))
                {
                    if(searchStrings.contains(s))
                        n2++;
                }
                if(n1>n2)return 1;
                if(n1<n2)return -1;
                return o1.getName().compareTo(o2.getName());
            }
        };

        placeList.sort(comparator);
        if(direction==OrderSelectorActivity.FLAG_DESC)
            Collections.reverse(placeList);
    }

    /**Metodo che applica l'ordine e la direzione
     * Per questioni di robustezza, se l'ordinamento non è ben definito, viene usato uno di default (BEST_MATCH, DESCENDING).
     * @param query query alla quale applicare l'ordine.*/
    private Query applyOrder(Query query) {
        if(order==null || direction==null)
        {
            waitingForResults.callback("Ordinamento non definito. Verrà usato quello di default",callbackCode);
            order= OrderSelectorActivity.FLAG_BEST_MATCH;
            direction= OrderSelectorActivity.FLAG_DESC;
        }
        switch(order)
        {
            case OrderSelectorActivity.FLAG_RATING:
                if(direction==OrderSelectorActivity.FLAG_DESC)
                    query=query.orderBy("avgReview",Query.Direction.DESCENDING).orderBy("name",Query.Direction.DESCENDING);
                else if(direction==OrderSelectorActivity.FLAG_ASC)
                    query=query.orderBy("avgReview",Query.Direction.ASCENDING).orderBy("name",Query.Direction.DESCENDING);
                break;
            case OrderSelectorActivity.FLAG_AGE:
                if(direction==OrderSelectorActivity.FLAG_DESC)
                    query=query.orderBy("addYear",Query.Direction.DESCENDING).orderBy("name",Query.Direction.DESCENDING);
                else if(direction==OrderSelectorActivity.FLAG_ASC)
                    query=query.orderBy("addYear",Query.Direction.ASCENDING).orderBy("name",Query.Direction.DESCENDING);
                break;
            case OrderSelectorActivity.FLAG_ALPHABETICAL:
                if(direction==OrderSelectorActivity.FLAG_DESC)
                    query=query.orderBy("name",Query.Direction.DESCENDING);
                else if(direction==OrderSelectorActivity.FLAG_ASC)
                    query=query.orderBy("name",Query.Direction.ASCENDING);
                break;
            case OrderSelectorActivity.FLAG_N_REVIEW:
                if(direction==OrderSelectorActivity.FLAG_DESC)
                    query=query.orderBy("nReviews",Query.Direction.DESCENDING).orderBy("name",Query.Direction.DESCENDING);
                else if(direction==OrderSelectorActivity.FLAG_ASC)
                    query=query.orderBy("nReviews",Query.Direction.ASCENDING).orderBy("name",Query.Direction.DESCENDING);
                break;
            case OrderSelectorActivity.FLAG_BEST_MATCH:
                isToReorderByBestMatch=true;
                break;
            default:
                waitingForResults.callback("Ordinamento non definito. Verrà usato quello di default",callbackCode);
                order= OrderSelectorActivity.FLAG_BEST_MATCH;
                direction= OrderSelectorActivity.FLAG_DESC;
                query=applyOrder(query);
                break;
        }
        return query;
    }

    /**Metodo che applica i filtri ad una query.
     * @param query query alla quale applicare i filtri
     * PRECONDIZIONE: la categoria di filtri da applicare deve essere compatibile con la categoria nella quale si stanno cercando i Place
     * @throws IllegalStateException nel caso in cui questa precondizione venga violata.*/
    private Query applyFilter(Query query) {
        if(category!=null)
            query=query.whereEqualTo("category",category);
        if(price!=null)
            query=query.whereEqualTo("priceTag", price);
        if(minRating!=null)
            query=query.whereGreaterThan("avgReview",minRating);
        if(tags!=null)
        {
            Iterator it = tags.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Integer key=(Integer)pair.getKey();
                ArrayList<String> tags=(ArrayList<String>) pair.getValue();
                if(tags!=null)
                {
                    switch(key)
                    {
                        case FiltersSelectorActivity.FLAG_GENERAL_TAGS:
                            for(String tag: tags)
                            {
                                Log.d("tag__",tag);
                                query=query.whereEqualTo("generalTags."+tag,true);
                            }
                            break;
                        case FiltersSelectorActivity.FLAG_CUISINE_TAGS:
                            if(category!=Place.CATEGORY_RESTAURANT)
                                throw new IllegalStateException("You can't set this filter!");
                            for(String tag: tags)
                                query=query.whereEqualTo("cuisineTags."+tag,true);
                            break;
                        case FiltersSelectorActivity.FLAG_SERVICE_TAGS:
                            if(category!=Place.CATEGORY_RESTAURANT)
                                throw new IllegalStateException("You can't set this filter!");
                            for(String tag: tags)
                                query=query.whereEqualTo("serviceTags."+tag,true);
                            break;
                        case FiltersSelectorActivity.FLAG_ROOM_TAGS:
                            if(category!=Place.CATEGORY_HOTEL)
                                throw new IllegalStateException("You can't set this filter!");
                            for(String tag: tags)
                                query=query.whereEqualTo("roomTags."+tag,true);
                            break;
                        case FiltersSelectorActivity.FLAG_ROOM_TYPE_TAGS:
                            if(category!=Place.CATEGORY_HOTEL)
                                throw new IllegalStateException("You can't set this filter!");
                            for(String tag: tags)
                                query=query.whereEqualTo("roomTypeTags."+tag,true);
                            break;
                    }
                }
            }
        }
        return query;
    }
}
