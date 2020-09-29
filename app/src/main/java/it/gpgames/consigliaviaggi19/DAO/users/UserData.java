package it.gpgames.consigliaviaggi19.DAO.users;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**La classe User detiene localmente le informazioni relative ad un utente.
 * La classe maniente una propria istanza (LocalInstance), che fa riferimento all'utente corrente.*/
public class UserData implements Parcelable {

    private String displayName;
    private String email;
    private Uri avatar;
    private String userID;
    private boolean isBlacklisted;
    private Integer nReview;
    private Float avgReview;
    private String registerDate;

    public UserData(String displayName, String email, String userID, boolean isBlacklisted, Integer nReview, Float avgReview, String registerDate) {
        this.displayName = displayName;
        this.email = email;
        this.userID = userID;
        this.isBlacklisted = isBlacklisted;
        this.nReview = nReview;
        this.avgReview = avgReview;
        this.registerDate = registerDate;
    }

    private static UserData localInstance;

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        isBlacklisted = blacklisted;
    }

    public int getnReview() {
        return nReview;
    }

    public void setnReview(int nReview) {
        this.nReview = nReview;
    }

    public float getAvgReview() {
        return avgReview;
    }

    public void setAvgReview(float avgReview) {
        this.avgReview = avgReview;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public static void setLocalInstance(UserData localInstance) {
        UserData.localInstance = localInstance;
    }


    public static void initiateLocalInstance()
    {
        FirebaseFirestore.getInstance().collection("userPool").whereEqualTo("userID",FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    QuerySnapshot result = task.getResult();
                    localInstance=result.toObjects(UserData.class).get(0);
                }
            }
        });
    }


    public UserData()
    {

    }

    public static UserData getLocalInstance()
    {
            return localInstance;
    }

    public UserData(Parcel in) {
        displayName = in.readString();
        email = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        userID = in.readString();
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeParcelable(avatar, flags);
        dest.writeString(userID);
    }

    /**La classe UserDataUpdate implementa Runnable. Viene avviato su un Thread secondario per
     * salvare localmente le informazioni in UserData. Effettua operazioni network.*/
    private class UserDataUpdater implements Runnable{
        @Override
        public void run() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                setDisplayName(user.getDisplayName());
                setEmail(user.getEmail());
                setAvatar(user.getPhotoUrl());
                setUserID(user.getUid());
            }
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    void setUserID(String userID) {
        this.userID = userID;
    }


    public void downloadUserDataFromFirebase(){
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(new UserDataUpdater());
    }

}
