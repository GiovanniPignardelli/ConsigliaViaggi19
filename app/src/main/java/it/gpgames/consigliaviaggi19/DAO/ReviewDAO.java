package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.DAO.models.reviews.Review;

public interface ReviewDAO {
    public void getReviewsByUserID(String dataID, final DatabaseCallback callback, int callbackCode);
    public void getReviewsByPlaceID(String dataID, final DatabaseCallback callback, int actualSort, int actualOrder, int callbackCode);
    public void getReviewsByPlaceIDAndUserID(String placeID, String userID, final DatabaseCallback callback, int callbackCode);
    public void createReview(Review review, final DatabaseCallback callback, int callbackCode);
}
