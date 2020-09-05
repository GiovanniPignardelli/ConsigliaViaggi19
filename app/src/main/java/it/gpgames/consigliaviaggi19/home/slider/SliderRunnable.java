package it.gpgames.consigliaviaggi19.home.slider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Ha il compito caricare le immagini dello slider dal database*/
public class SliderRunnable implements Runnable {

    List<SliderItem> SliderItemToShow = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public void run() {
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
