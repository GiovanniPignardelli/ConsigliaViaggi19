package it.gpgames.consigliaviaggi19.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.transition.Slide;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.SliderItem;
import it.gpgames.consigliaviaggi19.home.slider.SliderRunnable;
import it.gpgames.consigliaviaggi19.home.slider.SliderService;

public class MainActivity extends AppCompatActivity {
    public ImageView main_image;
    List<SliderItem> SliderItemToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_image=findViewById(R.id.main_image);
        Runnable sliderRunnable= new SliderRunnable();
        ExecutorService myExecutor= Executors.newSingleThreadExecutor();
        myExecutor.execute(sliderRunnable);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


}
