package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Review;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.userpanel.UserData;

public class WriteReviewActivity extends AppCompatActivity {

    private CircleImageView iPlacePic;
    private ImageView bBack;
    private EditText eReviewText;
    private RatingBar ratingBar;
    private TextView eTitle;
    private Button bSendReview;
    private androidx.appcompat.widget.Toolbar hider;

    private Place toShow;
    private String dbDocID;

    NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

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
        toShow=(Place)getIntent().getSerializableExtra("toShow");
        eTitle.setText(toShow.getName());
        dbDocID=toShow.getDbDocID();
        initPlaceImage();
        initListeners();
    }

    private void initPlaceImage() {
        FirebaseStorage.getInstance().getReference().child("Places").child("Pictures").child(dbDocID).child("main.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri!=null)
                    Glide.with(getApplicationContext()).load(uri).into(iPlacePic);
                else
                    Log.d("write_review","Impossibile caricare l'immagine. ");
            }
        });

    }

    private void initListeners(){
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                intent.putExtra("toShow", toShow);
                startActivity(intent);
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
                String day=dateString.substring(0,2);
                String month=dateString.substring(3,5);
                String year=dateString.substring(6);

                String reviewString=eReviewText.getText().toString();

                int rating=(int)ratingBar.getRating();

                sendReview(reviewString, rating, day,month,year);
            }
        });
    }

    private void sendReview(String reviewString, int rating, String day, String month, String year) {
        Review review=new Review(dbDocID, FirebaseAuth.getInstance().getUid(),reviewString,year,month,day, rating);
        FirebaseFirestore dbRef=FirebaseFirestore.getInstance();

        dbRef.collection("reviewPool")
                .add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("UploadReview", "Review caricata.");
                        refreshStats();
                        Toast.makeText(getApplicationContext(),"Recensione inviata", Toast.LENGTH_LONG).show();
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
        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID", FirebaseAuth.getInstance().getUid()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    UserData user = task.getResult().toObjects(UserData.class).get(0);
                    String documentID=task.getResult().getDocuments().get(0).getId();
                    Integer oldNum=user.getnReview();
                    Float oldAvg=user.getAvgReview();
                    FirebaseFirestore.getInstance().collection("userPool").document(documentID).update("nReview",oldNum+1);
                    FirebaseFirestore.getInstance().collection("userPool").document(documentID).update("avgReview", ((oldAvg+ratingBar.getRating())/(oldNum+1)));
                }
                else
                    Log.d("review","Errore");
            }
        });


        FirebaseFirestore.getInstance().collection("places").document(dbDocID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Place place=task.getResult().toObject(Place.class);
                Integer oldNum=place.getnReviews();
                Float oldAvg=place.getAvgReview();

                Float newAvg=(oldAvg+ratingBar.getRating())/(oldNum+1);

                Log.d("avg",newAvg.toString());

                FirebaseFirestore.getInstance().collection("places").document(dbDocID).update("nReviews",oldNum+1);
                FirebaseFirestore.getInstance().collection("places").document(dbDocID).update("avgReview", newAvg);

                toShow.setnReviews(oldNum+1);
                toShow.setAvgReview(newAvg);
                Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                intent.putExtra("toShow", toShow);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
}