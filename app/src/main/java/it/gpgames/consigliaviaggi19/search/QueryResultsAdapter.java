package it.gpgames.consigliaviaggi19.search;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItemsGetter;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;

/** La classe si occupa di adattare i contenuti dei places models al RecyclerView nella ResultsActivity*/
public class QueryResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Place> placesList;
    private LayoutInflater inflater;
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
        Place holdingPlace = placesList.get(position);

        holder.title.setText(holdingPlace.getName());
        holder.nReviews.setText(holdingPlace.getnReviews().toString());
        holder.rating.setRating(holdingPlace.getAvgReview());
        if(holdingPlace.getPictures()!=null && holdingPlace.getPictures().get(0)!=null)
        {
            String picUri= holdingPlace.getPictures().get(0);
            Glide.with(holder.itemView.getContext()).load(picUri).into(holder.image);
        }
        holder.location.setText(holdingPlace.getAddress()+", "+ holdingPlace.getCity()+", "+ holdingPlace.getState());

    }

    @Override
    public int getItemCount() {
        if(placesList !=null) return placesList.size();
        return 0;
    }

    public class ResultsViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView location;
        private TextView nReviews;
        private ImageView image;
        private RatingBar rating;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.Title);
            location=itemView.findViewById(R.id.addressView);
            nReviews=itemView.findViewById(R.id.numRew);
            image=itemView.findViewById(R.id.placeImage);
            rating=itemView.findViewById(R.id.ratingView);
        }
    }

    /**Listener attivato al click sulla view di un ResultsViewHolder. Richiama il metodo showDetails della classe ResultsActivity con il luogo da mostrare..*/
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
