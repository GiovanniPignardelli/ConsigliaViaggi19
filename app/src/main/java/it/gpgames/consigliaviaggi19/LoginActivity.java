package it.gpgames.consigliaviaggi19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.home.MainActivity;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class LoginActivity extends AppCompatActivity implements DatabaseCallback {


    private Button login,signin;
    private EditText e,p;
    private LoginDAO loginDao = DAOFactory.getDAOInstance().getLoginDAO();
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    FirebaseAuth fAuth;

    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        signin=findViewById(R.id.signin);
        e=findViewById(R.id.emailtxt);
        p=findViewById(R.id.passwordtxt);

        loginDao.isAuthenticated(this,0);

    }

    public void initListeners(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email=e.getText().toString().trim();
                password=p.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    e.setError("Inserire una email valida.");
                }

                else if(password.length()<8)
                {
                    p.setError("La password inserita deve avere almeno 8 caratteri.");
                }
                else
                {
                    loginDao.authentication(email,password,LoginActivity.this,0);
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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

    @Override
    public void callback(int callbackCode) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    public void callback(Place place, int callbackCode) {

    }

    @Override
    public void callback(Place place, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    @Override
    public void callback(User user, int callbackCode) {
        User.setLocalInstance(user);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {

    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {

    }

    @Override
    public void callback(List<Place> weakList, List<Place> topList, int callbackCode) {

    }


    @Override
    public void callback(String message, int callbackCode) {
        if(message == null) initListeners();
        else{
            userDao.getUserByID(message,this,0);
        }
    }

    @Override
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
