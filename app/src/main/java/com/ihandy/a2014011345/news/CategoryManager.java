package com.ihandy.a2014011345.news;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ihandy.a2014011345.news.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class CategoryManager extends AppCompatActivity {
    private LayoutInflater inflater;
    public ArrayList<Boolean> watched;
    public ArrayList<Boolean> liked;
    public ArrayList<String> title;
    public ArrayList<String> keys;
    public Button forgetAll;
    public Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_manager);

        setResult(1);
        load_databse();

        inflater = LayoutInflater.from(this);

        forgetAll = (Button) findViewById(R.id.forget_all);
        exitButton = (Button) findViewById(R.id.exit_category_manager);

        genLayout();
        forgetAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int i = 0;i<watched.size();++i){
                            watched.set(i, false);
                        }
                        for(NewsList a:MainActivity.newsLists) {
                            a.news = new HashMap<String, New>();
                            a.fragment = null;
                        }
                        MainActivity.viewPagerAdapter = null;
                    }
                }
        );

        exitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dump_dataset();
                        CategoryManager.this.finish();
                    }
                }
        );
    }

    public void dump_dataset(){
        SQLiteDatabase db =  new Database(this).getWritableDatabase();
        ArrayList<String> tmp = new ArrayList<>();
        for(int i = 0;i<title.size(); ++i) {
            tmp.add(ViewPagerAdapter.toDesc(title.get(i), watched.get(i), liked.get(i)));
        }
        Database.mydump(db, keys, tmp);
        db.close();
    }

    public void load_databse(){
        SQLiteDatabase db=new Database(this).getReadableDatabase();
        HashMap<String, String> map = Database.myload(db);

        watched = new ArrayList<>();
        liked=  new ArrayList<>();
        title=  new ArrayList<>();
        keys = new ArrayList<>();

        for(String key: map.keySet()){
            String[] tmp = ViewPagerAdapter.fromDesc(map.get(key));
            keys.add(key);
            title.add(tmp[0]);
            watched.add(tmp[1].equals("" + true));
            liked.add(tmp[2].equals("" + true));
        }
        db.close();
    }

    public void genLayout() {
        for (int j = 0; j < 2; ++j) {
            LinearLayout parent;
            Boolean iswached = (j == 0);
            if (iswached)
                parent = (LinearLayout) findViewById(R.id.watched);
            else
                parent = (LinearLayout) findViewById(R.id.unwached);
            while(parent.getChildCount()>1){
                parent.removeViewAt(1);
            }
            for (int i = 0; i < title.size(); ++i) {

                if (liked.get(i) == iswached) {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textView = new TextView(linearLayout.getContext());
                    CheckBox checkBox = new CheckBox(linearLayout.getContext());

                    Log.d("db", title.get(i) + " " + iswached);
                    textView.setText(title.get(i));
                    checkBox.setChecked(liked.get(i));
                    final int c = i;
                    checkBox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    liked.set(c, b);
                                    Log.e("genlayout", "haha");
                                    genLayout();
                                }
                            }
                    );


                    parent.addView(linearLayout);
                    Log.e("sum", getResources().getDisplayMetrics().widthPixels + "");
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                            getResources().getDisplayMetrics().widthPixels * 3 /4,
                            MATCH_PARENT
                    );

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, MATCH_PARENT);

                    textView.setTextColor(Color.rgb(255, 255, 255));
                    checkBox.setBackgroundColor(Color.rgb(255, 255, 255));
                    linearLayout.addView(textView, p);
                    linearLayout.addView(checkBox, params);
                }
            }
        }
    }
}
