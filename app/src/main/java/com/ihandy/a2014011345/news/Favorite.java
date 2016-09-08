package com.ihandy.a2014011345.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity {
    ArrayList<New> favoredNew;
    Button exitButton;
    ListView listView;

    class FavoriteListViewAdapter extends BaseAdapter{

        class ViewHolder extends Activity{
            public TextView textView;
        }

        private LayoutInflater mInflater;
        Context context;
        public FavoriteListViewAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            return favoredNew.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = null;
            final New piece = favoredNew.get(i);
            if(view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.favorite_line_layout, null);
                holder.textView = (TextView) view.findViewById(R.id.favorite_text_view);
                view.setTag(holder);
            }
            else
                holder = (ViewHolder)view.getTag();
            holder.textView.setText(piece.title);
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), WebNews.class);
                            intent.putExtra("url", piece.source_url);
                            intent.putExtra("news_id", piece.news_id);
                            intent.putExtra("favor", piece.isFavorite.toString());
                            startActivityForResult(intent, 2);
                        }
                    }
            );
            return view;
        }
    }

    void refresh(){
        favoredNew = NewsListFragment.getSortedArray(MainActivity.favoredNew);
        Log.i("favorite", "" + favoredNew.size());
        FavoriteListViewAdapter adapter = new FavoriteListViewAdapter(this);
        listView=(ListView)findViewById(R.id.favorite_list_view);
        listView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        exitButton = (Button)findViewById(R.id.exit_favorite);
        exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }

        );
        refresh();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Boolean checked = (data.getStringExtra("checked") .equals( "true" ));
        String news_id = data.getStringExtra("news_id");
        if(!checked){
            MainActivity.favoredNew.remove(news_id);
            refresh();
        }
    }
}
