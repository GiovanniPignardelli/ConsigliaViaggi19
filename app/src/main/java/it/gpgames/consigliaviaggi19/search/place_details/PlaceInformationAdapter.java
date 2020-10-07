package it.gpgames.consigliaviaggi19.search.place_details;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;

public class PlaceInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CLOCK_ID=R.drawable.clock;
    public static final int EURO_ID=R.drawable.euro;
    public static final int POINTER_ID=R.drawable.pointer;
    public static final int SERVICE_ID=R.drawable.waiter;
    public static final int PHONE_ID=R.drawable.phone;
    public static final int EMAIL_ID=R.drawable.email;
    public static final int WEB_ID=R.drawable.web;
    public static final int ROOMTYPE_ID=R.drawable.bed;
    public static final int ROOM_ID=R.drawable.room;
    public static final int TAGS_ID=R.drawable.building;
    public static final int FOOD_ID=R.drawable.food;

    private LayoutInflater inflater;
    List<Pair<Integer, String>> information;

    public PlaceInformationAdapter(Context context, List<Pair<Integer, String>> list)
    {
        inflater= LayoutInflater.from(context);
        this.information=list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.info_row,parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        InfoViewHolder holder = (InfoViewHolder) h;
        Log.d("adapt",information.get(position).first+" - "+information.get(position).second );
        holder.image.setImageResource(information.get(position).first);
        holder.info.setText(information.get(position).second);
    }

    @Override
    public int getItemCount() {
        return information.size();
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView info;
        private ImageView image;


        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            info=itemView.findViewById(R.id.information);
            image=itemView.findViewById(R.id.info_image);
        }
    }
}
