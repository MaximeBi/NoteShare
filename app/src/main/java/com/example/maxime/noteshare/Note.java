package com.example.maxime.noteshare;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kapil on 12/05/2016.
 */
public class Note implements Serializable {

    private String id;
    private String title;
    private Date creationDate;
    private Date lastUpdate;
    private ArrayList<String> keywords;
    private String content;
    private String author;
    private ArrayList<String> collaborators;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.creationDate = new Date();
        this.lastUpdate = new Date();
        createKey();
    }

    public void createKey() {
        if(id == null) {
            if(title != null) {
                id = title + "_" + DATE_FORMAT.format(creationDate);
            } else if (content != null) {
                id = (content.split(" "))[0] + "_" + DATE_FORMAT.format(creationDate);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(ArrayList<String> collaborators) {
        this.collaborators = collaborators;
    }
}
