package it.gpgames.consigliaviaggi19.home.slider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.home.MainActivity;

public class SliderService extends JobIntentService {

    public final static String MY_ACTION = "MY_ACTION";
    final Handler mHandler = new Handler();
    private static final String TAG = "SliderService";
    private static final int JOB_ID = 2;
    List<SliderItem> SliderItemToShow = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, SliderService.class, JOB_ID, intent);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /* Scarica tutte le immagini dello slider dal DB */
        final DatabaseReference sliderImgsRef = dbRef.child("home").child("slider");
        sliderImgsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer index = new Integer(1);
                while (dataSnapshot.hasChild(index.toString())) {
                    DataSnapshot currentRef = dataSnapshot.child(index.toString());
                    SliderItemToShow.add(new SliderItem(
                            getBitmapFromURL(currentRef.child("img").getValue().toString()),
                            currentRef.child("keyword").getValue().toString()
                    ));
                    index++;

                    Intent toMainActivity = new Intent();
                    toMainActivity.setAction(MY_ACTION);
                    sendBroadcast(toMainActivity);
                    Bundle sliderItemBundle = new Bundle();
                    sliderItemBundle.putParcelable("SliderItemsList", (Parcelable) SliderItemToShow);
                    toMainActivity.putExtra("SliderItemsList", sliderItemBundle);
                    sendBroadcast(toMainActivity);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /** Ottiene un Bitmap da un URL. */
    public static Bitmap getBitmapFromURL(String inUrl) {
        try {
            URL url = new URL(inUrl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch(IOException e) {
            System.out.println(e);
        }
        return null;
    }
}


