package it.gpgames.consigliaviaggi19.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.R;

/**Activity che permette di selezionare un raggio d'azione nel quale visualizzare i risultati*/
public class DistanceRadiusSliderActivity extends Activity {

    /**Intefaccia implementata da una classe che desidera ricevere callback sul raggio selezionato*/
    public interface RadiusSliderCallback{
        void receiveInputData(float value);
    }

    private SeekBar seekBar;
    private TextView tDistance;
    private boolean distanceIsSelected=false;
    private Button bBack;
    private Button bAccept;

    /**Classe che aspetta il risultato della selezione*/
    private RadiusSliderCallback activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_radius_slider);
        seekBar = findViewById(R.id.seekBar);
        tDistance = findViewById(R.id.textView24);
        bBack = findViewById(R.id.cancelButton);
        bAccept = findViewById(R.id.acceptButton);
        activity = MainActivity.getLastInstance();
        initListeners();
    }

    private void initListeners(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!distanceIsSelected)
                {
                    seekBar.setAlpha(1);
                    distanceIsSelected=true;
                }
                tDistance.setText(seekBar.getProgress()+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                activity.receiveInputData(seekBar.getProgress());
            }
        });


    }

}