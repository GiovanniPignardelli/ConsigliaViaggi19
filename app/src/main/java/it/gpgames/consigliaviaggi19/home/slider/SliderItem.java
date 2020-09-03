package it.gpgames.consigliaviaggi19.home.slider;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class SliderItem {

    String keyword;
    Bitmap img;

    public String getKeyword() {
        return keyword;
    }



    public SliderItem(Bitmap img, String keyword){
        this.img = img;
        this.keyword = keyword;
    }

    public Bitmap getImg() {
        return img;
    }
}
