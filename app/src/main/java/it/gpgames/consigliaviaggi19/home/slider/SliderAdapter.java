package it.gpgames.consigliaviaggi19.home.slider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import it.gpgames.consigliaviaggi19.R;

/** La classe SliderAdapter offre le funzionalità necessarie a far funzionare lo slider di immagini nella homepage. */
public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private List<SliderItem> sliderItemList;
    private ViewPager2 viewPager2;
    private String keyWord;

    public SliderAdapter(List<SliderItem> sliderItemList, ViewPager2 viewPager)
    {
        this.sliderItemList=sliderItemList;
        this.viewPager2=viewPager;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                R.layout.slide_item_container,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
            holder.setImage(sliderItemList.get(position));
    }

    /**Ottiene il numero di SliderItem presenti nella lista sliderItemList. */
    @Override
    public int getItemCount() {
        return sliderItemList.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SliderItem sliderItem)
        {
            SliderItem itemToShow=sliderItemList.get(sliderItem.getIndex());
            imageView.setImageBitmap(itemToShow.getImg());
            keyWord = itemToShow.getKeyword();
        }
    }


}
