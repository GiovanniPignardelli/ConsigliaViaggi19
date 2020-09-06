package it.gpgames.consigliaviaggi19.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.SliderAdapter;
import it.gpgames.consigliaviaggi19.home.slider.SliderItem;
import it.gpgames.consigliaviaggi19.home.slider.SliderItemsGetter;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    static List<SliderItem> SliderItemToShow = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.sliderImage);
        initSlider();
    }

    /** Scarica gli SliderItem dal FirebaseDatabase ed avvia la ViewPage. */
    void initSlider(){
        final DatabaseReference sliderImgsRef = dbRef.child("home").child("slider");
        sliderImgsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HandlerThread ht = new HandlerThread("SliderItemsGetterThread");
                ht.start();
                Handler sliderInitializator = new Handler(ht.getLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();
                        if(bundle.containsKey("SliderItemList")) {
                            SliderItemToShow =  bundle.getParcelableArrayList("SliderItemList");
                            Handler uiThread = new Handler(Looper.getMainLooper());
                            uiThread.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewPager.setAdapter(new SliderAdapter(SliderItemToShow,viewPager));
                                }
                            });
                        }
                    }
                };
                Runnable sliderRunnable= new SliderItemsGetter(sliderInitializator,dataSnapshot);
                sliderInitializator.post(sliderRunnable);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

}
