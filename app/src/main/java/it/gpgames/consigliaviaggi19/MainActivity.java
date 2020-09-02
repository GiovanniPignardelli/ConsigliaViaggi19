package it.gpgames.consigliaviaggi19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ImageView main_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_image=findViewById(R.id.main_image);

        Thread clock_thread=new Thread(){
            @Override
            public void run() {
                super.run();

                while(true)
                {
                    try {
                        Thread.sleep(5000);
                        if(main_image.getId()== android.content.res.)
                        {
                            main_image.setImageResource(R.drawable.berlino);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}
