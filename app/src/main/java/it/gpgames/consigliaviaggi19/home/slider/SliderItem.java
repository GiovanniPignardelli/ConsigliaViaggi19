package it.gpgames.consigliaviaggi19.home.slider;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SliderItem implements Parcelable {

    String keyword;
    Bitmap img;

    protected SliderItem(Parcel in) {
        keyword = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<SliderItem> CREATOR = new Creator<SliderItem>() {
        @Override
        public SliderItem createFromParcel(Parcel in) {
            return new SliderItem(in);
        }

        @Override
        public SliderItem[] newArray(int size) {
            return new SliderItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyword);
        dest.writeParcelable(img, flags);
    }
}
