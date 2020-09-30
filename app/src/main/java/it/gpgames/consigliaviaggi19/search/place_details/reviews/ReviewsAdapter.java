package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DatabaseCallback {

    private LayoutInflater inflater;
    private List<Review> reviewsList;
    private Context context;
    private RecyclerGetter activity;
    private UserOnClickListener userClickListener=new UserOnClickListener();
    private PlaceOnClickListener placeOnClickListener=new PlaceOnClickListener();
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();

    public static final int FLAG_PLACE=1,FLAG_USER=0;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ReviewViewHolder holder = (ReviewViewHolder) h;
        final Review actualReview=reviewsList.get(position);

        holder.reviewRating.setRating(actualReview.getRating());
        holder.reviewText.setText(actualReview.getText());

        switch(actualFlag)
        {
            case FLAG_PLACE:
                placeDao.getPlaceByID(actualReview.getPlaceId(), ReviewsAdapter.this, holder,0);

                FirebaseStorage.getInstance().getReference().child("Places").child("Pictures").child(actualReview.getPlaceId()).child("main.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri!=null)
                            Glide.with(context).load(uri).into(holder.userImage);
                    }
                });
                break;

            case FLAG_USER:
                userDao.getUserByID(actualReview.getUserId(),this,holder, 0);
        }

        holder.reviewDate.setText(actualReview.getDate());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public void callback(int callbackCode) {

    }

    @Override
    public void callback(Place place, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, ReviewViewHolder holder, int callbackCode) {
        holder.userName.setText(place.getName());
    }

    @Override
    public void callback(User user, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, ReviewViewHolder holder, int callbackCode) {
        holder.userName.setText(user.getDisplayName());
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
    public void callback(String message, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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

    private class UserOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View view) {
            int itemPosition = activity.getReviewsRecyclerView().getChildLayoutPosition(view);
            String userID= reviewsList.get(itemPosition).getUserId();

            activity.show(userID, FLAG_USER);
        }
    }

    private class PlaceOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int itemPosition = activity.getReviewsRecyclerView().getChildAdapterPosition(v);
            String placeID = reviewsList.get(itemPosition).getPlaceId();
            Log.d("click","Sto per mostrare una struttura");
            activity.show(placeID, FLAG_PLACE);
        }
    }

    public interface RecyclerGetter
    {
        RecyclerView getReviewsRecyclerView();
        void show(String id, int flag);
    }
}

