package it.gpgames.consigliaviaggi19.userpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.DAO.places.Hotel;
import it.gpgames.consigliaviaggi19.DAO.places.Place;
import it.gpgames.consigliaviaggi19.DAO.places.Restaurant;
import it.gpgames.consigliaviaggi19.DAO.places.Review;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class ShowUserReviewsActivity extends AppCompatActivity implements ReviewsAdapter.RecyclerGetter{
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

        Log.d("query", "sto per cercare l'utente con id:"+userID);
        FirebaseFirestore.getInstance().collection("reviewPool").whereEqualTo("userId",userID).orderBy("year", Query.Direction.ASCENDING).orderBy("month", Query.Direction.ASCENDING).orderBy("day", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<Review> reviewList=task.getResult().toObjects(Review.class);

                    reviewsAdapter=new ReviewsAdapter(ShowUserReviewsActivity.this, reviewList, ShowUserReviewsActivity.this, ReviewsAdapter.FLAG_PLACE);
                    recyclerView.setAdapter(reviewsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ShowUserReviewsActivity.this, RecyclerView.VERTICAL, false));
                }
                else
                    Log.d("query","Errore query in ShowUserReviewsActivity. "+task.getException().getMessage());
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

    @Override
    public void show(String id) {

        FirebaseFirestore.getInstance().collection("places").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Place toShow;
                    DocumentSnapshot document=task.getResult();
                    if(document.toObject(Place.class).getCategory().equals(Place.CATEGORY_RESTAURANT))
                    {
                        Log.d("gen", "sto generando ristorante");
                        Restaurant rest=new Restaurant(document.toObject(Place.class), (ArrayList<String>) document.get("cuisineTags"),(ArrayList<String>)document.get("serviceTags"), document.getId());
                        toShow=rest;
                    }
                    else if(document.toObject(Place.class).getCategory().equals(Place.CATEGORY_HOTEL))
                    {
                        Log.d("gen", "sto generando hotel");
                        Hotel hotel=new Hotel(document.toObject(Place.class),  document.get("hClass").toString(), (ArrayList<String>) document.get("roomTags"), (ArrayList<String>) document.get("roomTypeTags"),document.getId());
                        toShow=hotel;
                    }
                    else
                    {
                        Log.d("gen", "sto generando place");
                        Place place=new Place(document.toObject(Place.class), document.getId());
                        toShow=place;
                    }

                    Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                    intent.putExtra("toShow", toShow);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
                else
                    Log.d("query","Errore query in ShowUserReviewsActivity");
            }
        });

    }
}
