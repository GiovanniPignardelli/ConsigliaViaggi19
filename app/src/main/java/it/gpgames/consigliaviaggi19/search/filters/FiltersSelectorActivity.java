package it.gpgames.consigliaviaggi19.search.filters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.search.QueryResultsAdapter;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;

public class FiltersSelectorActivity extends Activity implements FilterTagsAdapter.TagSetter {

    private ArrayList<String> generalTags;
    private ArrayList<String> cuisineTags;
    private ArrayList<String> serviceTags;
    private ArrayList<String> roomTags;
    private ArrayList<String> roomTypeTags;

    public static final int FLAG_SERVICE_TAGS=0;
    public static final int FLAG_CUISINE_TAGS=1;
    public static final int FLAG_ROOM_TAGS=2;
    public static final int FLAG_ROOM_TYPE_TAGS=3;
    public static final int FLAG_GENERAL_TAGS=4;
    
    public static final int FLAG_ANY=0;
    public static final int FLAG_RESTAURANT=1;
    public static final int FLAG_HOTEL=2;
    public static final int FLAG_PLACE=3;

    public static final String PRICE_ONE="€";
    public static final String PRICE_TWO="€€";
    public static final String PRICE_THREE="€€€";

    FilterCallback filterCallback;

    private TextView distanceText,otherTagsText;
    private RatingBar ratingBar;
    private RadioGroup priceRadioGrop;
    private RadioButton oneRadio,twoRadio,threeRadio;
    private Spinner categorySpinner;
    private RecyclerView mainTags,secondaryTags;
    private Button applyButton,cancelButton;
    private SeekBar seekBar;

    private FilterTagsAdapter adapter;

    private HashMap<Integer,ArrayList<String>> actualTags=new HashMap<>();

    private boolean distanceIsSelected=false;
    private int actualDistance;

    private boolean ratingIsSelected=false;
    private float actualRating;

    private boolean priceIsSelected=false;
    private String actualPrice;

