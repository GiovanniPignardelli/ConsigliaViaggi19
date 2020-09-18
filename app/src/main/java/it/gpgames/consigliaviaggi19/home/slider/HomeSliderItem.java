package it.gpgames.consigliaviaggi19.home.slider;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class HomeSliderItem implements Parcelable {

    String keyword;
    Bitmap img;
    String desc;

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    protected HomeSliderItem(Parcel in) {
        keyword = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
        desc = in.readString();
        index = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(keyword);
        dest.writeParcelable(img, flags);
        dest.writeString(desc);
        dest.writeInt(index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HomeSliderItem> CREATOR = new Creator<HomeSliderItem>() {
        @Override
        public HomeSliderItem createFromParcel(Parcel in) {
            return new HomeSliderItem(in);
        }

        @Override
        public HomeSliderItem[] newArray(int size) {
            return new HomeSliderItem[size];
        }
    };

    public int getIndex() {
        return index;
    }

    int index;

    public String getKeyword() {
        return keyword;
    }



    public HomeSliderItem(Bitmap img, String keyword, String desc, int index){
        this.img = img;
        this.keyword = keyword;
        this.index=index;
        this.desc = desc;
    }

    public Bitmap getImg() {
        return img;
    }

}
