package it.gpgames.consigliaviaggi19.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.gpgames.consigliaviaggi19.R;

/**Activity che viene visualizzata quando non è stata riscontrata una connessione di rete dal NetworkChangeReceiver
 * @see NetworkChangeReceiver*/
public class NoConnectionActivity extends AppCompatActivity {

    Button bRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        bRetry=findViewById(R.id.retry_button);

        bRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkChangeReceiver.isConnected(getApplicationContext()))
                    finish();
                else
                    Toast.makeText(getApplicationContext(),"Non riesco a stabilire una connessione",Toast.LENGTH_LONG).show();
            }
        });

    }
}
