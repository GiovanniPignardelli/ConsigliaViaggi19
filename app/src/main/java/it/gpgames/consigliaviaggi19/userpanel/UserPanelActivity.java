package it.gpgames.consigliaviaggi19.userpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import it.gpgames.consigliaviaggi19.DAO.DAOFactory;
import it.gpgames.consigliaviaggi19.DAO.DatabaseCallback;
import it.gpgames.consigliaviaggi19.DAO.LoginDAO;
import it.gpgames.consigliaviaggi19.DAO.PlaceDAO;
import it.gpgames.consigliaviaggi19.DAO.ReviewDAO;
import it.gpgames.consigliaviaggi19.DAO.UserDAO;
import it.gpgames.consigliaviaggi19.DAO.firebaseDAO.HandshakeResponse;
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.LoginActivity;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

/**Activity per la visualizzazione delle informazioni relative ad un utente.
 * Questa viene utilizzata sia nel caso in cui un utente visualizzi il proprio pannello, sia nel caso
 * in cui un utente visualizzi un pannello altrui.
 * Nel primo caso l'activity mostra anche i pulsanti di gestione dell'account.
 * @see it.gpgames.consigliaviaggi19.DAO.DatabaseCallback*/
public class UserPanelActivity extends AppCompatActivity implements DatabaseCallback {

    public static final int IMGPRV=1;
    public static final int CHANGE_NAME=2;

    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    private ImageView bBack;
    private ImageView iUserPicture;
    private TextView tUserDisplayName,nReviews,avgReviews;
    private Button bChangeProfilePicture,bLogout,bResetPassword,bShowReviews,bChangeName;
    private Switch showFullNameSwitch;

