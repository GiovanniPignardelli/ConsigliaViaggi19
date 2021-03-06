package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.DatabaseUtilities;
import it.gpgames.consigliaviaggi19.DAO.ReviewDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
/**Implementazione Firebase dell'interfaccia ReviewDAO*/
public class ReviewFirebaseDAO implements ReviewDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    /**Cerca tutte le reviews associate ad un utente.
     * @param dataID id dell'utente del quale si vogliono scaricare le recensioni*/
    public void getReviewsByUserID(String dataID, final DatabaseCallback callback, final int callbackCode){
        dbRef.collection("reviewPool").whereEqualTo("userId",dataID).orderBy("rating",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        List<Review> reviews = (List<Review>) task.getResult().toObjects(Review.class);
                        callback.callback(reviews, callbackCode);
                    }
                    else callback.manageError(new DatabaseUtilities.DataNotFoundException(), callbackCode);
            }
        });
    }

    /**Metodo che restituisce al DatabaseCallback tutte le reviews associate ad un Place
     * @param dataID id del Place
     * @param actualSort Criterio di ordinamento delle reviews (è un flag che prende valore tra quelli in PlaceDetailsActivity)
     * @param actualOrder direzione di ordinamento (è un flag che prende valore tra quelli in PlaceDetailsActivity)
     * @see it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity*/
    public void getReviewsByPlaceID(String dataID, final DatabaseCallback callback, int actualSort, int actualOrder, final int callbackCode) {
        Query query = FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("placeId", dataID);
        if (actualSort == PlaceDetailsActivity.SORT_DATE && actualOrder == PlaceDetailsActivity.ORDER_ASC)
            query = query.orderBy("year", Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING);
        else if (actualSort == PlaceDetailsActivity.SORT_DATE && actualOrder == PlaceDetailsActivity.ORDER_DESC)
            query = query.orderBy("year", Query.Direction.DESCENDING).orderBy("month", Query.Direction.DESCENDING).orderBy("day", Query.Direction.DESCENDING);
        else if (actualSort == PlaceDetailsActivity.SORT_STAR && actualOrder == PlaceDetailsActivity.ORDER_ASC)
            query = query.orderBy("rating", Query.Direction.ASCENDING).orderBy("year", Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING);
        else
            query = query.orderBy("rating", Query.Direction.DESCENDING).orderBy("year", Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.callback(task.getResult().toObjects(Review.class), callbackCode);
                } else {
                    callback.manageError(new Exception("Error in obtaining the query."), callbackCode);
                }
            }
        });
    }

    /**Metodo che invia una recensione al Firestore.
     * @param review Review pre-generata.*/
    public void createReview(final Review review, final DatabaseCallback callback, final int callbackCode){
        dbRef.collection("reviewPool").add(review).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Log.d("UploadReview", "Review caricata.");
                    refreshStats(review, callback, callbackCode);
                    callback.callback("Recensione inviata", callbackCode);
                }
                else
                {
                    Log.d("UploadReview", "Qualcosa è andato storto");
                    callback.manageError(task.getException(), callbackCode);
                }
            }
        });
    }

    /**Metodo che viene richiamato all'inserimento di ogni recensione perchè le statistiche sulle recensioni dei place e degli user vengano opportunamente aggiornate
     * @deprecated perché questo task va effettuato server-side. (Momentaneamente client-side)*/
    private void refreshStats(final Review review, final DatabaseCallback callback, final int callbackCode) {

        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID", FirebaseAuth.getInstance().getUid()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObjects(User.class).get(0);
                    final String documentID = task.getResult().getDocuments().get(0).getId();
                    final float oldNum = user.getnReview();
                    final float oldSum = user.getSumReviews();
                    DocumentReference userDoc = FirebaseFirestore.getInstance().collection("userPool").document(documentID);

                    userDoc.update("nReview", oldNum + 1).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });

                    userDoc.update("sumReviews", (oldSum + review.getRating())).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });

                    userDoc.update("avgReview", ((oldSum + review.getRating()) / (oldNum + 1))).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });
                }
            }
        });

        FirebaseFirestore.getInstance().collection("places").document(review.getPlaceId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    final DocumentSnapshot doc=task.getResult();
                    final Place place=task.getResult().toObject(Place.class);
                    final float oldNum = place.getnReviews();
                    final float oldSum = place.getSumReviews();

                    final DocumentReference placeDoc=task.getResult().getReference();

                    placeDoc.update("avgReview", ((oldSum + review.getRating()) / (oldNum + 1))).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            placeDoc.update("sumReviews", (oldSum + review.getRating())).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callback.manageError(e, callbackCode);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    placeDoc.update("nReviews", oldNum + 1).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.manageError(e, callbackCode);
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            callback.callback(callbackCode);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else
                    callback.manageError(task.getException(),callbackCode);
            }
        });
    }

    /**Restituisce la reviews associata ad un utente e un place, se esiste, altrimenti restituisce null
     * @param placeID id del place
     * @param userID id dello user*/
    public void getReviewsByPlaceIDAndUserID(String placeID, String userID, final DatabaseCallback callback, int callbackCode){
        FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("placeId",placeID).whereEqualTo("userId", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    callback.callback(task.getResult().toObjects(Review.class),1);
                }
                else
                    callback.manageError(new Exception("Failed to obtain query."),1);
            }
        });
    }

}
