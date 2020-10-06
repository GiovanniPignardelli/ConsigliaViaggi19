package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;
import it.gpgames.consigliaviaggi19.userpanel.UserPanelActivity;

public interface UserDAO {
    public void getUserByID(final String userID, final DatabaseCallback callback, final ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
    public void getUserByID(String userID, final DatabaseCallback callback, int callbackCode);
    public void setAvatarByID(String uid, byte[] data, final DatabaseCallback callback, int callbackCode);
    void setShowFullName(String userID, boolean isChecked,DatabaseCallback callback,int callbackCode);
    void setUserFullName(String userID, String name, String surname, DatabaseCallback callback, int callbackCode);
}
