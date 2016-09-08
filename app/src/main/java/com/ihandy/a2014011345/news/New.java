package com.ihandy.a2014011345.news;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by hza on 16/9/4.
 */
public class New implements Serializable {

    public String categories, country, locale_category, origin;
    public String source_name, source_url, img_url;
    public String title;
    public Boolean isFavorite;
    public long fetched_time;
    public String news_id, updated_time;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if(w>100){
            h = (int)(100./ w * h);
            w = 100;
        }

        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    class ImageGetter extends URLHandler {
        Bitmap Image;
        InputStream is;
        public void parseUrlPath() {
            Drawable tImage = LoadImageFromWebOperations(urlpath);
            if(tImage != null) {
                Image = drawableToBitmap(tImage);
            }
            else
                Image = null;
        }

        public Drawable LoadImageFromWebOperations(String url) {
            try {
                is = (InputStream) new URL(url).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                return null;
            }
        }
    }
    transient ImageGetter imageGetter;
    public New(JSONObject news) {
        isFavorite = false;
        try {
            categories = news.getString("category");
            country = news.getString("country");
            fetched_time = news.getLong("fetched_time");

            JSONArray img_json= news.getJSONArray("imgs");
            if(img_json.length() > 0) {
                JSONObject img = img_json.getJSONObject(0);
                img_url = img.getString("url");
            }
            else {
                img_url = null;
            }

            locale_category = news.getString("locale_category");
            news_id = "" + news.getLong("news_id");
            origin = news.getString("origin");

            try {
                JSONObject source = news.getJSONObject("source");
                source_name = source.getString("name");
                source_url = source.getString("url");
            }
            catch (JSONException e){
                source_name = null;
                source_url = null;
            }

            title = news.getString("title");
            updated_time = news.getString("updated_time");
        }
        catch(JSONException e) {
            Log.d("app6", e.toString());
        }
    }

    public Drawable getImage(){
        try {
            if(imageGetter==null){
                imageGetter = new ImageGetter();
            }
            if(imageGetter.Image == null)
                imageGetter.parseURL(img_url, false, true);
            return new BitmapDrawable(Resources.getSystem(), imageGetter.Image );
        }
        catch (IOException e){
            Log.d("getImage", e.toString());
            return null;
        }
    }
}
