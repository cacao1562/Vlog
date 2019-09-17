package kr.co.valuesys.vlog.mobile.Model;

import android.graphics.Bitmap;
import android.net.Uri;

public class VideoInfo {

    private String title;
    private Bitmap img;
    private Uri uri;
    private String date;

    public VideoInfo(String title, Bitmap img, Uri uri, String date) {
        this.title = title;
        this.img = img;
        this.uri = uri;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImg() {
        return img;
    }

    public Uri getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }
}
