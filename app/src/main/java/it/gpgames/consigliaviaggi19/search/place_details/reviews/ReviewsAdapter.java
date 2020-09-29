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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.DAO.places.Place;
import it.gpgames.consigliaviaggi19.DAO.places.Review;
import it.gpgames.consigliaviaggi19.DAO.users.UserData;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private List<Review> reviewsList;
    private Context context;
    private RecyclerGetter activity;
    private UserOnClickListener userClickListener=new UserOnClickListener();
    private PlaceOnClickListener placeOnClickListener=new PlaceOnClickListener();

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
                FirebaseFirestore.getInstance().collection("places").document(actualReview.getPlaceId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            holder.userName.setText(task.getResult().toObject(Place.class).getName());
                        }
                    }
                });

                FirebaseStorage.getInstance().getReference().child("Places").child("Pictures").child(actualReview.getPlaceId()).child("main.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri!=null)
                            Glide.with(context).load(uri).into(holder.userImage);
                    }
                });
                break;

            case FLAG_USER:
                FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID",actualReview.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {

                            holder.userName.setText(task.getResult().toObjects(UserData.class).get(0).getDisplayName());
                        }
                    }
                });

                FirebaseStorage.getInstance().getReference().child("Users/Avatars/avatar_"+actualReview.getUserId()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(uri!=null)
                            Glide.with(context)
                                    .load(uri)
                                    .into(holder.userImage);
                    }
                });

                break;
        }

        holder.reviewDate.setText(actualReview.getDate());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
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
            String userUid= reviewsList.get(itemPosition).getUserId();
            activity.show(userUid);
        }
    }

    private class PlaceOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            int itemPosition = activity.getReviewsRecyclerView().getChildAdapterPosition(v);
            String placeID = reviewsList.get(itemPosition).getPlaceId();
            activity.show(placeID);
        }
    }

    public interface RecyclerGetter
    {
        public RecyclerView getReviewsRecyclerView();
        public void show(String id);
    }
}

