package com.ihandy.a2014011345.news;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hza on 16/9/4.
 */
public class NewGetter{
    URLHandler urlHandler;
    public NewGetter(){
        urlHandler = new URLHandler();
    }

    public HashMap<String, New> refresh(String category, String news_id) throws IOException {
        String path = "http://assignment.crazz.cn/news/query?locale=en&category=" +category;
        if(news_id!=null) {
            path = path + "&max_news_id=" + news_id;
        }
        urlHandler.parseURL(path, true, true);
        String content = urlHandler.getContent();
        //Log.d("newgetterpath",path);
        //Log.d("gettercontent", ""+ content);
        HashMap<String, New> newsArray =new HashMap<>();
        if(content == null) {
            return newsArray;
        }
        try {
            JSONObject jsonparser = (JSONObject) ((new JSONTokener(content)).nextValue());
            JSONArray array = jsonparser.getJSONObject("data").getJSONArray("news");
            for (int i=0; i<array.length(); i++) {
                JSONObject actor = array.getJSONObject(i);
                New piece = new New(actor);
                newsArray.put(piece.news_id, piece);
            }
        }
        catch(JSONException e) {
            Log.d("app4", content);
            Log.d("app5", e.toString());
        }
        return newsArray;
    }
}
