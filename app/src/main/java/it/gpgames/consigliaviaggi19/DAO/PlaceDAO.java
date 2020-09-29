package it.gpgames.consigliaviaggi19.DAO;

import it.gpgames.consigliaviaggi19.search.place_details.reviews.ReviewsAdapter;

public interface PlaceDAO {
    public void getPlaceByTags(final String searchString, DatabaseCallback callback, int callbackCode);

    public void getPlaceByID(final String id, DatabaseCallback callback, int callbackCode);

    public void getPlaceByID(final String dataID, final DatabaseCallback callback, final ReviewsAdapter.ReviewViewHolder holder, int callbackCode);
}
