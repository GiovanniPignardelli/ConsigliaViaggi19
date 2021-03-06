package it.gpgames.consigliaviaggi19.home.slider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** La classe HomeSliderItemsGetter inizializza lo slider di immagini dell'activity_main.xml.
 * Genera la lista di HomeSliderItem scaricando i dati dal FirebaseDatabase, per poi avviare
 * lo HomeSliderAdapter.
 */
public class HomeSliderItemsGetter implements Runnable {

    List<HomeSliderItem> SliderItemToShow = new ArrayList<>();
    Handler toMainActivityHandler;
    DataSnapshot resultDB;

    public HomeSliderItemsGetter(Handler handler, DataSnapshot ds){
        toMainActivityHandler = handler;
        resultDB = ds;
    }

    @Override
    public void run() {
        Integer index = new Integer(1);
        //Bitmap image = Bitmap.createBitmap(1000, 1000,Bitmap.Config.ARGB_8888); // Generic bitmap.
        while (resultDB.hasChild(index.toString())) {
            DataSnapshot currentRef = resultDB.child(index.toString());
            SliderItemToShow.add(new HomeSliderItem(
                    getBitmapFromURL(currentRef.child("img").getValue().toString()),
                    currentRef.child("keyword").getValue().toString(),
                    currentRef.child("desc").getValue().toString(),
                    index-1,
                    currentRef.child("location_type").getValue().toString()
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
    public static Bitmap getBitmapFromURL(String inUrl) {
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
