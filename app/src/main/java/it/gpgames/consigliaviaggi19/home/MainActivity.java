package it.gpgames.consigliaviaggi19.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.Login;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.userpanel.UserData;
import it.gpgames.consigliaviaggi19.userpanel.UserPanelActivity;
import it.gpgames.consigliaviaggi19.home.slider.SliderAdapter;
import it.gpgames.consigliaviaggi19.home.slider.SliderItem;
import it.gpgames.consigliaviaggi19.home.slider.SliderItemsGetter;

public class MainActivity extends AppCompatActivity {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    UserData userData = UserData.getUserInstance();

    private ViewPager2 viewPager;
    static List<SliderItem> SliderItemToShow = new ArrayList<>();

    private ImageView bUserPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.sliderImage);
        bUserPanel=findViewById(R.id.user);
        checkIfTokenHasExpired();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserData();
    }

    /**Controlla se il token d'accesso è scaduto. In tal caso è necessario ri-effettuare l'accesso.*/
    private void checkIfTokenHasExpired(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent accessNeeded = new Intent(MainActivity.this, Login.class);
            startActivity(accessNeeded);
        }
    }

    /**Inizializza tutte le componenti della activity_main.xml*/
    private void init()
    {
        initSlider();
        initListeners();
    }

    /**Inizializza localmente i dati utente (classe UserData).*/
    private void initUserData()
    {
        userData.downloadUserDataFromFirebase();
    }

    /**Inizializza i listener della activity_main.xml:
     * - OnClickListener(bUserPanel): button per aprire l'UserPanelActivity;
     * - AuthStateListener(FirebaseAuth.getInstance()): gestisce la scadenza del token di accesso.*/
    private void initListeners()
    {
        //Inizializzazione listener dello user_button, per accede al pannello di controllo dell'utente
        bUserPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUserPanel = new Intent(MainActivity.this, UserPanelActivity.class);
                Bundle userDataBundle = new Bundle();
                userDataBundle.putParcelable("UserData", userData);
                toUserPanel.putExtras(userDataBundle);
                startActivity(toUserPanel);
            }
        });

        // Inizializzazione listener sullo stato di autenticazione. In caso di scadenza token, il listener si attiva.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null)
                {
                    userData.cleanLocalUserData();
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        });
    }

    /** Inizializza lo slider di immagini nell'activity_main.xml. Nota: vedere SliderItemsGetter e SliderAdapter. */
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
