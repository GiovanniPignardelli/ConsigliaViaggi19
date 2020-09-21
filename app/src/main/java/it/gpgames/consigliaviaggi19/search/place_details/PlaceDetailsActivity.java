package it.gpgames.consigliaviaggi19.search.place_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderAdapter;
import it.gpgames.consigliaviaggi19.places.Hotel;
import it.gpgames.consigliaviaggi19.places.Place;
import it.gpgames.consigliaviaggi19.places.Restaurant;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.WriteReviewActivity;
import it.gpgames.consigliaviaggi19.search.place_details.slider.PlaceSliderAdapter;

public class PlaceDetailsActivity extends AppCompatActivity {

    private Place toShow;
    private TextView title;
    private ImageView back;
    private PlaceInformationAdapter placeInformationAdapter;
    private PlaceSliderAdapter sliderAdapter;
    private SliderView slider;
    private RecyclerView information;
    private Button bWriteReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        slider=findViewById(R.id.PlaceImagesSlider);
        toShow=(Place)getIntent().getSerializableExtra("toShow");
        title=findViewById(R.id.title);
        information=findViewById(R.id.recyclerInfoView);
        back=findViewById(R.id.back);
        bWriteReview=findViewById(R.id.writeReviewButton);

        init();
    }

    private void initListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PlaceDetailsActivity.this, WriteReviewActivity.class);
                i.putExtra("name", toShow.getName());
                i.putExtra("id", toShow.getDbDocID());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }



    private void init()
    {
        initListeners();
        initPlaceSlider();

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

        placeInformationAdapter = new PlaceInformationAdapter(PlaceDetailsActivity.this, info);
        information.setAdapter(placeInformationAdapter);
        information.setLayoutManager(new LinearLayoutManager(PlaceDetailsActivity.this, RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(information.getContext(),
                RecyclerView.VERTICAL);
        information.addItemDecoration(dividerItemDecoration);
    }

    private void initPlaceSlider() {
        final List<String> urls=new ArrayList<String>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("Places/Pictures/" + toShow.getDbDocID()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> list=listResult.getItems();
                final Integer itemsToGet = list.size();
                for(StorageReference ref: list)
                {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urls.add(uri.toString());
                            if(urls.size() == itemsToGet){
                                startSliderAdapter(urls);
                            }
                        }
                    });
                }
            }
        });
    }

    private void startSliderAdapter(List<String> urls) {
        sliderAdapter=new PlaceSliderAdapter(getApplicationContext(),urls, toShow.getDbDocID(), PlaceDetailsActivity.this);
        //sliderAdapter.notifyDataSetChanged();
        slider.setSliderAdapter(sliderAdapter);
        slider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        slider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        slider.setIndicatorSelectedColor(Color.WHITE);
        slider.setIndicatorUnselectedColor(Color.GRAY);
        slider.setScrollTimeInSec(4);
        slider.startAutoCycle();
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
