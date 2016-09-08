package com.ihandy.a2014011345.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class NewsList implements Serializable{
    public HashMap<String, New> news;
    public String category, title;
    public Boolean liked, watched;
    transient NewGetter getter;
    transient NewsListFragment fragment;

    public NewsList(String category, String title){
        this.category = category;
        this.title = title;
        liked = true;
        watched = false;
    }

    public void reload(){
        getter = new NewGetter();
    }

    public void setMessage(String[] tmp){
        //title = tmp[0];
        if(title==null)
            title = tmp[0];
        watched = tmp[1].equals("" + true);
        liked = tmp[2].equals("" + true);
    }

    HashMap<String, New> update(String last){
        if(getter == null)
            getter = new NewGetter();
        HashMap<String, New> tmp;
        try {
            tmp = (HashMap<String, New>) getter.refresh(this.category, last);
        } catch (IOException e) {
            Log.d("news failed", "IOexception");
            tmp = new HashMap<>();
        }
        if(news==null)
            news = tmp;
        else {
            for (String key : tmp.keySet()) {
                New val = tmp.get(key);
                if(news.get(key)==null) {
                    news.put(key, val);
                }
            }
        }
        if(news != null) {
            Log.d("news", "" + news.size());
        }
        return news;
    }

    public Fragment getFragment(){
        if(fragment == null) {
            fragment = new NewsListFragment();
            fragment.setNews(this);
        }
        return fragment;
    }
}
