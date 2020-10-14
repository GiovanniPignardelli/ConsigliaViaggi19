package it.gpgames.consigliaviaggi19.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.LoginFirebaseDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

/**Un'activity popup per l'inserimento del requestCode dal Backoffice, così da consentire l'accesso tramite 2FA.*/
public class BackofficeHandshakeActivity extends Activity implements DatabaseCallback {

    private EditText eHandshakeCode;
    private Button bAccept;
    private Button bCancel;
    private HandshakeResponse handshakeResponse = MainActivity.getHandshakeResponse();
    private LoginDAO loginDao = DAOFactory.getDAOInstance().getLoginDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backoffice_handshake);
        eHandshakeCode = findViewById(R.id.handshakeCode);
        bAccept = findViewById(R.id.acceptHButton);
        bCancel = findViewById(R.id.cancelHButton);
        initListeners();
    }

    private void initListeners(){
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handshakeResponse.setResponseCode(eHandshakeCode.getText().toString());
                loginDao.sendHandshakeResponse(handshakeResponse,BackofficeHandshakeActivity.this,0);
            }
        });
    }

    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(int callbackCode) {
        finish();
    }

    @Override
    public void callback(HandshakeResponse hreq, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void places_callback(List<Place> places, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(String message, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Log.d("error",e.getMessage());
        Toast.makeText(this,"Errore. Controllare i log.", Toast.LENGTH_LONG).show();
    }
}