package it.gpgames.consigliaviaggi19.DAO.models.users;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**La classe User detiene localmente le informazioni relative ad un utente.
 * La classe maniente una propria istanza (LocalInstance), che fa riferimento all'utente corrente.*/
public class User implements Parcelable {

    private static User localInstance = null;

    private String displayName;
    private String email;
    private String avatar;
    private String userID;
    private boolean isBlacklisted;
    private Integer nReview;
    private Float avgReview;
    private int sumReviews;
    private String registerDate;
    private String firstName;
    private String lastName;

    public User(String displayName, String email, String userID, boolean isBlacklisted, Integer nReview, Float avgReview, String registerDate,String avatar,String firstName,String lastName, int sumReviews) {
        this.displayName = displayName;
        this.email = email;
        this.userID = userID;
        this.isBlacklisted = isBlacklisted;
        this.nReview = nReview;
        this.avgReview = avgReview;
        this.registerDate = registerDate;
        this.avatar=avatar;
        this.firstName=firstName;
        this.lastName=lastName;
        this.sumReviews=sumReviews;
    }

    public int getSumReviews() {
        return sumReviews;
    }

    public void setSumReviews(int sumReviews) {
        this.sumReviews = sumReviews;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public static void setLocalInstance(User localInstance) {
        User.localInstance = localInstance;
    }

    public User()
    {

    }

    public static User getLocalInstance()
    {
            return localInstance;
    }

    public User(Parcel in) {
        displayName = in.readString();
        email = in.readString();
        avatar = in.readString();
        userID = in.readString();
        nReview = in.readInt();
        avgReview = in.readFloat();
        avatar=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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
        dest.writeString(avatar);
        dest.writeString(userID);
        dest.writeInt(nReview);
        dest.writeFloat(avgReview);
        dest.writeString(avatar);
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
