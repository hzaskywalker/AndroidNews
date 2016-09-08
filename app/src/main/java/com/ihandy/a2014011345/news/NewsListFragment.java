package com.ihandy.a2014011345.news;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by hza on 16/9/4.
 */

public class NewsListFragment extends Fragment {
    LayoutInflater inflater;
    ViewGroup container;
    ListView listView;
    TextView textView;
    ListViewAdapter adapter;

    ArrayList<New> news;
    NewsList newsList;

    class ListViewAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        Context context;
        public ListViewAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            if(news!=null)
                return news.size();
            else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public class ViewHolder extends Activity {
            public ImageView img;
            public TextView title;
            public New a;
            public TextView source;
            public Thread thread;

            public void ImageHandler(){
                thread = new Thread(){
                    public void run() {
                        while (true) {
                            if (a.getImage() != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        img.setBackground(a.getImage());
                                    }
                                });
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                };
                thread.start();
            }

        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            New piece = news.get(i);
            if(view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.image_with_text, null);
                holder.img = (ImageView) view.findViewById(R.id.img_in_list);
                holder.title = (TextView) view.findViewById(R.id.title_in_list);
                holder.source = (TextView) view.findViewById(R.id.news_source);
                view.setTag(holder);
            }
            else
                holder = (ViewHolder)view.getTag();

            if(holder.thread!=null && holder.a!=piece) {
                holder.thread.interrupt();
                holder.thread = null;
            }
            /*
            if(holder.a!=null && holder.a.imageGetter!=null && holder.a!=piece) {
                holder.a.imageGetter.Image = null;
                holder.thread = null;
            }
            */
            if(piece.imageGetter==null || piece.imageGetter.Image==null)
                holder.img.setBackgroundResource(R.drawable.ic_launcher);
            holder.a = piece;
            holder.ImageHandler();


            final String source_url = piece.source_url;
            holder.title.setText(piece.title);
            holder.source.setText(piece.source_name);

            final New tmp = piece;
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(source_url == null)
                        return;
                    Intent intent = new Intent(getContext(), WebNews.class);
                    intent.putExtra("url", source_url);
                    intent.putExtra("news_id", tmp.news_id);
                    intent.putExtra("favor", tmp.isFavorite.toString());
                    startActivityForResult(intent, 2);
                }
            });

            return view;
        }
    }

    public NewsListFragment(){
    }

    private boolean isScrollToBottom;
    private boolean isScrollToTop;
    private boolean isLoadingMore = false; //判断是不是"加载更多"

    public View refresh(){
        update(null);
        listView = (ListView) inflater.inflate(R.layout.news_list_layout, container, false);
        ArrayList<View> list = new ArrayList<>();
        adapter = new ListViewAdapter(this.getContext());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                            if (isScrollToBottom && !isLoadingMore) {
                                isLoadingMore = true;
                                listView.setSelection(listView.getCount());
                                String lastTime = news.get(news.size()-1).news_id.toString();
                                Log.d("what", "加载更多.." + " " + lastTime);
                                update(lastTime);
                                isLoadingMore = false;
                            }
                            else if(isScrollToTop){
                                Log.d("what", "不加载更多");
                                update(null);
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView absListView, int i, int i1, int totalItemCount) {
                        if (listView.getLastVisiblePosition() == totalItemCount - 1) {
                            isScrollToBottom = true;
                        } else {
                            isScrollToBottom = false;
                        }
                        if(i==0){
                            isScrollToTop = true;
                        }
                        else{
                            isScrollToTop = false;
                        }
                    }
                }
        );
        return listView;
    }

    public static ArrayList<New> getSortedArray(HashMap<String, New> map){

        ArrayList<Long> array = new ArrayList<>();
        for(String key:map.keySet()) {
            array.add(Long.parseLong(key));
        }
        Collections.sort(array);
        ArrayList<New> ans = new ArrayList<>();
        for(int i = array.size()-1;i>=0;--i) {
            Log.d("e", array.get(i).toString());
            ans.add(map.get(array.get(i).toString()));
        }
        return ans;
    }

    public void update(final String haha){
        new Thread(){
            @Override
            public void run() {
                HashMap < String, New > map = newsList.update(haha);
                if(news!=null && map.size() == news.size()){
                    return;
                }
                Log.d("updated", ""+ map.size());
                news = getSortedArray(map);
                Log.d("updated", getActivity().toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("updated", "123");
                        if(adapter!=null)
                            adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    public void setNews(NewsList a){
        newsList = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container==null)
            return null;
        this.inflater = inflater;
        this.container = container;
        return refresh();
    }
}
