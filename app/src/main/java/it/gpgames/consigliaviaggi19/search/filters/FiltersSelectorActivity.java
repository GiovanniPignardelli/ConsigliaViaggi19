package it.gpgames.consigliaviaggi19.search.filters;

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
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.search.ResultsActivity;

/**Activity che permette di selezionare vari filtri di ricerca prima di effettaure una query.
 * Contiene vari FLAG utili a rappresentare queste informazioni.
 * Implementa l'interfaccia TagSetter presente nel FilterTagsAdapter perchè quest'ulimo adatta ai vari RecyclerView i tag in base alla categoria selezionata. I listener di questi tag operano proprio su un riferimento ad un interfaccia di questo tipo*/
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

    /**riferimento alla classe che aspetta riscontro dei filtri selezionati*/
    private FilterCallback filterCallback;

    private TextView distanceText,otherTagsText;
    private RatingBar ratingBar;
    private RadioGroup priceRadioGrop;
    private RadioButton oneRadio,twoRadio,threeRadio;
    private Spinner categorySpinner;
    private RecyclerView mainTags,secondaryTags;
    private Button applyButton,cancelButton;

    private FilterTagsAdapter adapter;

    /**Tag attualmente selezinati. Le chiavi assumo valori dai FLAG_..._TAGS, mentre i valori da una delle liste di tag inizializzate
     * nel metodo onCreate()*/
    private HashMap<Integer,ArrayList<String>> actualTags=new HashMap<>();

    private boolean ratingIsSelected=false;
    private float actualRating;

    private boolean priceIsSelected=false;
    private String actualPrice;

    private int actualCategory=FLAG_ANY;

    @Override
    /**Inizializza i componenti gli ArrayList dei TAG dalle risorse dell'applicazione*/
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

        generalTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.general_tags)));
        cuisineTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cuisine_tags)));
        serviceTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.service_tags)));
        roomTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_tags)));
        roomTypeTags=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_type_tags)));

        filterCallback=ResultsActivity.getLastFilterInstance();


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
                        secondaryTags.setVisibility(View.INVISIBLE);
                        otherTagsText.setVisibility(View.INVISIBLE);
                        actualCategory=FLAG_ANY;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                categorySpinner.setSelection(0);
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
                if(priceIsSelected)
                    filterCallback.setPriceString(actualPrice);
                if(ratingIsSelected)
                    filterCallback.setMinRating((int)actualRating);

                filterCallback.setTags(actualTags);
                filterCallback.refreshFilter();

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

    /**Resetta tutti i tag.*/
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

    /**Inizializza il RecyclerView relativo ai tag non primari.*/
    private void setUpSecondaryTags(List<String> mergedLists) {
        adapter=new FilterTagsAdapter(mergedLists,this,this);
        secondaryTags.setAdapter(adapter);
        secondaryTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    /**Inizializza il RecyclerView dei tag generali.*/
    private void setUpGeneralRecyclerView() {
        adapter = new FilterTagsAdapter(generalTags,this,this);
        mainTags.setAdapter(adapter);
        mainTags.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    /**Aggiunge un tag alla lista di tag attuali. Se la lista non è ancora stata creata, la inizializza.*/
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
    /**Rimuove un tag dalla lista di tag attuali.*/
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

    /**Interfaccia che deve essere implementata da ogni classe che attende riscontro sulla selezione dei tag.*/
    public interface FilterCallback
    {
        void setCategory(int categoryFlag);
        void setMinRating(int rating);
        void setPriceString(String price);
        void setTags(HashMap<Integer,ArrayList<String>> tags);
        void refreshFilter();
    }
}
