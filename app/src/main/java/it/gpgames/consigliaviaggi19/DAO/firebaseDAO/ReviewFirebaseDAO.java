package it.gpgames.consigliaviaggi19.DAO.firebaseDAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class ReviewFirebaseDAO implements ReviewDAO {

    FirebaseFirestore dbRef = FirebaseFirestore.getInstance();

    public void getReviewsByUserID(String dataID, final DatabaseCallback callback, final int callbackCode){
        dbRef.collection("reviewPool").whereEqualTo("userId",dataID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void getReviewsByPlaceID(String dataID, final DatabaseCallback callback, final int callbackCode){
            dbRef.collection("reviewPool").whereEqualTo("placeId",dataID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void refreshStats(final Review review, final DatabaseCallback callback, final int callbackCode) {

        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID", FirebaseAuth.getInstance().getUid()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObjects(User.class).get(0);
                    final String documentID = task.getResult().getDocuments().get(0).getId();
                    final int oldNum = user.getnReview();
                    final int oldSum = user.getSumReviews();
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

                    userDoc.update("avgReview", (oldNum + review.getRating()) / oldNum + 1).addOnFailureListener(new OnFailureListener() {
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
                    Place place=task.getResult().toObject(Place.class);
                    final int oldNum = place.getnReviews();
                    final int oldSum = place.getSumReviews();

                    DocumentReference placeDoc=task.getResult().getReference();

                    placeDoc.update("nReview", oldNum + 1).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });

                    placeDoc.update("sumReviews", (oldSum + review.getRating())).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });

                    placeDoc.update("avgReview", (oldNum + review.getRating()) / oldNum + 1).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.manageError(e, callbackCode);
                        }
                    });

                }
                else
                    callback.manageError(task.getException(),callbackCode);
            }


        });
    }

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
