package it.gpgames.consigliaviaggi19.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItemsGetter;
import it.gpgames.consigliaviaggi19.places.Place;

/** La classe si occupa di adattare i contenuti dei places models ai layout*/
public class QueryResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Place> placesList;
    private LayoutInflater inflater;
    private Place holdingPlace;
    private ResultsActivity activity;
    private ListItemOnClickListener listener = new ListItemOnClickListener();


    public QueryResultsAdapter(Context context, List<Place> list, ResultsActivity activity) {
        this.placesList=list;
        this.activity=activity;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.place_result_container,parent, false);
        view.setOnClickListener(listener);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ResultsViewHolder holder = (ResultsViewHolder) h;
        holdingPlace=placesList.get(position);

        holder.title.setText(holdingPlace.getName());
        holder.nReviews.setText(holdingPlace.getnReviews().toString());
        holder.rating.setRating(holdingPlace.getAvgReview());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("Places/Pictures/"+holdingPlace.getDbDocID()+"/main.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap= HomeSliderItemsGetter.getBitmapFromURL( uri.toString() );
                        updateQueryImage(bitmap, holder);
                    }
                }).start();
            }
        });

        holder.location.setText(holdingPlace.getAddress()+", "+holdingPlace.getCity()+", "+holdingPlace.getState());

    }

    private void updateQueryImage(final Bitmap bitmap, final ResultsViewHolder holder)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.image.setImageBitmap(bitmap);
            }
        });
    }


    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public class ResultsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView title;
        TextView location;
        TextView nReviews;
        ImageView image;
        RatingBar rating;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            title=itemView.findViewById(R.id.Title);
            location=itemView.findViewById(R.id.addressView);
            nReviews=itemView.findViewById(R.id.numRew);
            image=itemView.findViewById(R.id.placeImage);
            rating=itemView.findViewById(R.id.ratingView);
        }
    }

    private class ListItemOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(final View view) {
            int itemPosition = activity.getRecyclerView().getChildLayoutPosition(view);
            Place toShow= placesList.get(itemPosition);
            ResultsActivity.showDetails(toShow);
        }
    }
}
