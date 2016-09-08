package com.ihandy.a2014011345.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WebNews extends AppCompatActivity {
    WebView webView;
    String urlpath;
    String news_id;
    Boolean isFavored;
    Button exit, shareit;
    Button favorIt;
    New piece;

    void update(){
        if(isFavored)
            favorIt.setBackgroundResource(R.drawable.heat_whit);
        else
            favorIt.setBackgroundResource(R.drawable.heart_black);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_news);

        Intent intent = getIntent();
        urlpath = intent.getStringExtra("url");
        news_id = intent.getStringExtra("news_id");
        piece = MainActivity.findNewbyId(news_id);
        /*
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        */

        exit = (Button) findViewById(R.id.web_page_goback);
        favorIt = (Button) findViewById(R.id.favor_it);
        isFavored = intent.getStringExtra("favor").equals( "true" );
        update();

        favorIt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isFavored = !isFavored;
                        update();
                        sendMessageBack();
                    }
                }
        );

        shareit = (Button) findViewById(R.id.share);
        shareit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        //intent.setType("image/*");
                        intent.setType("text/*");

                        /*
                        if(piece.getImage()!=null) {
                            Drawable img = piece.getImage();
                            Bitmap bmp = piece.drawableToBitmap(img);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 1000, stream);
                            byte[] byteArray = stream.toByteArray();

                            try {
                                stream.close();
                                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_2014011345_file.jpg");

                                f.createNewFile();
                                FileOutputStream fo = new FileOutputStream(f);
                                fo.write(byteArray);
                                fo.close();
                            }
                            catch (IOException e) {
                            }
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_2014011345_file.jpg"));
                        }
                        */
                        String message = piece.title + " " + urlpath + " ";
                        if(piece.img_url!=null){
                            message += piece.img_url;
                        }
                        intent.putExtra(Intent.EXTRA_TEXT, message);

                        startActivity(Intent.createChooser(intent, "分享到"));
                    }
                }
        );

        webView = (WebView) findViewById(R.id.webviewer);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                if (super.shouldOverrideUrlLoading(view, url)) return true;
                else return false;
            }
        });
        webView.loadUrl(urlpath);

        exit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendMessageBack();
                        finish();
                    }
                }
        );
    }

    public void sendMessageBack(){
        Log.i("haha", "hhhhh  " + isFavored);
        Intent i = new Intent();
        i.putExtra("checked", "" + isFavored);
        i.putExtra("news_id", news_id);
        setResult(2, i);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
        {
            webView.goBack();
            return true;
        }
        sendMessageBack();
        return super.onKeyDown(keyCode, event);
    }
}
