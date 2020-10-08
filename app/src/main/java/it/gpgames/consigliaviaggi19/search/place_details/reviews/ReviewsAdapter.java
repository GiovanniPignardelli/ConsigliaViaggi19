package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;

/**Adapter che adatta le reviews al RecyclerView
 * Implementa l'interfaccia DatabaseCallback perché effettua richieste ai DAO, e ne attende callback.
 * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback*/
public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DatabaseCallback {

    private LayoutInflater inflater;
    /**Lista di review da visualizzare*/
    private List<Review> reviewsList;
    private Context context;
    /**Riferimento all'activity che contiene il recyclerView al quale adattare i contenuti.*/
    private RecyclerGetter activity;
    /**Listener triggherato quando viene cliccata una recensione nella quale si mostrano informazioni sullo user che lo ha inserita*/
    private UserOnClickListener userClickListener=new UserOnClickListener();
    /**Listener triggherato quando viene cliccata una recensione nella quale di mostrano informazioni sulla struttura recensita.*/
    private PlaceOnClickListener placeOnClickListener=new PlaceOnClickListener();

    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();

    /**FLAG che indicano se devono essere visualizzate le informazioni circa la struttura recensita o l'utente che l'ha inserita.*/
    public static final int FLAG_PLACE=1,FLAG_USER=0;
    /**assume valore dai flag.*/
    private int actualFlag;

    public ReviewsAdapter(Context context, List<Review> list, RecyclerGetter activity, int flag)
    {
        inflater= LayoutInflater.from(context);
        this.reviewsList=list;
        this.context=context;
        this.activity=activity;
        this.actualFlag=flag;
    }

    @NonNull
    @Override
    /**In questo metodo viene anche settato il listener alla view in base all'informazione visualizzata*/
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.review_container,parent, false);

        switch(actualFlag)
        {
            case FLAG_USER:
                view.setOnClickListener(userClickListener);
                break;
            case FLAG_PLACE:
                view.setOnClickListener(placeOnClickListener);
                break;
            default:
                Log.d("switch", "Errore flag in reviewsadapter");
        }
        return new ReviewViewHolder(view);
    }

    @Override
    /**In questo metodo vengono richieste al placeDao o allo userDao le informazioni da visuliazzare, a seconda del valore di actualFlag.*/
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ReviewViewHolder holder = (ReviewViewHolder) h;
        final Review actualReview=reviewsList.get(position);

        holder.reviewRating.setRating(actualReview.getRating());
        holder.reviewText.setText(actualReview.getText());

        switch(actualFlag)
        {
            case FLAG_PLACE:
                placeDao.getPlaceByID(actualReview.getPlaceId(), ReviewsAdapter.this, holder,0);
                break;

            case FLAG_USER:
                userDao.getUserByID(actualReview.getUserId(),this,holder, 0);
                break;
        }

        holder.reviewDate.setText(actualReview.getDate());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, ReviewViewHolder holder, int callbackCode) {
        holder.userName.setText(place.getName());
        Glide.with(context).load(place.getPictures().get(0)).into(holder.userImage);
    }

    @Override
    public void callback(User user, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, ReviewViewHolder holder, int callbackCode) {
        holder.userName.setText(user.getShowingName());
        Glide.with(context)
                .load(user.getAvatar())
                .into(holder.userImage);
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
        Log.d("query",e.getMessage());
        Toast.makeText(context,"Errore. Controllare i log.", Toast.LENGTH_LONG).show();
    }

    /**Holder che mantiene gli elementi grafici relativi ad una recensione.*/
    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView reviewText;
        ImageView userImage;
        TextView userName;
        TextView reviewDate;
        RatingBar reviewRating;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            reviewDate=itemView.findViewById(R.id.reviewDate);
            reviewText=itemView.findViewById(R.id.reviewText);
            userImage=itemView.findViewById(R.id.userRatingImage);
            userImage.setImageResource(R.drawable.default_profile_picture);
            userName=itemView.findViewById(R.id.reviewUsername);
            reviewRating=itemView.findViewById(R.id.reviewRatingBar);
        }
    }

    /**Lister associato al click sulla recensione nel caso essa visualizzi informazioni sull'utente.*/
    private class UserOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View view) {
            int itemPosition = activity.getReviewsRecyclerView().getChildLayoutPosition(view);
            String userID= reviewsList.get(itemPosition).getUserId();

            activity.show(userID, FLAG_USER);
        }
    }
    /**Lister associato al click sulla recensione nel caso essa visualizzi informazioni sul place recensito.*/
    private class PlaceOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int itemPosition = activity.getReviewsRecyclerView().getChildAdapterPosition(v);
            String placeID = reviewsList.get(itemPosition).getPlaceId();
            activity.show(placeID, FLAG_PLACE);
        }
    }

    /**Interfaccia che deve implementare ogni classe che intende adattare ad un proprio RecyclerView le recensioni.*/
    public interface RecyclerGetter
    {
        RecyclerView getReviewsRecyclerView();
        void show(String id, int flag);
    }
}

