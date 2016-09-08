package com.ihandy.a2014011345.news.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ihandy.a2014011345.news.Database;
import com.ihandy.a2014011345.news.NewsList;
import com.ihandy.a2014011345.news.R;
import com.ihandy.a2014011345.news.CategoryGetter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static android.database.sqlite.SQLiteDatabase.CREATE_IF_NECESSARY;
import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;
import static android.database.sqlite.SQLiteDatabase.SQLITE_MAX_LIKE_PATTERN_LENGTH;
import static android.database.sqlite.SQLiteDatabase.openDatabase;

/**
 * Created by hza on 16/9/4.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ArrayList<NewsList> viewLists;
    ViewPager viewPager;

    public void setNewsList(ArrayList<NewsList> newsList){
        viewLists = newsList;
    }

    NewsList getNewList(int position){
        int num=0;
        for(int i = 0;i<viewLists.size();++i) {
            if (viewLists.get(i).liked) {
                if (num == position) {
                    return viewLists.get(i);
                }
                num += 1;
            }
        }
        return new NewsList("empty", "Nothing here");
    }

    public ViewPagerAdapter(FragmentManager fm, ArrayList<NewsList> newsLists, final ViewPager viewPager){
        super(fm);
        this.viewPager = viewPager;
        viewLists = newsLists;

        viewPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        getNewList(position).watched=true;
                    }
                }
        );
    }

    @Override
    public Fragment getItem(int position) {
        return getNewList(position).getFragment();
    }

    @Override
    public int getCount(){
        int num = 0;
        for(int i = 0;i<viewLists.size();++i)
            if(viewLists.get(i).liked)
                num+=1;
        return num;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return getNewList(position).title;
    }

    public static String toDesc(String desc, Boolean watched, Boolean liked){
        return desc + "@hza@" + watched + "@hza@" + liked;
    }


    public static String[] fromDesc(String desc){
        String[] ans= desc.split("@hza@");
        return ans;
    }
}
