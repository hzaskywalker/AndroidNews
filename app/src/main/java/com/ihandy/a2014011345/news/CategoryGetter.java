package com.ihandy.a2014011345.news;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hza on 16/9/4.
 */
public class CategoryGetter{
    public URLHandler urlHandler;
    public Map<String, String> categories;

    CategoryGetter(){
        urlHandler = new URLHandler();
        try {
            refresh();
        }
        catch(IOException e) {
            Log.d("app3", e.toString());
        }
    }
    public void refresh() throws IOException {
        long now_time = System.currentTimeMillis();
        String urlpath = "http://assignment.crazz.cn/news/en/category?timestamp=" + now_time;
        urlHandler.parseURL(urlpath, true, true);
        String content = urlHandler.getContent();
        if(content!=null) {
            categories = parse(content);
        }
        else
            categories = new HashMap<String, String>();
        //Log.i("Cg", "Parse String");
    }

    public Map<String, String> parse(String content){
        HashMap<String, String> ans = new HashMap<>();
        //Log.i("Cg", "Parse String");
        try {
            JSONTokener jsonparser = new JSONTokener(content);
            JSONObject all = (JSONObject) jsonparser.nextValue();
            all = all.getJSONObject("data").getJSONObject("categories");

            for (Iterator iter = all.keys(); iter.hasNext();) { //先遍历整个 people 对象
                String key = (String) iter.next();
                //Log.i("main activity", key);
                ans.put(key, all.getString(key));
            }
        }
        catch(JSONException e){
            Log.d("CgError", "Json Error");
        }
        finally {
        }
        return ans;
    }
}
