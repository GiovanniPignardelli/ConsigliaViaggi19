package it.gpgames.consigliaviaggi19.search;

import android.content.Context;
import android.util.Log;
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

import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Place;

public class QueryResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Place> placesList;
    private LayoutInflater inflater;
    private Place holdingPlace;

    public QueryResultsAdapter(Context context, List<Place> list) {
        this.context=context;
        this.placesList=list;
        inflater= LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.place_result_container,parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        ResultsViewHolder holder= (ResultsViewHolder)h;
        holdingPlace=placesList.get(position);

        holder.title.setText(holdingPlace.getName());
        //holder.rating.setNumStars(holdingPlace.get);
        holder.location.setText(holdingPlace.getAddress()+", "+holdingPlace.getCity()+", "+holdingPlace.getState());
    }

    @Override
    public int getItemCount() {
        Log.d("query","Ci sono "+placesList.size()+" elementi");
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
            title=itemView.findViewById(R.id.nameView);
            location=itemView.findViewById(R.id.addressView);
            nReviews=itemView.findViewById(R.id.textView10);
            image=itemView.findViewById(R.id.imageView2);
            rating=itemView.findViewById(R.id.ratingView);
        }
    }
}
