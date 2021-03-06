package it.gpgames.consigliaviaggi19.search.place_details.slider;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import it.gpgames.consigliaviaggi19.R;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItem;
import it.gpgames.consigliaviaggi19.home.slider.HomeSliderItemsGetter;

/**Adapter che adatta le immagini relative a una struttura nello SliderImage della PlaceDetailsActivity*/
public class PlaceSliderAdapter extends SliderViewAdapter<PlaceSliderAdapter.PlaceSliderVH> {

    private List<String> mSliderItems;
    private AppCompatActivity activity;

    public PlaceSliderAdapter(List<String> sliderImages, AppCompatActivity activity) {
        mSliderItems = sliderImages;
        this.activity=activity;

    }

    @Override
    public PlaceSliderVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container, null);
        return new PlaceSliderVH(inflate);
    }

    @Override
    public void onBindViewHolder(final PlaceSliderVH viewHolder, int position) {
        final String sliderItemString = mSliderItems.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap= HomeSliderItemsGetter.getBitmapFromURL( sliderItemString );
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.image.setImageBitmap(bitmap);
                    }
                });

            }
        }).start();
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    class PlaceSliderVH extends SliderViewAdapter.ViewHolder
    {
        ImageView image;

        public PlaceSliderVH(View itemView) {
            super(itemView);
            this.image=itemView.findViewById(R.id.iv_auto_image_slider);
        }
    }
}
