package it.gpgames.consigliaviaggi19.search.place_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.Edits;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.places.Hotel;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.QueryResultsAdapter;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;

public class PlaceDetailsActivity extends AppCompatActivity {

    private Place toShow;
    private TextView title, serviceTags, price, location, since, cuisineTags;
    private ImageView image,back;
    private PlaceInformationAdapter adapter;
    private RecyclerView information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        toShow=(Place)getIntent().getSerializableExtra("toShow");
        title=findViewById(R.id.title);
        image=findViewById(R.id.placeImage);
        information=findViewById(R.id.recyclerInfoView);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    private void init()
    {
        title.setText(toShow.getName());
        List<Pair<Integer,String>> info=new ArrayList<>();
        info.add(new Pair<Integer, String>(PlaceInformationAdapter.POINTER_ID, toShow.getAddress()+", "+toShow.getCity()+", "+toShow.getPostal_code()+", "+toShow.getState()));
        info.add(new Pair<Integer, String>(PlaceInformationAdapter.TAGS_ID, makeString(toShow.getTags())));
        if(toShow.getTelephone()!=null && !(toShow.getTelephone().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.PHONE_ID, toShow.getTelephone()));
        if(toShow.getEmail()!=null && !(toShow.getEmail().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.EMAIL_ID, toShow.getEmail()));
        if(toShow.getWebsite()!=null && !(toShow.getWebsite().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.WEB_ID,toShow.getWebsite()));


        if(toShow.getCategory().equals(Place.CATEGORY_RESTAURANT))
        {
            Restaurant toShowR=(Restaurant)toShow;
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.FOOD_ID, makeString(toShowR.getCuisineTags())));
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.SERVICE_ID, makeString(toShowR.getServiceTags())));
        }
        else if(toShow.getCategory().equals(Place.CATEGORY_HOTEL))
        {
            Hotel toShowH=(Hotel)toShow;
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.ROOM_ID, makeString(toShowH.getRoomTags())));
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.ROOMTYPE_ID, makeString(toShowH.getRoomTypeTags())));
        }

        if(toShow.getPriceTag()!=null && !(toShow.getPriceTag().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.EURO_ID, toShow.getPriceTag()));
        if(toShow.getAddYear()!=null && !(toShow.getAddYear().equals("")))
            info.add(new Pair<Integer, String>(PlaceInformationAdapter.CLOCK_ID, toShow.getAddYear()));

        adapter = new PlaceInformationAdapter(PlaceDetailsActivity.this, info);
        information.setAdapter(adapter);
        information.setLayoutManager(new LinearLayoutManager(PlaceDetailsActivity.this, RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                RecyclerView.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);
    }

    /**Riceve in input un array di stringhe, e genera una stringa composta dalle stesse parole dell'array, ma splittate con ", "*/
    private String makeString(List<String> tags) {
        String result=new String();
        for(String s: tags)
        {
            result=result.concat(s+", ");
        }

        return result.substring(0,result.length()-2);
    }
}
