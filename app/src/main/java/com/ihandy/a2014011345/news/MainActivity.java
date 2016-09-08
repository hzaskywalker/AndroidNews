package com.ihandy.a2014011345.news;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;

import com.ihandy.a2014011345.news.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    CategoryGetter categoryGetter;
    static ArrayList<NewsList> newsLists;
    static HashMap<String, New> favoredNew;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MenuLeft menuLeft;
    static ViewPagerAdapter viewPagerAdapter;

    public static New findNewbyId(String news_id) {
        for (NewsList newsList : newsLists) {
            if (newsList.news != null && newsList.news.get(news_id) != null)
                return newsList.news.get(news_id);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsLists = new ArrayList<>();
        favoredNew = new HashMap<>();
        rebuildViewPage(null);
        final Activity thisactivity = this;
        new Thread(){
            public void run() {
                categoryGetter = new CategoryGetter();

                Database helper = new Database(thisactivity);
                SQLiteDatabase db = helper.getReadableDatabase();
                HashMap<String, String> ans = Database.myload(db);

                favoredNew = new HashMap<>();
                final ArrayList<NewsList> _newsLists = new ArrayList<>();

                for(String a: ans.keySet()){
                    if(categoryGetter.categories.get(a) == null) {
                        categoryGetter.categories.put(a, ViewPagerAdapter.fromDesc(ans.get(a))[0]);
                    }
                }

                Log.i("category", categoryGetter.categories.size() + "" );

                Runnable toupdate = new Runnable() {
                    @Override
                    public void run() {
                        for (NewsList a : _newsLists) {
                            newsLists.add(a);
                        }
                        rebuildViewPage(null);
                    }
                };
//                thisactivity.runOnUiThread(toupdate);

                for (String key : categoryGetter.categories.keySet()) {
                    NewsList tmp;
                    Log.i("category", key);
                    if((tmp = Database.loadNews(key)) != null){
                        Log.d("loadNews", key + " " + tmp.news.size() + " " + tmp.category);
                        tmp.update(null);
                    }
                    else {
                        tmp = new NewsList(key, categoryGetter.categories.get(key));
                    }
                    _newsLists.add(tmp);

                    if(tmp.news != null) {
                        for (String b : tmp.news.keySet()) {
                            New t = tmp.news.get(b);
                            if (t.isFavorite)
                                favoredNew.put(t.news_id, t);
                        }
                    }
                }

                thisactivity.runOnUiThread(toupdate);
            }
        }.start();

        menuLeft = new MenuLeft(this);
    }

    protected void onStop(){
        Log.d("onStop", "stop");
        for(NewsList a:newsLists){
            Log.d("dump", a.category);
            Database.dumpNews(a.category, a);
        }
        dump_to_dataset();
        super.onStop();
    }


    public void onBackPressed() {
        System.out.println("按下back，立刻退出activity时调用");
        super.onBackPressed();
    }

    public synchronized void rebuildViewPage(ArrayList<NewsList> a){
        if(a!=null) {
            newsLists = a;
        }
        /*
        if(viewPagerAdapter!=null){
            viewPagerAdapter.notifyDataSetChanged();
            return;
        }
        */
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), newsLists, viewPager);
        load_from_dataset();

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Log.e("resultCode", "" + resultCode);
        if(resultCode == 1) {
            rebuildViewPage(null);
            Log.i("Mainresult", "rebuildViewPage");
        }
        else if(resultCode == 2){
            Boolean checked = (data.getStringExtra("checked") .equals( "true" ));
            String news_id = data.getStringExtra("news_id");

            for(NewsList newsList:newsLists){
                if(newsList.news!=null && newsList.news.get(news_id)!=null){
                    New tmp = newsList.news.get(news_id);
                    Log.i("getResult", "haha" + " " + checked + data.getStringExtra("checked"));
                    tmp.isFavorite = checked;
                    if(checked){
                        if(favoredNew.get(tmp)==null)
                            favoredNew.put(tmp.news_id, tmp);
                    }
                    else {
                        if(favoredNew.get(tmp.news_id)!=null)
                            favoredNew.remove(tmp.news_id);
                    }
                    break;
                }
            }
        }
    }

    public void load_from_dataset(){
        Database helper = new Database(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        HashMap<String, String> ans = Database.myload(db);

        for(NewsList newsList:newsLists){
            String tt = ans.get(newsList.category);
            if(tt!=null) {
                String[] tmp = ViewPagerAdapter.fromDesc(tt);
                newsList.setMessage(tmp);
            }
        }
        viewPagerAdapter.setNewsList(newsLists);
        db.close();
    }


    public void dump_to_dataset(){
        Database helper = new Database(this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ArrayList<String> tmp = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();
        for(NewsList newsList:newsLists) {
            key.add(newsList.category);
            tmp.add(ViewPagerAdapter.toDesc(newsList.title, newsList.watched, newsList.liked));
        }
        Database.mydump(db, key, tmp);
        db.close();
    }
}