    /**User attualmente visualizzato*/
    private User currentUser;

    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private ReviewDAO reviewDao = DAOFactory.getDAOInstance().getReviewDAO();
    private LoginDAO loginDao = DAOFactory.getDAOInstance().getLoginDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        bResetPassword = findViewById(R.id.reset_password_button);
        bBack=findViewById(R.id.back2);
        bChangeProfilePicture=findViewById(R.id.change_image_button);
        bChangeName=findViewById(R.id.changeNameButton);
        iUserPicture = findViewById(R.id.userPicture);
        iUserPicture.setImageResource(R.drawable.default_profile_picture);
        tUserDisplayName = findViewById(R.id.userDisplayName);
        bLogout = findViewById(R.id.logout_button);
        nReviews=findViewById(R.id.n_review_user);
        avgReviews=findViewById(R.id.avg_review_user);
        bShowReviews=findViewById(R.id.show_user_review_button);
        showFullNameSwitch=findViewById(R.id.fullNameSwitch);
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        currentUser = getIntent().getParcelableExtra("userToShow");
        loadCurrentUserOnView();
    }



    /**Inizializza i Listeners dell'UserPanelActivity*/
    private void initListeners()
    {
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), IMGPRV);
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDao.signOut(UserPanelActivity.this,1);
            }
        });

        bResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDao.resetPasswordRequest(UserPanelActivity.this,2);
            }
        });

        bShowReviews.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reviewDao.getReviewsByUserID(currentUser.getUserID(),UserPanelActivity.this, 0);
            }
        });

        bChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserPanelActivity.this, ChangeNameActivity.class);
                startActivityForResult(i, CHANGE_NAME);
            }
        });

        showFullNameSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked=showFullNameSwitch.isChecked();
                userDao.setShowFullName(currentUser.getUserID(), isChecked,UserPanelActivity.this,3);
                bBack.setEnabled(false);
                bChangeProfilePicture.setEnabled(false);
                bLogout.setEnabled(false);
                bResetPassword.setEnabled(false);
                bShowReviews.setEnabled(false);
                bChangeName.setEnabled(false);
                showFullNameSwitch.setEnabled(false);
            }
        });

    }

    /**Aggiorna le views con le informazioni dell'utente corrente.*/
    private void loadCurrentUserOnView(){
        tUserDisplayName.setText(currentUser.getShowingName());

        nReviews.setText(String.valueOf(currentUser.getnReview()));
        avgReviews.setText(String.valueOf(currentUser.getAvgReview()));
        if(currentUser.getAvatar()!=null && !currentUser.getAvatar().equals("")) Glide.with(getApplicationContext())
                .load(currentUser.getAvatar())
                .into(iUserPicture);

        if(currentUser.getUserID().equals(User.getLocalInstance().getUserID()))
        {
            showFullNameSwitch.setVisibility(View.VISIBLE);
            bLogout.setVisibility(View.VISIBLE);
            bChangeName.setVisibility(View.VISIBLE);
            bResetPassword.setVisibility(View.VISIBLE);
            bChangeProfilePicture.setVisibility(View.VISIBLE);
            if(currentUser.getShowingFlag()==User.FLAG_FULLNAME)
                showFullNameSwitch.setChecked(true);
            else
                showFullNameSwitch.setChecked(false);
            bLogout.setEnabled(true);
            bChangeName.setEnabled(true);
            bResetPassword.setEnabled(true);
            bChangeProfilePicture.setEnabled(true);
            if(!currentUser.getFirstName().equals("") || !currentUser.getLastName().equals(""))
                showFullNameSwitch.setEnabled(true);
            else
                showFullNameSwitch.setEnabled(false);
        }

        bBack.setEnabled(true);
        bShowReviews.setEnabled(true);
    }

    @Override
    /**L'activity attende risultati sia dal cambio di immagine di un utente, sia dal cambio del nome completo.*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMGPRV) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap picToUpload;
                try {
                    picToUpload = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    uploadUserPicture(picToUpload);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == CHANGE_NAME)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                boolean nameHasChanged=data.getBooleanExtra("change",false);
                if(nameHasChanged)
                {
                    boolean nameHasBeenReset=data.getBooleanExtra("reset", false);
                    if(nameHasBeenReset)
                    {
                        userDao.setUserFullName(currentUser.getUserID(),"","",this,3);
                    }
                    else
                    {
                        String name=data.getStringExtra("name");
                        if(name==null)name="";

                        String surname=data.getStringExtra("surname");
                        if(surname==null)surname="";

                        userDao.setUserFullName(currentUser.getUserID(),name,surname,this,3);
                    }
                }
            }
        }
    }

    /**Passa il media ottenuto dalla galleria (onActivityResult) all'UserDAO per l'update.*/
    private void uploadUserPicture(Bitmap pic)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        bLogout.setEnabled(false);
        bResetPassword.setEnabled(false);
        bChangeProfilePicture.setEnabled(false);
        showFullNameSwitch.setEnabled(false);
        bBack.setEnabled(false);
        bShowReviews.setEnabled(false);
        String uid = User.getLocalInstance().getUserID();
        userDao.setAvatarByID(uid, data, this, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void callback(Place place, MarkerOptions mOpt, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(int callbackCode) {
        switch(callbackCode){
            case CALLBACK_DEFAULT_CODE:
                finish();
                break;
            case 1:
                User.setLocalInstance(null);
                Intent i=new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
            case 2:
                Toast.makeText(getApplicationContext(),"Reset Password: controlla la casella di posta elettronica.",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                userDao.getUserByID(currentUser.getUserID(),this,1);
                break;

        }
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
    /**Allora l'activity ha chiesto informazioni circa un utente. Quelle ottenute vengono visualizzate.*/
    public void callback(User user, int callbackCode) {
        switch(callbackCode)
        {
            case 1:
                currentUser=user;
                loadCurrentUserOnView();
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public void callback(User user, ReviewsAdapter.ReviewViewHolder holder, int callbackCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void callback(List<Review> reviews, int callbackCode) {
        if(reviews.size()>0)
        {
            Intent i=new Intent(getApplicationContext(),ShowUserReviewsActivity.class);
            i.putExtra("reviewsToShow",(Serializable) reviews);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else
            Toast.makeText(getApplicationContext(),"L'utente non ha inserito recensioni.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void callback(String message, int callbackCode) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this, "Errore. Controllare i log.", Toast.LENGTH_SHORT).show();
        Log.d("query",e.getMessage());
    }
}
