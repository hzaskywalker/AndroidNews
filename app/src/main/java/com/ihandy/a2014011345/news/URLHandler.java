package com.ihandy.a2014011345.news;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hza on 16/9/4.
 */
class URLHandler{
    private String content;
    public String urlpath;
    protected String getContentByUrl(String path)throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000);
            conn.setConnectTimeout(150000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;
        }
        finally {
            if(is != null) {
                is.close();
            }
        }
    }

    String getContent() {
        return content;
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String tmp;
        String ans = "";
        while((tmp = reader.readLine())!=null)
            ans += tmp;
        return ans;
    }

    public void parseContent(String content){
       this.content = content;
    }

    public void parseUrlPath() throws IOException{
        String content = getContentByUrl(urlpath);
        parseContent(content);
    }

    public void parseURL(String path, boolean anotherprocess, boolean need_join) throws IOException {
        urlpath = path;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    parseUrlPath();
                } catch (IOException e) {
                    Log.d("URLPARSEERROR", e.toString());
                    urlpath = null;
                    content = null;
                }
            }
        };
        if(anotherprocess) {
            Thread thread = new Thread(runnable);
            thread.start();
            if(need_join) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Log.d("app2", e.toString());
                }
            }
        }
        else {
            runnable.run();
        }
    }
}
