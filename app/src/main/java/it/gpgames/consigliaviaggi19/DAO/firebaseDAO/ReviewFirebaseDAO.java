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
        dbRef.collection("reviewPool")
                .add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UploadReview", "Review caricata.");
                        refreshStats(review, callback, callbackCode);
                        callback.showMessage("Recensione inviata", callbackCode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UploadReview", "Qualcosa è andato storto");
                        callback.manageError(e, callbackCode);
                    }
                });
    }

    private void refreshStats(final Review review, final DatabaseCallback callback, final int callbackCode) {
        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID", FirebaseAuth.getInstance().getUid()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    User user = task.getResult().toObjects(User.class).get(0);
                    String documentID=task.getResult().getDocuments().get(0).getId();
                    Integer oldNum=user.getnReview();
                    Float oldAvg=user.getAvgReview();
                    FirebaseFirestore.getInstance().collection("userPool").document(documentID).update("nReview",oldNum+1);
                    FirebaseFirestore.getInstance().collection("userPool").document(documentID).update("avgReview", ((oldAvg+review.getRating())/(oldNum+1)));
                }

            }
        });


        FirebaseFirestore.getInstance().collection("places").document(review.getPlaceId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    PlaceFirebaseDAO placeFDao = (PlaceFirebaseDAO) DAOFactory.getDAOInstance().getPlaceDAO();
                    Place place = placeFDao.generatePlace(task.getResult());
                    Integer oldNum = place.getnReviews();
                    Float oldAvg = place.getAvgReview();
                    Float newAvg = (oldAvg + review.getRating()) / (oldNum + 1);
                    Log.d("Fratello",oldNum.toString());
                    Log.d("Fratello",oldAvg.toString());
                    Log.d("Fratello",newAvg.toString());
                    FirebaseFirestore.getInstance().collection("places").document(place.getDbDocID()).update("nReviews", oldNum + 1);
                    FirebaseFirestore.getInstance().collection("places").document(place.getDbDocID()).update("avgReview", newAvg);
                    callback.callback(place, callbackCode);
                }
                else{
                    callback.manageError(task.getException(),0);
                }
            }
        });
    }

    public void getReviewsByPlaceIDAndUserID(String placeID, String userID, final DatabaseCallback callback, int callbackCode){
        FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("placeId",placeID).whereEqualTo("userId", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Log.d("fratm",doc.toObject(Review.class).getPlaceId());
                    }
                    callback.callback(task.getResult().toObjects(Review.class),1);
                }
                else
                    callback.manageError(new Exception("Failed to obtain query."),1);
            }
        });
    }

}
