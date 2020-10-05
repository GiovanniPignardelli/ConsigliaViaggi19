package it.gpgames.consigliaviaggi19.userpanel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import it.gpgames.consigliaviaggi19.DAO.models.places.Place;
import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;
import it.gpgames.consigliaviaggi19.DAO.models.users.User;
import it.gpgames.consigliaviaggi19.LoginActivity;
import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.network.NetworkChangeReceiver;
import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public class UserPanelActivity extends AppCompatActivity implements DatabaseCallback {

    public static final int IMGPRV=1;

    private static final NetworkChangeReceiver networkChangeReceiver=NetworkChangeReceiver.getNetworkChangeReceiverInstance();

    private ImageView bBack;
    private ImageView iUserPicture;
    private TextView tUserDisplayName,nReviews,avgReviews;
    private Button bChangeProfilePicture,bLogout,bResetPassword,bShowReviews;
    private User currentUser;
    private UserDAO userDao = DAOFactory.getDAOInstance().getUserDAO();
    private PlaceDAO placeDao = DAOFactory.getDAOInstance().getPlaceDAO();
    private ReviewDAO reviewDao = DAOFactory.getDAOInstance().getReviewDAO();
    private LoginDAO loginDao = DAOFactory.getDAOInstance().getLoginDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        bResetPassword = findViewById(R.id.reset_password_button);
        bBack=findViewById(R.id.back2);
        bChangeProfilePicture=findViewById(R.id.change_image_button);
        iUserPicture = findViewById(R.id.userPicture);
        iUserPicture.setImageResource(R.drawable.default_profile_picture);
        tUserDisplayName = findViewById(R.id.userDisplayName);
        bLogout = findViewById(R.id.logout_button);
        nReviews=findViewById(R.id.n_review_user);
        avgReviews=findViewById(R.id.avg_review_user);
        bShowReviews=findViewById(R.id.show_user_review_button);
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        currentUser = getIntent().getParcelableExtra("userToShow");
        if(currentUser.getUserID().equals(User.getLocalInstance().getUserID()))
        {
            bLogout.setVisibility(View.VISIBLE);
            bResetPassword.setVisibility(View.VISIBLE);
            bChangeProfilePicture.setVisibility(View.VISIBLE);
        }
        loadCurrentUserOnView();
    }



    /**Inizializza i Listeners dell'UserPanelActivity:
     * OnClickListener(bBack): button per tornare alla MainActivity;
     * OnClickListener(bChangeProfilePicture): cambia immagine profilo per lo User.
     * OnClickListener(bLogout): effettua sign-out.
     * OnClickListener(bResetPassword): invia email per il reset della password.*/
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
    }

    private void loadCurrentUserOnView(){
        tUserDisplayName.setText(currentUser.getDisplayName());
        nReviews.setText(String.valueOf(currentUser.getnReview()));
        avgReviews.setText(String.valueOf(currentUser.getAvgReview()));
        if(currentUser.getAvatar()!=null) Glide.with(getApplicationContext())
                .load(currentUser.getAvatar())
                .into(iUserPicture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMGPRV)
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

    /**Passa il media ottenuto dalla galleria (onActivityResult) all'UserDAO per l'update.*/
    private void uploadUserPicture(Bitmap pic)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        bChangeProfilePicture.setEnabled(false);
        bResetPassword.setEnabled(false);
        bLogout.setEnabled(false);
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
        }
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
    public void manageError(Exception e, int callbackCode) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        finish();
    }
}
