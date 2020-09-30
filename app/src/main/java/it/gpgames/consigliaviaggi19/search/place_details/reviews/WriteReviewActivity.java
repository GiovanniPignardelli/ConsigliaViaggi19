package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.ReviewDAO;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;

public class WriteReviewActivity extends AppCompatActivity implements DatabaseCallback {

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
    private ReviewDAO reviewDao = DAOFactory.getDAOInstance().getReviewDAO();

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
        String uri = toShow.getPictures().get(0);
                if(uri!=null)
                    Glide.with(getApplicationContext()).load(uri).into(iPlacePic);
                else Log.d("write_review","Impossibile caricare l'immagine. ");
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
        Review review=new Review(dbDocID, User.getLocalInstance().getUserID(),reviewString,year,month,day, rating);
        reviewDao.createReview(review,this,0);
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

    @Override
    public void callback(int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, int callbackCode) {
        Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
        intent.putExtra("toShow", place);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(String message, int callbackCode) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        hider.setVisibility(View.INVISIBLE);
        bSendReview.setEnabled(true);
    }
}