package it.gpgames.consigliaviaggi19.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Place;

public class QueryResultsAdapter extends FirestoreRecyclerAdapter<Place, QueryResultsAdapter.ResultsViewHolder> {

    public QueryResultsAdapter(@NonNull FirestoreRecyclerOptions<Place> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ResultsViewHolder holder, int position, @NonNull Place model) {
        holder.title.setText(model.getName());
        holder.location.setText(model.getAddress()+", "+model.getCity()+", "+model.getState());
        //TO IMPLEMENT holder.rating, holder.nReviews, holder.image


    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.place_result_container, parent, false);
        return new ResultsViewHolder(view);
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
            title=itemView.findViewById(R.id.nameView);
            location=itemView.findViewById(R.id.addressView);
            nReviews=itemView.findViewById(R.id.textView10);
            image=itemView.findViewById(R.id.imageView2);
            rating=itemView.findViewById(R.id.ratingView);
        }
    }
}
