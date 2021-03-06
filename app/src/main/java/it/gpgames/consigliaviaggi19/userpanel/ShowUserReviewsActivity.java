package it.gpgames.consigliaviaggi19.userpanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

/**Activity che permette la visualizzazione di tutte le recensioni relative ad un utente.
 * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback
 * @see it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter.RecyclerGetter*/
public class ShowUserReviewsActivity extends AppCompatActivity implements ReviewsAdapter.RecyclerGetter, DatabaseCallback {

    private RecyclerView recyclerView;
    private ReviewsAdapter reviewsAdapter;
    private ImageView back;
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();


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
        List<Review> reviews = (List<Review>) getIntent().getSerializableExtra("reviewsToShow");
        reviewsAdapter=new ReviewsAdapter(ShowUserReviewsActivity.this, reviews, ShowUserReviewsActivity.this, ReviewsAdapter.FLAG_PLACE);
        recyclerView.setAdapter(reviewsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowUserReviewsActivity.this, RecyclerView.VERTICAL, false));

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
    public void show(String id, int flag) {

        switch(flag) {
            case ReviewsAdapter.FLAG_PLACE:
                placeDao.getPlaceByID(id, this, 0);
                break;

            case ReviewsAdapter.FLAG_USER:
                userDao.getUserByID(id, this, 0);
                break;
        }
    }

    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(HandshakeResponse hreq, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, int callbackCode) {
        Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
        intent.putExtra("toShow", place);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, int callbackCode) {
        Intent intent = new Intent(getApplicationContext(), UserPanelActivity.class);
        intent.putExtra("userToShow", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
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
    public void places_callback(List<Place> places, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(String message, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this,"Errore, controllare i log.",Toast.LENGTH_LONG).show();
        Log.d("query",e.getMessage());
    }
}
