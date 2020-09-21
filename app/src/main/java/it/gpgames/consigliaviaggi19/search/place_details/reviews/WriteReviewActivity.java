package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.gpgames.consigliaviaggi19.R;

public class WriteReviewActivity extends AppCompatActivity {

    private CircleImageView iPlacePic;
    private ImageView bBack;
    private EditText eReviewText;
    private EditText eDate;
    private RatingBar ratingBar;
    private TextView eTitle;
    private Button bSendReview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        iPlacePic = findViewById(R.id.imageView2);
        bBack = findViewById(R.id.back4);
        eReviewText = findViewById(R.id.reviewText);
        eDate = findViewById(R.id.dateReview);
        ratingBar = findViewById(R.id.ratingReview);
        bSendReview = findViewById(R.id.sendReview);
        eTitle = findViewById(R.id.placeTitle);
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
            @Override
            public void onClick(View v) {
                if(ratingBar.getNumStars() < 1){
                    bSendReview.setError("Scegli un punteggio di rating!");
                    return;
                }
                if(eReviewText.getText().length() < 30){
                    eReviewText.setError("Inserire almeno 30 caratteri!");
                    return;
                }

            }
        });
    }
}