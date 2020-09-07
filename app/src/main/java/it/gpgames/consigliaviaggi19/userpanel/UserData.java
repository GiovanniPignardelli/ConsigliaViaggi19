package it.gpgames.consigliaviaggi19.userpanel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.gpgames.consigliaviaggi19.home.slider.SliderItemsGetter;

/**La classe User detiene localmente le informazioni relative all'utente della sessione attuale. Tutti i suoi
 * membri sono static, così da evitare involontarie duplicazioni dei dati utente.*/
public class UserData implements Parcelable {

    private String displayName;
    private String email;
    private Uri avatar;
    private String userID;
    private boolean isEmailVerified;

    public UserData(Parcel in) {
        displayName = in.readString();
        email = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        userID = in.readString();
        isEmailVerified = in.readByte() != 0;
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

    public UserData() {

    }

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
        dest.writeByte((byte) (isEmailVerified ? 1 : 0));
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
                setEmailVerified(user.isEmailVerified());
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

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getUserID() {
        return userID;
    }

    void setUserID(String userID) {
        this.userID = userID;
    }

    public void downloadUserData(){
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(new UserDataUpdater());
    }

}
