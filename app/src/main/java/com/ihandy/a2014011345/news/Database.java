package com.ihandy.a2014011345.news;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hza on 16/9/5.
 */
public class Database extends SQLiteOpenHelper {
    /**
     * 一个简单的数据库,用于存放分类信息。
     */
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "category.db";
    public static Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void onCreate(SQLiteDatabase db) {
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void mydump(SQLiteDatabase db, ArrayList<String> titleList, ArrayList<String> desc){
        db.execSQL("DROP TABLE IF EXISTS category");
        db.execSQL("CREATE TABLE category (key VARCHAR, desc VARCHAR)");
        for(int i = 0;i<desc.size();++i){
            db.execSQL("INSERT INTO category VALUES (?, ?)", new Object[]{titleList.get(i), desc.get(i)});
        }
    }

    public static void dumpNews(String category, NewsList newsList){
        try {
            Log.d("dumpNews", category);
            File file = new File(context.getFilesDir(), "__" + category);
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
            os.writeObject(newsList);
        }
        catch (IOException e){
            Log.d("file", e.toString());
        }
    }

    public static NewsList loadNews(String category){
        File file = new File(context.getFilesDir(), "__" + category);
        if(!file.exists()){
            return null;
        }
        else{
            try{
                ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));
                try {
                    return (NewsList) os.readObject();
                }
                catch (ClassNotFoundException e){
                    Log.d("class Not Found", e.toString());
                    return null;
                }
            }
            catch (IOException e){
                return null;
            }
        }
    }

    public static HashMap<String, String> myload(SQLiteDatabase db){
        HashMap<String, String> result = new HashMap<>();
        Cursor c;
        try {
            c = db.query("category", null, null, null, null, null, null);
        }
        catch(SQLiteException e){
            return result;
        }
        while (c.moveToNext()) {
            String _id = c.getString(c.getColumnIndex("key"));
            String d = c.getString(c.getColumnIndex("desc"));
            Log.i("db", _id + ", " + d);
            result.put(_id, d);
        }
        c.close();
        return result;
    }
}
