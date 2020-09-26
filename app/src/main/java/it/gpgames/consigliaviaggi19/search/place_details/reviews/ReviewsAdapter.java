package it.gpgames.consigliaviaggi19.search.place_details.reviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Review;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;
import it.gpgames.consigliaviaggi19.search.place_details.PlaceDetailsActivity;
import it.gpgames.consigliaviaggi19.userpanel.UserData;
import it.gpgames.consigliaviaggi19.userpanel.UserPanelActivity;

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    List<Review> reviewsList;
    Context context;
    private PlaceDetailsActivity activity;
    UserOnClickListener listener=new UserOnClickListener();

    public ReviewsAdapter(Context context, List<Review> list, PlaceDetailsActivity activity)
    {
        inflater= LayoutInflater.from(context);
        this.reviewsList=list;
        this.context=context;
        this.activity=activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.review_container,parent, false);
        view.setOnClickListener(listener);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ReviewViewHolder holder = (ReviewViewHolder) h;
        Review actualReview=reviewsList.get(position);

        holder.reviewRating.setRating(actualReview.getRating());
        holder.reviewText.setText(actualReview.getText());

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
            activity.showUser(userUid);
        }
    }
}

