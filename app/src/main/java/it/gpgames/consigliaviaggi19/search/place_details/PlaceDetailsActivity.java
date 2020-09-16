package it.gpgames.consigliaviaggi19.search.place_details;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.Edits;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Iterator;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;

public class PlaceDetailsActivity extends AppCompatActivity {

    private Place toShow;
    private TextView title, serviceTags, price, location, since, cuisineTags;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        toShow=(Place)getIntent().getSerializableExtra("toShow");
        title=findViewById(R.id.title);
        serviceTags=findViewById(R.id.serviceText);
        price=findViewById(R.id.priceText);
        location=findViewById(R.id.locationText);
        since=findViewById(R.id.timeText2);
        cuisineTags=findViewById(R.id.foodText);
        image=findViewById(R.id.placeImage);
        init();
    }

    private void init()
    {
        title.setText(toShow.getName());
        Log.d("search",toShow.getClass().toString()+" - "+Restaurant.class);
        if(toShow.getCategory().equals(Restaurant.CATEGORY_RESTAURANT) )
        {
            Log.d("search", "E' un ristorante");
            Restaurant toShowR=(Restaurant) toShow;
            cuisineTags.setText("");

            Iterator<String> iterator=toShowR.getCuisineTags().iterator();
            String tag;
            while(iterator.hasNext())
            {
                tag=iterator.next();
                cuisineTags.setText(cuisineTags.getText().toString()+tag);
                if(iterator.hasNext())cuisineTags.setText(cuisineTags.getText()+", ");
            }

            serviceTags.setText("");
            iterator=toShowR.getServiceTags().iterator();
            while(iterator.hasNext())
            {
                tag=iterator.next();
                serviceTags.setText(serviceTags.getText().toString()+tag);
                if(iterator.hasNext())serviceTags.setText(serviceTags.getText()+", ");
            }


        }
        else
        {
            Log.d("search", "Non è un ristorante");

        }


        price.setText(toShow.getPriceTag());

        location.setText(toShow.getAddress()+", "+toShow.getCity()+", "+toShow.getState());

        since.setText(toShow.getAddYear());

        //image.setImageBitmap(...);


    }
}
