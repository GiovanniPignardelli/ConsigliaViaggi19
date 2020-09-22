package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Review;
import it.gpgames.consigliaviaggi19.userpanel.UserData;

public class WriteReviewActivity extends AppCompatActivity {

    private CircleImageView iPlacePic;
    private ImageView bBack;
    private EditText eReviewText;
    private RatingBar ratingBar;
    private TextView eTitle;
    private Button bSendReview;
    private String dbDocID;
    private androidx.appcompat.widget.Toolbar hider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        iPlacePic = findViewById(R.id.placeImage);
        bBack = findViewById(R.id.back4);
        eReviewText = findViewById(R.id.textReview);
        ratingBar = findViewById(R.id.ratingView);
        bSendReview = findViewById(R.id.sendReview);
        eTitle = findViewById(R.id.placeTitle);
        hider=findViewById(R.id.hider);
        init();
    }

    private void init() {
        Intent i=getIntent();
        eTitle.setText(i.getStringExtra("name"));
        dbDocID=i.getStringExtra("id");
        initListeners();
    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bSendReview.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(ratingBar.getRating() < 1){
                    eReviewText.setError("Scegli un punteggio di rating!");
                    return;
                }
                if(eReviewText.getText().length() < 30){
                    eReviewText.setError("Inserire almeno 30 caratteri!");
                    return;
                }

                hider.setVisibility(View.VISIBLE);
                bSendReview.setEnabled(false);

                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String dateString=date.format(formatter);

                String reviewString=eReviewText.getText().toString();

                int rating=(int)ratingBar.getRating();

                sendReview(reviewString, rating, dateString);
            }
        });
    }

    private void sendReview(String reviewString, int rating, String dateString) {
        Review review=new Review(dbDocID, FirebaseAuth.getInstance().getUid(),reviewString,dateString, rating);
        FirebaseFirestore dbRef=FirebaseFirestore.getInstance();

        dbRef.collection("reviewPool")
                .add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UploadReview", "Review caricata.");
                        hider.setVisibility(View.INVISIBLE);
                        bSendReview.setEnabled(true);
                        refreshStats();
                        Toast.makeText(getApplicationContext(),"Recensione inviata", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UploadReview", "Qualcosa è andato storto");
                        hider.setVisibility(View.INVISIBLE);
                        bSendReview.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Errore. Recensione non inviata", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void refreshStats() {

        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID", FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    UserData user = task.getResult().toObjects(UserData.class).get(0);
                    int oldNum=user.getnReview();
                    float oldAvg=user.getAvgReview();
                    FirebaseFirestore.getInstance().collection("userPool").document(FirebaseAuth.getInstance().getUid()).update("nReview",oldNum+1);
                    FirebaseFirestore.getInstance().collection("userPool").document(FirebaseAuth.getInstance().getUid()).update("avgReview", (oldAvg+ratingBar.getRating())/oldNum+1);
                }
            }
        });

        FirebaseFirestore.getInstance().collection("places").document(dbDocID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Place place=task.getResult().toObject(Place.class);
                    int oldNum=place.getnReview();
                    float oldAvg=place.getAvgReview().floatValue();
                    FirebaseFirestore.getInstance().collection("places").document(dbDocID).update("nReviews",oldNum+1);
                    FirebaseFirestore.getInstance().collection("places").document(dbDocID).update("avgReview", (oldAvg+ratingBar.getRating())/oldNum+1);
                    Log.d("refresh", "Tutto ok");
                }
                else
                {
                    Log.d("refresh", "Niente ok");
                }
            }
        });
    }
}