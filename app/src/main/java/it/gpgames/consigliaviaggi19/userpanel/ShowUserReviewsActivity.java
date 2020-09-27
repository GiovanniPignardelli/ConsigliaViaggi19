package it.gpgames.consigliaviaggi19.userpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Review;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class ShowUserReviewsActivity extends AppCompatActivity implements ReviewsAdapter.recyclerGetter{
    RecyclerView recyclerView;
    ReviewsAdapter reviewsAdapter;
    String userID;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_reviews);
        recyclerView=findViewById(R.id.userReviewRecycler);
        back=findViewById(R.id.back5);
        init();
    }

    private void init()
    {
        userID=getIntent().getStringExtra("id");
        if(userID==null)
        {
            Toast.makeText(getApplicationContext(),"Errore nel caricamento delle reviews. Riprovare.", Toast.LENGTH_LONG).show();
            finish();
        }

        FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("userId",userID).orderBy("year", Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<Review> reviewList=task.getResult().toObjects(Review.class);

                    reviewsAdapter=new ReviewsAdapter(ShowUserReviewsActivity.this, reviewList, null, ReviewsAdapter.FLAG_PLACE);
                    recyclerView.setAdapter(reviewsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ShowUserReviewsActivity.this, RecyclerView.VERTICAL, false));
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public RecyclerView getReviewsRecyclerView() {
        return recyclerView;
    }
}
