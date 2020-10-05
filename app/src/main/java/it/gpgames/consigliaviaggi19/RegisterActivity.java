package it.gpgames.consigliaviaggi19;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.RegisterDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class RegisterActivity extends AppCompatActivity implements DatabaseCallback {
    EditText eUser,ePsw,eEmail,confirmPsw,fName,lName;
    Button bLogin,bRegister;
    RegisterDAO registerDAO= DAOFactory.getDAOInstance().getRegisterDAO();

    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        eUser=findViewById(R.id.usernametxt);
        ePsw=findViewById(R.id.passwordtxt);
        eEmail=findViewById(R.id.emailtxt);
        bLogin=findViewById(R.id.login);
        bRegister=findViewById(R.id.signin);
        confirmPsw=findViewById(R.id.confirmpasswordtxt);
        fName=findViewById(R.id.nametxt);
        lName=findViewById(R.id.surnametxt);
        initListeners();
    }

    private void initListeners(){
        bRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                final String email=eEmail.getText().toString().trim();
                final String username=eUser.getText().toString().trim();
                final String password=ePsw.getText().toString().trim();
                final String confirmPassword=confirmPsw.getText().toString().trim();
                final String firstName=fName.getText().toString().trim();
                final String lastName=lName.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    eEmail.setError("Inserire una email valida.");
                    return;
                }

                if(TextUtils.isEmpty(username))
                {
                    eUser.setError("Inserire un username valido.");
                    return;
                }

                if(TextUtils.isEmpty(password) || password.length()<8)
                {
                    ePsw.setError("Inserire una password valida. Almeno 8 caratteri.");
                    return;
                }

                if(!confirmPassword.equals(password))
                {
                    confirmPsw.setError("Le password non corrispondono!");
                    return;
                }

                if(!firstName.equals("") && !isAlpha(firstName))
                {
                    fName.setError("Il nome può contenere solo lettere.");
                    return;
                }

                if(!lastName.equals("") && !isAlpha(lastName))
                {
                    fName.setError("Il cognome può contenere solo lettere.");
                    return;
                }

                LocalDate date = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String dateString=date.format(formatter);

                User toRegister=new User(username,email,null,false,0, (float) 0,dateString,null,firstName,lastName,0);
                registerDAO.register(toRegister,password,RegisterActivity.this,0);


            }
        });

        bLogin.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
    }

    /**Restituisce true se una stringa contiene solo caratteri alfabetici*/
    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(int callbackCode) {
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
        User.setLocalInstance(user);
        Intent i=new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
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

    }

    @Override
    public void callback(String message, int callbackCode) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this,"Errore. Controllare i log.", Toast.LENGTH_LONG);
        Log.d("callback error",e.getMessage());
    }
}