    private int actualCategory=FLAG_ANY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_selector);
        ratingBar=findViewById(R.id.filter_ratingbar);
        ratingBar.setAlpha((float)0.5);
        priceRadioGrop=findViewById(R.id.radioGroup);
        priceRadioGrop.setAlpha((float)0.5);
        oneRadio=findViewById(R.id.one_radio);
        twoRadio=findViewById(R.id.two_radio);
        threeRadio=findViewById(R.id.three_radio);
        categorySpinner=findViewById(R.id.filter_category_spinner);
        mainTags=findViewById(R.id.filter_main_recycler);
        secondaryTags=findViewById(R.id.filters_second_recycler);
        applyButton=findViewById(R.id.filter_apply_button);
        cancelButton=findViewById(R.id.filter_cancel_button);
        distanceText=findViewById(R.id.textView24);
        otherTagsText=findViewById(R.id.textView23);
        seekBar=findViewById(R.id.seekBar);
        seekBar.setAlpha((float)0.5);

        generalTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.general_tags)));
        cuisineTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cuisine_tags)));
        serviceTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.service_tags)));
        roomTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_tags)));
        roomTypeTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_type_tags)));

        filterCallback=ResultsActivity.getLastInstance();


        setUpListeners();
        setUpGeneralRecyclerView();
    }

    private void setUpListeners() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cleanTags();
                List<String> mergedLists;
                switch(position)
                {
                    case FLAG_PLACE:
                        secondaryTags.setVisibility(View.INVISIBLE);
                        otherTagsText.setVisibility(View.INVISIBLE);
                        actualCategory=FLAG_PLACE;
                        break;
                    case FLAG_RESTAURANT:
                        mergedLists=new ArrayList<>(serviceTags);
                        mergedLists.addAll(cuisineTags);
                        setUpSecondaryTags(mergedLists);
                        secondaryTags.setVisibility(View.VISIBLE);
                        otherTagsText.setVisibility(View.VISIBLE);
                        actualCategory=FLAG_RESTAURANT;
                        break;
                    case FLAG_HOTEL:
                        mergedLists=new ArrayList<>(roomTags);
                        mergedLists.addAll(roomTypeTags);
                        setUpSecondaryTags(mergedLists);
                        secondaryTags.setVisibility(View.VISIBLE);
                        otherTagsText.setVisibility(View.VISIBLE);
                        actualCategory=FLAG_HOTEL;
                        break;
                    case FLAG_ANY:
                        mergedLists=new ArrayList<>(serviceTags);
                        mergedLists.addAll(cuisineTags);
                        mergedLists.addAll(roomTags);
                        mergedLists.addAll(roomTypeTags);
                        setUpSecondaryTags(mergedLists);
                        secondaryTags.setVisibility(View.VISIBLE);
                        otherTagsText.setVisibility(View.VISIBLE);
                        actualCategory=FLAG_ANY;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                categorySpinner.setSelection(0);
            }

        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!distanceIsSelected)
                {
                    seekBar.setAlpha(1);
                    distanceIsSelected=true;
                }
                actualDistance=progress;
                distanceText.setText(progress+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!ratingIsSelected)
                {
                    ratingBar.setAlpha(1);
                    ratingIsSelected=true;
                }

                actualRating=ratingBar.getRating();
            }
        });

        priceRadioGrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!priceIsSelected)
                {
                    priceRadioGrop.setAlpha(1);
                    priceIsSelected=true;
                }
            }
        });

        oneRadio.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                actualPrice=PRICE_ONE;
            }
        });

        twoRadio.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                actualPrice=PRICE_TWO;
            }
        });

        threeRadio.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                actualPrice=PRICE_THREE;
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                filterCallback.setCategory(actualCategory);
                if(distanceIsSelected)
                    filterCallback.setMaxDistance(actualDistance);
                if(priceIsSelected)
                    filterCallback.setPriceString(actualPrice);
                if(ratingIsSelected)
                    filterCallback.setMinRating((int)actualRating);

                filterCallback.setTags(actualTags);
                filterCallback.refresh();

                if(actualTags.get(FLAG_GENERAL_TAGS)==null)Log.d("TAG__", "null");
                Iterator it = actualTags.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Integer key = (Integer) pair.getKey();
                    ArrayList<String> tags = (ArrayList<String>) pair.getValue();
                    Log.d("TAG_KEY:",key.toString());
                    for(String tag: tags)
                    {
                        Log.d("TAG_VALUE:",tag);
                    }
                }
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cleanTags() {
        actualTags=new HashMap<>();
        for (int childCount = mainTags.getChildCount(), i = 0; i < childCount; ++i) {
            final FilterTagsAdapter.TagViewHolder holder =(FilterTagsAdapter.TagViewHolder) mainTags.getChildViewHolder(mainTags.getChildAt(i));
            holder.box.setChecked(false);
        }

        for (int childCount = secondaryTags.getChildCount(), i = 0; i < childCount; ++i) {
            final FilterTagsAdapter.TagViewHolder holder =(FilterTagsAdapter.TagViewHolder) secondaryTags.getChildViewHolder(secondaryTags.getChildAt(i));
            holder.box.setChecked(false);
        }
    }

    private void setUpSecondaryTags(List<String> mergedLists) {
        adapter=new FilterTagsAdapter(mergedLists,this,this);
        secondaryTags.setAdapter(adapter);
        secondaryTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void setUpGeneralRecyclerView() {
        adapter = new FilterTagsAdapter(generalTags,this,this);
        mainTags.setAdapter(adapter);
        mainTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    public void addTag(String tag) {
        ArrayList<String> toAdd;

        if(generalTags.contains(tag))
        {
            toAdd=actualTags.get(FLAG_GENERAL_TAGS);
            if(toAdd==null)
            {
                toAdd=new ArrayList<String>();
                toAdd.add(tag);
                actualTags.put(FLAG_GENERAL_TAGS,toAdd);
            }
            else
                toAdd.add(tag);
        }

        if(cuisineTags.contains(tag))
        {
            toAdd=actualTags.get(FLAG_CUISINE_TAGS);
            if(toAdd==null)
            {
                toAdd=new ArrayList<String>();
                toAdd.add(tag);
                actualTags.put(FLAG_CUISINE_TAGS,toAdd);
            }
            else
                toAdd.add(tag);
        }

        if(serviceTags.contains(tag))
        {
            toAdd=actualTags.get(FLAG_SERVICE_TAGS);
            if(toAdd==null)
            {
                toAdd=new ArrayList<String>();
                toAdd.add(tag);
                actualTags.put(FLAG_SERVICE_TAGS,toAdd);
            }
            else
                toAdd.add(tag);
        }

        if(roomTags.contains(tag))
        {
            toAdd=actualTags.get(FLAG_ROOM_TAGS);
            if(toAdd==null)
            {
                toAdd=new ArrayList<String>();
                toAdd.add(tag);
                actualTags.put(FLAG_ROOM_TAGS,toAdd);
            }
            else
                toAdd.add(tag);
        }

        if(roomTypeTags.contains(tag))
        {
            toAdd=actualTags.get(FLAG_ROOM_TYPE_TAGS);
            if(toAdd==null)
            {
                toAdd=new ArrayList<String>();
                toAdd.add(tag);
                actualTags.put(FLAG_ROOM_TYPE_TAGS,toAdd);
            }
            else
                toAdd.add(tag);
        }
    }

    @Override
    public void removeTag(String tag) {
        List<String> toRemove;
        if(generalTags.contains(tag))
        {
            toRemove=actualTags.get(FLAG_GENERAL_TAGS);
            if(toRemove==null)toRemove=new ArrayList<String>();
            toRemove.remove(tag);
        }

        if(cuisineTags.contains(tag))
        {
            toRemove=actualTags.get(FLAG_CUISINE_TAGS);
            if(toRemove==null)toRemove=new ArrayList<String>();
            toRemove.remove(tag);
        }

        if(serviceTags.contains(tag))
        {
            toRemove=actualTags.get(FLAG_SERVICE_TAGS);
            if(toRemove==null)toRemove=new ArrayList<String>();
            toRemove.remove(tag);
        }

        if(roomTags.contains(tag))
        {
            toRemove=actualTags.get(FLAG_ROOM_TAGS);
            if(toRemove==null)toRemove=new ArrayList<String>();
            toRemove.remove(tag);
        }

        if(roomTypeTags.contains(tag))
        {
            toRemove=actualTags.get(FLAG_ROOM_TYPE_TAGS);
            if(toRemove==null)toRemove=new ArrayList<String>();
            toRemove.remove(tag);
        }
    }

    public interface FilterCallback
    {
        void setCategory(int categoryFlag);
        void setMinRating(int rating);
        void setPriceString(String price);
        void setTags(HashMap<Integer,ArrayList<String>> tags);
        void setMaxDistance(int km);
        void refresh();
    }
}
