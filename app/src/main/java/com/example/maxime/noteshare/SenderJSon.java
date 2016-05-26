package com.example.maxime.noteshare;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Maxime on 21/05/2016.
 */
public class SenderJSon {

    private Note n;
    private static final String IP_ADRESS = "172.25.12.95";
    private static final String PORT = "8080";

    public String getIpAdresse(){
        return IP_ADRESS;
    }

    public Note getNote(){
        return this.n;
    }

    public String getPort(){
        return this.PORT;
    }

    public SenderJSon(Note n){
        this.n = n;
    }

    public String getFinalUrl(){
        String url = "http://" + getIpAdresse() + ":" + getPort();
        return url;
    }

    public String noteToJSon(){
        Gson gson = new Gson();
        String s = "";
        s = gson.toJson(n);
        return s;
    }
}