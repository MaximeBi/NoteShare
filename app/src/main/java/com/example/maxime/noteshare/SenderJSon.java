package com.example.maxime.noteshare;

import android.util.Log;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Maxime on 21/05/2016.
 */
public class SenderJSon {

    private Note n;
    private String ipAdresse;
    private String port;

    public String getIpAdresse(){
        return this.ipAdresse;
    }

    public Note getNote(){
        return this.n;
    }

    public String getPort(){
        return this.port;
    }


    public SenderJSon(Note n, String URL, String port){
        this.n = n;
        this.ipAdresse = URL;
        this.port = port;
    }

    public String constructQuery(){
        String url = "http://" + getIpAdresse() + ":" + getPort() + "/" + noteToJSon(getNote());
        return url;
    }

    public String noteToJSon(Note n){
        Gson gson = new Gson();
        String s = "";
        try{
            s = URLEncoder.encode(gson.toJson(n), "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return s;
    }
}
