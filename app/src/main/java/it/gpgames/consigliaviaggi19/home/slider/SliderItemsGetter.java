package it.gpgames.consigliaviaggi19.home.slider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Ha il compito caricare le immagini dello slider dal database*/
public class SliderItemsGetter implements Runnable {

    List<SliderItem> SliderItemToShow = new ArrayList<>();
    Handler toMainActivityHandler;
    DataSnapshot resultDB;

    public SliderItemsGetter(Handler handler, DataSnapshot ds){
        toMainActivityHandler = handler;
        resultDB = ds;
    }

    @Override
    public void run() {
        Integer index = new Integer(1);
        Bitmap image = Bitmap.createBitmap(1000, 1000,Bitmap.Config.ARGB_8888); // Generic bitmap.
        while (resultDB.hasChild(index.toString())) {
            DataSnapshot currentRef = resultDB.child(index.toString());
            SliderItemToShow.add(new SliderItem(
                    getBitmapFromURL(currentRef.child("img").getValue().toString()),
                    currentRef.child("keyword").getValue().toString()
            ));
            index++;
        }

        // Invia la lista ottenuta alla MainActivity.
        Message msg = toMainActivityHandler.obtainMessage();
        Bundle toMainActivityBundle = new Bundle();
        toMainActivityBundle.putParcelableArrayList("SliderItemList", (ArrayList<? extends Parcelable>) SliderItemToShow);
        msg.setData(toMainActivityBundle);
        toMainActivityHandler.sendMessage(msg);
    }

    /** Ottiene un oggetto Bitmap da una stringa contenente URL. */
    public Bitmap getBitmapFromURL(String inUrl) {
        try {
            Log.d("pic", inUrl);
            URL url = new URL(inUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        return null;
    }
}
